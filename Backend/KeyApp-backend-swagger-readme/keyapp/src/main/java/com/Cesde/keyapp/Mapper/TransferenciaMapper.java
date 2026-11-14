package com.Cesde.keyapp.Mapper;

import com.Cesde.keyapp.Modelo.MTransferencia;
import java.util.Map;

/** Construye la respuesta pública de una transferencia sin exponer toda la entidad. */
public class TransferenciaMapper {
    private TransferenciaMapper() {}

    public static Map<String, String> toResponse(MTransferencia transferencia) {
        if (transferencia == null) throw new IllegalArgumentException("La transferencia es obligatoria");
        String nombre = transferencia.getProfesorDestino() == null
                ? "el profesor destino" : transferencia.getProfesorDestino().getNombre();
        return Map.of("mensaje", "Solicitud enviada a " + nombre + ". La reserva cambia de dueño cuando la acepte");
    }
}
