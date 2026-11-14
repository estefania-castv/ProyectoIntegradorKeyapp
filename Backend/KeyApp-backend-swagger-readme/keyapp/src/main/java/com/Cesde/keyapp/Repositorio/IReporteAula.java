package com.Cesde.keyapp.Repositorio;

import com.Cesde.keyapp.Modelo.MReporteAula;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IReporteAula extends JpaRepository<MReporteAula, Integer> {

    List<MReporteAula> findAllByOrderByFechaDesc();

    List<MReporteAula> findByProfesor_IdUsuarioOrderByFechaDesc(Integer idUsuario);
}
