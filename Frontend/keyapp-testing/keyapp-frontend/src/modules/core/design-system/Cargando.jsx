import Icono from "./Icono";

export default function Cargando({ texto = "Cargando..." }) {
  return (
    <div className="kp-cargando" role="status">
      <Icono nombre="key" tamano={26} animar="siempre" />
      <span>{texto}</span>
    </div>
  );
}
