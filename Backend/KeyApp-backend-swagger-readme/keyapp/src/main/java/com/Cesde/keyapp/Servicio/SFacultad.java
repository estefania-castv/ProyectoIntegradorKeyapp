package com.Cesde.keyapp.Servicio;

import com.Cesde.keyapp.Modelo.MFacultad;
import com.Cesde.keyapp.Repositorio.IFacultad;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SFacultad {

    @Autowired
    private IFacultad iFacultad;

    public List<MFacultad> listar() {
        return iFacultad.findAll();
    }

    public Optional<MFacultad> buscarPorId(Integer id) {
        return iFacultad.findById(id);
    }

    public MFacultad guardar(MFacultad facultad) {
        return iFacultad.save(facultad);
    }

    public void eliminar(Integer id) {
        iFacultad.deleteById(id);
    }
}
