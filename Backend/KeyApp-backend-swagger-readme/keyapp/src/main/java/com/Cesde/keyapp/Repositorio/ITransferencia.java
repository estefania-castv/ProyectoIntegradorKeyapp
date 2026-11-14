package com.Cesde.keyapp.Repositorio;

import com.Cesde.keyapp.Modelo.MTransferencia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ITransferencia extends JpaRepository<MTransferencia, Integer> {

    List<MTransferencia> findByProfesorDestino_IdUsuarioOrderByFechaTransferenciaDesc(Integer idUsuario);

    List<MTransferencia> findByProfesorOrigen_IdUsuarioOrderByFechaTransferenciaDesc(Integer idUsuario);

    List<MTransferencia> findByReserva_IdReservaAndEstado(Integer idReserva, String estado);

    // Las solicitudes pendientes de reservas que ya no están vigentes (terminaron o se cancelaron) expiran
    @Modifying(clearAutomatically = true)
    @Query("UPDATE MTransferencia t SET t.estado = 'EXPIRADA' " +
            "WHERE t.estado = 'PENDIENTE' " +
            "AND t.reserva.idReserva IN (SELECT r.idReserva FROM MReserva r " +
            "WHERE r.estado NOT IN ('ACTIVA', 'TRANSFERIDA'))")
    int expirarPendientesDeReservasNoVigentes();
}
