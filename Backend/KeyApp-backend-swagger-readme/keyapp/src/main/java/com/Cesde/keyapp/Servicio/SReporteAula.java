package com.Cesde.keyapp.Servicio;

import com.Cesde.keyapp.Modelo.MAula;
import com.Cesde.keyapp.Modelo.MReporteAula;
import com.Cesde.keyapp.Modelo.MUsuario;
import com.Cesde.keyapp.Repositorio.IAula;
import com.Cesde.keyapp.Repositorio.IReporteAula;
import com.Cesde.keyapp.Repositorio.IUsuario;
import com.Cesde.keyapp.Seguridad.UsuarioAutenticado;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

@Service
public class SReporteAula {

    private static final Set<String> TIPOS = Set.of("EQUIPOS", "MOBILIARIO", "ENERGIA", "RED", "LIMPIEZA", "OTRO");
    private static final int MAX_CARACTERES = 500;

    private final IReporteAula reporteRepo;
    private final IAula aulaRepo;
    private final IUsuario usuarioRepo;

    public SReporteAula(IReporteAula reporteRepo, IAula aulaRepo, IUsuario usuarioRepo) {
        this.reporteRepo = reporteRepo;
        this.aulaRepo = aulaRepo;
        this.usuarioRepo = usuarioRepo;
    }

    // El administrador lee todos los reportes; cada profesor solo los suyos.
    public List<MReporteAula> listar(UsuarioAutenticado usuario) {
        return usuario.esAdmin()
                ? reporteRepo.findAllByOrderByFechaDesc()
                : reporteRepo.findByProfesor_IdUsuarioOrderByFechaDesc(usuario.idUsuario());
    }

    // El autor sale del token, nunca del cuerpo de la petición.
    public MReporteAula crear(Integer idAula, Integer idAutor, String tipo, String descripcion) {
        String tipoLimpio = tipo == null ? "" : tipo.trim().toUpperCase();
        if (!TIPOS.contains(tipoLimpio)) {
            throw new IllegalArgumentException("Tipo de reporte no válido");
        }
        if (descripcion == null || descripcion.isBlank()) {
            throw new IllegalArgumentException("Describe qué está pasando en el aula");
        }
        String texto = descripcion.trim();
        if (texto.length() > MAX_CARACTERES) {
            throw new IllegalArgumentException("La descripción no puede pasar de " + MAX_CARACTERES + " caracteres");
        }
        if (idAula == null) {
            throw new IllegalArgumentException("Elige el aula");
        }
        MAula aula = aulaRepo.findById(idAula)
                .orElseThrow(() -> new NoSuchElementException("El aula no existe"));
        MUsuario autor = usuarioRepo.findById(idAutor)
                .orElseThrow(() -> new NoSuchElementException("Usuario no válido"));
        return reporteRepo.save(new MReporteAula(tipoLimpio, texto, "ABIERTO", LocalDateTime.now(), aula, autor));
    }

    public MReporteAula resolver(Integer id) {
        MReporteAula reporte = reporteRepo.findById(id)
                .orElseThrow(() -> new NoSuchElementException("El reporte no existe"));
        reporte.setEstado("RESUELTO");
        reporte.setFechaResolucion(LocalDateTime.now());
        return reporteRepo.save(reporte);
    }
}
