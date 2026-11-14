package com.Cesde.keyapp.Repositorio;

import com.Cesde.keyapp.Modelo.MHorarioAcademico;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalTime;

@Repository
public interface IHorarioAcademico extends JpaRepository<MHorarioAcademico, Integer> {

    // Cuenta las clases programadas del aula, ese dia de la semana,
    // cuyo horario se cruza con el pedido.
    @Query("SELECT COUNT(h) FROM MHorarioAcademico h " +
            "WHERE h.aula.idAula = :idAula " +
            "AND h.diaSemana = :dia " +
            "AND h.horaInicio < :horaFin " +
            "AND h.horaFin > :horaInicio")
    long contarTraslapes(@Param("idAula") Integer idAula,
                         @Param("dia") String dia,
                         @Param("horaInicio") LocalTime horaInicio,
                         @Param("horaFin") LocalTime horaFin);
}