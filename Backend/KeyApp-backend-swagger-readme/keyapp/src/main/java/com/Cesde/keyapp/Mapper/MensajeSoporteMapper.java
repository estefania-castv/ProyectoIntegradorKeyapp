package com.Cesde.keyapp.Mapper;

import com.Cesde.keyapp.Dto.MensajeSoporteResponse;
import com.Cesde.keyapp.Modelo.MMensajeSoporte;

public class MensajeSoporteMapper {
    private MensajeSoporteMapper() {}

    public static MensajeSoporteResponse toResponse(MMensajeSoporte m) {
        return new MensajeSoporteResponse(
                m.getIdMensaje(),
                m.getTexto(),
                m.getFecha(),
                m.getAutor().getIdUsuario(),
                m.getAutor().getNombre(),
                m.getAutor().getRol());
    }
}
