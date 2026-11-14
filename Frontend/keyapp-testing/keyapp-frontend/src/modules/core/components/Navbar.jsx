import { NavLink } from "react-router-dom";
import icon from "@/assets/keyapp-icon.png";
import { useAuth } from "@/modules/auth/hooks/useAuth";
import { useDatos } from "@/modules/core/hooks/useDatos";
import { Icono } from "@/modules/core/design-system";

const claseLink = ({ isActive }) => `nav-link kp-nav-link kp-anim ${isActive ? "active" : ""}`;

function Enlace({ to, end, icono, aviso, children }) {
  return (
    <li className="nav-item">
      <NavLink to={to} end={end} className={claseLink} title={children}>
        <Icono nombre={icono} tamano={19} />
        <span className="kp-nav-txt">{children}</span>
        {aviso > 0 && <span className="kp-aviso" aria-label={`${aviso} pendientes`}>{aviso}</span>}
      </NavLink>
    </li>
  );
}

export default function Navbar() {
  const { usuario, esAdmin, logout } = useAuth();
  const { transferencias } = useDatos();
  const inicial = (usuario.nombre || "?").trim().charAt(0).toUpperCase();

  // Transferencias que otro profesor te envió y todavía no respondes
  const porResponder = transferencias.filter(
    (t) => t.estado === "PENDIENTE" && t.idDestino === usuario.idUsuario
  ).length;

  return (
    <nav className="navbar navbar-kp navbar-dark navbar-expand-md mb-4">
      <div className="container py-2">
        <NavLink to="/reservas" className="navbar-brand d-flex align-items-center gap-2">
          <img src={icon} alt="" className="kp-logo" />
          <span className="fs-4 fw-semibold">KeyApp</span>
        </NavLink>

        <ul className="kp-nav">
          <Enlace to="/dashboard" icono="dashboard">Dashboard</Enlace>
          <Enlace to="/reservas" end icono="calendar" aviso={porResponder}>Reservas</Enlace>
          {esAdmin && <Enlace to="/reservas/nueva" icono="plus">Nueva reserva</Enlace>}
          <Enlace to="/aulas" icono="aula">Aulas</Enlace>
          <Enlace to="/reportes" icono="flag">Reportes</Enlace>
          {esAdmin && <Enlace to="/estadisticas" icono="dashboard">Estadísticas</Enlace>}
          <Enlace to="/soporte" icono="chat">Soporte</Enlace>
          {esAdmin && <Enlace to="/usuarios" icono="users">Usuarios</Enlace>}
        </ul>

        <div className="kp-user">
          <span className="kp-avatar" aria-hidden="true">{inicial}</span>
          <span className="kp-user-info">
            <strong>{usuario.nombre}</strong>
            <small>{usuario.rol}</small>
          </span>
          <button className="kp-salir kp-anim" onClick={logout} title="Cerrar sesión">
            <Icono nombre="logout" tamano={17} />
            <span className="d-none d-sm-inline">Cerrar sesión</span>
          </button>
        </div>
      </div>
    </nav>
  );
}
