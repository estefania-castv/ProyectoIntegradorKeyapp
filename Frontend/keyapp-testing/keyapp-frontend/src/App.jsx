import { Navigate, Route, Routes } from "react-router-dom";
import LayoutPrivado from "@/modules/core/components/LayoutPrivado";
import RutaProtegida from "@/modules/auth/components/RutaProtegida";
import LoginPage from "@/modules/auth/pages/LoginPage";
import DashboardPage from "@/modules/dashboard/pages/DashboardPage";
import ReservasPage from "@/modules/reservas/pages/ReservasPage";
import NuevaReservaPage from "@/modules/reservas/pages/NuevaReservaPage";
import TransferirPage from "@/modules/reservas/pages/TransferirPage";
import AulasPage from "@/modules/aulas/pages/AulasPage";
import UsuariosPage from "@/modules/usuarios/pages/UsuariosPage";
import ReportesPage from "@/modules/reportes/pages/ReportesPage";
import EstadisticasPage from "@/modules/reportes/pages/EstadisticasPage";
import SoportePage from "@/modules/soporte/pages/SoportePage";
import NoAutorizadaPage from "@/modules/core/pages/NoAutorizadaPage";
import NoEncontradaPage from "@/modules/core/pages/NoEncontradaPage";

export default function App() {
  return (
    <Routes>
      {/* Pública */}
      <Route path="/login" element={<LoginPage />} />

      {/* Requieren sesión */}
      <Route element={<RutaProtegida />}>
        <Route element={<LayoutPrivado />}>
          <Route index element={<Navigate to="/reservas" replace />} />
          <Route path="dashboard" element={<DashboardPage />} />
          <Route path="reservas" element={<ReservasPage />} />
          <Route path="aulas" element={<AulasPage />} />
          <Route path="reportes" element={<ReportesPage />} />
          <Route path="soporte" element={<SoportePage />} />
          <Route path="no-autorizado" element={<NoAutorizadaPage />} />

          {/* Solo ADMINISTRADOR: crea las reservas y administra usuarios */}
          <Route element={<RutaProtegida roles={["ADMINISTRADOR"]} />}>
            <Route path="reservas/nueva" element={<NuevaReservaPage />} />
            <Route path="usuarios" element={<UsuariosPage />} />
            <Route path="estadisticas" element={<EstadisticasPage />} />
          </Route>

          {/* Solo PROFESOR: transfiere sus reservas */}
          <Route element={<RutaProtegida roles={["PROFESOR"]} />}>
            <Route path="reservas/:id/transferir" element={<TransferirPage />} />
          </Route>
        </Route>
      </Route>

      <Route path="*" element={<NoEncontradaPage />} />
    </Routes>
  );
}
