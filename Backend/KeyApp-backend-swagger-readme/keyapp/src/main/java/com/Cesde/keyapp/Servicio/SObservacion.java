package com.Cesde.keyapp.Servicio;

import com.Cesde.keyapp.Modelo.MObservacion;
import com.Cesde.keyapp.Repositorio.IObservacion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class SObservacion {

    @Autowired
    private IObservacion iObservacion;

    public List<MObservacion> listar() {
        return iObservacion.findAll();
    }

    public Optional<MObservacion> buscarPorId(Integer id) {
        return iObservacion.findById(id);
    }

    public MObservacion registrar(MObservacion observacion) {
        observacion.setEstado("ABIERTA");
        observacion.setFecha(LocalDateTime.now());
        return iObservacion.save(observacion);
    }

    public void resolver(Integer id) {
        iObservacion.findById(id).ifPresent(obs -> {
            obs.setEstado("RESUELTA");
            iObservacion.save(obs);
        });
    }

    public void eliminar(Integer id) {
        iObservacion.deleteById(id);
    }
}
