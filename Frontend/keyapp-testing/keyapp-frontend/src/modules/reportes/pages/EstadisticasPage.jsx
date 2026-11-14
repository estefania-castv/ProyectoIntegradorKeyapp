import { useState } from "react";
import { api } from "@/modules/core/lib/api";
import { EncabezadoPagina, Icono } from "@/modules/core/design-system";

const TARJETAS = [
  { clave: "totalReservas", titulo: "Total de reservas", icono: "calendar" },
  { clave: "reservasActivas", titulo: "Reservas activas", icono: "check" },
  { clave: "reservasCanceladas", titulo: "Reservas canceladas", icono: "x" },
  { clave: "reservasFinalizadas", titulo: "Reservas finalizadas", icono: "flag" },
  { clave: "totalAulas", titulo: "Total de aulas", icono: "aula" },
  { clave: "aulasDisponibles", titulo: "Aulas disponibles", icono: "check" },
];

const GRAFICAS = [
  { clave: "reservasPorMes", titulo: "Reservas por mes" },
  { clave: "reservasPorAula", titulo: "Aulas con más reservas" },
  { clave: "reservasPorEstado", titulo: "Reservas por estado" },
  { clave: "reservasPorProfesor", titulo: "Profesores con más reservas" },
];

export default function EstadisticasPage() {
  const [datos, setDatos] = useState(null);
  const [cargando, setCargando] = useState(false);
  const [error, setError] = useState("");

  const generar = async () => {
    setCargando(true);
    setError("");
    try {
      const resultado = await api.get("/estadisticas/reservas");
      setDatos(resultado);
    } catch (e) {
      setError(e.message || "No se pudieron generar las estadísticas.");
    } finally {
      setCargando(false);
    }
  };

  const resumen = datos?.resumen;
  const graficas = datos?.graficas;

  return (
    <>
      <EncabezadoPagina
        icono="dashboard"
        titulo="Estadísticas de KeyApp"
        detalle="Análisis de reservas y uso de las aulas generado con Python"
      />

      <div className="card border-0 mb-4">
        <div className="card-body d-flex flex-wrap align-items-center justify-content-between gap-3">
          <div>
            <h6 className="kp-card-titulo mb-1">
              <Icono nombre="dashboard" tamano={20} /> Reportes estadísticos
            </h6>
            <p className="text-muted small mb-0">
              Los datos se consultan desde KeyApp y se procesan con requests, pandas, numpy y matplotlib.
            </p>
          </div>
          <button className="btn btn-kp kp-anim" onClick={generar} disabled={cargando}>
            <Icono nombre="swap" tamano={18} animar={cargando ? "siempre" : "hover"} />
            {cargando ? "Generando..." : datos ? "Actualizar estadísticas" : "Generar estadísticas"}
          </button>
        </div>
      </div>

      {error && (
        <div className="alert alert-danger" role="alert">
          <strong>No fue posible generar el reporte.</strong>
          <div>{error}</div>
          <small>Verifica que Spring Boot esté ejecutándose y que Python tenga instaladas las librerías.</small>
        </div>
      )}

      {!datos && !cargando && !error && (
        <div className="card border-0">
          <div className="card-body text-center py-5">
            <Icono nombre="dashboard" tamano={36} />
            <h5 className="mt-3">Aún no has generado estadísticas</h5>
            <p className="text-muted mb-0">Pulsa «Generar estadísticas» para consultar las reservas y crear las gráficas.</p>
          </div>
        </div>
      )}

      {cargando && (
        <div className="card border-0">
          <div className="card-body text-center py-5">
            <div className="spinner-border" role="status" aria-label="Generando" />
            <p className="text-muted mt-3 mb-0">Python está consultando y analizando los datos de KeyApp...</p>
          </div>
        </div>
      )}

      {resumen && (
        <>
          <div className="row g-3 mb-4">
            {TARJETAS.map((tarjeta) => (
              <div className="col-6 col-md-4 col-xl-2" key={tarjeta.clave}>
                <div className="card border-0 h-100">
                  <div className="card-body">
                    <div className="text-muted small mb-2">{tarjeta.titulo}</div>
                    <div className="fs-3 fw-semibold">{resumen[tarjeta.clave] ?? 0}</div>
                  </div>
                </div>
              </div>
            ))}
            <div className="col-12 col-md-6">
              <div className="card border-0 h-100">
                <div className="card-body">
                  <div className="text-muted small">Porcentaje de cancelación</div>
                  <div className="fs-3 fw-semibold">{resumen.porcentajeCancelacion ?? 0}%</div>
                </div>
              </div>
            </div>
            <div className="col-12 col-md-6">
              <div className="card border-0 h-100">
                <div className="card-body">
                  <div className="text-muted small">Promedio de reservas por día con actividad</div>
                  <div className="fs-3 fw-semibold">{resumen.promedioReservasDiarias ?? 0}</div>
                </div>
              </div>
            </div>
          </div>

          <div className="row g-3">
            {GRAFICAS.map((grafica) => (
              <div className="col-12 col-xl-6" key={grafica.clave}>
                <div className="card border-0 h-100">
                  <div className="card-body">
                    <h6 className="kp-card-titulo">{grafica.titulo}</h6>
                    {graficas?.[grafica.clave] ? (
                      <img
                        className="img-fluid w-100"
                        src={`data:image/png;base64,${graficas[grafica.clave]}`}
                        alt={grafica.titulo}
                      />
                    ) : (
                      <p className="text-muted mb-0">No hay información para esta gráfica.</p>
                    )}
                  </div>
                </div>
              </div>
            ))}
          </div>
        </>
      )}
    </>
  );
}
