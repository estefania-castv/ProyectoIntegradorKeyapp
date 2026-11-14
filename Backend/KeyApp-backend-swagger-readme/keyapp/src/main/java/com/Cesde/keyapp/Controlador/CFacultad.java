package com.Cesde.keyapp.Controlador;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import com.Cesde.keyapp.Modelo.MFacultad;
import com.Cesde.keyapp.Servicio.SFacultad;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Gestión de facultades", description = "Servicios CRUD de las facultades.")
@RestController
@RequestMapping("/api/facultades")
public class CFacultad {

    @Autowired
    private SFacultad sFacultad;

    @Operation(summary = "Listar facultades", description = "Devuelve todos los registros de facultades.")
    @GetMapping
    public List<MFacultad> listar() {
        return sFacultad.listar();
    }

    @Operation(summary = "Consultar una facultad por llave primaria", description = "Devuelve una facultad según su identificador; 404 si no existe.")
    @GetMapping("/{id}")
    public ResponseEntity<MFacultad> buscarPorId(@PathVariable Integer id) {
        return sFacultad.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Adicionar una facultad", description = "Crea un nuevo registro de una facultad.")
    @PostMapping
    public ResponseEntity<MFacultad> crear(@RequestBody MFacultad facultad) {
        return ResponseEntity.status(HttpStatus.CREATED).body(sFacultad.guardar(facultad));
    }

    @Operation(summary = "Actualizar una facultad", description = "Modifica los datos de una facultad existente; 404 si no existe.")
    @PutMapping("/{id}")
    public ResponseEntity<MFacultad> actualizar(@PathVariable Integer id, @RequestBody MFacultad facultad) {
        if (sFacultad.buscarPorId(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        facultad.setIdFacultad(id);
        return ResponseEntity.ok(sFacultad.guardar(facultad));
    }

    @Operation(summary = "Eliminar una facultad", description = "Elimina una facultad por su identificador.")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        sFacultad.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
