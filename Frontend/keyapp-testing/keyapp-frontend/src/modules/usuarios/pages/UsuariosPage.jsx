import { useMemo, useState } from "react";
import Buscador from "@/modules/core/components/Buscador";
import { Cargando, EncabezadoPagina, EstadoVacio, Icono } from "@/modules/core/design-system";
import { useAuth } from "@/modules/auth/hooks/useAuth";
import { useDatos } from "@/modules/core/hooks/useDatos";
import { useBusqueda } from "@/modules/core/hooks/useBusqueda";

const correoDe = (u) => u.email ?? u.correo ?? "";
const textoUsuario = (u) => `${u.nombre ?? ""} ${correoDe(u)} ${u.rol ?? ""}`;

export default function UsuariosPage() {
  const { usuario: yo } = useAuth();
  const { usuarios, cargando, cambiarRolUsuario } = useDatos();
  const [rol, setRol] = useState("");

  const porRol = useMemo(() => usuarios.filter((u) => !rol || u.rol === rol), [usuarios, rol]);
  const { texto, setTexto, resultado } = useBusqueda(porRol, textoUsuario);

  const cambiarRol = (u, nuevoRol) => {
    if (nuevoRol === u.rol) return;
    const texto =
      nuevoRol === "ADMINISTRADOR"
        ? `¿Volver administrador a ${u.nombre}? Podrá crear reservas y administrar usuarios, pero ya no abrirá puertas.`
        : `¿Pasar a ${u.nombre} a profesor? Dejará de administrar la aplicación.`;
    if (window.confirm(texto)) cambiarRolUsuario(u.idUsuario, nuevoRol);
  };

  return (
    <>
    <EncabezadoPagina
      icono="users"
      titulo="Usuarios"
      detalle={cargando ? undefined : `${resultado.length} ${resultado.length === 1 ? "usuario" : "usuarios"}`}
    />
    <div className="card border-0">
      <div className="card-body">
        <div className="row g-2 mb-3">
          <div className="col-md">
            <Buscador valor={texto} onChange={setTexto} placeholder="Buscar por nombre, correo o rol..." />
          </div>
          <div className="col-md-auto">
            <select className="form-select" value={rol} onChange={(e) => setRol(e.target.value)}>
              <option value="">Todos los roles</option>
              <option value="PROFESOR">PROFESOR</option>
              <option value="ADMINISTRADOR">ADMINISTRADOR</option>
            </select>
          </div>
        </div>

        {cargando ? (
          <Cargando texto="Cargando usuarios..." />
        ) : resultado.length === 0 ? (
          <EstadoVacio icono="users" texto="No hay usuarios que coincidan." />
        ) : (
          <div className="table-responsive">
            <table className="table align-middle">
              <thead>
                <tr><th>Nombre</th><th>Correo</th><th>Rol</th><th>Cambiar rol</th></tr>
              </thead>
              <tbody>
                {resultado.map((u) => (
                  <tr key={u.idUsuario} className="kp-anim">
                    <td>
                      <div className="kp-celda">
                        <span className="kp-mini-avatar">{(u.nombre || "?").trim().charAt(0).toUpperCase()}</span>
                        {u.nombre}
                      </div>
                    </td>
                    <td><span className="kp-dato"><Icono nombre="mail" tamano={16} />{correoDe(u) || "—"}</span></td>
                    <td>
                      <span className={`kp-estado ${u.rol === "ADMINISTRADOR" ? "kp-estado--warn" : "kp-estado--info"}`}>
                        <Icono nombre={u.rol === "ADMINISTRADOR" ? "key" : "user"} tamano={14} />
                        {u.rol}
                      </span>
                    </td>
                    <td>
                      {u.idUsuario === yo.idUsuario ? (
                        <span className="kp-nota">Es tu cuenta</span>
                      ) : (
                        <select
                          className="form-select form-select-sm kp-select-rol"
                          value={u.rol}
                          aria-label={`Rol de ${u.nombre}`}
                          onChange={(e) => cambiarRol(u, e.target.value)}
                        >
                          <option value="PROFESOR">Profesor</option>
                          <option value="ADMINISTRADOR">Administrador</option>
                        </select>
                      )}
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}
      </div>
    </div>
    </>
  );
}
