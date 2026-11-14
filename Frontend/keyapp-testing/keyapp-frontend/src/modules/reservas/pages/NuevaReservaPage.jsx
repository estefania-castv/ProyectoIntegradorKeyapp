import { useNavigate } from "react-router-dom";
import { EncabezadoPagina } from "@/modules/core/design-system";
import FormReserva from "@/modules/reservas/components/FormReserva";
import { useAuth } from "@/modules/auth/hooks/useAuth";
import { useDatos } from "@/modules/core/hooks/useDatos";

export default function NuevaReservaPage() {
  const { usuario, esAdmin } = useAuth();
  const { aulas, profesores, crearReserva } = useDatos();
  const navigate = useNavigate();

  const enviar = async (datos) => {
    const idProfesor = esAdmin ? datos.idProfesor : usuario.idUsuario;
    const ok = await crearReserva({ ...datos, idProfesor });
    if (ok) navigate("/reservas");
  };

  return (
    <>
      <EncabezadoPagina icono="plus" titulo="Nueva reserva" detalle="Elige aula, fecha y horario." />
      <div className="card border-0">
        <div className="card-body">
          <FormReserva aulas={aulas} profesores={profesores} esAdmin={esAdmin} onSubmit={enviar} />
        </div>
      </div>
    </>
  );
}
