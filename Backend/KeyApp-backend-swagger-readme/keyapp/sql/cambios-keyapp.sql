-- ============================================================
-- KeyApp - cambios de base de datos (ejecutar UNA sola vez)
-- phpMyAdmin > keyapp_db > pestaña SQL. Hibernate tiene ddl-auto=none,
-- por eso las tablas se crean aquí.
-- ============================================================
USE keyapp_db;

-- 1) Transferencias con aceptar / rechazar.
--    Las que ya existían quedan como ACEPTADA (antes se aplicaban de inmediato).
ALTER TABLE transferencia
    ADD COLUMN estado VARCHAR(20) NOT NULL DEFAULT 'ACEPTADA',
    ADD COLUMN fecha_respuesta DATETIME NULL,
    ADD COLUMN id_profesor_origen INT NULL,
    ADD CONSTRAINT fk_transferencia_origen FOREIGN KEY (id_profesor_origen) REFERENCES usuario(id_usuario);

-- 2) Reportes (novedades) de las aulas
CREATE TABLE IF NOT EXISTS reporte_aula (
    id_reporte        INT AUTO_INCREMENT PRIMARY KEY,
    tipo              VARCHAR(30)  NOT NULL,
    descripcion       VARCHAR(500) NOT NULL,
    estado            VARCHAR(20)  NOT NULL,
    fecha             DATETIME     NOT NULL,
    fecha_resolucion  DATETIME     NULL,
    id_aula           INT          NOT NULL,
    id_profesor       INT          NOT NULL,
    CONSTRAINT fk_reporte_aula     FOREIGN KEY (id_aula)     REFERENCES aula(id_aula),
    CONSTRAINT fk_reporte_profesor FOREIGN KEY (id_profesor) REFERENCES usuario(id_usuario),
    INDEX idx_reporte_fecha (fecha)
);

-- 3) Ventana de soporte (una conversación por usuario)
CREATE TABLE IF NOT EXISTS mensaje_soporte (
    id_mensaje  INT AUTO_INCREMENT PRIMARY KEY,
    texto       VARCHAR(500) NOT NULL,
    fecha       DATETIME     NOT NULL,
    id_usuario  INT          NOT NULL,
    id_autor    INT          NOT NULL,
    CONSTRAINT fk_soporte_usuario FOREIGN KEY (id_usuario) REFERENCES usuario(id_usuario),
    CONSTRAINT fk_soporte_autor   FOREIGN KEY (id_autor)   REFERENCES usuario(id_usuario),
    INDEX idx_soporte_usuario_fecha (id_usuario, fecha)
);

-- 4) Nuevos estados de aula (MANTENIMIENTO, SIN_ENERGIA, PROBLEMAS_DE_RED, FUERA_DE_SERVICIO...).
--    Si tu columna "estado" de aula fuera un ENUM, esto la deja como texto libre (si ya es VARCHAR no cambia nada).
ALTER TABLE aula MODIFY estado VARCHAR(20) NOT NULL;
