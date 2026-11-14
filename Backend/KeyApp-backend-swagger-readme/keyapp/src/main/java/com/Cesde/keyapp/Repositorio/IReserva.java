package com.Cesde.keyapp.Repositorio;

import com.Cesde.keyapp.Modelo.MReserva;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Repository
public interface IReserva extends JpaRepository<MReserva, Integer> {

    // Cuenta las reservas vigentes (ACTIVA o TRANSFERIDA) del aula, en esa fecha, cuyo horario se cruza
    // con el pedido. Dos horarios se cruzan si: inicioA < finB  Y  finA > inicioB
    @Query("SELECT COUNT(r) FROM MReserva r " +
            "WHERE r.aula.idAula = :idAula " +
            "AND r.fecha = :fecha " +
            "AND r.estado IN ('ACTIVA', 'TRANSFERIDA') " +
            "AND r.horaInicio < :horaFin " +
            "AND r.horaFin > :horaInicio")
    long contarTraslapes(@Param("idAula") Integer idAula,
                         @Param("fecha") LocalDate fecha,
                         @Param("horaInicio") LocalTime horaInicio,
                         @Param("horaFin") LocalTime horaFin);

    // Reservas vigentes que están en curso en este momento
    @Query("SELECT r FROM MReserva r " +
            "WHERE r.fecha = :fecha " +
            "AND r.estado IN ('ACTIVA', 'TRANSFERIDA') " +
            "AND r.horaInicio <= :hora " +
            "AND r.horaFin > :hora")
    List<MReserva> reservasEnCurso(@Param("fecha") LocalDate fecha,
                                   @Param("hora") LocalTime hora);

    // Pasada la hora de fin, la reserva queda FINALIZADA y el aula liberada
    @Modifying(clearAutomatically = true)
    @Query("UPDATE MReserva r SET r.estado = 'FINALIZADA' " +
            "WHERE r.estado IN ('ACTIVA', 'TRANSFERIDA') " +
            "AND (r.fecha < :hoy OR (r.fecha = :hoy AND r.horaFin <= :hora))")
    int finalizarVencidas(@Param("hoy") LocalDate hoy, @Param("hora") LocalTime hora);
}
