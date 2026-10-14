# =====================================================================
# AsistApp - Lambda: asistapp-permisos  (Python 3.14)
# HU08: Solicitar permiso (trabajador) y aprobar/rechazar (administrador)
#
# Rutas (API Gateway):
#   GET  /tipos-permiso
#   GET  /permisos                          (administrador: todos)
#   GET  /permisos/trabajador/{trabajadorId}
#   POST /permisos                          body: { trabajadorId, tipo, fechaInicio, fechaFin, motivo }
#   PUT  /permisos/{id}                     body: { estado: APROBADO|RECHAZADO, observacion, revisadoPor }
#
# Variables de entorno:
#   TABLA_PERMISOS     = Permisos
#   TABLA_TRABAJADORES = Trabajadores
#   TABLA_TIPOS        = TiposPermiso
# =====================================================================
import base64
import json
import os
import re
import uuid
from datetime import date, datetime, timedelta, timezone
from decimal import Decimal

import boto3
from boto3.dynamodb.conditions import Attr
from botocore.exceptions import ClientError

dynamodb = boto3.resource("dynamodb")
tabla_permisos = dynamodb.Table(os.environ.get("TABLA_PERMISOS", "Permisos"))
tabla_trabajadores = dynamodb.Table(os.environ.get("TABLA_TRABAJADORES", "Trabajadores"))
tabla_tipos = dynamodb.Table(os.environ.get("TABLA_TIPOS", "TiposPermiso"))

ZONA_PERU = timezone(timedelta(hours=-5))


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


def fecha_valida(fecha):
    if not fecha or not re.fullmatch(r"\d{4}-\d{2}-\d{2}", fecha):
        return False
    try:
        date.fromisoformat(fecha)
        return True
    except ValueError:
        return False


def scan_todo(tabla, **params):
    items = []
    while True:
        resultado = tabla.scan(**params)
        items.extend(resultado.get("Items", []))
        if "LastEvaluatedKey" not in resultado:
            return items
        params["ExclusiveStartKey"] = resultado["LastEvaluatedKey"]


def mas_recientes_primero(items):
    return sorted(items, key=lambda p: p.get("fechaSolicitud", ""), reverse=True)


def lambda_handler(event, context):
    # HTTP API envía routeKey; REST API envía httpMethod + resource
    route_key = event.get("routeKey") or f"{event.get('httpMethod')} {event.get('resource')}"
    p = event.get("pathParameters") or {}
    try:
        if route_key == "GET /tipos-permiso":
            return get_tipos()
        if route_key == "GET /permisos":
            return get_permisos()
        if route_key == "GET /permisos/trabajador/{trabajadorId}":
            return get_permisos_trabajador(p.get("trabajadorId"))
        if route_key == "POST /permisos":
            return insertar_permiso(leer_body(event))
        if route_key == "PUT /permisos/{id}":
            return actualizar_estado(p.get("id"), leer_body(event))
        return respuesta(404, False, None, "Ruta no encontrada")
    except Exception as error:
        print("ERROR:", repr(error))
        return respuesta(500, False, None, "Error interno del servidor")


def get_tipos():
    items = sorted(scan_todo(tabla_tipos), key=lambda t: t.get("nombre", ""))
    return respuesta(200, True, {"items": items, "nextKey": None})


def get_permisos():
    items = mas_recientes_primero(scan_todo(tabla_permisos))
    return respuesta(200, True, {"items": items, "nextKey": None})


def get_permisos_trabajador(trabajador_id):
    items = scan_todo(tabla_permisos, FilterExpression=Attr("trabajadorId").eq(trabajador_id))
    return respuesta(200, True, {"items": mas_recientes_primero(items), "nextKey": None})


# HU08 - Solicitar permiso
def insertar_permiso(body):
    trabajador_id = body.get("trabajadorId")
    tipo = body.get("tipo")
    fecha_inicio = body.get("fechaInicio")
    fecha_fin = body.get("fechaFin")
    motivo = (body.get("motivo") or "").strip()

    if not all([trabajador_id, tipo, fecha_inicio, fecha_fin, motivo]):
        return respuesta(200, False, None, "Todos los campos son obligatorios")
    if not fecha_valida(fecha_inicio) or not fecha_valida(fecha_fin):
        return respuesta(200, False, None, "Las fechas deben tener el formato AAAA-MM-DD")
    if fecha_fin < fecha_inicio:
        return respuesta(200, False, None, "La fecha de fin no puede ser anterior a la de inicio")

    trabajador = tabla_trabajadores.get_item(Key={"id": trabajador_id}).get("Item")
    if not trabajador:
        return respuesta(200, False, None, "Trabajador no encontrado")

    ahora = datetime.now(timezone.utc)
    anio = datetime.now(ZONA_PERU).year
    permiso = {
        "id": str(uuid.uuid4()),
        "codigo": f"SOL-{anio}-{str(int(ahora.timestamp() * 1000))[-5:]}",
        "trabajadorId": trabajador_id,
        "trabajadorNombre": trabajador.get("nombres"),
        "area": trabajador.get("area"),
        "tipo": tipo,
        "fechaInicio": fecha_inicio,
        "fechaFin": fecha_fin,
        "motivo": motivo,
        "estado": "PENDIENTE",
        "observacion": None,
        "revisadoPor": None,
        "fechaSolicitud": ahora.isoformat(),
    }
    tabla_permisos.put_item(Item=permiso)
    return respuesta(201, True, permiso)


# Administrador: aprobar o rechazar
def actualizar_estado(permiso_id, body):
    estado = body.get("estado")
    if estado not in ("APROBADO", "RECHAZADO"):
        return respuesta(200, False, None, "Estado inválido")
    try:
        resultado = tabla_permisos.update_item(
            Key={"id": permiso_id},
            UpdateExpression="SET estado = :e, observacion = :o, revisadoPor = :r, fechaRevision = :f",
            ConditionExpression="attribute_exists(id)",
            ExpressionAttributeValues={
                ":e": estado,
                ":o": body.get("observacion") or None,
                ":r": body.get("revisadoPor") or None,
                ":f": datetime.now(timezone.utc).isoformat(),
            },
            ReturnValues="ALL_NEW",
        )
        return respuesta(200, True, resultado.get("Attributes"))
    except ClientError as error:
        if error.response["Error"]["Code"] == "ConditionalCheckFailedException":
            return respuesta(200, False, None, "Permiso no encontrado")
        raise

