# KeyApp – Testing del frontend y conexión con el backend

## 1. Alcance del cambio

Se agregó la configuración de Jest y React Testing Library y se crearon pruebas para las reglas de apertura, normalización de texto, formulario de inicio de sesión y control de rutas por sesión/rol.

No se cambió el código de los componentes originales, sus estilos, las rutas, las llamadas HTTP ni las reglas de negocio. Los cambios funcionales se limitan a `package.json` (scripts y dependencias de desarrollo) y a los archivos nuevos de configuración y pruebas.

## 2. Tecnologías

- **React 19:** construye la interfaz mediante componentes.
- **Vite 8:** servidor de desarrollo y herramienta de compilación.
- **React Router 7:** navegación entre páginas y protección de rutas.
- **Jest:** ejecuta las pruebas y evalúa las expectativas (`expect`).
- **React Testing Library:** renderiza componentes React en un DOM simulado y permite probarlos como los usaría una persona.
- **jest-dom:** agrega verificaciones como `toBeInTheDocument()`.
- **Babel:** transforma JSX y módulos ES para que Jest pueda ejecutarlos.

## 3. Instalación y ejecución

Abre una terminal en la carpeta `keyapp-frontend` (la carpeta donde está `package.json`). Ejecuta:

```bash
npm install
```

Esto instala las dependencias declaradas en `package.json` y actualiza `package-lock.json` si es necesario. Después ejecuta todas las pruebas:

```bash
npm test
```

Para ejecutar las pruebas una sola vez, sin modo interactivo:

```bash
npm test -- --runInBand
```

Para ejecutar una prueba mientras desarrollas:

```bash
npm run test:watch
```

Para generar un informe de cobertura:

```bash
npm run test:coverage
```

La cobertura indica qué líneas, funciones y ramas fueron recorridas por las pruebas; no significa por sí sola que el sistema esté libre de errores.

## 4. Pruebas agregadas

| Archivo | Qué comprueba |
|---|---|
| `src/modules/core/utils/__tests__/texto.test.js` | Que `normalizar` convierta a minúsculas, elimine tildes y acepte valores nulos. |
| `src/modules/core/utils/__tests__/aperturas.test.js` | Fecha local, ventana de apertura de 20 minutos, reservas futuras y reserva terminada. |
| `src/modules/auth/pages/__tests__/LoginPage.test.jsx` | Que el formulario se muestre, envíe correo/contraseña a `login` y presente el error cuando el acceso falla. |
| `src/modules/auth/components/__tests__/RutaProtegida.test.jsx` | Que una sesión inexistente vaya al login, un rol incorrecto sea rechazado y el rol permitido pueda entrar. |

Los tests del login simulan el hook de autenticación: validan el comportamiento de la interfaz sin llamar a un backend real ni necesitar credenciales reales. Las pruebas de utilidades comprueban funciones existentes directamente.

## 5. Cómo funciona el frontend

1. `index.html` contiene el elemento raíz donde React monta la aplicación.
2. `src/main.jsx` inicia React con `createRoot`, importa los estilos y envuelve la aplicación en `StrictMode`, `BrowserRouter` y `AuthProvider`.
3. `AuthProvider` conserva el usuario autenticado en el estado de React y sincroniza la sesión con `sessionStorage`.
4. `src/App.jsx` declara las rutas. `/login` es pública; las demás rutas principales requieren sesión mediante `RutaProtegida`.
5. `RutaProtegida` verifica que exista un usuario y, en rutas restringidas, comprueba el rol (`ADMINISTRADOR` o `PROFESOR`).
6. Cada página utiliza componentes, hooks y contextos de su módulo. Por ejemplo, `ReservasPage` presenta reservas y utiliza `useDatos` para consultar o ejecutar operaciones.
7. `DatosProvider` mantiene en estado las aulas, usuarios, reservas, estados de aulas y transferencias. Sus funciones llaman a la API y actualizan el estado para que React vuelva a dibujar la interfaz.

## 6. Cómo se conecta React con Spring Boot

La comunicación está centralizada en `src/modules/core/lib/api.js`.

- La URL base se toma de `import.meta.env.VITE_API_URL`.
- Si no se define esa variable, usa `http://localhost:8080/api`.
- `api.get`, `api.post` y `api.put` usan `fetch` para hacer solicitudes HTTP.
- Cuando hay un cuerpo, se serializa como JSON y se envía `Content-Type: application/json`.
- Si existe un token JWT en `sessionStorage` dentro del objeto `usuario`, se envía en la cabecera `Authorization: Bearer ...`.
- Si el backend responde con error, el cliente lanza `ApiError`. Si responde `401` en una ruta diferente al login, notifica al `AuthProvider` para cerrar la sesión.

### Flujo de inicio de sesión

1. La persona escribe correo y contraseña en `LoginPage.jsx`.
2. El formulario ejecuta `login(email, password)` del contexto de autenticación.
3. `AuthProvider` envía `POST /auth/login` con `{ email, password }`.
4. `api.js` construye la petición a `http://localhost:8080/api/auth/login` (si no se configuró otra URL).
5. Spring Boot valida las credenciales y devuelve los datos del usuario; si devuelve un token, queda guardado dentro de `usuario` en `sessionStorage`.
6. React actualiza el contexto y la aplicación redirige al usuario a las reservas.

### Flujo de carga de información

1. `DatosProvider` ejecuta al iniciar `GET /aulas`, `GET /usuarios` y `GET /reservas`.
2. Spring Boot recibe las solicitudes en sus controladores, ejecuta la lógica de servicios y consulta la base de datos mediante sus repositorios.
3. El backend devuelve respuestas JSON.
4. `DatosProvider` guarda las respuestas en sus estados `aulas`, `usuarios` y `reservas`.
5. Las páginas que consumen `useDatos()` reciben esos datos y React actualiza la interfaz.

### Flujo de apertura simulada de puerta

1. La interfaz muestra u oculta el botón según la reserva y la ventana de tiempo calculada por `ventanaApertura` en `aperturas.js`.
2. Al pulsar abrir, `DatosProvider.abrirPuerta(id)` envía `POST /reservas/{id}/abrir` con `{ idUsuario }`.
3. El backend debe validar la reserva, el usuario y la ventana de tiempo. La regla definitiva de autorización debe estar en el backend; el cálculo del frontend solo orienta la interfaz.
4. El frontend muestra el mensaje que devuelve el backend o el error correspondiente.

## 7. Configuración de la URL del backend

Para trabajar localmente con el backend en el puerto 8080, no necesitas definir `VITE_API_URL`, porque esa es la URL predeterminada.

Si el backend está en otra dirección, crea un archivo `.env` en la raíz del frontend (junto a `package.json`) y agrega, por ejemplo:

```env
VITE_API_URL=http://localhost:8080/api
```

La URL debe terminar en `/api` y no debe terminar en una barra adicional, porque `api.js` concatena esa base con rutas como `/reservas`.

Después de cambiar `.env`, detén y vuelve a iniciar Vite:

```bash
npm run dev
```

El backend también debe permitir solicitudes CORS desde el origen donde se ejecuta Vite (normalmente `http://localhost:5173`). Si el navegador muestra un error CORS, se debe revisar la configuración CORS de Spring Boot; no se corrige cambiando los componentes React.

## 8. Qué significa que pasen las pruebas

Si Jest informa que todos los tests pasaron, significa que los comportamientos concretos de esos casos se cumplieron. No prueba todavía todos los módulos ni sustituye una prueba de integración con Spring Boot y MySQL. Para completar la validación del sistema conviene probar también en ejecución real el login, consulta y creación de reservas, cambio de estado de aulas, transferencia y apertura simulada.

## 9. Estructura nueva

```text
keyapp-frontend/
├── babel.config.cjs
├── jest.config.cjs
├── test/
│   ├── setupTests.js
│   ├── styleMock.cjs
│   └── fileMock.cjs
└── src/
    └── modules/
        ├── auth/
        │   ├── components/__tests__/RutaProtegida.test.jsx
        │   └── pages/__tests__/LoginPage.test.jsx
        └── core/
            └── utils/__tests__/
                ├── aperturas.test.js
                └── texto.test.js
```
