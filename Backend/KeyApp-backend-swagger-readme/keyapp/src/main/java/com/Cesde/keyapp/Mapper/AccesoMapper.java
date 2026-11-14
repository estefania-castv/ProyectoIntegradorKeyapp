package com.Cesde.keyapp.Mapper;

import com.Cesde.keyapp.Dto.AbrirResponse;

/** Centraliza la construcción de la respuesta de apertura simulada. */
public class AccesoMapper {
    private AccesoMapper() {}

    public static AbrirResponse toResponse(boolean abierta, String mensaje) {
        return new AbrirResponse(abierta, mensaje);
    }
}
