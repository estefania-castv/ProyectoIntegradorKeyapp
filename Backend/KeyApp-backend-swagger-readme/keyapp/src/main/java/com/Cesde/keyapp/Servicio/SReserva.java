package com.Cesde.keyapp.Servicio;

import com.Cesde.keyapp.Modelo.MAula;
import com.Cesde.keyapp.Modelo.MReserva;
import com.Cesde.keyapp.Modelo.MUsuario;
import com.Cesde.keyapp.Repositorio.IAula;
import com.Cesde.keyapp.Repositorio.IHorarioAcademico;
import com.Cesde.keyapp.Repositorio.IReserva;
import com.Cesde.keyapp.Repositorio.IUsuario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class SReserva {

    @Autowired
    private IReserva iReserva;

    @Autowired
    private IHorarioAcademico iHorarioAcademico;

    @Autowired
    private IAula iAula;

    @Autowired
    private IUsuario iUsuario;

    public List<MReserva> listar() {
        return iReserva.findAll();
    }

    public Optional<MReserva> buscarPorId(Integer id) {
        return iReserva.findById(id);
    }

    // Valida las reglas de negocio y luego guarda la reserva nueva.
    // El estado y la fecha de creacion los controla el servicio.
    public MReserva crear(MReserva reserva) {
        if (reserva.getAula() == null || reserva.getProfesor() == null) {
            throw new IllegalArgumentException("Debes indicar el aula y el profesor");
        }
        if (reserva.getFecha() == null || reserva.getHoraInicio() == null || reserva.getHoraFin() == null) {
            throw new IllegalArgumentException("Debes indicar la fecha y el horario");
        }
        if (!reserva.getHoraFin().isAfter(reserva.getHoraInicio())) {
            throw new IllegalArgumentException("La hora de fin debe ser posterior a la hora de inicio");
        }

        // 0) El aula debe existir y estar DISPONIBLE (no en mantenimiento, sin energía, sin red, etc.)
        MAula aulaBD = iAula.findById(reserva.getAula().getIdAula())
                .orElseThrow(() -> new IllegalArgumentException("El aula no existe"));
        if (!"DISPONIBLE".equals(aulaBD.getEstado())) {
            throw new IllegalArgumentException("El aula no está disponible (estado: "
                    + aulaBD.getEstado().replace('_', ' ').toLowerCase() + ")");
        }
        // La reserva se asigna a un profesor (el administrador no abre puertas)
        MUsuario profesorBD = iUsuario.findById(reserva.getProfesor().getIdUsuario())
                .orElseThrow(() -> new IllegalArgumentException("El profesor no existe"));
        if (!"PROFESOR".equals(profesorBD.getRol())) {
            throw new IllegalArgumentException("La reserva debe asignarse a un profesor");
        }
        if (LocalDateTime.of(reserva.getFecha(), reserva.getHoraFin()).isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("No se puede reservar en una hora que ya pasó");
        }

        // 1) Que no se cruce con otra reserva activa del aula
        long traslapesReservas = iReserva.contarTraslapes(
                reserva.getAula().getIdAula(),
                reserva.getFecha(),
                reserva.getHoraInicio(),
                reserva.getHoraFin());
        if (traslapesReservas > 0) {
            throw new IllegalArgumentException("El aula ya esta reservada en ese horario");
        }

        // 2) Que no se cruce con una clase del horario academico
        long traslapesClases = iHorarioAcademico.contarTraslapes(
                reserva.getAula().getIdAula(),
                nombreDia(reserva.getFecha()),
                reserva.getHoraInicio(),
                reserva.getHoraFin());
        if (traslapesClases > 0) {
            throw new IllegalArgumentException("El aula tiene una clase programada en ese horario");
        }

        reserva.setEstado("ACTIVA");
        reserva.setFechaCreacion(LocalDateTime.now());
        return iReserva.save(reserva);
    }

    // Convierte la fecha al nombre del dia, igual a como esta guardado en horario_academico
    private String nombreDia(LocalDate fecha) {
        return switch (fecha.getDayOfWeek()) {
            case MONDAY -> "Lunes";
            case TUESDAY -> "Martes";
            case WEDNESDAY -> "Miércoles";
            case THURSDAY -> "Jueves";
            case FRIDAY -> "Viernes";
            case SATURDAY -> "Sábado";
            case SUNDAY -> "Domingo";
        };
    }

    public MReserva actualizar(MReserva reserva) {
        return iReserva.save(reserva);
    }

    public void cancelar(Integer id) {
        iReserva.findById(id).ifPresent(reserva -> {
            reserva.setEstado("CANCELADA");
            iReserva.save(reserva);
        });
    }

    public void eliminar(Integer id) {
        iReserva.deleteById(id);
    }
}