package com.Cesde.keyapp.Controlador;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import com.Cesde.keyapp.Modelo.MHorarioAcademico;
import com.Cesde.keyapp.Servicio.SHorarioAcademico;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Gestión de horarios académicos", description = "Servicios CRUD de los horarios de clase por aula y profesor.")
@RestController
@RequestMapping("/api/horarios")
public class CHorarioAcademico {

    @Autowired
    private SHorarioAcademico sHorarioAcademico;

    @Operation(summary = "Listar horarios académicos", description = "Devuelve todos los registros de horarios académicos.")
    @GetMapping
    public List<MHorarioAcademico> listar() {
        return sHorarioAcademico.listar();
    }

    @Operation(summary = "Consultar un horario académico por llave primaria", description = "Devuelve un horario académico según su identificador; 404 si no existe.")
    @GetMapping("/{id}")
    public ResponseEntity<MHorarioAcademico> buscarPorId(@PathVariable Integer id) {
        return sHorarioAcademico.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Adicionar un horario académico", description = "Crea un nuevo registro de un horario académico.")
    @PostMapping
    public ResponseEntity<MHorarioAcademico> crear(@RequestBody MHorarioAcademico horario) {
        return ResponseEntity.status(HttpStatus.CREATED).body(sHorarioAcademico.guardar(horario));
    }

    @Operation(summary = "Actualizar un horario académico", description = "Modifica los datos de un horario académico existente; 404 si no existe.")
    @PutMapping("/{id}")
    public ResponseEntity<MHorarioAcademico> actualizar(@PathVariable Integer id, @RequestBody MHorarioAcademico horario) {
        if (sHorarioAcademico.buscarPorId(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        horario.setIdHorario(id);
        return ResponseEntity.ok(sHorarioAcademico.guardar(horario));
    }

    @Operation(summary = "Eliminar un horario académico", description = "Elimina un horario académico por su identificador.")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        sHorarioAcademico.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
