package com.Cesde.keyapp.Servicio;

import com.Cesde.keyapp.Modelo.MUsuario;
import com.Cesde.keyapp.Repositorio.IUsuario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class SUsuario {

    @Autowired
    private IUsuario iUsuario;

    public List<MUsuario> listar() {
        return iUsuario.findAll();
    }

    public Optional<MUsuario> buscarPorId(Integer id) {
        return iUsuario.findById(id);
    }

    public Optional<MUsuario> buscarPorEmail(String email) {
        return iUsuario.findByEmail(email);
    }

    public MUsuario guardar(MUsuario usuario) {
        return iUsuario.save(usuario);
    }

    public void eliminar(Integer id) {
        iUsuario.deleteById(id);
    }

    // El administrador cambia el rol de otro usuario (PROFESOR <-> ADMINISTRADOR).
    // No puede cambiar el suyo, para no quedarse sin ningún administrador por accidente.
    public MUsuario cambiarRol(Integer id, String rol, Integer idActor) {
        String nuevo = rol == null ? "" : rol.trim().toUpperCase();
        if (!nuevo.equals("PROFESOR") && !nuevo.equals("ADMINISTRADOR")) {
            throw new IllegalArgumentException("Rol no válido. Usa PROFESOR o ADMINISTRADOR");
        }
        if (id.equals(idActor)) {
            throw new IllegalArgumentException("No puedes cambiar tu propio rol");
        }
        MUsuario usuario = iUsuario.findById(id)
                .orElseThrow(() -> new NoSuchElementException("El usuario no existe"));
        usuario.setRol(nuevo);
        return iUsuario.save(usuario);
    }
}
