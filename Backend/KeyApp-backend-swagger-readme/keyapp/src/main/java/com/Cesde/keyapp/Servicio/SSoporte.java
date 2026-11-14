package com.Cesde.keyapp.Servicio;

import com.Cesde.keyapp.Dto.ConversacionSoporteResponse;
import com.Cesde.keyapp.Modelo.MMensajeSoporte;
import com.Cesde.keyapp.Modelo.MUsuario;
import com.Cesde.keyapp.Repositorio.IMensajeSoporte;
import com.Cesde.keyapp.Repositorio.IUsuario;
import com.Cesde.keyapp.Seguridad.UsuarioAutenticado;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

@Service
public class SSoporte {

    private static final int MAX_CARACTERES = 500;

    private final IMensajeSoporte mensajeRepo;
    private final IUsuario usuarioRepo;

    public SSoporte(IMensajeSoporte mensajeRepo, IUsuario usuarioRepo) {
        this.mensajeRepo = mensajeRepo;
        this.usuarioRepo = usuarioRepo;
    }

    // Un profesor siempre ve SU conversación; el administrador elige de quién (idUsuario).
    private Integer duenioDeLaConversacion(UsuarioAutenticado actor, Integer idUsuario) {
        if (!actor.esAdmin()) {
            return actor.idUsuario();
        }
        if (idUsuario == null) {
            throw new IllegalArgumentException("Elige la conversación");
        }
        return idUsuario;
    }

    public List<MMensajeSoporte> mensajes(UsuarioAutenticado actor, Integer idUsuario) {
        return mensajeRepo.findByUsuario_IdUsuarioOrderByFechaAsc(duenioDeLaConversacion(actor, idUsuario));
    }

    public MMensajeSoporte enviar(UsuarioAutenticado actor, Integer idUsuario, String texto) {
        if (texto == null || texto.isBlank()) {
            throw new IllegalArgumentException("Escribe un mensaje");
        }
        String limpio = texto.trim();
        if (limpio.length() > MAX_CARACTERES) {
            throw new IllegalArgumentException("El mensaje no puede pasar de " + MAX_CARACTERES + " caracteres");
        }
        Integer idDuenio = duenioDeLaConversacion(actor, idUsuario);
        MUsuario duenio = usuarioRepo.findById(idDuenio)
                .orElseThrow(() -> new NoSuchElementException("El usuario no existe"));
        MUsuario autor = usuarioRepo.findById(actor.idUsuario())
                .orElseThrow(() -> new NoSuchElementException("Usuario no válido"));
        return mensajeRepo.save(new MMensajeSoporte(limpio, LocalDateTime.now(), duenio, autor));
    }

    // Solo ADMINISTRADOR: lista de conversaciones, la más reciente primero.
    public List<ConversacionSoporteResponse> conversaciones(UsuarioAutenticado actor) {
        if (!actor.esAdmin()) {
            throw new SecurityException("Solo los administradores ven todas las conversaciones");
        }
        Map<Integer, ConversacionSoporteResponse> porUsuario = new LinkedHashMap<>();
        for (MMensajeSoporte m : mensajeRepo.findAllByOrderByFechaDesc()) {
            Integer id = m.getUsuario().getIdUsuario();
            porUsuario.putIfAbsent(id, new ConversacionSoporteResponse(
                    id, m.getUsuario().getNombre(), m.getTexto(), m.getFecha()));
        }
        return List.copyOf(porUsuario.values());
    }
}
