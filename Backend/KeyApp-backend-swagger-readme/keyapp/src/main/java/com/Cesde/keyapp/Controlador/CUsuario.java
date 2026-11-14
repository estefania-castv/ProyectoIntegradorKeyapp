package com.Cesde.keyapp.Controlador;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import com.Cesde.keyapp.Dto.CambiarRolRequest;
import com.Cesde.keyapp.Modelo.MUsuario;
import com.Cesde.keyapp.Seguridad.UsuarioAutenticado;
import com.Cesde.keyapp.Servicio.SUsuario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

@Tag(name = "Gestión de usuarios", description = "Servicios CRUD de usuarios y cambio de rol.")
@RestController
@RequestMapping("/api/usuarios")
public class CUsuario {

    @Autowired
    private SUsuario sUsuario;

    @Operation(summary = "Listar usuarios", description = "Devuelve todos los registros de usuarios.")
    @GetMapping
    public List<MUsuario> listar() {
        return sUsuario.listar();
    }

    @Operation(summary = "Consultar un usuario por llave primaria", description = "Devuelve un usuario según su identificador; 404 si no existe.")
    @GetMapping("/{id}")
    public ResponseEntity<MUsuario> buscarPorId(@PathVariable Integer id) {
        return sUsuario.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Adicionar un usuario", description = "Crea un nuevo registro de un usuario.")
    @PostMapping
    public ResponseEntity<MUsuario> crear(@RequestBody MUsuario usuario) {
        return ResponseEntity.status(HttpStatus.CREATED).body(sUsuario.guardar(usuario));
    }

    @Operation(summary = "Actualizar un usuario", description = "Modifica los datos de un usuario existente; 404 si no existe.")
    @PutMapping("/{id}")
    public ResponseEntity<MUsuario> actualizar(@PathVariable Integer id, @RequestBody MUsuario usuario) {
        if (sUsuario.buscarPorId(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        usuario.setIdUsuario(id);
        return ResponseEntity.ok(sUsuario.guardar(usuario));
    }

    // Solo ADMINISTRADOR (lo exige ConfiguracionSeguridad): vuelve administrador a un profesor o al revés.
    // El usuario afectado debe volver a iniciar sesión para que el cambio le aplique.
    @Operation(summary = "Cambiar el rol de un usuario", description = "Solo ADMINISTRADOR. Convierte un PROFESOR en ADMINISTRADOR o al revés; el usuario debe volver a iniciar sesión.")
    @PutMapping("/{id}/rol")
    public ResponseEntity<?> cambiarRol(@PathVariable Integer id,
                                        @RequestBody CambiarRolRequest req,
                                        @AuthenticationPrincipal UsuarioAutenticado actor) {
        try {
            return ResponseEntity.ok(sUsuario.cambiarRol(id, req.rol(), actor.idUsuario()));
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("mensaje", e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("mensaje", e.getMessage()));
        }
    }

    @Operation(summary = "Eliminar un usuario", description = "Elimina un usuario por su identificador.")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        sUsuario.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
