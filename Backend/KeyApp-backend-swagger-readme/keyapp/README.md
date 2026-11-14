# KeyApp – Backend

## 1. Nombre del proyecto y descripción

**KeyApp** es una aplicación para gestionar las reservas de aulas de una institución educativa y el acceso a ellas mediante **cerraduras inteligentes (IoT)**. Como no hay cerraduras reales, la apertura de la puerta se simula y queda registrada.

Este repositorio contiene el **backend (API REST)**. Está dirigido a:

- **Administradores:** crean reservas, agregan aulas y cambian su estado, cambian roles, leen los reportes de aulas y atienden la ventana de soporte.
- **Profesores:** consultan sus reservas, abren la puerta de su aula (desde la hora de inicio y hasta 20 minutos después), transfieren reservas a otro profesor (el destino acepta o rechaza), informan novedades de las aulas y escriben a soporte.

## 2. Tecnologías utilizadas

| Componente | Tecnología |
|---|---|
| Lenguaje | Java 17 o superior (probado con Java 21) |
| Framework | Spring Boot 3.3.4 (Web, Data JPA, Validation, Security) |
| Base de datos | MySQL / MariaDB (XAMPP en local), base `keyapp_db` |
| Construcción | Maven |
| Seguridad | Spring Security + JWT (jjwt 0.12.6) + BCrypt |
| Utilidades | Lombok |
| Documentación | Swagger / OpenAPI (springdoc-openapi 2.6.0) |
| Estadísticas | Python 3 (requests, pandas, numpy, matplotlib) |
| Pruebas | JUnit 5 (spring-boot-starter-test) |

## 3. Arquitectura

Paquete base: `com.Cesde.keyapp`

| Capa | Paquete | Prefijo | Responsabilidad |
|---|---|---|---|
| Entity | `Modelo` | `M` | Entidades con sus atributos y relaciones (`MAula`, `MReserva`, `MUsuario`, …) |
| Repository | `Repositorio` | `I` | Acceso a datos (interfaces JPA) |
| Service | `Servicio` | `S` | Reglas de negocio (traslapes, ventana de 20 minutos, transferencias, cierre de reservas…) |
| Controller | `Controlador` | `C` | Recibe las solicitudes HTTP y devuelve la respuesta |
| DTO | `Dto` | – | Objetos de entrada y salida de la API (`LoginRequest`, `AbrirResponse`, …) |
| Mapper | `Mapper` | – | Convierte entidades en DTO (`AuthMapper`, `TransferenciaMapper`, …) |
| Seguridad | `Seguridad` | – | `JwtServicio`, `JwtFiltro`, `UsuarioAutenticado` |
| Configuración | `Configuracion` | – | `ConfiguracionSeguridad` (reglas por rol y CORS) y `ConfiguracionSwagger` |

Flujo de una petición: `Cliente → JwtFiltro → Controller → Service → Repository → Base de datos`.

## 4. Requisitos previos

- **JDK 17 o superior** (el proyecto compila con `java.version=17`).
- **Maven** (o el Maven integrado de IntelliJ IDEA).
- **MySQL/MariaDB** (por ejemplo XAMPP) con la base de datos `keyapp_db` creada y con sus tablas.
- **Python 3** (solo para el endpoint de estadísticas) y las librerías de `Reportes-python/requirements.txt`.
- Variables de entorno (opcionales; todas tienen un valor por defecto para desarrollo local):

| Variable | Valor por defecto | Para qué sirve |
|---|---|---|
| `DB_URL` | `jdbc:mysql://localhost:3306/keyapp_db?...` | URL de la base de datos |
| `DB_USER` | `root` | Usuario de la base de datos |
| `DB_PASSWORD` | (vacía) | Contraseña de la base de datos |
| `SHOW_SQL` | `true` | Mostrar las consultas SQL en consola |
| `JWT_SECRET` | clave de desarrollo | Secreto del token (mínimo 32 caracteres). **Cámbialo en producción** |
| `JWT_EXPIRACION_MS` | `28800000` (8 horas) | Duración del token |
| `CORS_ORIGENES` | `http://localhost:5173` | URL(s) del frontend, separadas por coma |
| `PORT` | `8080` | Puerto del servidor |
| `PYTHON_EXECUTABLE` | `python` | Ejecutable de Python para las estadísticas |
| `KEYAPP_API_URL` | `http://localhost:8080/api` | URL de la API que consulta el script de Python |

## 5. Instalación y configuración

1. **Clonar el repositorio**
   ```bash
   git clone <url-del-repositorio>
   cd keyapp
   ```
2. **Base de datos:** en XAMPP inicia MySQL, crea la base `keyapp_db` con sus tablas y datos de prueba, y ejecuta **una sola vez** `sql/cambios-keyapp.sql` en phpMyAdmin (pestaña SQL). Hibernate está en `ddl-auto=none`: no crea ni modifica tablas.
3. **Variables de entorno:** solo si tu configuración difiere de la local (ver tabla anterior).
4. **Librerías de Python** (solo para estadísticas):
   ```bash
   pip install -r Reportes-python/requirements.txt
   ```
5. **Pruebas** (opcional): `mvn test` (ver sección 8).
6. **Iniciar el aplicativo**
   - En IntelliJ: clic derecho en `pom.xml` → Maven → *Reload project* y ejecutar `KeyappApplication`.
   - O por terminal: `mvn spring-boot:run`.

   La API queda en `http://localhost:8080`.

## 6. Configuración interna

- **URL base de la API:** `http://localhost:8080/api`
- **Puerto:** `8080` (`server.port`, variable `PORT`).
- **Base de datos:** `jdbc:mysql://localhost:3306/keyapp_db`, usuario `root` sin contraseña (XAMPP por defecto).
- **Autenticación:** se inicia sesión con `POST /api/auth/login` (correo y contraseña) y se recibe un token JWT. En las demás peticiones se envía el encabezado `Authorization: Bearer <token>`.
- **Usuarios de ingreso:** son los de la tabla `usuario` de `keyapp_db` (roles `PROFESOR` y `ADMINISTRADOR`); las contraseñas se guardan cifradas con BCrypt.
- **CORS:** solo se aceptan las URL de `app.cors.origenes`.
- **Tareas programadas:** cada minuto `SCierreReservas` pone en `FINALIZADA` las reservas cuya hora de fin ya pasó.

## 7. Endpoints principales

Todos requieren token JWT, salvo el login y Swagger. «Admin» = solo `ADMINISTRADOR`.

**Autenticación**

| Método | Ruta | Descripción |
|---|---|---|
| POST | `/api/auth/login` | Inicia sesión y devuelve el token |

**Reservas**

| Método | Ruta | Descripción |
|---|---|---|
| GET | `/api/reservas` | Lista las reservas (admin: todas; profesor: las suyas) |
| GET | `/api/reservas/{id}` | Consulta una reserva por llave primaria |
| POST | `/api/reservas` | Adiciona una reserva (admin) |
| PUT | `/api/reservas/{id}` | Actualiza una reserva (admin) |
| PUT | `/api/reservas/{id}/cancelar` | Cancela una reserva (dueño o admin) |
| DELETE | `/api/reservas/{id}` | Elimina una reserva (admin) |
| POST | `/api/reservas/{id}/abrir` | Abre la puerta (profesor dueño, hasta 20 min después del inicio) |
| POST | `/api/reservas/{id}/transferir` | Solicita transferir la reserva a otro profesor |

**Transferencias**

| Método | Ruta | Descripción |
|---|---|---|
| GET | `/api/transferencias/mias` | Solicitudes pendientes o rechazadas del usuario |
| PUT | `/api/transferencias/{id}/aceptar` | El profesor destino acepta |
| PUT | `/api/transferencias/{id}/rechazar` | El profesor destino rechaza |
| GET | `/api/transferencias` · `/{id}` | Consulta el historial (admin) |
| DELETE | `/api/transferencias/{id}` | Elimina una transferencia (admin) |

**Aulas**

| Método | Ruta | Descripción |
|---|---|---|
| GET | `/api/aulas` · `/{id}` | Lista / consulta aulas |
| GET | `/api/aulas/estado-actual` | Estado de cada aula en este momento |
| POST | `/api/aulas` | Adiciona un aula (admin) |
| PUT | `/api/aulas/{id}` | Actualiza un aula (admin) |
| PUT | `/api/aulas/{id}/estado` | Cambia el estado del aula (admin) |
| DELETE | `/api/aulas/{id}` | Elimina un aula (admin) |

**Usuarios**

| Método | Ruta | Descripción |
|---|---|---|
| GET | `/api/usuarios` · `/{id}` | Lista / consulta usuarios |
| POST | `/api/usuarios` | Adiciona un usuario (admin) |
| PUT | `/api/usuarios/{id}` | Actualiza un usuario (admin) |
| PUT | `/api/usuarios/{id}/rol` | Cambia el rol PROFESOR ↔ ADMINISTRADOR (admin) |
| DELETE | `/api/usuarios/{id}` | Elimina un usuario (admin) |

**Reportes de aulas y soporte**

| Método | Ruta | Descripción |
|---|---|---|
| GET | `/api/reportes` | Lista reportes (admin: todos; profesor: los suyos) |
| POST | `/api/reportes` | Crea un reporte de aula |
| PUT | `/api/reportes/{id}/resolver` | Marca el reporte como resuelto (admin) |
| GET | `/api/soporte/mensajes` | Mensajes de la conversación de soporte |
| POST | `/api/soporte/mensajes` | Envía un mensaje de soporte |
| GET | `/api/soporte/conversaciones` | Lista las conversaciones (admin) |

**Otros recursos (CRUD, solo admin):** `/api/facultades`, `/api/cerraduras`, `/api/horarios`, `/api/observaciones`, `/api/notificaciones`, `/api/registros-acceso`.

**Estadísticas:** `GET /api/estadisticas/reservas` (admin) ejecuta `Reportes-python/reportes.py` y devuelve el resumen en JSON.

### Documentación con Swagger

Con la aplicación en ejecución, abre en el navegador:

```
http://localhost:8080/doc/swagger-ui/index.html
```

Para probar los servicios protegidos: ejecuta `POST /api/auth/login` desde Swagger, copia el campo `token`, pulsa el botón **Authorize** y pega el token.

## 8. Pruebas unitarias

Las pruebas están en `src/test/java` y usan JUnit 5 (incluido en `spring-boot-starter-test`). La prueba incluida, `JwtServicioTest`, no necesita base de datos.

- Desde la terminal: `mvn test`
- Desde IntelliJ: clic derecho sobre `src/test/java` (o sobre una clase de prueba) → *Run Tests*.

Para agregar una prueba nueva, crea una clase en `src/test/java` con el mismo paquete de la clase que vas a probar y anota los métodos con `@Test`.

## 9. Licencias y derechos de autor

Proyecto académico desarrollado en CESDE (Backend II). © 2026 Estephania Castro Vega. Todos los derechos reservados; uso con fines educativos.
