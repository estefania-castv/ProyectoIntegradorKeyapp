package com.Cesde.keyapp.Seguridad;

import com.Cesde.keyapp.Modelo.MUsuario;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Service
public class JwtServicio {

    private static final Logger log = LoggerFactory.getLogger(JwtServicio.class);
    private static final String CLAVE_POR_DEFECTO = "cambia-esta-clave-en-produccion-0123456789abcdef";

    private final SecretKey clave;
    private final long expiracionMs;

    public JwtServicio(@Value("${jwt.secret}") String secreto,
                       @Value("${jwt.expiracion-ms}") long expiracionMs) {
        byte[] bytes = secreto.getBytes(StandardCharsets.UTF_8);
        if (bytes.length < 32) {
            throw new IllegalStateException("jwt.secret debe tener al menos 32 caracteres");
        }
        if (CLAVE_POR_DEFECTO.equals(secreto)) {
            log.warn("Se está usando la clave JWT por defecto: úsala SOLO en desarrollo. "
                    + "En la nube define la variable de entorno JWT_SECRET.");
        }
        this.clave = Keys.hmacShaKeyFor(bytes);
        this.expiracionMs = expiracionMs;
    }

    public String generar(MUsuario usuario) {
        Date ahora = new Date();
        return Jwts.builder()
                .subject(usuario.getEmail())
                .claim("idUsuario", usuario.getIdUsuario())
                .claim("rol", usuario.getRol())
                .issuedAt(ahora)
                .expiration(new Date(ahora.getTime() + expiracionMs))
                .signWith(clave)
                .compact();
    }

    // Lanza JwtException si el token es inválido, está manipulado o expiró.
    public UsuarioAutenticado leer(String token) {
        Claims c = Jwts.parser()
                .verifyWith(clave)
                .build()
                .parseSignedClaims(token)
                .getPayload();
        return new UsuarioAutenticado(
                c.get("idUsuario", Integer.class),
                c.getSubject(),
                c.get("rol", String.class));
    }
}
