import { Link } from "react-router-dom";
import { Icono } from "@/modules/core/design-system";

export default function NoEncontradaPage() {
  return (
    <div className="kp-error">
      <div className="kp-vacio-icono"><Icono nombre="key" tamano={46} animar="siempre" /></div>
      <h1>404</h1>
      <p className="lead">La página que buscas no existe.</p>
      <Link to="/reservas" className="btn btn-kp kp-anim">
        <Icono nombre="door" tamano={18} /> Ir al inicio
      </Link>
    </div>
  );
}
