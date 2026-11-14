package com.Cesde.keyapp.Seguridad;

// Datos del usuario que viajan dentro del token JWT.
public record UsuarioAutenticado(Integer idUsuario, String email, String rol) {

    public boolean esAdmin() {
        return "ADMINISTRADOR".equals(rol);
    }
}
