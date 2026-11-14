package com.Cesde.keyapp.Mapper;

import com.Cesde.keyapp.Dto.TransferenciaDetalleResponse;
import com.Cesde.keyapp.Modelo.MTransferencia;

public class TransferenciaDetalleMapper {
    private TransferenciaDetalleMapper() {}

    public static TransferenciaDetalleResponse toResponse(MTransferencia t) {
        var reserva = t.getReserva();
        var origen = t.getProfesorOrigen();
        var destino = t.getProfesorDestino();
        return new TransferenciaDetalleResponse(
                t.getIdTransferencia(),
                reserva.getIdReserva(),
                reserva.getAula().getNombre(),
                reserva.getFecha(),
                reserva.getHoraInicio(),
                reserva.getHoraFin(),
                origen == null ? null : origen.getIdUsuario(),
                origen == null ? null : origen.getNombre(),
                destino.getIdUsuario(),
                destino.getNombre(),
                t.getMotivo(),
                t.getEstado(),
                t.getFechaTransferencia(),
                t.getFechaRespuesta());
    }
}
