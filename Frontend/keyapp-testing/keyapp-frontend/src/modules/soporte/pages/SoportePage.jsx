import { useCallback, useEffect, useState } from "react";
import { Cargando, EncabezadoPagina, EstadoVacio, Icono } from "@/modules/core/design-system";
import { useAuth } from "@/modules/auth/hooks/useAuth";
import { api } from "@/modules/core/lib/api";
import { formatoFechaHora } from "@/modules/core/utils/fechas";
import ChatSoporte from "@/modules/soporte/components/ChatSoporte";

// Profesor: escribe a soporte. Administrador: ve las conversaciones y responde.
export default function SoportePage() {
  const { esAdmin } = useAuth();
  const [conversaciones, setConversaciones] = useState([]);
  const [cargando, setCargando] = useState(esAdmin);
  const [elegida, setElegida] = useState(null);

  const cargar = useCallback(async () => {
    try {
      setConversaciones(await api.get("/soporte/conversaciones"));
    } catch {
      /* se reintenta en el próximo ciclo */
    } finally {
      setCargando(false);
    }
  }, []);

  useEffect(() => {
    if (!esAdmin) return undefined;
    const inicial = setTimeout(cargar, 0);
    const id = setInterval(cargar, 10000);
    return () => {
      clearTimeout(inicial);
      clearInterval(id);
    };
  }, [esAdmin, cargar]);

  if (!esAdmin) {
    return (
      <>
        <EncabezadoPagina icono="chat" titulo="Soporte" detalle="Escribe si presentas algún inconveniente" />
        <div className="card border-0">
          <div className="card-body">
            <ChatSoporte />
          </div>
        </div>
      </>
    );
  }

  const actual = conversaciones.find((c) => c.idUsuario === elegida);

  return (
    <>
      <EncabezadoPagina
        icono="chat"
        titulo="Soporte"
        detalle={cargando ? undefined : `${conversaciones.length} ${conversaciones.length === 1 ? "conversación" : "conversaciones"}`}
      />
      <div className="kp-soporte">
        <div className="card border-0 kp-conversaciones">
          <div className="card-body">
            {cargando ? (
              <Cargando texto="Cargando..." />
            ) : conversaciones.length === 0 ? (
              <EstadoVacio icono="chat" texto="Nadie ha escrito a soporte todavía." />
            ) : (
              conversaciones.map((c) => (
                <button
                  key={c.idUsuario}
                  className={`kp-conv kp-anim ${c.idUsuario === elegida ? "kp-conv--activa" : ""}`}
                  onClick={() => setElegida(c.idUsuario)}
                >
                  <span className="kp-mini-avatar">{(c.nombre || "?").trim().charAt(0).toUpperCase()}</span>
                  <span className="kp-conv-texto">
                    <strong>{c.nombre}</strong>
                    <small>{c.ultimoMensaje}</small>
                  </span>
                  <span className="kp-nota">{formatoFechaHora(c.fecha)}</span>
                </button>
              ))
            )}
          </div>
        </div>

        <div className="card border-0">
          <div className="card-body">
            {elegida ? (
              <>
                <h6 className="kp-card-titulo"><Icono nombre="user" tamano={20} />{actual?.nombre ?? "Conversación"}</h6>
                <ChatSoporte key={elegida} idUsuario={elegida} placeholder="Escribe tu respuesta..." />
              </>
            ) : (
              <EstadoVacio icono="chat" texto="Elige una conversación para responder." />
            )}
          </div>
        </div>
      </div>
    </>
  );
}
