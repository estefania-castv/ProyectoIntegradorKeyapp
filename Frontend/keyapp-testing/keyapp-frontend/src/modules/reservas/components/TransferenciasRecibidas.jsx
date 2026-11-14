import { useState } from "react";
import { Icono } from "@/modules/core/design-system";

// Solicitudes que otro profesor te envió: aceptas (la reserva pasa a ser tuya) o rechazas.
export default function TransferenciasRecibidas({ pendientes, onAceptar, onRechazar }) {
  const [ocupada, setOcupada] = useState(null);

  if (pendientes.length === 0) return null;

  const responder = async (id, accion) => {
    setOcupada(id);
    await accion(id);
    setOcupada(null);
  };

  return (
    <div className="card border-0 kp-recibidas mb-3">
      <div className="card-body">
        <h6 className="kp-card-titulo">
          <Icono nombre="swap" tamano={20} animar="siempre" />
          Te transfirieron {pendientes.length === 1 ? "una reserva" : `${pendientes.length} reservas`}
        </h6>
        <div className="kp-recibidas-lista">
          {pendientes.map((t) => (
            <div key={t.idTransferencia} className="kp-recibida kp-anim">
              <div className="kp-recibida-info">
                <strong>{t.origen || "Otro profesor"}</strong> quiere pasarte el aula <strong>{t.aula}</strong>
                <div className="kp-recibida-datos">
                  <span className="kp-dato"><Icono nombre="calendar" tamano={16} />{t.fecha}</span>
                  <span className="kp-dato">
                    <Icono nombre="clock" tamano={16} />
                    {t.horaInicio?.slice(0, 5)} - {t.horaFin?.slice(0, 5)}
                  </span>
                </div>
                {t.motivo && <div className="kp-nota">“{t.motivo}”</div>}
              </div>
              <div className="kp-acciones">
                <button
                  className="btn btn-kp kp-anim"
                  disabled={ocupada === t.idTransferencia}
                  onClick={() => responder(t.idTransferencia, onAceptar)}
                >
                  <Icono nombre="check" tamano={17} /> Aceptar
                </button>
                <button
                  className="btn btn-outline-danger kp-anim"
                  disabled={ocupada === t.idTransferencia}
                  onClick={() => responder(t.idTransferencia, onRechazar)}
                >
                  <Icono nombre="x" tamano={17} /> Rechazar
                </button>
              </div>
            </div>
          ))}
        </div>
      </div>
    </div>
  );
}
