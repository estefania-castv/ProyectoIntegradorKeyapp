# React + Vite

This template provides a minimal setup to get React working in Vite with HMR and some ESLint rules.

Currently, two official plugins are available:

- [@vitejs/plugin-react](https://github.com/vitejs/vite-plugin-react/blob/main/packages/plugin-react) uses [Oxc](https://oxc.rs)
- [@vitejs/plugin-react-swc](https://github.com/vitejs/vite-plugin-react/blob/main/packages/plugin-react-swc) uses [SWC](https://swc.rs/)

## React Compiler

The React Compiler is not enabled on this template because of its impact on dev & build performances. To add it, see [this documentation](https://react.dev/learn/react-compiler/installation).

## Expanding the ESLint configuration

If you are developing a production application, we recommend using TypeScript with type-aware lint rules enabled. Check out the [TS template](https://github.com/vitejs/vite/tree/main/packages/create-vite/template-react-ts) for information on how to integrate TypeScript and [`typescript-eslint`](https://typescript-eslint.io) in your project.

## Estructura de carpetas (screaming architecture)

El código está organizado por **módulos de negocio** en `src/modules/`. Cada módulo agrupa lo suyo
(`components`, `pages`, `hooks`, `states`, `lib`, `utils`, `design-system`). Se importa con el alias `@`
(= `src`), por ejemplo `@/modules/auth/hooks/useAuth`.

```
src/
├── assets/            logos e imágenes
├── modules/
│   ├── core/          navbar, layout, alertas, buscador, DatosProvider, cliente de la API, utilidades
│   ├── auth/          login, AuthProvider, useAuth, RutaProtegida
│   ├── reservas/      listado, nueva reserva, transferir, tabla y formulario
│   ├── aulas/         página de aulas
│   └── usuarios/      página de usuarios (solo ADMINISTRADOR)
├── App.jsx            rutas
├── index.css
└── main.jsx
```
