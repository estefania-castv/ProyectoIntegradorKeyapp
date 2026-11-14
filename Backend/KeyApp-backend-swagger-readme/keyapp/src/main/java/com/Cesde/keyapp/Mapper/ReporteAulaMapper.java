package com.Cesde.keyapp.Mapper;

import com.Cesde.keyapp.Dto.ReporteAulaResponse;
import com.Cesde.keyapp.Modelo.MReporteAula;

public class ReporteAulaMapper {
    private ReporteAulaMapper() {}

    public static ReporteAulaResponse toResponse(MReporteAula r) {
        return new ReporteAulaResponse(
                r.getIdReporte(),
                r.getAula().getIdAula(),
                r.getAula().getNombre(),
                r.getTipo(),
                r.getDescripcion(),
                r.getEstado(),
                r.getFecha(),
                r.getFechaResolucion(),
                r.getProfesor().getIdUsuario(),
                r.getProfesor().getNombre());
    }
}
