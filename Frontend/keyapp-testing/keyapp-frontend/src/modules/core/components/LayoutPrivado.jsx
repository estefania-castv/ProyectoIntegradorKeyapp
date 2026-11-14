import { Outlet } from "react-router-dom";
import { DatosProvider } from "@/modules/core/states/DatosProvider";
import Navbar from "@/modules/core/components/Navbar";
import Alertas from "@/modules/core/components/Alertas";

// Solo se monta con sesión iniciada: al cerrar sesión los datos se descartan solos.
export default function LayoutPrivado() {
  return (
    <DatosProvider>
      <Navbar />
      <div className="container pb-5">
        <Alertas />
        <Outlet />
      </div>
    </DatosProvider>
  );
}
