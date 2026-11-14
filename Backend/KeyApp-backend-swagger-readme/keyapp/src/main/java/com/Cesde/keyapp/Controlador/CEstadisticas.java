package com.Cesde.keyapp.Controlador;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Tag(name = "Estadísticas con Python", description = "Genera estadísticas de reservas y aulas ejecutando el script Reportes-python/reportes.py.")
@RestController
@RequestMapping("/api/estadisticas")
public class CEstadisticas {

    @Value("${app.python.ejecutable:python}")
    private String ejecutablePython;

    @Value("${app.python.api-url:http://localhost:8080/api}")
    private String urlApi;

    private final ObjectMapper objectMapper = new ObjectMapper();

    // Genera las estadisticas con Python. La seguridad de Spring exige ADMINISTRADOR
    // para este endpoint y el JWT se reenvia a las consultas internas de la API.
    @Operation(summary = "Generar estadísticas de reservas", description = "Solo ADMINISTRADOR. Ejecuta Python (requests, pandas, numpy, matplotlib) y devuelve el resumen en JSON.")
    @GetMapping("/reservas")
    public ResponseEntity<?> generar(@RequestHeader(value = "Authorization", required = false) String autorizacion) {
        if (autorizacion == null || !autorizacion.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("mensaje", "No se recibio el token de autenticacion."));
        }

        Path carpetaProyecto = Path.of("").toAbsolutePath();
        Path script = carpetaProyecto.resolve("Reportes-python").resolve("reportes.py");

        if (!Files.exists(script)) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("mensaje", "No se encontro el script Reportes-python/reportes.py."));
        }

        Path salida = carpetaProyecto.resolve("Reportes-python").resolve("Reportes");
        Path archivoSalida = null;
        Path archivoError = null;

        try {
            Files.createDirectories(salida);
            archivoSalida = Files.createTempFile("keyapp-estadisticas-", ".json");
            archivoError = Files.createTempFile("keyapp-estadisticas-error-", ".txt");

            ProcessBuilder proceso = new ProcessBuilder(ejecutablePython, script.toString());
            proceso.directory(carpetaProyecto.toFile());
            proceso.environment().put("KEYAPP_TOKEN", autorizacion.substring("Bearer ".length()));
            proceso.environment().put("KEYAPP_API_URL", urlApi);
            proceso.environment().put("KEYAPP_REPORTES_SALIDA", salida.toString());
            proceso.redirectOutput(archivoSalida.toFile());
            proceso.redirectError(archivoError.toFile());

            Process ejecutando = proceso.start();
            boolean termino = ejecutando.waitFor(90, TimeUnit.SECONDS);

            if (!termino) {
                ejecutando.destroyForcibly();
                return ResponseEntity.status(HttpStatus.GATEWAY_TIMEOUT)
                        .body(Map.of("mensaje", "La generacion de reportes supero el tiempo permitido."));
            }

            String contenido = Files.readString(archivoSalida, StandardCharsets.UTF_8).trim();
            if (ejecutando.exitValue() != 0) {
                String detalle = Files.readString(archivoError, StandardCharsets.UTF_8).trim();
                return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(Map.of(
                        "mensaje", "Python no pudo generar las estadisticas.",
                        "detalle", detalle.isBlank() ? contenido : detalle
                ));
            }

            // El script imprime primero el resumen de consola del ejemplo y al final el JSON.
            int inicioJson = contenido.lastIndexOf("\n{");
            if (inicioJson >= 0) {
                contenido = contenido.substring(inicioJson + 1);
            } else if (!contenido.startsWith("{")) {
                int primeraLlave = contenido.indexOf('{');
                if (primeraLlave >= 0) contenido = contenido.substring(primeraLlave);
            }

            JsonNode resultado = objectMapper.readTree(contenido);
            return ResponseEntity.ok(resultado);

        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("mensaje", "No se pudo ejecutar Python. Verifica su instalacion y las librerias.",
                            "detalle", e.getMessage()));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("mensaje", "La generacion de reportes fue interrumpida."));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("mensaje", "Ocurrio un error al procesar los reportes.",
                            "detalle", e.getMessage() == null ? "Error inesperado" : e.getMessage()));
        } finally {
            try {
                if (archivoSalida != null) Files.deleteIfExists(archivoSalida);
                if (archivoError != null) Files.deleteIfExists(archivoError);
            } catch (IOException ignored) {
                // Los archivos temporales se eliminan cuando sea posible.
            }
        }
    }
}
