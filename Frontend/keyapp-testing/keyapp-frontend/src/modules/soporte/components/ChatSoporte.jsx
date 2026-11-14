import { useCallback, useEffect, useRef, useState } from "react";
import { EstadoVacio, Icono } from "@/modules/core/design-system";
import { useAuth } from "@/modules/auth/hooks/useAuth";
import { api } from "@/modules/core/lib/api";
import { formatoFechaHora } from "@/modules/core/utils/fechas";

/**
 * Conversación de soporte. El profesor ve la suya; el administrador pasa `idUsuario` para ver la de otra persona.
 * Se actualiza sola cada 6 segundos.
 */
export default function ChatSoporte({ idUsuario, placeholder = "Escribe tu mensaje..." }) {
  const { usuario, esAdmin } = useAuth();
  const [mensajes, setMensajes] = useState([]);
  const [cargando, setCargando] = useState(true);
  const [texto, setTexto] = useState("");
  const [enviando, setEnviando] = useState(false);
  const [error, setError] = useState("");
  const lista = useRef(null);

  const ruta = esAdmin ? `/soporte/mensajes?idUsuario=${idUsuario}` : "/soporte/mensajes";

  const cargar = useCallback(async () => {
    try {
      setMensajes(await api.get(ruta));
      setError("");
    } catch (e) {
      setError(e.message);
    } finally {
      setCargando(false);
    }
  }, [ruta]);

  useEffect(() => {
    const inicial = setTimeout(cargar, 0);
    const id = setInterval(cargar, 6000);
    return () => {
      clearTimeout(inicial);
      clearInterval(id);
    };
  }, [cargar]);

  // Siempre mostrar el mensaje más reciente
  useEffect(() => {
    if (lista.current) lista.current.scrollTop = lista.current.scrollHeight;
  }, [mensajes.length]);

  const enviar = async (e) => {
    e.preventDefault();
    if (!texto.trim()) return;
    setEnviando(true);
    try {
      await api.post(
        "/soporte/mensajes",
        { idUsuario: esAdmin ? idUsuario : undefined, texto },
        { mensajeError: "No se pudo enviar el mensaje" }
      );
      setTexto("");
      await cargar();
    } catch (err) {
      setError(err.message);
    }
    setEnviando(false);
  };

  return (
    <div className="kp-chat">
      <div className="kp-chat-lista" ref={lista}>
        {cargando ? (
          <div className="kp-chat-aviso"><Icono nombre="chat" tamano={26} animar="siempre" /></div>
        ) : mensajes.length === 0 ? (
          <EstadoVacio
            icono="chat"
            texto={esAdmin ? "Esta conversación aún no tiene mensajes." : "Escríbenos si tienes algún inconveniente; un administrador te responde aquí."}
          />
        ) : (
          mensajes.map((m) => {
            const mio = m.idAutor === usuario.idUsuario;
            return (
              <div key={m.idMensaje} className={`kp-burbuja ${mio ? "kp-burbuja--mia" : ""}`}>
                {!mio && (
                  <span className="kp-burbuja-autor">
                    {m.autor}
                    {m.rol === "ADMINISTRADOR" && <em> · soporte</em>}
                  </span>
                )}
                <p>{m.texto}</p>
                <span className="kp-burbuja-hora">{formatoFechaHora(m.fecha)}</span>
              </div>
            );
          })
        )}
      </div>

      {error && <div className="kp-chat-error">{error}</div>}

      <form className="kp-chat-form" onSubmit={enviar}>
        <input
          className="form-control"
          value={texto}
          maxLength={500}
          placeholder={placeholder}
          onChange={(e) => setTexto(e.target.value)}
          aria-label="Mensaje"
        />
        <button type="submit" className="btn btn-kp kp-anim" disabled={enviando || !texto.trim()}>
          <Icono nombre="send" tamano={18} animar={enviando ? "siempre" : "hover"} />
          <span className="d-none d-sm-inline">Enviar</span>
        </button>
      </form>
    </div>
  );
}
