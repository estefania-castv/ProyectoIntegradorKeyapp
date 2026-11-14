package com.Cesde.keyapp.Repositorio;

import com.Cesde.keyapp.Modelo.MUsuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IUsuario extends JpaRepository<MUsuario, Integer> {

    // Metodo derivado: Spring Data JPA genera la consulta a partir del nombre
    Optional<MUsuario> findByEmail(String email);
}
