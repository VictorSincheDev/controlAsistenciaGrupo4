# =====================================================================
# AsistApp - Lambda: asistapp-trabajadores  (Python 3.14)
# HU01: Iniciar sesión  |  HU07: Consultar horario laboral (perfil)
#
# Rutas (API Gateway):
#   POST /login                 -> inicia sesión con DNI y contraseña
#   GET  /trabajadores/{id}     -> datos del trabajador (perfil + horario)
#
# Variables de entorno:
#   TABLA_TRABAJADORES = Trabajadores
# =====================================================================
import base64
import hashlib
import json
import os
from datetime import datetime, timezone
from decimal import Decimal

import boto3
from boto3.dynamodb.conditions import Attr

dynamodb = boto3.resource("dynamodb")
tabla_trabajadores = dynamodb.Table(os.environ.get("TABLA_TRABAJADORES", "Trabajadores"))


# DynamoDB devuelve los números como Decimal: los convertimos para poder enviarlos en JSON
def convertir_decimal(valor):
    if isinstance(valor, Decimal):
        return int(valor) if valor % 1 == 0 else float(valor)
    raise TypeError(f"Tipo no serializable: {type(valor)}")


# Misma estructura de respuesta que usa la app: { success, data, mensaje, timestamp }
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


def hash_password(password):
    return hashlib.sha256(password.encode("utf-8")).hexdigest()


# Nunca devolvemos el hash de la contraseña a la app
def sin_password(trabajador):
    return {k: v for k, v in trabajador.items() if k != "passwordHash"}


def lambda_handler(event, context):
    # HTTP API envía routeKey; REST API envía httpMethod + resource
    route_key = event.get("routeKey") or f"{event.get('httpMethod')} {event.get('resource')}"
    path = event.get("pathParameters") or {}
    try:
        if route_key == "POST /login":
            return login(leer_body(event))
        if route_key == "GET /trabajadores/{id}":
            return get_trabajador(path.get("id"))
        return respuesta(404, False, None, "Ruta no encontrada")
    except Exception as error:
        print("ERROR:", repr(error))
        return respuesta(500, False, None, "Error interno del servidor")


# HU01 - Iniciar sesión
def login(body):
    dni = str(body.get("dni") or "").strip()
    password = body.get("password") or ""
    if not dni or not password:
        return respuesta(200, False, None, "Ingresa tu DNI y contraseña")

    params = {"FilterExpression": Attr("dni").eq(dni)}
    items = []
    while True:
        resultado = tabla_trabajadores.scan(**params)
        items.extend(resultado.get("Items", []))
        if "LastEvaluatedKey" not in resultado:
            break
        params["ExclusiveStartKey"] = resultado["LastEvaluatedKey"]

    trabajador = items[0] if items else None
    if not trabajador or trabajador.get("passwordHash") != hash_password(password):
        return respuesta(200, False, None, "DNI o contraseña incorrectos")
    if trabajador.get("estado") and trabajador.get("estado") != "ACTIVO":
        return respuesta(200, False, None, "Tu usuario se encuentra inactivo. Comunícate con RRHH")

    return respuesta(200, True, sin_password(trabajador))


# HU07 - Perfil y horario del trabajador
def get_trabajador(trabajador_id):
    resultado = tabla_trabajadores.get_item(Key={"id": trabajador_id})
    item = resultado.get("Item")
    if not item:
        return respuesta(404, False, None, "Trabajador no encontrado")
    return respuesta(200, True, sin_password(item))
