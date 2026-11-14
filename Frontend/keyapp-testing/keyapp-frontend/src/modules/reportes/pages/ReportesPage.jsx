import { useCallback, useEffect, useMemo, useState } from "react";
import { useSearchParams } from "react-router-dom";
import { Cargando, EncabezadoPagina, EstadoBadge, EstadoVacio, Icono } from "@/modules/core/design-system";
import { useAuth } from "@/modules/auth/hooks/useAuth";
import { useDatos } from "@/modules/core/hooks/useDatos";
import { api } from "@/modules/core/lib/api";
import { formatoFechaHora } from "@/modules/core/utils/fechas";

const TIPOS = [
  { valor: "EQUIPOS", etiqueta: "Equipos (proyector, computadores...)", icono: "dashboard" },
  { valor: "MOBILIARIO", etiqueta: "Mobiliario (sillas, mesas, tablero)", icono: "aula" },
  { valor: "ENERGIA", etiqueta: "Energía / luz", icono: "bolt" },
  { valor: "RED", etiqueta: "Red / internet", icono: "signal" },
  { valor: "LIMPIEZA", etiqueta: "Aseo / limpieza", icono: "check" },
  { valor: "OTRO", etiqueta: "Otro", icono: "flag" },
];
const etiquetaTipo = (v) => TIPOS.find((t) => t.valor === v)?.etiqueta ?? v;
const iconoTipo = (v) => TIPOS.find((t) => t.valor === v)?.icono ?? "flag";

// Profesores: informan problemas de un aula. Administrador: lee las novedades y las marca como resueltas.
export default function ReportesPage() {
  const { esAdmin } = useAuth();
  const { aulas, notificar } = useDatos();
  const [params] = useSearchParams();

  const [reportes, setReportes] = useState([]);
  const [cargando, setCargando] = useState(true);
  const [filtro, setFiltro] = useState("ABIERTO");
  const [form, setForm] = useState(() => ({ idAula: params.get("aula") ?? "", tipo: "", descripcion: "" }));
  const [enviando, setEnviando] = useState(false);

  const cargar = useCallback(async () => {
    try {
      setReportes(await api.get("/reportes"));
    } catch (e) {
      notificar("error", e.message, "/reportes");
    } finally {
      setCargando(false);
    }
  }, [notificar]);

  // Al entrar y luego cada 30 s, para que el administrador vea las novedades nuevas sin recargar.
  useEffect(() => {
    const inicial = setTimeout(cargar, 0);
    const id = setInterval(cargar, 30000);
    return () => {
      clearTimeout(inicial);
      clearInterval(id);
    };
  }, [cargar]);

  const cambiar = (e) => setForm({ ...form, [e.target.name]: e.target.value });

  const enviar = async (e) => {
    e.preventDefault();
    setEnviando(true);
    try {
      await api.post(
        "/reportes",
        { idAula: Number(form.idAula), tipo: form.tipo, descripcion: form.descripcion },
        { mensajeError: "No se pudo enviar el reporte" }
      );
      notificar("ok", "Reporte enviado. El administrador lo verá en sus novedades", "/reportes");
      setForm({ idAula: "", tipo: "", descripcion: "" });
      await cargar();
    } catch (err) {
      notificar("error", err.message, "/reportes");
    }
    setEnviando(false);
  };

  const resolver = async (id) => {
    try {
      await api.put(`/reportes/${id}/resolver`, undefined, { mensajeError: "No se pudo marcar como resuelto" });
      notificar("ok", "Reporte marcado como resuelto", "/reportes");
      await cargar();
    } catch (err) {
      notificar("error", err.message, "/reportes");
    }
  };

  const visibles = useMemo(
    () => (esAdmin && filtro ? reportes.filter((r) => r.estado === filtro) : reportes),
    [reportes, esAdmin, filtro]
  );
  const abiertos = reportes.filter((r) => r.estado === "ABIERTO").length;

  return (
    <>
      <EncabezadoPagina
        icono="flag"
        titulo={esAdmin ? "Novedades de las aulas" : "Reportes de aulas"}
        detalle={
          cargando
            ? undefined
            : esAdmin
              ? `${abiertos} ${abiertos === 1 ? "abierto" : "abiertos"} por revisar`
              : "Cuéntanos si algo del aula no funciona"
        }
      />

      {!esAdmin && (
        <div className="card border-0 mb-3">
          <div className="card-body">
            <h6 className="kp-card-titulo"><Icono nombre="flag" tamano={20} />Nuevo reporte</h6>
            <form onSubmit={enviar} className="row g-2 align-items-end">
              <div className="col-md-3">
                <label className="form-label small">Aula</label>
                <select name="idAula" className="form-select" value={form.idAula} onChange={cambiar} required>
                  <option value="">Aula...</option>
                  {aulas.map((a) => (
                    <option key={a.idAula} value={a.idAula}>{a.nombre}</option>
                  ))}
                </select>
              </div>
              <div className="col-md-4">
                <label className="form-label small">¿Qué tipo de problema es?</label>
                <select name="tipo" className="form-select" value={form.tipo} onChange={cambiar} required>
                  <option value="">Tipo...</option>
                  {TIPOS.map((t) => (
                    <option key={t.valor} value={t.valor}>{t.etiqueta}</option>
                  ))}
                </select>
              </div>
              <div className="col-12">
                <label className="form-label small">Describe qué pasa</label>
                <textarea
                  name="descripcion"
                  className="form-control"
                  rows={3}
                  maxLength={500}
                  value={form.descripcion}
                  onChange={cambiar}
                  placeholder="Ej.: el proyector no enciende y el tablero no tiene marcadores"
                  required
                />
                <div className="form-text text-end">{form.descripcion.length}/500</div>
              </div>
              <div className="col-auto">
                <button type="submit" className="btn btn-kp kp-anim" disabled={enviando}>
                  <Icono nombre="send" tamano={18} animar={enviando ? "siempre" : "hover"} />
                  {enviando ? "Enviando..." : "Enviar reporte"}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}

      <div className="card border-0">
        <div className="card-body">
          <div className="d-flex flex-wrap justify-content-between align-items-center gap-2 mb-3">
            <h6 className="kp-card-titulo mb-0">
              <Icono nombre="chat" tamano={20} />
              {esAdmin ? "Reportes de los profesores" : "Mis reportes"}
            </h6>
            {esAdmin && (
              <select className="form-select w-auto" value={filtro} onChange={(e) => setFiltro(e.target.value)}>
                <option value="ABIERTO">Abiertos</option>
                <option value="RESUELTO">Resueltos</option>
                <option value="">Todos</option>
              </select>
            )}
          </div>

          {cargando ? (
            <Cargando texto="Cargando reportes..." />
          ) : visibles.length === 0 ? (
            <EstadoVacio icono="flag" texto={esAdmin ? "No hay reportes en esta lista." : "Aún no has enviado reportes."} />
          ) : (
            <div className="kp-reportes">
              {visibles.map((r) => (
                <article key={r.idReporte} className={`kp-reporte kp-anim ${r.estado === "RESUELTO" ? "kp-reporte--ok" : ""}`}>
                  <span className="kp-celda-ic"><Icono nombre={iconoTipo(r.tipo)} tamano={20} /></span>
                  <div className="kp-reporte-cuerpo">
                    <div className="kp-reporte-top">
                      <strong>{r.aula}</strong>
                      <span className="kp-nota">{etiquetaTipo(r.tipo)}</span>
                      <EstadoBadge estado={r.estado} />
                    </div>
                    <p>{r.descripcion}</p>
                    <div className="kp-reporte-pie">
                      {esAdmin && <span className="kp-dato"><Icono nombre="user" tamano={15} />{r.profesor}</span>}
                      <span className="kp-dato"><Icono nombre="clock" tamano={15} />{formatoFechaHora(r.fecha)}</span>
                      {r.fechaResolucion && (
                        <span className="kp-dato"><Icono nombre="check" tamano={15} />Resuelto {formatoFechaHora(r.fechaResolucion)}</span>
                      )}
                    </div>
                  </div>
                  {esAdmin && r.estado === "ABIERTO" && (
                    <button className="btn btn-outline-secondary btn-sm kp-anim" onClick={() => resolver(r.idReporte)}>
                      <Icono nombre="check" tamano={16} /> Marcar resuelto
                    </button>
                  )}
                </article>
              ))}
            </div>
          )}
        </div>
      </div>
    </>
  );
}
