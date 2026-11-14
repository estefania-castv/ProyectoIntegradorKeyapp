import { useLocation } from "react-router-dom";
import { useDatos } from "@/modules/core/hooks/useDatos";
import { Icono } from "@/modules/core/design-system";

export default function Alertas() {
  const { mensaje, cerrarMensaje } = useDatos();
  const { pathname } = useLocation();

  if (!mensaje || mensaje.ruta !== pathname) return null;

  const esError = mensaje.tipo === "error";
  const clase = esError ? "alert-danger" : "alert-success";
  return (
    <div className={`alert ${clase} alert-dismissible`} role="alert">
      <Icono nombre={esError ? "alerta" : "check"} tamano={20} animar="siempre" />
      <span>{mensaje.texto}</span>
      <button type="button" className="btn-close" onClick={cerrarMensaje} aria-label="Cerrar" />
    </div>
  );
}
