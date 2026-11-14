package com.Cesde.keyapp.Servicio;

import com.Cesde.keyapp.Modelo.MReserva;
import com.Cesde.keyapp.Modelo.MTransferencia;
import com.Cesde.keyapp.Modelo.MUsuario;
import com.Cesde.keyapp.Repositorio.IReserva;
import com.Cesde.keyapp.Repositorio.ITransferencia;
import com.Cesde.keyapp.Repositorio.IUsuario;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class STransferirReserva {

    private final IReserva reservaRepo;
    private final IUsuario usuarioRepo;
    private final ITransferencia transferenciaRepo;

    public STransferirReserva(IReserva reservaRepo, IUsuario usuarioRepo, ITransferencia transferenciaRepo) {
        this.reservaRepo = reservaRepo;
        this.usuarioRepo = usuarioRepo;
        this.transferenciaRepo = transferenciaRepo;
    }

    // Crea una solicitud PENDIENTE para el profesor destino. La reserva sigue siendo de quien transfiere
    // hasta que el destino la acepte. Si ya había una solicitud pendiente, esta la reemplaza.
    // Si el destino la rechazó, quien transfiere puede enviarla de nuevo (a la misma u otra persona).
    @Transactional
    public MTransferencia transferir(Integer idReserva, Integer idUsuario,
                                     Integer idDestino, String motivo) {
        MReserva reserva = reservaRepo.findById(idReserva)
                .orElseThrow(() -> new IllegalArgumentException("La reserva no existe"));

        MUsuario actor = idUsuario == null ? null : usuarioRepo.findById(idUsuario).orElse(null);
        if (actor == null) {
            throw new SecurityException("Usuario no válido");
        }
        // Solo el profesor dueño de la reserva puede transferirla (el administrador no).
        if (!"PROFESOR".equals(actor.getRol())) {
            throw new SecurityException("Solo los profesores pueden transferir reservas");
        }
        if (!reserva.getProfesor().getIdUsuario().equals(actor.getIdUsuario())) {
            throw new SecurityException("Esta reserva no es tuya");
        }

        String estado = reserva.getEstado();
        if (!"ACTIVA".equals(estado) && !"TRANSFERIDA".equals(estado)) {
            throw new IllegalArgumentException(
                    "Solo se pueden transferir reservas vigentes (estado actual: " + estado + ")");
        }
        if (LocalDateTime.of(reserva.getFecha(), reserva.getHoraFin()).isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("La reserva ya terminó");
        }

        MUsuario destino = idDestino == null ? null : usuarioRepo.findById(idDestino).orElse(null);
        if (destino == null || !"PROFESOR".equals(destino.getRol())) {
            throw new IllegalArgumentException("El profesor destino no es válido");
        }
        if (destino.getIdUsuario().equals(actor.getIdUsuario())) {
            throw new IllegalArgumentException("La reserva ya pertenece a ese profesor");
        }

        // Una sola solicitud pendiente por reserva: la anterior queda cancelada
        for (MTransferencia previa : transferenciaRepo.findByReserva_IdReservaAndEstado(idReserva, "PENDIENTE")) {
            previa.setEstado("CANCELADA");
            transferenciaRepo.save(previa);
        }

        String texto = motivo == null ? null : motivo.trim();
        if (texto != null && texto.length() > 255) {
            texto = texto.substring(0, 255);
        }
        if (texto != null && texto.isEmpty()) {
            texto = null;
        }

        MTransferencia solicitud = new MTransferencia(LocalDateTime.now(), texto, reserva, destino);
        solicitud.setProfesorOrigen(actor);
        return transferenciaRepo.save(solicitud);
    }
}
