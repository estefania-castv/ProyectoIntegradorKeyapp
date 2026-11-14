package com.Cesde.keyapp.Servicio;

import com.Cesde.keyapp.Modelo.MReserva;
import com.Cesde.keyapp.Modelo.MTransferencia;
import com.Cesde.keyapp.Modelo.MUsuario;
import com.Cesde.keyapp.Repositorio.IReserva;
import com.Cesde.keyapp.Repositorio.ITransferencia;
import com.Cesde.keyapp.Repositorio.IUsuario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class STransferencia {

    @Autowired
    private ITransferencia iTransferencia;

    @Autowired
    private IReserva iReserva;

    @Autowired
    private IUsuario iUsuario;

    public List<MTransferencia> listar() {
        return iTransferencia.findAll();
    }

    public Optional<MTransferencia> buscarPorId(Integer id) {
        return iTransferencia.findById(id);
    }

    // Registra la transferencia y actualiza el profesor y el estado de la reserva.
    // Todo en una transaccion: si algo falla, no queda nada a medias.
    @Transactional
    public MTransferencia transferir(MTransferencia transferencia) {
        MReserva reserva = iReserva.findById(transferencia.getReserva().getIdReserva())
                .orElseThrow(() -> new IllegalArgumentException("La reserva no existe"));
        MUsuario destino = iUsuario.findById(transferencia.getProfesorDestino().getIdUsuario())
                .orElseThrow(() -> new IllegalArgumentException("El profesor destino no existe"));

        transferencia.setReserva(reserva);
        transferencia.setProfesorDestino(destino);
        transferencia.setFechaTransferencia(LocalDateTime.now());
        MTransferencia guardada = iTransferencia.save(transferencia);

        reserva.setProfesor(destino);
        reserva.setEstado("TRANSFERIDA");
        iReserva.save(reserva);

        return guardada;
    }

    public void eliminar(Integer id) {
        iTransferencia.deleteById(id);
    }
}