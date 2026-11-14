package com.Cesde.keyapp.Repositorio;

import com.Cesde.keyapp.Modelo.MMensajeSoporte;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IMensajeSoporte extends JpaRepository<MMensajeSoporte, Integer> {

    // Conversación de un usuario, del mensaje más antiguo al más reciente
    List<MMensajeSoporte> findByUsuario_IdUsuarioOrderByFechaAsc(Integer idUsuario);

    // Todos los mensajes, del más reciente al más antiguo (para armar la lista de conversaciones)
    List<MMensajeSoporte> findAllByOrderByFechaDesc();
}
