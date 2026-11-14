package com.Cesde.keyapp.Seguridad;

import com.Cesde.keyapp.Modelo.MUsuario;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

// Prueba unitaria sencilla: no necesita base de datos ni levantar Spring.
class JwtServicioTest {

    private static final String SECRETO = "clave-de-prueba-con-mas-de-32-caracteres-0123456789";

    private MUsuario usuario(String rol) {
        MUsuario u = new MUsuario();
        u.setIdUsuario(7);
        u.setEmail("prueba@keyapp.com");
        u.setRol(rol);
        return u;
    }

    @Test
    void generaYLeeElTokenConLosDatosDelUsuario() {
        JwtServicio jwt = new JwtServicio(SECRETO, 60_000);

        UsuarioAutenticado leido = jwt.leer(jwt.generar(usuario("ADMINISTRADOR")));

        assertEquals(7, leido.idUsuario());
        assertEquals("prueba@keyapp.com", leido.email());
        assertEquals("ADMINISTRADOR", leido.rol());
        assertTrue(leido.esAdmin());
    }

    @Test
    void elProfesorNoEsAdministrador() {
        JwtServicio jwt = new JwtServicio(SECRETO, 60_000);

        UsuarioAutenticado leido = jwt.leer(jwt.generar(usuario("PROFESOR")));

        assertFalse(leido.esAdmin());
    }

    @Test
    void rechazaUnSecretoMuyCorto() {
        assertThrows(IllegalStateException.class, () -> new JwtServicio("corto", 60_000));
    }
}
