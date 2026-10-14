# =====================================================================
# AsistApp - Lambda: asistapp-asistencias  (Python 3.14)
# HU02 Registrar ingreso      HU03 Registrar salida
# HU04 Historial              HU05 Tardanzas (estado TARDANZA)
# HU06 Ausencias              HU09 Registros de todos (administrador)
# HU10 Reportes de asistencia (administrador)
#
# Rutas (API Gateway):
#   POST /asistencias/ingreso                 body: { trabajadorId }
#   POST /asistencias/salida                  body: { trabajadorId }
#   GET  /asistencias/hoy/{trabajadorId}
#   GET  /asistencias/trabajador/{trabajadorId}
#   GET  /asistencias                         (administrador)
#   GET  /ausencias/{trabajadorId}
#   GET  /reportes/{desde}/{hasta}            (administrador, fechas AAAA-MM-DD)
#
# Variables de entorno:
#   TABLA_TRABAJADORES = Trabajadores
#   TABLA_ASISTENCIAS  = Asistencias   (clave de partición: trabajadorId, de ordenación: fecha)
#   TABLA_PERMISOS     = Permisos
# =====================================================================
import base64
import json
import os
import re
from datetime import date, datetime, timedelta, timezone
from decimal import Decimal

import boto3
from boto3.dynamodb.conditions import Attr, Key
from botocore.exceptions import ClientError

dynamodb = boto3.resource("dynamodb")
tabla_trabajadores = dynamodb.Table(os.environ.get("TABLA_TRABAJADORES", "Trabajadores"))
tabla_asistencias = dynamodb.Table(os.environ.get("TABLA_ASISTENCIAS", "Asistencias"))
tabla_permisos = dynamodb.Table(os.environ.get("TABLA_PERMISOS", "Permisos"))

# Python: lunes = 0 ... domingo = 6
DIAS = ["Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado", "Domingo"]
ZONA_PERU = timezone(timedelta(hours=-5))  # Perú = UTC-5, sin horario de verano


# ---------- Respuesta y utilidades ----------
def convertir_decimal(valor):
    if isinstance(valor, Decimal):
        return int(valor) if valor % 1 == 0 else float(valor)
    raise TypeError(f"Tipo no serializable: {type(valor)}")


def respuesta(status_code, success, data, mensaje=None):
    return {
        "statusCode": status_code,
        "headers": {"Content-Type": "application/json"},
        "body": json.dumps(
            {
                "success": success,
                "data": data,
                "mensaje": mensaje,
                "timestamp": datetime.now(timezone.utc).isoformat(),
            },
            default=convertir_decimal,
            ensure_ascii=False,
        ),
    }


def leer_body(event):
    body = event.get("body")
    if not body:
        return {}
    if event.get("isBase64Encoded"):
        body = base64.b64decode(body).decode("utf-8")
    return json.loads(body)


def ahora_lima():
    ahora = datetime.now(ZONA_PERU)
    return ahora.strftime("%Y-%m-%d"), ahora.strftime("%H:%M"), ahora.weekday()


def a_minutos(hhmm):
    horas, minutos = hhmm.split(":")
    return int(horas) * 60 + int(minutos)


def dia_semana_de(fecha):
    return date.fromisoformat(fecha).weekday()


def sumar_dias(fecha, dias):
    return (date.fromisoformat(fecha) + timedelta(days=dias)).isoformat()


def fecha_valida(fecha):
    if not fecha or not re.fullmatch(r"\d{4}-\d{2}-\d{2}", fecha):
        return False
    try:
        date.fromisoformat(fecha)
        return True
    except ValueError:
        return False


# Hora de salida según el día (sábado media jornada, domingo descanso)
def hora_salida_del_dia(trabajador, dia_semana):
    if dia_semana == 6:
        return None
    if dia_semana == 5:
        return trabajador.get("horaSalidaSabado") or None
    return trabajador.get("horaSalida")


def es_dia_laborable(trabajador, fecha):
    return hora_salida_del_dia(trabajador, dia_semana_de(fecha)) is not None


def dias_laborables(trabajador, desde, hasta):
    lista = []
    fecha = desde
    while fecha <= hasta:
        if es_dia_laborable(trabajador, fecha):
            lista.append(fecha)
        fecha = sumar_dias(fecha, 1)
    return lista


# ---------- Acceso a datos ----------
def scan_todo(tabla, **params):
    items = []
    while True:
        resultado = tabla.scan(**params)
        items.extend(resultado.get("Items", []))
        if "LastEvaluatedKey" not in resultado:
            return items
        params["ExclusiveStartKey"] = resultado["LastEvaluatedKey"]


def get_trabajador(trabajador_id):
    return tabla_trabajadores.get_item(Key={"id": trabajador_id}).get("Item")


def asistencias_de_trabajador(trabajador_id, desde="0000-00-00", hasta="9999-12-31"):
    items = []
    params = {
        "KeyConditionExpression": Key("trabajadorId").eq(trabajador_id) & Key("fecha").between(desde, hasta),
        "ScanIndexForward": False,  # más recientes primero
    }
    while True:
        resultado = tabla_asistencias.query(**params)
        items.extend(resultado.get("Items", []))
        if "LastEvaluatedKey" not in resultado:
            return items
        params["ExclusiveStartKey"] = resultado["LastEvaluatedKey"]


def permisos_aprobados(trabajador_id=None):
    filtro = Attr("estado").eq("APROBADO")
    if trabajador_id:
        filtro = filtro & Attr("trabajadorId").eq(trabajador_id)
    return scan_todo(tabla_permisos, FilterExpression=filtro)


def permiso_que_cubre(permisos, trabajador_id, fecha):
    for p in permisos:
        if p.get("trabajadorId") == trabajador_id and p.get("fechaInicio") <= fecha <= p.get("fechaFin"):
            return p
    return None


# ---------- Handler ----------
def lambda_handler(event, context):
    # HTTP API envía routeKey; REST API envía httpMethod + resource
    route_key = event.get("routeKey") or f"{event.get('httpMethod')} {event.get('resource')}"
    p = event.get("pathParameters") or {}
    try:
        if route_key == "POST /asistencias/ingreso":
            return registrar_ingreso(leer_body(event))
        if route_key == "POST /asistencias/salida":
            return registrar_salida(leer_body(event))
        if route_key == "GET /asistencias/hoy/{trabajadorId}":
            return asistencia_de_hoy(p.get("trabajadorId"))
        if route_key == "GET /asistencias/trabajador/{trabajadorId}":
            return historial(p.get("trabajadorId"))
        if route_key == "GET /asistencias":
            return todas_las_asistencias()
        if route_key == "GET /ausencias/{trabajadorId}":
            return ausencias(p.get("trabajadorId"))
        if route_key == "GET /reportes/{desde}/{hasta}":
            return reporte(p.get("desde"), p.get("hasta"))
        return respuesta(404, False, None, "Ruta no encontrada")
    except Exception as error:
        print("ERROR:", repr(error))
        return respuesta(500, False, None, "Error interno del servidor")


# HU02 - Registrar ingreso (la hora oficial la pone el servidor, no el celular)
def registrar_ingreso(body):
    trabajador_id = body.get("trabajadorId")
    if not trabajador_id:
        return respuesta(200, False, None, "Falta el trabajador")
    trabajador = get_trabajador(trabajador_id)
    if not trabajador:
        return respuesta(200, False, None, "Trabajador no encontrado")

    fecha, hora, dia_semana = ahora_lima()
    salida_hoy = hora_salida_del_dia(trabajador, dia_semana)
    if not salida_hoy:
        return respuesta(200, False, None, f"Hoy {DIAS[dia_semana]} es tu día de descanso")

    diferencia = a_minutos(hora) - a_minutos(trabajador["horaIngreso"])
    tolerancia = int(trabajador.get("toleranciaMin", 10))
    es_tardanza = diferencia > tolerancia

    asistencia = {
        "id": f"{trabajador_id}#{fecha}",
        "trabajadorId": trabajador_id,
        "fecha": fecha,
        "trabajadorNombre": trabajador.get("nombres"),
        "area": trabajador.get("area"),
        "sede": trabajador.get("sede"),
        "horaIngreso": hora,
        "horaSalida": None,
        "horarioIngreso": trabajador["horaIngreso"],
        "horarioSalida": salida_hoy,
        "estado": "TARDANZA" if es_tardanza else "A_TIEMPO",
        "minutosTardanza": diferencia if es_tardanza else 0,
        "minutosTrabajados": 0,
        "registradoEn": datetime.now(timezone.utc).isoformat(),
    }

    try:
        tabla_asistencias.put_item(
            Item=asistencia,
            ConditionExpression="attribute_not_exists(trabajadorId)",  # solo un ingreso por día
        )
    except ClientError as error:
        if error.response["Error"]["Code"] == "ConditionalCheckFailedException":
            return respuesta(200, False, None, "Ya registraste tu ingreso hoy")
        raise
    return respuesta(201, True, asistencia)


# HU03 - Registrar salida
def registrar_salida(body):
    trabajador_id = body.get("trabajadorId")
    if not trabajador_id:
        return respuesta(200, False, None, "Falta el trabajador")
    fecha, hora, _ = ahora_lima()

    actual = tabla_asistencias.get_item(Key={"trabajadorId": trabajador_id, "fecha": fecha}).get("Item")
    if not actual:
        return respuesta(200, False, None, "Primero debes registrar tu ingreso")
    if actual.get("horaSalida"):
        return respuesta(200, False, None, "Ya registraste tu salida hoy")

    minutos_trabajados = max(0, a_minutos(hora) - a_minutos(actual["horaIngreso"]))
    resultado = tabla_asistencias.update_item(
        Key={"trabajadorId": trabajador_id, "fecha": fecha},
        UpdateExpression="SET horaSalida = :hs, minutosTrabajados = :mt",
        ConditionExpression="attribute_exists(trabajadorId)",
        ExpressionAttributeValues={":hs": hora, ":mt": minutos_trabajados},
        ReturnValues="ALL_NEW",
    )
    return respuesta(200, True, resultado.get("Attributes"))


# Estado del día para la pantalla de Inicio (data = null si aún no marca)
def asistencia_de_hoy(trabajador_id):
    fecha, _, _ = ahora_lima()
    item = tabla_asistencias.get_item(Key={"trabajadorId": trabajador_id, "fecha": fecha}).get("Item")
    return respuesta(200, True, item)


# HU04 / HU05 - Historial del trabajador (la app filtra las tardanzas)
def historial(trabajador_id):
    items = asistencias_de_trabajador(trabajador_id)
    return respuesta(200, True, {"items": items, "nextKey": None})


# HU09 - Todos los registros (administrador)
def todas_las_asistencias():
    items = scan_todo(tabla_asistencias)
    items.sort(key=lambda a: a.get("fecha", "") + a.get("horaIngreso", ""), reverse=True)
    return respuesta(200, True, {"items": items, "nextKey": None})


# HU06 - Ausencias: días laborables (últimos 60 días, hasta ayer) sin marcación de ingreso
def ausencias(trabajador_id):
    trabajador = get_trabajador(trabajador_id)
    if not trabajador:
        return respuesta(200, False, None, "Trabajador no encontrado")

    hoy, _, _ = ahora_lima()
    hasta = sumar_dias(hoy, -1)
    desde = sumar_dias(hoy, -60)
    if trabajador.get("fechaRegistro") and trabajador["fechaRegistro"] > desde:
        desde = trabajador["fechaRegistro"]

    marcados = {a["fecha"] for a in asistencias_de_trabajador(trabajador_id, desde, hasta)}
    permisos = permisos_aprobados(trabajador_id)

    items = []
    for fecha in dias_laborables(trabajador, desde, hasta):
        if fecha in marcados:
            continue
        permiso = permiso_que_cubre(permisos, trabajador_id, fecha)
        items.append({
            "fecha": fecha,
            "dia": DIAS[dia_semana_de(fecha)],
            "tipo": "JUSTIFICADA" if permiso else "INJUSTIFICADA",
            "motivo": permiso["tipo"] if permiso else "Sin marcación de entrada ni salida",
        })
    items.reverse()  # más recientes primero
    return respuesta(200, True, {"items": items, "nextKey": None})


# HU10 - Reporte de asistencia por trabajador en un rango de fechas
def reporte(desde, hasta):
    if not fecha_valida(desde) or not fecha_valida(hasta) or desde > hasta:
        return respuesta(200, False, None, "Rango de fechas inválido (usa AAAA-MM-DD)")
    hoy, _, _ = ahora_lima()
    limite = hasta if hasta < hoy else sumar_dias(hoy, -1)  # las ausencias solo cuentan hasta ayer

    trabajadores = [t for t in scan_todo(tabla_trabajadores) if t.get("rol") == "TRABAJADOR"]
    asistencias = scan_todo(tabla_asistencias, FilterExpression=Attr("fecha").between(desde, hasta))
    permisos = permisos_aprobados()

    items = []
    for t in trabajadores:
        propias = [a for a in asistencias if a.get("trabajadorId") == t["id"]]
        marcados = {a["fecha"] for a in propias}
        inicio = t["fechaRegistro"] if t.get("fechaRegistro") and t["fechaRegistro"] > desde else desde
        sin_marcar = [f for f in dias_laborables(t, inicio, limite) if f not in marcados] if inicio <= limite else []
        justificadas = len([f for f in sin_marcar if permiso_que_cubre(permisos, t["id"], f)])
        tardanzas = [a for a in propias if a.get("estado") == "TARDANZA"]

        items.append({
            "trabajadorId": t["id"],
            "nombres": t.get("nombres"),
            "dni": t.get("dni"),
            "area": t.get("area"),
            "diasAsistidos": len(propias),
            "aTiempo": len(propias) - len(tardanzas),
            "tardanzas": len(tardanzas),
            "minutosTardanza": sum(int(a.get("minutosTardanza", 0)) for a in tardanzas),
            "ausenciasInjustificadas": len(sin_marcar) - justificadas,
            "ausenciasJustificadas": justificadas,
        })
    items.sort(key=lambda r: r["nombres"] or "")
    return respuesta(200, True, {"items": items, "nextKey": None})
