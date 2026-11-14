package com.Cesde.keyapp.Repositorio;

import com.Cesde.keyapp.Modelo.MNotificacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface INotificacion extends JpaRepository<MNotificacion, Integer> {
}
