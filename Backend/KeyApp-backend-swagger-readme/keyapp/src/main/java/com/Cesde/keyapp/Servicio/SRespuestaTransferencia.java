package com.Cesde.keyapp.Servicio;

import com.Cesde.keyapp.Modelo.MReserva;
import com.Cesde.keyapp.Modelo.MTransferencia;
import com.Cesde.keyapp.Repositorio.IReserva;
import com.Cesde.keyapp.Repositorio.ITransferencia;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

@Service
public class SRespuestaTransferencia {

    private static final Set<String> RESERVA_VIGENTE = Set.of("ACTIVA", "TRANSFERIDA");

    private final ITransferencia transferenciaRepo;
    private final IReserva reservaRepo;

    public SRespuestaTransferencia(ITransferencia transferenciaRepo, IReserva reservaRepo) {
        this.transferenciaRepo = transferenciaRepo;
        this.reservaRepo = reservaRepo;
    }

    // Solicitudes del usuario (recibidas y enviadas) que todavía importan: PENDIENTES y RECHAZADAS
    // de reservas que siguen vigentes. Así el destino ve qué aceptar y el origen ve qué se rechazó.
    public List<MTransferencia> mias(Integer idUsuario) {
        Map<Integer, MTransferencia> unicas = new LinkedHashMap<>();
        List<MTransferencia> todas = new ArrayList<>();
        todas.addAll(transferenciaRepo.findByProfesorDestino_IdUsuarioOrderByFechaTransferenciaDesc(idUsuario));
        todas.addAll(transferenciaRepo.findByProfesorOrigen_IdUsuarioOrderByFechaTransferenciaDesc(idUsuario));
        todas.sort(Comparator.comparing(MTransferencia::getFechaTransferencia).reversed());
        for (MTransferencia t : todas) {
            boolean relevante = "PENDIENTE".equals(t.getEstado()) || "RECHAZADA".equals(t.getEstado());
            if (relevante && RESERVA_VIGENTE.contains(t.getReserva().getEstado())) {
                unicas.putIfAbsent(t.getIdTransferencia(), t);
            }
        }
        return new ArrayList<>(unicas.values());
    }

    @Transactional
    public MTransferencia aceptar(Integer idTransferencia, Integer idUsuario) {
        MTransferencia t = pendienteDelDestino(idTransferencia, idUsuario);
        MReserva reserva = t.getReserva();

        boolean vigente = RESERVA_VIGENTE.contains(reserva.getEstado())
                && LocalDateTime.of(reserva.getFecha(), reserva.getHoraFin()).isAfter(LocalDateTime.now());
        if (!vigente) {
            t.setEstado("EXPIRADA");
            transferenciaRepo.save(t);
            throw new IllegalArgumentException("La reserva ya terminó o fue cancelada");
        }
        // Si la reserva cambió de dueño mientras tanto, la solicitud ya no tiene sentido
        if (t.getProfesorOrigen() != null
                && !reserva.getProfesor().getIdUsuario().equals(t.getProfesorOrigen().getIdUsuario())) {
            t.setEstado("CANCELADA");
            transferenciaRepo.save(t);
            throw new IllegalArgumentException("La reserva cambió de dueño, esta solicitud ya no es válida");
        }

        reserva.setProfesor(t.getProfesorDestino());
        reserva.setEstado("TRANSFERIDA");
        reservaRepo.save(reserva);

        t.setEstado("ACEPTADA");
        t.setFechaRespuesta(LocalDateTime.now());
        return transferenciaRepo.save(t);
    }

    // Rechazar no toca la reserva: sigue siendo de quien la transfirió, que puede transferirla de nuevo.
    @Transactional
    public MTransferencia rechazar(Integer idTransferencia, Integer idUsuario) {
        MTransferencia t = pendienteDelDestino(idTransferencia, idUsuario);
        t.setEstado("RECHAZADA");
        t.setFechaRespuesta(LocalDateTime.now());
        return transferenciaRepo.save(t);
    }

    private MTransferencia pendienteDelDestino(Integer idTransferencia, Integer idUsuario) {
        MTransferencia t = transferenciaRepo.findById(idTransferencia)
                .orElseThrow(() -> new NoSuchElementException("La transferencia no existe"));
        if (idUsuario == null || !t.getProfesorDestino().getIdUsuario().equals(idUsuario)) {
            throw new SecurityException("Esta transferencia no es para ti");
        }
        if (!"PENDIENTE".equals(t.getEstado())) {
            throw new IllegalArgumentException("Esta transferencia ya fue respondida o ya no está pendiente");
        }
        return t;
    }
}
