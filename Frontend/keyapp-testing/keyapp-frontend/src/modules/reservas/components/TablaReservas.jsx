import { Link } from "react-router-dom";
import { EstadoBadge, EstadoVacio, Icono } from "@/modules/core/design-system";
import { reservaTerminada, ventanaApertura } from "@/modules/core/utils/aperturas";

const iniciales = (nombre = "") =>
  nombre.trim().split(/\s+/).slice(0, 2).map((p) => p.charAt(0).toUpperCase()).join("") || "?";

const VIGENTES = ["ACTIVA", "TRANSFERIDA"];

/**
 * esAdmin: el administrador crea y cancela reservas, pero no abre puertas ni transfiere.
 * transferenciaEnviada(idReserva): solicitud pendiente o rechazada que este profesor envió para esa reserva.
 */
export default function TablaReservas({ reservas, esAdmin, ahora, transferenciaEnviada, onAbrir, onCancelar }) {
  if (reservas.length === 0) {
    return <EstadoVacio icono="calendar" texto="No hay reservas que coincidan." />;
  }

  return (
    <div className="table-responsive">
      <table className="table align-middle">
        <thead>
          <tr>
            <th>Aula</th>
            <th>Profesor</th>
            <th>Fecha</th>
            <th>Horario</th>
            <th>Estado</th>
            <th></th>
          </tr>
        </thead>
        <tbody>
          {reservas.map((r) => {
            const terminada = reservaTerminada(r, ahora);
            const vigente = VIGENTES.includes(r.estado) && !terminada;
            // Pasada la hora de fin el aula queda liberada, aunque el servidor aún no haya actualizado el estado.
            const estadoMostrado = VIGENTES.includes(r.estado) && terminada ? "FINALIZADA" : r.estado;
            const apertura = vigente && !esAdmin ? ventanaApertura(r, ahora) : null;
            const envio = !esAdmin && vigente ? transferenciaEnviada?.(r.idReserva) : null;

            return (
              <tr key={r.idReserva} className="kp-anim">
                <td>
                  <div className="kp-celda">
                    <span className="kp-celda-ic"><Icono nombre="door" tamano={18} /></span>
                    {r.aula?.nombre}
                  </div>
                </td>
                <td>
                  <div className="kp-celda">
                    <span className="kp-mini-avatar">{iniciales(r.profesor?.nombre)}</span>
                    {r.profesor?.nombre}
                  </div>
                </td>
                <td>
                  <span className="kp-dato"><Icono nombre="calendar" tamano={16} />{r.fecha}</span>
                </td>
                <td>
                  <span className="kp-dato">
                    <Icono nombre="clock" tamano={16} />
                    {r.horaInicio?.slice(0, 5)} - {r.horaFin?.slice(0, 5)}
                  </span>
                </td>
                <td>
                  <EstadoBadge estado={estadoMostrado} />
                  {envio?.estado === "PENDIENTE" && (
                    <div className="kp-nota">Esperando respuesta de {envio.destino}</div>
                  )}
                  {envio?.estado === "RECHAZADA" && (
                    <div className="kp-nota kp-nota--bad">{envio.destino} la rechazó</div>
                  )}
                </td>
                <td className="text-end">
                  {vigente && (
                    <div className="kp-acciones">
                      {apertura?.estado === "abierta" && (
                        <button
                          className="btn btn-kp kp-anim"
                          title={`Puedes abrir hasta las ${apertura.hasta}`}
                          onClick={() => onAbrir(r.idReserva)}
                        >
                          <Icono nombre="door" tamano={17} /> Abrir puerta
                        </button>
                      )}
                      {apertura?.estado === "antes" && (
                        <span className="kp-nota">Abre a las {apertura.desde}</span>
                      )}
                      {apertura?.estado === "futura" && (
                        <span className="kp-nota">Se abre el día de la reserva</span>
                      )}
                      {apertura?.estado === "vencida" && (
                        <span className="kp-nota kp-nota--bad">Pasaron los 20 min para abrir</span>
                      )}

                      {!esAdmin && (
                        <Link className="btn btn-outline-secondary kp-anim" to={`/reservas/${r.idReserva}/transferir`}>
                          <Icono nombre="swap" tamano={17} />
                          {envio?.estado === "RECHAZADA" ? "Transferir de nuevo" : "Transferir"}
                        </Link>
                      )}
                      <button className="btn btn-outline-danger kp-anim" onClick={() => onCancelar(r.idReserva)}>
                        <Icono nombre="x" tamano={17} /> Cancelar
                      </button>
                    </div>
                  )}
                </td>
              </tr>
            );
          })}
        </tbody>
      </table>
    </div>
  );
}
