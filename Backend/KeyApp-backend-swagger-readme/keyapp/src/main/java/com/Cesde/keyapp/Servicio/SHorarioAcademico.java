package com.Cesde.keyapp.Servicio;

import com.Cesde.keyapp.Modelo.MHorarioAcademico;
import com.Cesde.keyapp.Repositorio.IHorarioAcademico;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SHorarioAcademico {

    @Autowired
    private IHorarioAcademico iHorarioAcademico;

    public List<MHorarioAcademico> listar() {
        return iHorarioAcademico.findAll();
    }

    public Optional<MHorarioAcademico> buscarPorId(Integer id) {
        return iHorarioAcademico.findById(id);
    }

    public MHorarioAcademico guardar(MHorarioAcademico horario) {
        return iHorarioAcademico.save(horario);
    }

    public void eliminar(Integer id) {
        iHorarioAcademico.deleteById(id);
    }
}
