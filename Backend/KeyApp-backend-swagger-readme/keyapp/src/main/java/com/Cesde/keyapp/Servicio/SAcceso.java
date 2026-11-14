package com.Cesde.keyapp.Servicio;

import com.Cesde.keyapp.Dto.AbrirResponse;
import com.Cesde.keyapp.Mapper.AccesoMapper;
import com.Cesde.keyapp.Modelo.MCerradura;
import com.Cesde.keyapp.Modelo.MRegistroAcceso;
import com.Cesde.keyapp.Modelo.MReserva;
import com.Cesde.keyapp.Modelo.MUsuario;
import com.Cesde.keyapp.Repositorio.IRegistroAcceso;
import com.Cesde.keyapp.Repositorio.IReserva;
import com.Cesde.keyapp.Repositorio.IUsuario;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.LocalTime;

@Service
public class SAcceso {

    // Tiempo máximo, desde el inicio de la reserva, para abrir la puerta
    static final int MINUTOS_PARA_ABRIR = 20;

    private final IReserva reservaRepo;
    private final IUsuario usuarioRepo;
    private final IRegistroAcceso registroRepo;

    public SAcceso(IReserva reservaRepo, IUsuario usuarioRepo, IRegistroAcceso registroRepo) {
        this.reservaRepo = reservaRepo;
        this.usuarioRepo = usuarioRepo;
        this.registroRepo = registroRepo;
    }

    @Transactional
    public AbrirResponse abrir(Integer idReserva, Integer idUsuario) {
        LocalDateTime ahora = LocalDateTime.now();

        MReserva reserva = reservaRepo.findById(idReserva).orElse(null);
        if (reserva == null) {
            return denegar(ahora, null, null, "RESERVA NO ENCONTRADA", "La reserva no existe");
        }

        MCerradura cerradura = reserva.getAula().getCerradura();

        MUsuario usuario = idUsuario == null ? null : usuarioRepo.findById(idUsuario).orElse(null);
        if (usuario == null) {
            return denegar(ahora, reserva, cerradura, "USUARIO NO ENCONTRADO", "Usuario no válido");
        }

        // Solo los profesores abren puertas (el administrador crea las reservas pero no abre).
        if (!"PROFESOR".equals(usuario.getRol())) {
            return denegar(ahora, reserva, cerradura, "USUARIO NO AUTORIZADO",
                    "Solo los profesores pueden abrir las aulas");
        }
        boolean esDuenio = reserva.getProfesor().getIdUsuario().equals(usuario.getIdUsuario());
        if (!esDuenio) {
            return denegar(ahora, reserva, cerradura, "USUARIO NO AUTORIZADO", "Esta reserva no es tuya");
        }

        // ACTIVA y TRANSFERIDA cuentan como reservas vigentes
        String estado = reserva.getEstado();
        if (!"ACTIVA".equals(estado) && !"TRANSFERIDA".equals(estado)) {
            return denegar(ahora, reserva, cerradura, "RESERVA NO ACTIVA",
                    "La reserva no está vigente (estado: " + estado + ")");
        }

        if (cerradura == null || !"ACTIVA".equals(cerradura.getEstado())) {
            return denegar(ahora, reserva, cerradura, "CERRADURA NO DISPONIBLE",
                    "La cerradura de esta aula no está disponible");
        }

        // Se puede abrir desde la hora de inicio hasta MINUTOS_PARA_ABRIR minutos después
        // (sin pasar de la hora de fin). Pasado ese tiempo, la puerta ya no abre.
        LocalTime hora = ahora.toLocalTime();
        LocalTime limite = reserva.getHoraInicio().plusMinutes(MINUTOS_PARA_ABRIR);
        if (limite.isBefore(reserva.getHoraInicio()) || limite.isAfter(reserva.getHoraFin())) {
            limite = reserva.getHoraFin();
        }
        boolean esHoy = reserva.getFecha().equals(ahora.toLocalDate());
        boolean enHorario = esHoy
                && !hora.isBefore(reserva.getHoraInicio())
                && !hora.isAfter(limite)
                && hora.isBefore(reserva.getHoraFin());
        if (!enHorario) {
            return denegar(ahora, reserva, cerradura, "FUERA DE HORARIO",
                    "Solo puedes abrir el día de la reserva, entre " + reserva.getHoraInicio()
                            + " y " + limite + " (hasta " + MINUTOS_PARA_ABRIR + " minutos después del inicio)");
        }

        registrar(ahora, "APERTURA", "PUERTA ABIERTA", reserva, cerradura);
        return AccesoMapper.toResponse(true, "Puerta abierta: " + reserva.getAula().getNombre());
    }

    private AbrirResponse denegar(LocalDateTime ahora, MReserva reserva, MCerradura cerradura,
                                  String resultado, String mensaje) {
        registrar(ahora, "FALLIDO", resultado, reserva, cerradura);
        return AccesoMapper.toResponse(false, mensaje);
    }

    private void registrar(LocalDateTime ahora, String tipoEvento, String resultado,
                           MReserva reserva, MCerradura cerradura) {
        registroRepo.save(new MRegistroAcceso(ahora, tipoEvento, resultado, reserva, cerradura));
    }
}
