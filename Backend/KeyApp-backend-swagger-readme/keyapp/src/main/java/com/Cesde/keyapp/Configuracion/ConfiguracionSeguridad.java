package com.Cesde.keyapp.Configuracion;

import com.Cesde.keyapp.Seguridad.JwtFiltro;
import com.Cesde.keyapp.Seguridad.JwtServicio;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
public class ConfiguracionSeguridad {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filtros(HttpSecurity http, JwtServicio jwtServicio) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)            // la API usa token, no cookies
                .cors(Customizer.withDefaults())                  // usa el bean corsConfigurationSource
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(e -> e.authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers("/error").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/auth/login").permitAll()
                        // Documentación Swagger (se puede ver sin token; los endpoints siguen protegidos)
                        .requestMatchers("/doc/**", "/swagger-ui/**", "/swagger-ui.html", "/v3/api-docs/**").permitAll()

                        // Cualquier usuario con sesión (PROFESOR o ADMINISTRADOR)
                        .requestMatchers(HttpMethod.GET, "/api/aulas", "/api/aulas/*",
                                "/api/usuarios", "/api/usuarios/*").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/reservas", "/api/reservas/*").authenticated()
                        // Crear reservas (POST /api/reservas) es solo del ADMINISTRADOR; abrir y transferir
                        // las valida el servicio (solo el profesor dueño de la reserva).
                        .requestMatchers(HttpMethod.POST, "/api/reservas/*/abrir",
                                "/api/reservas/*/transferir").authenticated()
                        .requestMatchers(HttpMethod.PUT, "/api/reservas/*/cancelar").authenticated()

                        // Transferencias: el profesor destino las ve y responde
                        .requestMatchers(HttpMethod.GET, "/api/transferencias/mias").authenticated()
                        .requestMatchers(HttpMethod.PUT, "/api/transferencias/*/aceptar",
                                "/api/transferencias/*/rechazar").authenticated()

                        // Reportes de aulas: todos informan; resolverlos es solo del ADMINISTRADOR
                        .requestMatchers(HttpMethod.GET, "/api/reportes").authenticated()
                        .requestMatchers(HttpMethod.POST, "/api/reportes").authenticated()

                        // Ventana de soporte: cada controlador/servicio limita qué conversación ve cada rol
                        .requestMatchers(HttpMethod.GET, "/api/soporte/**").authenticated()
                        .requestMatchers(HttpMethod.POST, "/api/soporte/**").authenticated()

                        // Todo lo demás (crear/editar/borrar usuarios, aulas, cerraduras, horarios,
                        // observaciones, registros de acceso, etc.): solo ADMINISTRADOR
                        .anyRequest().hasRole("ADMINISTRADOR"))
                .addFilterBefore(new JwtFiltro(jwtServicio), UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    // Orígenes permitidos (la URL del frontend). Se configura con la variable CORS_ORIGENES.
    @Bean
    public CorsConfigurationSource corsConfigurationSource(@Value("${app.cors.origenes}") List<String> origenes) {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(origenes);
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("Authorization", "Content-Type"));

        UrlBasedCorsConfigurationSource fuente = new UrlBasedCorsConfigurationSource();
        fuente.registerCorsConfiguration("/**", config);
        return fuente;
    }
}
