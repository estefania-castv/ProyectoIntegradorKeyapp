import Icono from "./Icono";

export default function EstadoVacio({ icono = "vacio", texto }) {
  return (
    <div className="kp-vacio">
      <div className="kp-vacio-icono">
        <Icono nombre={icono} tamano={34} animar="flotar" />
      </div>
      <p>{texto}</p>
    </div>
  );
}
