package com.Cesde.keyapp.Controlador;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import com.Cesde.keyapp.Modelo.MCerradura;
import com.Cesde.keyapp.Servicio.SCerradura;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Gestión de cerraduras", description = "Servicios CRUD de las cerraduras inteligentes de las aulas.")
@RestController
@RequestMapping("/api/cerraduras")
public class CCerradura {

    @Autowired
    private SCerradura sCerradura;

    @Operation(summary = "Listar cerraduras", description = "Devuelve todos los registros de cerraduras.")
    @GetMapping
    public List<MCerradura> listar() {
        return sCerradura.listar();
    }

    @Operation(summary = "Consultar una cerradura por llave primaria", description = "Devuelve una cerradura según su identificador; 404 si no existe.")
    @GetMapping("/{id}")
    public ResponseEntity<MCerradura> buscarPorId(@PathVariable Integer id) {
        return sCerradura.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Adicionar una cerradura", description = "Crea un nuevo registro de una cerradura.")
    @PostMapping
    public ResponseEntity<MCerradura> crear(@RequestBody MCerradura cerradura) {
        return ResponseEntity.status(HttpStatus.CREATED).body(sCerradura.guardar(cerradura));
    }

    @Operation(summary = "Actualizar una cerradura", description = "Modifica los datos de una cerradura existente; 404 si no existe.")
    @PutMapping("/{id}")
    public ResponseEntity<MCerradura> actualizar(@PathVariable Integer id, @RequestBody MCerradura cerradura) {
        if (sCerradura.buscarPorId(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        cerradura.setIdCerradura(id);
        return ResponseEntity.ok(sCerradura.guardar(cerradura));
    }

    @Operation(summary = "Eliminar una cerradura", description = "Elimina una cerradura por su identificador.")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        sCerradura.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
