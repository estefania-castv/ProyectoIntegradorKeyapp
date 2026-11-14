package com.Cesde.keyapp.Servicio;

import com.Cesde.keyapp.Repositorio.IReserva;
import com.Cesde.keyapp.Repositorio.ITransferencia;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

// Cada minuto libera las aulas: las reservas cuya hora de fin ya pasó quedan FINALIZADA,
// y las solicitudes de transferencia pendientes de reservas que ya no están vigentes expiran.
@Service
public class SCierreReservas {

    private final IReserva reservaRepo;
    private final ITransferencia transferenciaRepo;

    public SCierreReservas(IReserva reservaRepo, ITransferencia transferenciaRepo) {
        this.reservaRepo = reservaRepo;
        this.transferenciaRepo = transferenciaRepo;
    }

    @Scheduled(fixedRate = 60_000, initialDelay = 5_000)
    @Transactional
    public void cerrarVencidas() {
        LocalDateTime ahora = LocalDateTime.now();
        reservaRepo.finalizarVencidas(ahora.toLocalDate(), ahora.toLocalTime());
        transferenciaRepo.expirarPendientesDeReservasNoVigentes();
    }
}
