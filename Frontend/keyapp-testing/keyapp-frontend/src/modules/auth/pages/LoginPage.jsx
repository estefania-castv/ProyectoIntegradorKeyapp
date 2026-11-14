import { useState } from "react";
import { Navigate, useLocation } from "react-router-dom";
import logo from "@/assets/keyapp-logo.png";
import { useAuth } from "@/modules/auth/hooks/useAuth";
import { Icono } from "@/modules/core/design-system";

export default function LoginPage() {
  const { usuario, login } = useAuth();
  const location = useLocation();
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState("");
  const [cargando, setCargando] = useState(false);

  // Si ya hay sesión, vuelve a la página que se quería visitar.
  if (usuario) {
    return <Navigate to={location.state?.desde || "/reservas"} replace />;
  }

  const enviar = async (e) => {
    e.preventDefault();
    setError("");
    setCargando(true);
    try {
      await login(email, password);
    } catch (err) {
      setError(err.message);
      setCargando(false);
    }
  };

  return (
    <div className="kp-login">
      <aside className="kp-login-hero" aria-hidden="true">
        <div className="kp-escena">
          <div className="kp-escena-aro" />
          <Icono nombre="signal" tamano={38} className="kp-escena-signal" />
          <Icono nombre="lock" tamano={118} className="kp-escena-lock" animar="siempre" />
          <span className="kp-flota f1"><Icono nombre="calendar" tamano={24} animar="siempre" /></span>
          <span className="kp-flota f2"><Icono nombre="door" tamano={24} animar="siempre" /></span>
          <span className="kp-flota f3"><Icono nombre="key" tamano={24} animar="siempre" /></span>
          <span className="kp-flota f4"><Icono nombre="clock" tamano={24} animar="siempre" /></span>
        </div>
        <div className="kp-login-lema">
          <h2>Reserva el aula y abre la puerta desde tu celular</h2>
          <p>Sin llaves, sin filas, sin vueltas.</p>
        </div>
      </aside>

      <main className="kp-login-panel">
        <img src={logo} alt="KeyApp" className="kp-login-logo" />
        <div className="card kp-login-card border-0">
          <div className="card-body">
            <h2>Iniciar sesión</h2>
            <p className="kp-login-sub">Entra con tu correo institucional.</p>
            <form onSubmit={enviar}>
              <div className="mb-3">
                <label className="form-label">Correo</label>
                <div className="kp-campo kp-anim">
                  <Icono nombre="mail" tamano={19} />
                  <input
                    type="email"
                    className="form-control"
                    value={email}
                    onChange={(e) => setEmail(e.target.value)}
                    required
                  />
                </div>
              </div>
              <div className="mb-3">
                <label className="form-label">Contraseña</label>
                <div className="kp-campo kp-anim">
                  <Icono nombre="lock" tamano={19} />
                  <input
                    type="password"
                    className="form-control"
                    value={password}
                    onChange={(e) => setPassword(e.target.value)}
                    required
                  />
                </div>
              </div>
              {error && (
                <div className="alert alert-danger py-2">
                  <Icono nombre="alerta" tamano={18} />
                  <span>{error}</span>
                </div>
              )}
              <button type="submit" className="btn btn-kp kp-anim w-100 py-2" disabled={cargando}>
                <Icono nombre="key" tamano={19} animar={cargando ? "siempre" : "hover"} />
                {cargando ? "Entrando..." : "Entrar"}
              </button>
            </form>
          </div>
        </div>
      </main>
    </div>
  );
}
