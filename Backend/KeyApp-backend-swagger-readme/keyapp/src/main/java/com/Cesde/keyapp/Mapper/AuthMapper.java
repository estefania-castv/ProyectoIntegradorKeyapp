package com.Cesde.keyapp.Mapper;

import com.Cesde.keyapp.Dto.LoginResponse;
import com.Cesde.keyapp.Modelo.MUsuario;

/** Convierte entidades de autenticación a DTO sin exponer passwordHash. */
public class AuthMapper {
    private AuthMapper() {}

    public static LoginResponse toLoginResponse(MUsuario usuario, String token) {
        if (usuario == null) throw new IllegalArgumentException("El usuario es obligatorio");
        return new LoginResponse(usuario.getIdUsuario(), usuario.getNombre(),
                usuario.getEmail(), usuario.getRol(), token);
    }
}
