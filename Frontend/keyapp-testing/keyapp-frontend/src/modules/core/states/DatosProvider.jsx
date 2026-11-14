import { useCallback, useEffect, useMemo, useState } from "react";
import { DatosContext } from "@/modules/core/states/datosContext";
import { api } from "@/modules/core/lib/api";
import { useAuth } from "@/modules/auth/hooks/useAuth";

export function DatosProvider({ children }) {
  const { usuario } = useAuth();
  const [aulas, setAulas] = useState([]);
  const [usuarios, setUsuarios] = useState([]);
  const [reservas, setReservas] = useState([]);
  // Estado de cada aula AHORA, calculado por el backend: { [idAula]: { estado, profesor, horaInicio, horaFin } }
  const [estadosAulas, setEstadosAulas] = useState({});
  // Solicitudes de transferencia pendientes o rechazadas (recibidas y enviadas)
  const [transferencias, setTransferencias] = useState([]);
  const [cargando, setCargando] = useState(true);
  // { tipo: "error" | "ok", texto, ruta }  -> solo se muestra en la ruta indicada
  const [mensaje, setMensaje] = useState(null);

  const notificar = useCallback((tipo, texto, ruta = window.location.pathname) => {
    setMensaje({ tipo, texto, ruta });
  }, []);
  const cerrarMensaje = useCallback(() => setMensaje(null), []);

  const cargarReservas = useCallback(async () => {
    try {
      setReservas(await api.get("/reservas"));
    } catch (e) {
      notificar("error", e.message);
    }
  }, [notificar]);

  // Estas tres lecturas se repiten solas y no molestan al usuario si fallan una vez.
  const cargarEstadosAulas = useCallback(async () => {
    try {
      const lista = await api.get("/aulas/estado-actual");
      setEstadosAulas(Object.fromEntries(lista.map((e) => [e.idAula, e])));
    } catch {
      /* si falla, se muestra el estado guardado del aula */
    }
  }, []);

  const cargarTransferencias = useCallback(async () => {
    try {
      setTransferencias(await api.get("/transferencias/mias"));
    } catch {
      /* se reintenta en el próximo ciclo */
    }
  }, []);

  const refrescarReservas = useCallback(async () => {
    try {
      setReservas(await api.get("/reservas"));
    } catch {
      /* se reintenta en el próximo ciclo */
    }
  }, []);

  // Carga inicial de aulas, usuarios y reservas.
  useEffect(() => {
    let activo = true;
    Promise.all([api.get("/aulas"), api.get("/usuarios"), api.get("/reservas")])
      .then(([a, u, r]) => {
        if (!activo) return;
        setAulas(a);
        setUsuarios(u);
        setReservas(r);
      })
      .catch((e) => activo && notificar("error", e.message))
      .finally(() => activo && setCargando(false));
    return () => {
      activo = false;
    };
  }, [notificar]);

  // Estado de las aulas y transferencias: al entrar y luego cada 30 s (así las aulas se liberan solas).
  useEffect(() => {
    const actualizar = () => {
      cargarEstadosAulas();
      cargarTransferencias();
    };
    const inicial = setTimeout(actualizar, 0);
    const id = setInterval(() => {
      actualizar();
      refrescarReservas();
    }, 30000);
    return () => {
      clearTimeout(inicial);
      clearInterval(id);
    };
  }, [cargarEstadosAulas, cargarTransferencias, refrescarReservas]);

  const profesores = useMemo(() => usuarios.filter((u) => u.rol === "PROFESOR"), [usuarios]);

  const crearReserva = useCallback(
    async ({ idAula, idProfesor, fecha, horaInicio, horaFin }) => {
      try {
        await api.post(
          "/reservas",
          {
            fecha,
            horaInicio,
            horaFin,
            aula: { idAula: Number(idAula) },
            profesor: { idUsuario: Number(idProfesor) },
          },
          { mensajeError: "No se pudo crear la reserva" }
        );
        notificar("ok", "Reserva creada", "/reservas");
        await cargarReservas();
        cargarEstadosAulas();
        return true;
      } catch (e) {
        notificar("error", e.message);
        return false;
      }
    },
    [notificar, cargarReservas, cargarEstadosAulas]
  );

  const cancelarReserva = useCallback(
    async (id) => {
      try {
        await api.put(`/reservas/${id}/cancelar`, undefined, {
          mensajeError: "No se pudo cancelar la reserva",
        });
        notificar("ok", "Reserva cancelada");
        await cargarReservas();
        cargarEstadosAulas();
        cargarTransferencias();
      } catch (e) {
        notificar("error", e.message);
      }
    },
    [notificar, cargarReservas, cargarEstadosAulas, cargarTransferencias]
  );

  const abrirPuerta = useCallback(
    async (id) => {
      try {
        const data = await api.post(
          `/reservas/${id}/abrir`,
          { idUsuario: usuario.idUsuario },
          { mensajeError: "No se pudo abrir la puerta" }
        );
        notificar("ok", data?.mensaje || "Puerta abierta");
      } catch (e) {
        notificar("error", e.message);
      }
    },
    [notificar, usuario]
  );

  // Crea una SOLICITUD: la reserva cambia de dueño solo cuando el otro profesor la acepta.
  const transferirReserva = useCallback(
    async (id, idProfesorDestino, motivo) => {
      try {
        const data = await api.post(
          `/reservas/${id}/transferir`,
          {
            idUsuario: usuario.idUsuario,
            idProfesorDestino: Number(idProfesorDestino),
            motivo,
          },
          { mensajeError: "No se pudo transferir la reserva" }
        );
        notificar("ok", data?.mensaje || "Solicitud de transferencia enviada", "/reservas");
        await cargarTransferencias();
        await cargarReservas();
        return true;
      } catch (e) {
        notificar("error", e.message);
        return false;
      }
    },
    [notificar, cargarReservas, cargarTransferencias, usuario]
  );

  const responderTransferencia = useCallback(
    async (id, accion) => {
      try {
        await api.put(`/transferencias/${id}/${accion}`, undefined, {
          mensajeError: "No se pudo responder la transferencia",
        });
        notificar(
          "ok",
          accion === "aceptar" ? "Aceptaste la reserva: ya es tuya" : "Rechazaste la transferencia",
          "/reservas"
        );
      } catch (e) {
        notificar("error", e.message, "/reservas");
      }
      await cargarTransferencias();
      await cargarReservas();
    },
    [notificar, cargarReservas, cargarTransferencias]
  );
  const aceptarTransferencia = useCallback((id) => responderTransferencia(id, "aceptar"), [responderTransferencia]);
  const rechazarTransferencia = useCallback((id) => responderTransferencia(id, "rechazar"), [responderTransferencia]);

  // ----- Solo administrador -----
  const crearAula = useCallback(
    async ({ nombre, ubicacion, capacidad, idFacultad, estado }) => {
      try {
        const nueva = await api.post(
          "/aulas",
          {
            nombre,
            ubicacion,
            capacidad: Number(capacidad),
            estado,
            facultad: { idFacultad: Number(idFacultad) },
          },
          { mensajeError: "No se pudo crear el aula" }
        );
        setAulas((prev) => [...prev, nueva]);
        notificar("ok", `Aula "${nueva.nombre}" creada`, "/aulas");
        cargarEstadosAulas();
        return true;
      } catch (e) {
        notificar("error", e.message, "/aulas");
        return false;
      }
    },
    [notificar, cargarEstadosAulas]
  );

  const cambiarEstadoAula = useCallback(
    async (id, estado) => {
      try {
        const actualizada = await api.put(
          `/aulas/${id}/estado`,
          { estado },
          { mensajeError: "No se pudo cambiar el estado del aula" }
        );
        setAulas((prev) => prev.map((a) => (a.idAula === id ? { ...a, estado: actualizada.estado } : a)));
        notificar("ok", "Estado del aula actualizado", "/aulas");
        cargarEstadosAulas();
      } catch (e) {
        notificar("error", e.message, "/aulas");
      }
    },
    [notificar, cargarEstadosAulas]
  );

  const cambiarRolUsuario = useCallback(
    async (id, rol) => {
      try {
        const actualizado = await api.put(
          `/usuarios/${id}/rol`,
          { rol },
          { mensajeError: "No se pudo cambiar el rol" }
        );
        setUsuarios((prev) => prev.map((u) => (u.idUsuario === id ? { ...u, rol: actualizado.rol } : u)));
        notificar(
          "ok",
          `${actualizado.nombre} ahora es ${actualizado.rol}. Debe volver a iniciar sesión para que se aplique`,
          "/usuarios"
        );
      } catch (e) {
        notificar("error", e.message, "/usuarios");
      }
    },
    [notificar]
  );

  const value = useMemo(
    () => ({
      aulas,
      usuarios,
      profesores,
      reservas,
      estadosAulas,
      transferencias,
      cargando,
      mensaje,
      notificar,
      cerrarMensaje,
      crearReserva,
      cancelarReserva,
      abrirPuerta,
      transferirReserva,
      aceptarTransferencia,
      rechazarTransferencia,
      crearAula,
      cambiarEstadoAula,
      cambiarRolUsuario,
    }),
    [
      aulas, usuarios, profesores, reservas, estadosAulas, transferencias, cargando, mensaje,
      notificar, cerrarMensaje, crearReserva, cancelarReserva, abrirPuerta, transferirReserva,
      aceptarTransferencia, rechazarTransferencia, crearAula, cambiarEstadoAula, cambiarRolUsuario,
    ]
  );

  return <DatosContext.Provider value={value}>{children}</DatosContext.Provider>;
}
