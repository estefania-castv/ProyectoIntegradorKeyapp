package com.Cesde.keyapp.Servicio;

import com.Cesde.keyapp.Modelo.MCerradura;
import com.Cesde.keyapp.Modelo.MRegistroAcceso;
import com.Cesde.keyapp.Modelo.MReserva;
import com.Cesde.keyapp.Repositorio.ICerradura;
import com.Cesde.keyapp.Repositorio.IRegistroAcceso;
import com.Cesde.keyapp.Repositorio.IReserva;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Service
public class SRegistroAcceso {

    @Autowired
    private IRegistroAcceso iRegistroAcceso;

    @Autowired
    private IReserva iReserva;

    @Autowired
    private ICerradura iCerradura;

    public List<MRegistroAcceso> listar() {
        return iRegistroAcceso.findAll();
    }

    public Optional<MRegistroAcceso> buscarPorId(Integer id) {
        return iRegistroAcceso.findById(id);
    }

    /**
     * Regla de negocio central de KeyApp:
     * antes de "abrir" un aula se valida que exista una reserva ACTIVA,
     * vigente para el dia/hora actual, y asociada a la cerradura del aula.
     * Sea cual sea el resultado, siempre queda un registro en REGISTRO_ACCESO
     * para efectos de trazabilidad.
     */
    public MRegistroAcceso solicitarApertura(Integer idReserva, Integer idCerradura) {
        Optional<MReserva> reservaOpt = iReserva.findById(idReserva);
        Optional<MCerradura> cerraduraOpt = iCerradura.findById(idCerradura);

        MRegistroAcceso registro = new MRegistroAcceso();
        registro.setFechaHora(LocalDateTime.now());
        registro.setTipoEvento("APERTURA");

        if (reservaOpt.isEmpty() || cerraduraOpt.isEmpty()) {
            registro.setTipoEvento("FALLIDO");
            registro.setResultado("Reserva o cerradura inexistente");
            return iRegistroAcceso.save(registro);
        }

        MReserva reserva = reservaOpt.get();
        MCerradura cerradura = cerraduraOpt.get();
        LocalTime ahora = LocalTime.now();

        boolean reservaVigente = "ACTIVA".equals(reserva.getEstado())
                && !ahora.isBefore(reserva.getHoraInicio())
                && !ahora.isAfter(reserva.getHoraFin());

        registro.setReserva(reserva);
        registro.setCerradura(cerradura);

        if (reservaVigente) {
            registro.setResultado("EXITO");
        } else {
            registro.setTipoEvento("FALLIDO");
            registro.setResultado("La reserva no esta activa o esta fuera de horario");
        }

        return iRegistroAcceso.save(registro);
    }

    public void eliminar(Integer id) {
        iRegistroAcceso.deleteById(id);
    }
}
