package com.Cesde.keyapp.Servicio;

import com.Cesde.keyapp.Modelo.MNotificacion;
import com.Cesde.keyapp.Repositorio.INotificacion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class SNotificacion {

    @Autowired
    private INotificacion iNotificacion;

    public List<MNotificacion> listar() {
        return iNotificacion.findAll();
    }

    public Optional<MNotificacion> buscarPorId(Integer id) {
        return iNotificacion.findById(id);
    }

    public MNotificacion enviar(MNotificacion notificacion) {
        notificacion.setFechaEnvio(LocalDateTime.now());
        notificacion.setLeido(false);
        return iNotificacion.save(notificacion);
    }

    public void marcarLeida(Integer id) {
        iNotificacion.findById(id).ifPresent(n -> {
            n.setLeido(true);
            iNotificacion.save(n);
        });
    }

    public void eliminar(Integer id) {
        iNotificacion.deleteById(id);
    }
}
