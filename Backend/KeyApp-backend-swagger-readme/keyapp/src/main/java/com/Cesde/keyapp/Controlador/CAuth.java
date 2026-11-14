package com.Cesde.keyapp.Controlador;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import com.Cesde.keyapp.Dto.LoginRequest;
import com.Cesde.keyapp.Dto.LoginResponse;
import com.Cesde.keyapp.Seguridad.JwtServicio;
import com.Cesde.keyapp.Mapper.AuthMapper;
import com.Cesde.keyapp.Servicio.SAuth;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Tag(name = "Autenticación", description = "Inicio de sesión con correo y contraseña; devuelve el token JWT.")
@RestController
@RequestMapping("/api/auth")
public class CAuth {

    private final SAuth authService;
    private final JwtServicio jwtServicio;

    public CAuth(SAuth authService, JwtServicio jwtServicio) {
        this.authService = authService;
        this.jwtServicio = jwtServicio;
    }

    @Operation(summary = "Iniciar sesión", description = "Valida las credenciales y devuelve los datos del usuario junto con el token JWT. Responde 401 si son inválidas.")
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest req) {
        try {
            return authService.autenticar(req.email(), req.password())
                    .<ResponseEntity<?>>map(u -> ResponseEntity.ok(
                            AuthMapper.toLoginResponse(u, jwtServicio.generar(u))))
                    .orElse(ResponseEntity.status(401).body(Map.of("mensaje", "Credenciales inválidas")));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("mensaje", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("mensaje", "No fue posible iniciar sesión"));
        }
    }
}
