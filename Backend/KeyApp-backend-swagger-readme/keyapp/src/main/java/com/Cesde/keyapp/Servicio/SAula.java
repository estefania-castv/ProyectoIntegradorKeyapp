package com.Cesde.keyapp.Servicio;

import com.Cesde.keyapp.Modelo.MAula;
import com.Cesde.keyapp.Repositorio.IAula;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;

@Service
public class SAula {

    // DISPONIBLE y RESERVADA (esta última se calcula, no se guarda) son los estados normales;
    // el resto los pone el administrador cuando el aula no se puede usar.
    private static final Set<String> ESTADOS = Set.of(
            "DISPONIBLE", "MANTENIMIENTO", "SIN_ENERGIA", "PROBLEMAS_DE_RED", "FUERA_DE_SERVICIO", "INACTIVA");

    @Autowired
    private IAula iAula;

    public List<MAula> listar() {
        return iAula.findAll();
    }

    public Optional<MAula> buscarPorId(Integer id) {
        return iAula.findById(id);
    }

    public MAula guardar(MAula aula) {
        return iAula.save(aula);
    }

    public void eliminar(Integer id) {
        iAula.deleteById(id);
    }

    // Crea un aula nueva validando los datos (estado por defecto: DISPONIBLE).
    public MAula crear(MAula aula) {
        if (aula.getNombre() == null || aula.getNombre().isBlank()) {
            throw new IllegalArgumentException("El aula necesita un nombre");
        }
        if (aula.getCapacidad() == null || aula.getCapacidad() <= 0) {
            throw new IllegalArgumentException("La capacidad debe ser mayor que cero");
        }
        if (aula.getFacultad() == null || aula.getFacultad().getIdFacultad() == null) {
            throw new IllegalArgumentException("Elige la facultad del aula");
        }
        String estado = aula.getEstado() == null || aula.getEstado().isBlank()
                ? "DISPONIBLE" : aula.getEstado().trim().toUpperCase();
        if (!ESTADOS.contains(estado)) {
            throw new IllegalArgumentException("Estado no válido para el aula");
        }
        aula.setIdAula(null);
        aula.setNombre(aula.getNombre().trim());
        aula.setEstado(estado);
        return iAula.save(aula);
    }

    // Cambia solo el estado del aula.
    public MAula cambiarEstado(Integer id, String estado) {
        String nuevo = estado == null ? "" : estado.trim().toUpperCase();
        if (!ESTADOS.contains(nuevo)) {
            throw new IllegalArgumentException("Estado no válido para el aula");
        }
        MAula aula = iAula.findById(id)
                .orElseThrow(() -> new NoSuchElementException("El aula no existe"));
        aula.setEstado(nuevo);
        return iAula.save(aula);
    }
}
