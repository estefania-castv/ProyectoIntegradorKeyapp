import { Icono } from "@/modules/core/design-system";

export default function Buscador({ valor, onChange, placeholder = "Buscar..." }) {
  return (
    <div className="input-group kp-buscador kp-anim">
      <Icono nombre="search" tamano={18} className="kp-buscador-ic" />
      <input
        type="search"
        className="form-control"
        placeholder={placeholder}
        value={valor}
        onChange={(e) => onChange(e.target.value)}
      />
      {valor && (
        <button
          type="button"
          className="btn btn-outline-secondary kp-anim"
          onClick={() => onChange("")}
          aria-label="Limpiar búsqueda"
        >
          <Icono nombre="x" tamano={16} />
        </button>
      )}
    </div>
  );
}
