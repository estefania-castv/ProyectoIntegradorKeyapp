import { useMemo, useState } from "react";
import { Link } from "react-router-dom";
import Buscador from "@/modules/core/components/Buscador";
import { Cargando, EncabezadoPagina, EstadoBadge, EstadoVacio, Icono } from "@/modules/core/design-system";
import { useAuth } from "@/modules/auth/hooks/useAuth";
import { useDatos } from "@/modules/core/hooks/useDatos";
import { useBusqueda } from "@/modules/core/hooks/useBusqueda";
import FormAula from "@/modules/aulas/components/FormAula";
import { ESTADOS_AULA, etiquetaEstadoAula, iconoEstadoAula } from "@/modules/aulas/estados";

const textoAula = (a) => `${a.nombre ?? ""} ${a.ubicacion ?? ""}`;

export default function AulasPage() {
  const { esAdmin } = useAuth();
  const { aulas, estadosAulas, cargando, crearAula, cambiarEstadoAula } = useDatos();
  const [estado, setEstado] = useState("");
  const [agregando, setAgregando] = useState(false);

  // El estado que se muestra viene del backend: si el aula está disponible pero hay una reserva en curso,
  // dice RESERVADA (con el profesor y la hora de fin); si no, el estado que le puso el administrador.
  const aulasConEstado = useMemo(
    () =>
      aulas.map((a) => {
        const actual = estadosAulas[a.idAula];
        return { ...a, actual, estadoMostrado: actual?.estado ?? a.estado };
      }),
    [aulas, estadosAulas]
  );

  const estados = useMemo(
    () => [...new Set(aulasConEstado.map((a) => a.estadoMostrado).filter(Boolean))],
    [aulasConEstado]
  );
  const porEstado = useMemo(
    () => aulasConEstado.filter((a) => !estado || a.estadoMostrado === estado),
    [aulasConEstado, estado]
  );
  const { texto, setTexto, resultado } = useBusqueda(porEstado, textoAula);

  return (
    <>
      <EncabezadoPagina
        icono="aula"
        titulo="Aulas"
        detalle={cargando ? undefined : `${resultado.length} ${resultado.length === 1 ? "aula" : "aulas"}`}
      >
        {esAdmin && (
          <button className="btn btn-kp kp-anim" onClick={() => setAgregando((v) => !v)}>
            <Icono nombre="plus" tamano={18} /> {agregando ? "Cerrar" : "Agregar aula"}
          </button>
        )}
      </EncabezadoPagina>

      {esAdmin && agregando && (
        <div className="card border-0 mb-3">
          <div className="card-body">
            <h6 className="kp-card-titulo"><Icono nombre="aula" tamano={20} />Nueva aula</h6>
            <FormAula onCrear={crearAula} onCerrar={() => setAgregando(false)} />
          </div>
        </div>
      )}

      <div className="card border-0">
        <div className="card-body">
          <div className="row g-2 mb-3">
            <div className="col-md">
              <Buscador valor={texto} onChange={setTexto} placeholder="Buscar aula por nombre o ubicación..." />
            </div>
            <div className="col-md-auto">
              <select className="form-select" value={estado} onChange={(e) => setEstado(e.target.value)}>
                <option value="">Todos los estados</option>
                {estados.map((e) => (
                  <option key={e} value={e}>{etiquetaEstadoAula(e)}</option>
                ))}
              </select>
            </div>
          </div>

          {cargando ? (
            <Cargando texto="Cargando aulas..." />
          ) : resultado.length === 0 ? (
            <EstadoVacio icono="aula" texto="No hay aulas que coincidan." />
          ) : (
            <div className="kp-aulas">
              {resultado.map((a) => {
                const reservada = a.estadoMostrado === "RESERVADA";
                const libre = a.estadoMostrado === "DISPONIBLE";
                return (
                  <div
                    key={a.idAula}
                    className={`kp-aula kp-anim ${reservada ? "kp-aula--reservada" : libre ? "" : "kp-aula--ocupada"}`}
                  >
                    <div className="kp-aula-top">
                      <span className="kp-icon-tile">
                        <Icono nombre={libre ? "door" : iconoEstadoAula(a.estadoMostrado)} tamano={24} />
                      </span>
                    </div>
                    <strong>{a.nombre}</strong>
                    <EstadoBadge estado={a.estadoMostrado} />

                    <div className="kp-aula-info">
                      {reservada && (
                        <>
                          <span className="kp-dato"><Icono nombre="user" tamano={16} />{a.actual?.profesor}</span>
                          <span className="kp-dato">
                            <Icono nombre="clock" tamano={16} />
                            Hasta las {a.actual?.horaFin?.slice(0, 5)}
                          </span>
                        </>
                      )}
                      {a.ubicacion && <span className="kp-dato"><Icono nombre="aula" tamano={16} />{a.ubicacion}</span>}
                      {a.capacidad && <span className="kp-dato"><Icono nombre="users" tamano={16} />{a.capacidad} personas</span>}
                    </div>

                    {esAdmin ? (
                      <label className="kp-aula-estado">
                        <span>Estado del aula</span>
                        <select
                          className="form-select form-select-sm"
                          value={a.estado}
                          onChange={(e) => cambiarEstadoAula(a.idAula, e.target.value)}
                        >
                          {ESTADOS_AULA.map((e) => (
                            <option key={e.valor} value={e.valor}>{e.etiqueta}</option>
                          ))}
                        </select>
                      </label>
                    ) : (
                      <Link to={`/reportes?aula=${a.idAula}`} className="btn btn-outline-secondary btn-sm kp-anim">
                        <Icono nombre="flag" tamano={16} /> Reportar un problema
                      </Link>
                    )}
                  </div>
                );
              })}
            </div>
          )}
        </div>
      </div>
    </>
  );
}
