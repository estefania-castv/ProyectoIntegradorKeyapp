package com.Cesde.keyapp.Repositorio;

import com.Cesde.keyapp.Modelo.MRegistroAcceso;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IRegistroAcceso extends JpaRepository<MRegistroAcceso, Integer> {
}
