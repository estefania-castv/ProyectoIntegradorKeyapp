import Icono from "./Icono";

export default function EncabezadoPagina({ icono, titulo, detalle, children }) {
  return (
    <div className="kp-encabezado">
      <div className="kp-encabezado-titulo">
        <span className="kp-icon-tile kp-anim">
          <Icono nombre={icono} tamano={24} />
        </span>
        <div>
          <h1>{titulo}</h1>
          {detalle && <p>{detalle}</p>}
        </div>
      </div>
      {children && <div className="kp-encabezado-acciones">{children}</div>}
    </div>
  );
}
