import { useMemo, useState } from "react";
import { Link } from "react-router-dom";
import Buscador from "@/modules/core/components/Buscador";
import { Cargando, EncabezadoPagina, Icono } from "@/modules/core/design-system";
import TablaReservas from "@/modules/reservas/components/TablaReservas";
import TransferenciasRecibidas from "@/modules/reservas/components/TransferenciasRecibidas";
import { useAuth } from "@/modules/auth/hooks/useAuth";
import { useDatos } from "@/modules/core/hooks/useDatos";
import { useBusqueda } from "@/modules/core/hooks/useBusqueda";
import { useAhora } from "@/modules/core/hooks/useAhora";

const ESTADOS = ["ACTIVA", "TRANSFERIDA", "CANCELADA", "MODIFICADA", "FINALIZADA"];

const textoReserva = (r) => `${r.aula?.nombre ?? ""} ${r.profesor?.nombre ?? ""} ${r.fecha ?? ""}`;

export default function ReservasPage() {
  const { usuario, esAdmin } = useAuth();
  const {
    reservas, transferencias, cargando, abrirPuerta, cancelarReserva, aceptarTransferencia, rechazarTransferencia,
  } = useDatos();
  const ahora = useAhora();
  const [estado, setEstado] = useState("");
  const [fecha, setFecha] = useState("");

  // El profesor solo ve sus reservas; el administrador ve todas.
  const visibles = useMemo(
    () => (esAdmin ? reservas : reservas.filter((r) => r.profesor?.idUsuario === usuario.idUsuario)),
    [reservas, esAdmin, usuario]
  );

  const porFiltros = useMemo(
    () => visibles.filter((r) => (!estado || r.estado === estado) && (!fecha || r.fecha === fecha)),
    [visibles, estado, fecha]
  );

  const { texto, setTexto, resultado } = useBusqueda(porFiltros, textoReserva);

  // Solicitudes que otro profesor me envió y espero responder
  const recibidas = useMemo(
    () => transferencias.filter((t) => t.estado === "PENDIENTE" && t.idDestino === usuario.idUsuario),
    [transferencias, usuario]
  );
  // Última solicitud que yo envié por cada reserva (pendiente o rechazada); sirve para avisarme el resultado
  const transferenciaEnviada = (idReserva) =>
    transferencias.find((t) => t.idReserva === idReserva && t.idOrigen === usuario.idUsuario);

  return (
    <>
      <EncabezadoPagina
        icono="calendar"
        titulo={esAdmin ? "Reservas" : "Mis reservas"}
        detalle={cargando ? undefined : `${resultado.length} ${resultado.length === 1 ? "reserva" : "reservas"}`}
      >
        {esAdmin && (
          <Link to="/reservas/nueva" className="btn btn-kp kp-anim">
            <Icono nombre="plus" tamano={18} /> Nueva reserva
          </Link>
        )}
      </EncabezadoPagina>

      {!esAdmin && (
        <TransferenciasRecibidas
          pendientes={recibidas}
          onAceptar={aceptarTransferencia}
          onRechazar={rechazarTransferencia}
        />
      )}

    <div className="card border-0">
      <div className="card-body">

        <div className="row g-2 mb-3">
          <div className="col-md">
            <Buscador valor={texto} onChange={setTexto} placeholder="Buscar por aula, profesor o fecha..." />
          </div>
          <div className="col-md-auto">
            <select className="form-select" value={estado} onChange={(e) => setEstado(e.target.value)}>
              <option value="">Todos los estados</option>
              {ESTADOS.map((e) => (
                <option key={e} value={e}>{e}</option>
              ))}
            </select>
          </div>
          <div className="col-md-auto">
            <input type="date" className="form-control" value={fecha} onChange={(e) => setFecha(e.target.value)} />
          </div>
        </div>

        {cargando ? (
          <Cargando texto="Cargando reservas..." />
        ) : (
          <TablaReservas
            reservas={resultado}
            esAdmin={esAdmin}
            ahora={ahora}
            transferenciaEnviada={transferenciaEnviada}
            onAbrir={abrirPuerta}
            onCancelar={cancelarReserva}
          />
        )}
      </div>
    </div>
    </>
  );
}
