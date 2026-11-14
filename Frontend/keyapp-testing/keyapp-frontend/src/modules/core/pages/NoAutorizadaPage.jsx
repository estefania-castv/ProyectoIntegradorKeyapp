import { Link } from "react-router-dom";
import { Icono } from "@/modules/core/design-system";

export default function NoAutorizadaPage() {
  return (
    <div className="kp-error">
      <div className="kp-vacio-icono"><Icono nombre="lock" tamano={46} animar="siempre" /></div>
      <h1>403</h1>
      <p className="lead">No tienes permiso para ver esta página.</p>
      <Link to="/reservas" className="btn btn-kp kp-anim">
        <Icono nombre="calendar" tamano={18} /> Volver a mis reservas
      </Link>
    </div>
  );
}
