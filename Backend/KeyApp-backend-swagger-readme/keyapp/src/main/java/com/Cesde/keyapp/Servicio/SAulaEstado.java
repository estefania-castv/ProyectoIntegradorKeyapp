package com.Cesde.keyapp.Servicio;

import com.Cesde.keyapp.Dto.AulaEstadoResponse;
import com.Cesde.keyapp.Modelo.MReserva;
import com.Cesde.keyapp.Repositorio.IAula;
import com.Cesde.keyapp.Repositorio.IReserva;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SAulaEstado {

    private final IAula aulaRepo;
    private final IReserva reservaRepo;

    public SAulaEstado(IAula aulaRepo, IReserva reservaRepo) {
        this.aulaRepo = aulaRepo;
        this.reservaRepo = reservaRepo;
    }

    // Estado de cada aula AHORA: si está DISPONIBLE pero hay una reserva en curso, queda RESERVADA
    // (con el nombre del profesor y hasta qué hora). Los demás estados (MANTENIMIENTO, SIN_ENERGIA,
    // PROBLEMAS_DE_RED, FUERA_DE_SERVICIO, INACTIVA) se respetan tal cual.
    // Lo ve cualquier usuario con sesión, aunque un profesor no pueda listar las reservas de otros.
    public List<AulaEstadoResponse> estadoActual() {
        LocalDateTime ahora = LocalDateTime.now();
        Map<Integer, MReserva> enCursoPorAula = new HashMap<>();
        for (MReserva r : reservaRepo.reservasEnCurso(ahora.toLocalDate(), ahora.toLocalTime())) {
            enCursoPorAula.putIfAbsent(r.getAula().getIdAula(), r);
        }

        return aulaRepo.findAll().stream().map(aula -> {
            MReserva r = "DISPONIBLE".equals(aula.getEstado()) ? enCursoPorAula.get(aula.getIdAula()) : null;
            if (r != null) {
                return new AulaEstadoResponse(aula.getIdAula(), "RESERVADA",
                        r.getProfesor().getNombre(), r.getHoraInicio(), r.getHoraFin());
            }
            return new AulaEstadoResponse(aula.getIdAula(), aula.getEstado(), null, null, null);
        }).toList();
    }
}
