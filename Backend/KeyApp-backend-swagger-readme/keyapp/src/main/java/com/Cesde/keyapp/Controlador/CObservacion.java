package com.Cesde.keyapp.Controlador;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import com.Cesde.keyapp.Modelo.MObservacion;
import com.Cesde.keyapp.Servicio.SObservacion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Gestión de observaciones", description = "Observaciones que el administrador registra sobre las aulas.")
@RestController
@RequestMapping("/api/observaciones")
public class CObservacion {

    @Autowired
    private SObservacion sObservacion;

    @Operation(summary = "Listar observaciones", description = "Devuelve todas las observaciones.")
    @GetMapping
    public List<MObservacion> listar() {
        return sObservacion.listar();
    }

    @Operation(summary = "Consultar una observación por llave primaria", description = "Devuelve la observación según su identificador; 404 si no existe.")
    @GetMapping("/{id}")
    public ResponseEntity<MObservacion> buscarPorId(@PathVariable Integer id) {
        return sObservacion.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Registrar una observación", description = "Crea una observación sobre un aula.")
    @PostMapping
    public ResponseEntity<MObservacion> registrar(@RequestBody MObservacion observacion) {
        return ResponseEntity.status(HttpStatus.CREATED).body(sObservacion.registrar(observacion));
    }

    @Operation(summary = "Resolver una observación", description = "Marca la observación como RESUELTA.")
    @PutMapping("/{id}/resolver")
    public ResponseEntity<Void> resolver(@PathVariable Integer id) {
        sObservacion.resolver(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Eliminar una observación", description = "Elimina la observación por su identificador.")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        sObservacion.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
