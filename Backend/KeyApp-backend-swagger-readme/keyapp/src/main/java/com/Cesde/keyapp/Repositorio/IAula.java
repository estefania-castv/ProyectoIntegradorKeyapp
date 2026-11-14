package com.Cesde.keyapp.Repositorio;

import com.Cesde.keyapp.Modelo.MAula;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IAula extends JpaRepository<MAula, Integer> {
}
