import { useState } from "react";
import { Link, Navigate, useNavigate, useParams } from "react-router-dom";
import { Cargando, EncabezadoPagina, Icono } from "@/modules/core/design-system";
import { useAuth } from "@/modules/auth/hooks/useAuth";
import { useDatos } from "@/modules/core/hooks/useDatos";

export default function TransferirPage() {
  const { id } = useParams();
  const navigate = useNavigate();
  const { usuario } = useAuth();
  const { reservas, profesores, cargando, transferirReserva } = useDatos();
  const [destino, setDestino] = useState("");
  const [motivo, setMotivo] = useState("");
  const [enviando, setEnviando] = useState(false);

  if (cargando) return <Cargando />;

  const reserva = reservas.find((r) => String(r.idReserva) === id);
  if (!reserva) return <Navigate to="/reservas" replace />;

  // Un profesor solo puede transferir sus propias reservas.
  if (reserva.profesor?.idUsuario !== usuario.idUsuario) {
    return <Navigate to="/no-autorizado" replace />;
  }

  const confirmar = async (e) => {
    e.preventDefault();
    setEnviando(true);
    const ok = await transferirReserva(reserva.idReserva, destino, motivo);
    setEnviando(false);
    if (ok) navigate("/reservas");
  };

  return (
    <>
    <EncabezadoPagina
      icono="swap"
      titulo="Transferir reserva"
      detalle={`${reserva.aula?.nombre} · ${reserva.fecha} · El otro profesor debe aceptarla`}
    />
    <div className="card border-0">
      <div className="card-body">
        <form onSubmit={confirmar} className="row g-2 align-items-end mt-1">
          <div className="col-md">
            <label className="form-label small">Profesor destino</label>
            <select className="form-select" value={destino} onChange={(e) => setDestino(e.target.value)} required>
              <option value="">Profesor...</option>
              {profesores
                .filter((p) => p.idUsuario !== reserva.profesor?.idUsuario)
                .map((p) => (
                  <option key={p.idUsuario} value={p.idUsuario}>{p.nombre}</option>
                ))}
            </select>
          </div>
          <div className="col-md">
            <label className="form-label small">Motivo (opcional)</label>
            <input
              type="text"
              className="form-control"
              value={motivo}
              maxLength={200}
              onChange={(e) => setMotivo(e.target.value)}
            />
          </div>
          <div className="col-md-auto">
            <button type="submit" className="btn btn-kp kp-anim" disabled={enviando}>
              <Icono nombre="swap" tamano={18} animar={enviando ? "siempre" : "hover"} />
              {enviando ? "Enviando..." : "Enviar solicitud"}
            </button>
          </div>
          <div className="col-md-auto">
            <Link to="/reservas" className="btn btn-outline-secondary kp-anim"><Icono nombre="x" tamano={16} /> Cancelar</Link>
          </div>
        </form>
      </div>
    </div>
    </>
  );
}
