package com.Cesde.keyapp.Repositorio;

import com.Cesde.keyapp.Modelo.MFacultad;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IFacultad extends JpaRepository<MFacultad, Integer> {
}
