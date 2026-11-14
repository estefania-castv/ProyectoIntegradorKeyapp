package com.Cesde.keyapp.Servicio;

import com.Cesde.keyapp.Modelo.MUsuario;
import com.Cesde.keyapp.Repositorio.IUsuario;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class SAuth {

    private final IUsuario usuarioRepo;
    private final PasswordEncoder encoder;

    public SAuth(IUsuario usuarioRepo, PasswordEncoder encoder) {
        this.usuarioRepo = usuarioRepo;
        this.encoder = encoder;
    }

    public Optional<MUsuario> autenticar(String email, String password) {
        return usuarioRepo.findByEmail(email)
                .filter(u -> encoder.matches(password, u.getPasswordHash()));
    }
}
