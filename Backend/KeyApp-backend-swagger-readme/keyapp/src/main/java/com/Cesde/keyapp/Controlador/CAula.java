package com.Cesde.keyapp.Controlador;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import com.Cesde.keyapp.Dto.AulaEstadoResponse;
import com.Cesde.keyapp.Dto.CambiarEstadoAulaRequest;
import com.Cesde.keyapp.Modelo.MAula;
import com.Cesde.keyapp.Servicio.SAula;
import com.Cesde.keyapp.Servicio.SAulaEstado;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

@Tag(name = "Gestión de aulas", description = "Consulta, creación y cambio de estado de las aulas.")
@RestController
@RequestMapping("/api/aulas")
public class CAula {

    @Autowired
    private SAula sAula;

    @Autowired
    private SAulaEstado sAulaEstado;

    @Operation(summary = "Listar aulas", description = "Devuelve todos los registros de aulas.")
    @GetMapping
    public List<MAula> listar() {
        return sAula.listar();
    }

    // Cualquier usuario con sesión: estado de cada aula en este momento (DISPONIBLE, RESERVADA, MANTENIMIENTO...)
    @Operation(summary = "Estado actual de las aulas", description = "Calcula en este momento si cada aula está DISPONIBLE, RESERVADA, en MANTENIMIENTO, etc.")
    @GetMapping("/estado-actual")
    public List<AulaEstadoResponse> estadoActual() {
        return sAulaEstado.estadoActual();
    }

    // Solo ADMINISTRADOR (lo exige ConfiguracionSeguridad): cambia el estado del aula.
    @Operation(summary = "Cambiar el estado de un aula", description = "Solo ADMINISTRADOR. Estados: DISPONIBLE, MANTENIMIENTO, SIN_ENERGIA, PROBLEMAS_DE_RED, FUERA_DE_SERVICIO, INACTIVA.")
    @PutMapping("/{id}/estado")
    public ResponseEntity<?> cambiarEstado(@PathVariable Integer id, @RequestBody CambiarEstadoAulaRequest req) {
        try {
            return ResponseEntity.ok(sAula.cambiarEstado(id, req.estado()));
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("mensaje", e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("mensaje", e.getMessage()));
        }
    }

    @Operation(summary = "Consultar un aula por llave primaria", description = "Devuelve un aula según su identificador; 404 si no existe.")
    @GetMapping("/{id}")
    public ResponseEntity<MAula> buscarPorId(@PathVariable Integer id) {
        return sAula.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Solo ADMINISTRADOR (lo exige ConfiguracionSeguridad): agrega un aula nueva.
    @Operation(summary = "Adicionar un aula", description = "Solo ADMINISTRADOR. Agrega un aula nueva con validaciones.")
    @PostMapping
    public ResponseEntity<?> crear(@RequestBody MAula aula) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(sAula.crear(aula));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("mensaje", e.getMessage()));
        }
    }

    @Operation(summary = "Actualizar un aula", description = "Modifica los datos de un aula existente; 404 si no existe.")
    @PutMapping("/{id}")
    public ResponseEntity<MAula> actualizar(@PathVariable Integer id, @RequestBody MAula aula) {
        if (sAula.buscarPorId(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        aula.setIdAula(id);
        return ResponseEntity.ok(sAula.guardar(aula));
    }

    @Operation(summary = "Eliminar un aula", description = "Elimina un aula por su identificador.")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        sAula.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
