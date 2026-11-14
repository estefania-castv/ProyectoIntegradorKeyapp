import { useMemo } from "react";
import { Link } from "react-router-dom";
import {
  Bar, BarChart, CartesianGrid, Cell, Pie, PieChart,
  ResponsiveContainer, Tooltip, XAxis, YAxis, Legend,
} from "recharts";
import { Cargando, EncabezadoPagina, EstadoBadge, EstadoVacio, Icono } from "@/modules/core/design-system";
import { useAuth } from "@/modules/auth/hooks/useAuth";
import { useDatos } from "@/modules/core/hooks/useDatos";

const COLOR_ESTADO = {
  ACTIVA: "#40b8a6",
  TRANSFERIDA: "#2a7c87",
  CANCELADA: "#adb5bd",
  MODIFICADA: "#f0ad4e",
  FINALIZADA: "#2c3440",
};
const PRIMARIO = "#2a7c87";

// "YYYY-MM-DD" en hora local (evita el desfase de toISOString con UTC).
function hoyISO() {
  const d = new Date();
  const mes = String(d.getMonth() + 1).padStart(2, "0");
  const dia = String(d.getDate()).padStart(2, "0");
  return `${d.getFullYear()}-${mes}-${dia}`;
}

// Cuenta ocurrencias por clave y devuelve [{ nombre, total }] ordenado desc.
function contarPor(items, clave) {
  const mapa = new Map();
  items.forEach((i) => {
    const k = clave(i);
    if (k) mapa.set(k, (mapa.get(k) ?? 0) + 1);
  });
  return [...mapa.entries()]
    .map(([nombre, total]) => ({ nombre, total }))
    .sort((a, b) => b.total - a.total);
}

function Kpi({ titulo, valor, detalle, icono, tono = "" }) {
  return (
    <div className="col-6 col-lg-3">
      <div className={`card kp-kpi kp-anim border-0 ${tono}`}>
        <div className="card-body">
          <div>
            <div className="text-muted small fw-semibold">{titulo}</div>
            <div className="kp-kpi-valor">{valor}</div>
            {detalle && <div className="text-muted small">{detalle}</div>}
          </div>
          <span className="kp-kpi-icono"><Icono nombre={icono} tamano={24} /></span>
        </div>
      </div>
    </div>
  );
}

function Panel({ titulo, icono, children, vacio }) {
  return (
    <div className="card kp-anim border-0 h-100">
      <div className="card-body">
        <h6 className="kp-card-titulo"><Icono nombre={icono} tamano={20} />{titulo}</h6>
        {vacio ? <EstadoVacio icono={icono} texto="Sin datos para mostrar." /> : (
          <div style={{ width: "100%", height: 260 }}>{children}</div>
        )}
      </div>
    </div>
  );
}

const ESTILO_TOOLTIP = { borderRadius: 12, border: "1px solid #e1eaec", boxShadow: "0 10px 24px -10px rgba(14,68,75,.3)" };

export default function DashboardPage() {
  const { usuario, esAdmin } = useAuth();
  const { reservas, aulas, cargando } = useDatos();

  // Igual que ReservasPage: el profesor solo ve lo suyo.
  const visibles = useMemo(
    () => (esAdmin ? reservas : reservas.filter((r) => r.profesor?.idUsuario === usuario.idUsuario)),
    [reservas, esAdmin, usuario]
  );

  const m = useMemo(() => {
    const hoy = hoyISO();
    const vigentes = visibles.filter((r) => r.estado === "ACTIVA" || r.estado === "TRANSFERIDA");
    const canceladas = visibles.filter((r) => r.estado === "CANCELADA").length;
    const disponibles = aulas.filter((a) => a.estado === "DISPONIBLE").length;

    const porEstado = contarPor(visibles, (r) => r.estado).map((e) => ({ ...e, fill: COLOR_ESTADO[e.nombre] ?? "#adb5bd" }));

    // Últimos 14 días con actividad, en orden cronológico.
    const porDia = contarPor(visibles, (r) => r.fecha)
      .sort((a, b) => a.nombre.localeCompare(b.nombre))
      .slice(-14)
      .map((d) => ({ ...d, etiqueta: d.nombre.slice(5) }));

    const proximas = vigentes
      .filter((r) => r.fecha >= hoy)
      .sort((a, b) => `${a.fecha}${a.horaInicio}`.localeCompare(`${b.fecha}${b.horaInicio}`))
      .slice(0, 6);

    return {
      total: visibles.length,
      vigentes: vigentes.length,
      hoy: vigentes.filter((r) => r.fecha === hoy).length,
      tasaCancelacion: visibles.length ? Math.round((canceladas / visibles.length) * 100) : 0,
      canceladas,
      disponibles,
      porEstado,
      porDia,
      topAulas: contarPor(visibles, (r) => r.aula?.nombre).slice(0, 5),
      topProfes: contarPor(visibles, (r) => r.profesor?.nombre).slice(0, 5),
      proximas,
    };
  }, [visibles, aulas]);

  if (cargando) return <Cargando texto="Cargando dashboard..." />;

  return (
    <>
      <EncabezadoPagina
        icono="dashboard"
        titulo={esAdmin ? "Dashboard general" : "Mi dashboard"}
        detalle={`${m.total} reservas registradas`}
      />

      <div className="row g-3 mb-3">
        <Kpi titulo="Reservas vigentes" valor={m.vigentes} detalle={`${m.total} en total`} icono="calendar" />
        <Kpi titulo="Para hoy" valor={m.hoy} detalle="Activas o transferidas" icono="clock" tono="kp-kpi--gold" />
        <Kpi titulo="Aulas disponibles" valor={m.disponibles} detalle={`de ${aulas.length} aulas`} icono="door" />
        <Kpi titulo="Tasa de cancelación" valor={`${m.tasaCancelacion}%`} detalle={`${m.canceladas} canceladas`} icono="x" tono="kp-kpi--rojo" />
      </div>

      <div className="row g-3 mb-3">
        <div className="col-lg-8">
          <Panel titulo="Reservas por día (últimos 14 con actividad)" icono="calendar" vacio={m.porDia.length === 0}>
            <ResponsiveContainer>
              <BarChart data={m.porDia}>
                <CartesianGrid strokeDasharray="3 3" stroke="#e1eaec" vertical={false} />
                <XAxis dataKey="etiqueta" />
                <YAxis allowDecimals={false} />
                <Tooltip contentStyle={ESTILO_TOOLTIP} cursor={{ fill: "rgba(64,184,166,.1)" }} labelFormatter={(_, p) => p?.[0]?.payload?.nombre} />
                <Bar dataKey="total" name="Reservas" fill={PRIMARIO} radius={[8, 8, 0, 0]} />
              </BarChart>
            </ResponsiveContainer>
          </Panel>
        </div>
        <div className="col-lg-4">
          <Panel titulo="Por estado" icono="dashboard" vacio={m.porEstado.length === 0}>
            <ResponsiveContainer>
              <PieChart>
                <Pie data={m.porEstado} dataKey="total" nameKey="nombre" innerRadius={58} outerRadius={88} paddingAngle={3} cornerRadius={6} stroke="none">
                  {m.porEstado.map((e) => <Cell key={e.nombre} fill={e.fill} />)}
                </Pie>
                <Tooltip contentStyle={ESTILO_TOOLTIP} cursor={{ fill: "rgba(64,184,166,.1)" }} />
                <Legend />
              </PieChart>
            </ResponsiveContainer>
          </Panel>
        </div>
      </div>

      <div className="row g-3 mb-3">
        <div className={esAdmin ? "col-lg-6" : "col-12"}>
          <Panel titulo="Aulas más reservadas" icono="aula" vacio={m.topAulas.length === 0}>
            <ResponsiveContainer>
              <BarChart data={m.topAulas} layout="vertical" margin={{ left: 20 }}>
                <CartesianGrid strokeDasharray="3 3" stroke="#e1eaec" horizontal={false} />
                <XAxis type="number" allowDecimals={false} />
                <YAxis type="category" dataKey="nombre" width={90} />
                <Tooltip contentStyle={ESTILO_TOOLTIP} cursor={{ fill: "rgba(64,184,166,.1)" }} />
                <Bar dataKey="total" name="Reservas" fill="#40b8a6" radius={[0, 8, 8, 0]} />
              </BarChart>
            </ResponsiveContainer>
          </Panel>
        </div>
        {esAdmin && (
          <div className="col-lg-6">
            <Panel titulo="Profesores con más reservas" icono="users" vacio={m.topProfes.length === 0}>
              <ResponsiveContainer>
                <BarChart data={m.topProfes} layout="vertical" margin={{ left: 20 }}>
                  <CartesianGrid strokeDasharray="3 3" stroke="#e1eaec" horizontal={false} />
                  <XAxis type="number" allowDecimals={false} />
                  <YAxis type="category" dataKey="nombre" width={110} />
                  <Tooltip contentStyle={ESTILO_TOOLTIP} cursor={{ fill: "rgba(64,184,166,.1)" }} />
                  <Bar dataKey="total" name="Reservas" fill={PRIMARIO} radius={[0, 8, 8, 0]} />
                </BarChart>
              </ResponsiveContainer>
            </Panel>
          </div>
        )}
      </div>

      <div className="card border-0">
        <div className="card-body">
          <div className="d-flex justify-content-between align-items-center mb-3">
            <h6 className="kp-card-titulo mb-0"><Icono nombre="clock" tamano={20} />Próximas reservas</h6>
            <Link to="/reservas" className="btn btn-outline-secondary btn-sm kp-anim">
              <Icono nombre="calendar" tamano={16} /> Ver todas
            </Link>
          </div>
          {m.proximas.length === 0 ? (
            <EstadoVacio icono="calendar" texto="No hay reservas próximas." />
          ) : (
            <div className="table-responsive">
              <table className="table align-middle mb-0">
                <thead>
                  <tr><th>Aula</th>{esAdmin && <th>Profesor</th>}<th>Fecha</th><th>Horario</th><th>Estado</th></tr>
                </thead>
                <tbody>
                  {m.proximas.map((r) => (
                    <tr key={r.idReserva} className="kp-anim">
                      <td>
                        <div className="kp-celda">
                          <span className="kp-celda-ic"><Icono nombre="door" tamano={18} /></span>
                          {r.aula?.nombre}
                        </div>
                      </td>
                      {esAdmin && <td>{r.profesor?.nombre}</td>}
                      <td><span className="kp-dato"><Icono nombre="calendar" tamano={16} />{r.fecha}</span></td>
                      <td><span className="kp-dato"><Icono nombre="clock" tamano={16} />{r.horaInicio?.slice(0, 5)} - {r.horaFin?.slice(0, 5)}</span></td>
                      <td><EstadoBadge estado={r.estado} /></td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          )}
        </div>
      </div>
    </>
  );
}
