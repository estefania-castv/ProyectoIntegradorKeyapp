const clases = {
  // reservas
  ACTIVA: "ok",
  TRANSFERIDA: "info",
  CANCELADA: "off",
  MODIFICADA: "warn",
  FINALIZADA: "dark",
  // aulas
  DISPONIBLE: "ok",
  RESERVADA: "res",
  MANTENIMIENTO: "warn",
  SIN_ENERGIA: "warn",
  PROBLEMAS_DE_RED: "warn",
  FUERA_DE_SERVICIO: "bad",
  INACTIVA: "off",
  // transferencias y reportes
  PENDIENTE: "warn",
  ACEPTADA: "ok",
  RECHAZADA: "bad",
  EXPIRADA: "off",
  ABIERTO: "warn",
  RESUELTO: "ok",
};

export default function EstadoBadge({ estado }) {
  return (
    <span className={`kp-estado kp-estado--${clases[estado] || "off"}`}>
      <span className="kp-estado-dot" />
      {String(estado ?? "").replaceAll("_", " ")}
    </span>
  );
}
