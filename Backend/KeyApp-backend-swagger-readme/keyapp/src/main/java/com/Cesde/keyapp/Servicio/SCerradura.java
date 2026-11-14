package com.Cesde.keyapp.Servicio;

import com.Cesde.keyapp.Modelo.MCerradura;
import com.Cesde.keyapp.Repositorio.ICerradura;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SCerradura {

    @Autowired
    private ICerradura iCerradura;

    public List<MCerradura> listar() {
        return iCerradura.findAll();
    }

    public Optional<MCerradura> buscarPorId(Integer id) {
        return iCerradura.findById(id);
    }

    public MCerradura guardar(MCerradura cerradura) {
        return iCerradura.save(cerradura);
    }

    public void eliminar(Integer id) {
        iCerradura.deleteById(id);
    }
}
