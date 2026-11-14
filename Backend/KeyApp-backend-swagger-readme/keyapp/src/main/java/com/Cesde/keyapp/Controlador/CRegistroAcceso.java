package com.Cesde.keyapp.Controlador;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import com.Cesde.keyapp.Modelo.MRegistroAcceso;
import com.Cesde.keyapp.Servicio.SRegistroAcceso;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Registros de acceso", description = "Historial de aperturas y eventos de las cerraduras.")
@RestController
@RequestMapping("/api/registros-acceso")
public class CRegistroAcceso {

    @Autowired
    private SRegistroAcceso sRegistroAcceso;

    @Operation(summary = "Listar registros de acceso", description = "Devuelve todos los registros de acceso.")
    @GetMapping
    public List<MRegistroAcceso> listar() {
        return sRegistroAcceso.listar();
    }

    @Operation(summary = "Consultar un registro de acceso por llave primaria", description = "Devuelve el registro según su identificador; 404 si no existe.")
    @GetMapping("/{id}")
    public ResponseEntity<MRegistroAcceso> buscarPorId(@PathVariable Integer id) {
        return sRegistroAcceso.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Endpoint principal de KeyApp: el profesor pide abrir el aula desde la app
    @Operation(summary = "Solicitar la apertura de una cerradura", description = "Registra la solicitud de apertura a partir de la reserva y la cerradura indicadas.")
    @PostMapping("/abrir")
    public ResponseEntity<MRegistroAcceso> abrir(@RequestParam Integer idReserva,
                                                  @RequestParam Integer idCerradura) {
        return ResponseEntity.ok(sRegistroAcceso.solicitarApertura(idReserva, idCerradura));
    }

    @Operation(summary = "Eliminar un registro de acceso", description = "Elimina el registro por su identificador.")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        sRegistroAcceso.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
