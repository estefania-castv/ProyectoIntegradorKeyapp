import { useEffect, useState } from "react";
import { Icono } from "@/modules/core/design-system";
import { api } from "@/modules/core/lib/api";
import { ESTADOS_AULA } from "@/modules/aulas/estados";

const formVacio = { nombre: "", ubicacion: "", capacidad: "", idFacultad: "", estado: "DISPONIBLE" };

// Solo el administrador agrega aulas.
export default function FormAula({ onCrear, onCerrar }) {
  const [form, setForm] = useState(formVacio);
  const [facultades, setFacultades] = useState([]);
  const [errorFacultades, setErrorFacultades] = useState("");
  const [enviando, setEnviando] = useState(false);

  useEffect(() => {
    let activo = true;
    api
      .get("/facultades")
      .then((lista) => activo && setFacultades(lista))
      .catch((e) => activo && setErrorFacultades(e.message));
    return () => {
      activo = false;
    };
  }, []);

  const cambiar = (e) => setForm({ ...form, [e.target.name]: e.target.value });

  const enviar = async (e) => {
    e.preventDefault();
    setEnviando(true);
    const ok = await onCrear(form);
    setEnviando(false);
    if (ok) {
      setForm(formVacio);
      onCerrar();
    }
  };

  return (
    <form onSubmit={enviar} className="kp-form-aula row g-2 align-items-end">
      <div className="col-md-3">
        <label className="form-label small">Nombre del aula</label>
        <input name="nombre" className="form-control" value={form.nombre} maxLength={50} onChange={cambiar} required />
      </div>
      <div className="col-md-3">
        <label className="form-label small">Ubicación</label>
        <input name="ubicacion" className="form-control" value={form.ubicacion} maxLength={100} onChange={cambiar} />
      </div>
      <div className="col-md-2">
        <label className="form-label small">Capacidad</label>
        <input
          name="capacidad"
          type="number"
          min="1"
          className="form-control"
          value={form.capacidad}
          onChange={cambiar}
          required
        />
      </div>
      <div className="col-md-4">
        <label className="form-label small">Facultad</label>
        <select name="idFacultad" className="form-select" value={form.idFacultad} onChange={cambiar} required>
          <option value="">{errorFacultades ? "No se pudieron cargar" : "Facultad..."}</option>
          {facultades.map((f) => (
            <option key={f.idFacultad} value={f.idFacultad}>{f.nombre}</option>
          ))}
        </select>
      </div>
      <div className="col-md-4">
        <label className="form-label small">Estado inicial</label>
        <select name="estado" className="form-select" value={form.estado} onChange={cambiar}>
          {ESTADOS_AULA.map((e) => (
            <option key={e.valor} value={e.valor}>{e.etiqueta}</option>
          ))}
        </select>
      </div>
      <div className="col-md-auto">
        <button type="submit" className="btn btn-kp kp-anim" disabled={enviando}>
          <Icono nombre="plus" tamano={18} animar={enviando ? "siempre" : "hover"} />
          {enviando ? "Guardando..." : "Agregar aula"}
        </button>
      </div>
      <div className="col-md-auto">
        <button type="button" className="btn btn-outline-secondary kp-anim" onClick={onCerrar}>
          <Icono nombre="x" tamano={16} /> Cancelar
        </button>
      </div>
    </form>
  );
}
