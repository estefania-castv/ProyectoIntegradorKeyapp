import { useContext } from "react";
import { DatosContext } from "@/modules/core/states/datosContext";

export function useDatos() {
  const ctx = useContext(DatosContext);
  if (!ctx) throw new Error("useDatos debe usarse dentro de <DatosProvider>");
  return ctx;
}
