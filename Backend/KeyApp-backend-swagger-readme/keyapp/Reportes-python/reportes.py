# ============================================================
# KEYAPP - REPORTES ESTADISTICOS CON PYTHON
# Adaptacion del ejemplo de prendas (Armario Digital).
# Librerias: requests, pandas, numpy y matplotlib.
# ============================================================

# Importamos os para trabajar con carpetas y variables de entorno
import os

# Importamos requests para consultar la API de Spring Boot
import requests

# Importamos pandas para organizar la informacion en tablas
import pandas as pd

# Importamos numpy para realizar calculos matematicos
import numpy as np

# Importamos matplotlib para crear las graficas
import matplotlib
matplotlib.use("Agg")
import matplotlib.pyplot as plt

# Importamos json y base64 para entregar los resultados al backend
import json
import base64


# Direccion de la API de KeyApp
URL_RESERVAS = os.getenv("KEYAPP_API_URL", "http://localhost:8080/api").rstrip("/") + "/reservas"
URL_AULAS = os.getenv("KEYAPP_API_URL", "http://localhost:8080/api").rstrip("/") + "/aulas"

# Token JWT enviado por el backend. No se guarda ningun token en el codigo.
TOKEN = os.getenv("KEYAPP_TOKEN", "")

# Carpeta donde guardaremos las imagenes de los reportes
SALIDA = os.getenv(
    "KEYAPP_REPORTES_SALIDA",
    os.path.join(os.path.dirname(os.path.abspath(__file__)), "Reportes")
)

# Creamos la carpeta si todavia no existe
os.makedirs(SALIDA, exist_ok=True)

# Encabezados para consultar los endpoints protegidos de KeyApp
ENCABEZADOS = {
    "Authorization": f"Bearer {TOKEN}",
    "Accept": "application/json"
}


# ============================================================
# CONSULTAR LOS DATOS DE KEYAPP
# ============================================================

# Verificamos que el backend haya enviado el token de la sesion
if not TOKEN:
    raise RuntimeError("No se recibio el token de autenticacion de KeyApp.")

# Realizamos una peticion GET para consultar las reservas
respuesta_reservas = requests.get(URL_RESERVAS, headers=ENCABEZADOS, timeout=20)

# Verificamos si la consulta de reservas tuvo algun error
respuesta_reservas.raise_for_status()

# Convertimos la respuesta JSON en datos de Python
datos_reservas = respuesta_reservas.json()

# Realizamos una peticion GET para consultar las aulas
respuesta_aulas = requests.get(URL_AULAS, headers=ENCABEZADOS, timeout=20)

# Verificamos si la consulta de aulas tuvo algun error
respuesta_aulas.raise_for_status()

# Convertimos la respuesta JSON de aulas en datos de Python
datos_aulas = respuesta_aulas.json()


# ============================================================
# ORGANIZAR LOS DATOS DE LAS RESERVAS
# ============================================================

# Creamos una lista para organizar los datos
filas = []

# Recorremos cada reserva recibida desde la API
for r in datos_reservas:

    # Obtenemos la informacion del aula y del profesor
    aula = r.get("aula") or {}
    profesor = r.get("profesor") or {}

    # Organizamos los datos de cada reserva
    filas.append({
        "id": r.get("idReserva"),
        "fecha": r.get("fecha"),
        "hora_inicio": r.get("horaInicio"),
        "hora_fin": r.get("horaFin"),
        "estado": r.get("estado", "SIN ESTADO"),
        "aula": aula.get("nombre", "Sin aula"),
        "profesor": profesor.get("nombre", "Sin profesor")
    })


# Convertimos la lista en un DataFrame de Pandas
df = pd.DataFrame(
    filas,
    columns=["id", "fecha", "hora_inicio", "hora_fin", "estado", "aula", "profesor"]
)

# Convertimos las fechas para poder agrupar las reservas por mes y dia
df["fecha"] = pd.to_datetime(df["fecha"], errors="coerce")

# Eliminamos filas que no tengan una fecha valida
df = df.dropna(subset=["fecha"]).copy()

# Normalizamos los estados como texto en mayuscula
df["estado"] = df["estado"].fillna("SIN ESTADO").astype(str).str.upper()

# Convertimos la capacidad de las aulas a una tabla de Pandas
df_aulas = pd.DataFrame(datos_aulas)


# ============================================================
# MOSTRAR ESTADISTICAS EN LA TERMINAL
# ============================================================

# Calculamos la cantidad total de reservas
total_reservas = len(df)

# Contamos las reservas por estado
reservas_estado = df["estado"].value_counts()

# Calculamos las reservas activas y canceladas
reservas_activas = int(reservas_estado.get("ACTIVA", 0))
reservas_canceladas = int(reservas_estado.get("CANCELADA", 0))
reservas_finalizadas = int(reservas_estado.get("FINALIZADA", 0))

# Convertimos las cantidades a un arreglo de NumPy
cantidades_estado = reservas_estado.to_numpy(dtype=float)

# Calculamos el porcentaje de cancelacion
porcentaje_cancelacion = (
    float(np.sum(df["estado"].eq("CANCELADA")) / total_reservas * 100)
    if total_reservas else 0.0
)

# Calculamos el promedio de reservas por dia con actividad
reservas_por_dia = df.groupby(df["fecha"].dt.date).size().to_numpy(dtype=float)
promedio_diario = float(np.mean(reservas_por_dia)) if len(reservas_por_dia) else 0.0

# Contamos las aulas disponibles
if "estado" in df_aulas.columns:
    aulas_disponibles = int(
        df_aulas["estado"].fillna("").astype(str).str.upper().eq("DISPONIBLE").sum()
    )
else:
    aulas_disponibles = 0

total_aulas = len(df_aulas)

# Mostramos el titulo del reporte
print("\n===== REPORTE ESTADISTICO KEYAPP =====")
print("Total reservas:", total_reservas)
print("Reservas activas:", reservas_activas)
print("Reservas canceladas:", reservas_canceladas)
print("Reservas finalizadas:", reservas_finalizadas)
print("Porcentaje de cancelacion:", f"{porcentaje_cancelacion:.1f}%")
print("Promedio de reservas por dia:", f"{promedio_diario:.2f}")
print("Total aulas:", total_aulas)
print("Aulas disponibles:", aulas_disponibles)


# ============================================================
# PREPARAR AGRUPACIONES PARA LAS GRAFICAS
# ============================================================

# Agrupamos las reservas por mes
if not df.empty:
    df["mes"] = df["fecha"].dt.to_period("M").astype(str)
    reservas_mes = df.groupby("mes").size().sort_index()
    reservas_aula = df["aula"].value_counts().head(10)
    reservas_profesor = df["profesor"].value_counts().head(10)
else:
    reservas_mes = pd.Series(dtype="int64")
    reservas_aula = pd.Series(dtype="int64")
    reservas_profesor = pd.Series(dtype="int64")


# Funcion para guardar cada grafica y convertirla a Base64
def guardar_grafica(nombre_archivo):
    ruta = os.path.join(SALIDA, nombre_archivo)
    plt.tight_layout()
    plt.savefig(ruta, dpi=150, bbox_inches="tight")
    plt.close()

    with open(ruta, "rb") as archivo:
        return base64.b64encode(archivo.read()).decode("ascii")


# ============================================================
# 1. GRAFICA: CANTIDAD DE RESERVAS POR MES
# ============================================================

if not reservas_mes.empty:
    reservas_mes.plot(kind="bar", title="Reservas por mes")
    plt.xlabel("Mes")
    plt.ylabel("Cantidad")
else:
    plt.figure()
    plt.text(0.5, 0.5, "No hay reservas para mostrar", ha="center", va="center")
    plt.title("Reservas por mes")
    plt.axis("off")

grafica_reservas_mes = guardar_grafica("reservas_por_mes.png")


# ============================================================
# 2. GRAFICA: AULAS CON MAS RESERVAS
# ============================================================

if not reservas_aula.empty:
    reservas_aula.sort_values().plot(kind="barh", title="Aulas con mas reservas")
    plt.xlabel("Cantidad de reservas")
    plt.ylabel("Aula")
else:
    plt.figure()
    plt.text(0.5, 0.5, "No hay reservas para mostrar", ha="center", va="center")
    plt.title("Aulas con mas reservas")
    plt.axis("off")

grafica_reservas_aula = guardar_grafica("reservas_por_aula.png")


# ============================================================
# 3. GRAFICA: PORCENTAJE DE RESERVAS POR ESTADO
# ============================================================

if not reservas_estado.empty:
    reservas_estado.plot(
        kind="pie",
        autopct="%1.1f%%",
        title="Reservas por estado"
    )
    plt.ylabel("")
else:
    plt.figure()
    plt.text(0.5, 0.5, "No hay estados para mostrar", ha="center", va="center")
    plt.title("Reservas por estado")
    plt.axis("off")

grafica_reservas_estado = guardar_grafica("reservas_por_estado.png")


# ============================================================
# 4. GRAFICA: PROFESORES CON MAS RESERVAS
# ============================================================

if not reservas_profesor.empty:
    reservas_profesor.sort_values().plot(
        kind="barh",
        title="Profesores con mas reservas"
    )
    plt.xlabel("Cantidad de reservas")
    plt.ylabel("Profesor")
else:
    plt.figure()
    plt.text(0.5, 0.5, "No hay reservas para mostrar", ha="center", va="center")
    plt.title("Profesores con mas reservas")
    plt.axis("off")

grafica_reservas_profesor = guardar_grafica("reservas_por_profesor.png")


# ============================================================
# ENTREGAR RESULTADOS AL BACKEND DE KEYAPP
# ============================================================

# Construimos el resultado con las estadisticas y las imagenes
resultado = {
    "resumen": {
        "totalReservas": int(total_reservas),
        "reservasActivas": reservas_activas,
        "reservasCanceladas": reservas_canceladas,
        "reservasFinalizadas": reservas_finalizadas,
        "porcentajeCancelacion": round(porcentaje_cancelacion, 1),
        "promedioReservasDiarias": round(promedio_diario, 2),
        "totalAulas": int(total_aulas),
        "aulasDisponibles": aulas_disponibles
    },
    "graficas": {
        "reservasPorMes": grafica_reservas_mes,
        "reservasPorAula": grafica_reservas_aula,
        "reservasPorEstado": grafica_reservas_estado,
        "reservasPorProfesor": grafica_reservas_profesor
    }
}

# El backend recibe el JSON por la salida estandar
print(json.dumps(resultado, ensure_ascii=False))
