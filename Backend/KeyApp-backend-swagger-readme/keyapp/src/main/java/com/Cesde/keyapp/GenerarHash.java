package com.Cesde.keyapp;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class GenerarHash {

    public static void main(String[] args) {

        BCryptPasswordEncoder encoder =
                new BCryptPasswordEncoder();

        String password = "keyapp2026";

        String hash = encoder.encode(password);

        System.out.println("Hash generado:");
        System.out.println(hash);

        System.out.println("Validación:");
        System.out.println(encoder.matches(password, hash));
    }
}
