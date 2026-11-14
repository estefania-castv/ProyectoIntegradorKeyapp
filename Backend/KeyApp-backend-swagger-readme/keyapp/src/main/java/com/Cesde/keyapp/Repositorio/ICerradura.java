package com.Cesde.keyapp.Repositorio;

import com.Cesde.keyapp.Modelo.MCerradura;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ICerradura extends JpaRepository<MCerradura, Integer> {
}
