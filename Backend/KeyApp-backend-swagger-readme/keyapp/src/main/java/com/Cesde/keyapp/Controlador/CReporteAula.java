package com.Cesde.keyapp.Controlador;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import com.Cesde.keyapp.Dto.ReporteAulaRequest;
import com.Cesde.keyapp.Mapper.ReporteAulaMapper;
import com.Cesde.keyapp.Seguridad.UsuarioAutenticado;
import com.Cesde.keyapp.Servicio.SReporteAula;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

// Reportes (novedades) de las aulas: los profesores informan y el administrador lee y marca como resueltos.
@Tag(name = "Reportes (novedades) de aulas", description = "Los usuarios informan novedades de un aula y el administrador las lee y resuelve.")
@RestController
@RequestMapping("/api/reportes")
public class CReporteAula {

    private final SReporteAula sReporte;

    public CReporteAula(SReporteAula sReporte) {
        this.sReporte = sReporte;
    }

    // Administrador: todos. Profesor: solo los suyos.
    @Operation(summary = "Listar reportes de aulas", description = "Administrador: todos. Profesor: solo los suyos.")
    @GetMapping
    public List<?> listar(@AuthenticationPrincipal UsuarioAutenticado usuario) {
        return sReporte.listar(usuario).stream().map(ReporteAulaMapper::toResponse).toList();
    }

    @Operation(summary = "Crear un reporte de aula", description = "Registra una novedad (EQUIPOS, MOBILIARIO, ENERGIA, RED, LIMPIEZA, OTRO). El autor sale del token.")
    @PostMapping
    public ResponseEntity<?> crear(@RequestBody ReporteAulaRequest req,
                                   @AuthenticationPrincipal UsuarioAutenticado usuario) {
        try {
            var guardado = sReporte.crear(req.idAula(), usuario.idUsuario(), req.tipo(), req.descripcion());
            return ResponseEntity.status(HttpStatus.CREATED).body(ReporteAulaMapper.toResponse(guardado));
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("mensaje", e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("mensaje", e.getMessage()));
        }
    }

    // Solo ADMINISTRADOR (lo exige ConfiguracionSeguridad)
    @Operation(summary = "Resolver un reporte de aula", description = "Solo ADMINISTRADOR. Marca el reporte como RESUELTO.")
    @PutMapping("/{id}/resolver")
    public ResponseEntity<?> resolver(@PathVariable Integer id) {
        try {
            return ResponseEntity.ok(ReporteAulaMapper.toResponse(sReporte.resolver(id)));
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("mensaje", e.getMessage()));
        }
    }
}
