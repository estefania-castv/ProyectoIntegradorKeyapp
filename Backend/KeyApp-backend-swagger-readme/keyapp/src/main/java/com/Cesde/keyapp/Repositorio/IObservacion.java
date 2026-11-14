package com.Cesde.keyapp.Repositorio;

import com.Cesde.keyapp.Modelo.MObservacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IObservacion extends JpaRepository<MObservacion, Integer> {
}
