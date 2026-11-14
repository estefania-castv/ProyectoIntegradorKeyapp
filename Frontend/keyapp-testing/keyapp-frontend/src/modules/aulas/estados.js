// Estados que el administrador puede poner a un aula. RESERVADA no está aquí porque se calcula sola.
export const ESTADOS_AULA = [
  { valor: "DISPONIBLE", etiqueta: "Disponible", icono: "door" },
  { valor: "MANTENIMIENTO", etiqueta: "En mantenimiento", icono: "wrench" },
  { valor: "SIN_ENERGIA", etiqueta: "Sin energía", icono: "bolt" },
  { valor: "PROBLEMAS_DE_RED", etiqueta: "Problemas de red", icono: "signal" },
  { valor: "FUERA_DE_SERVICIO", etiqueta: "Temporalmente fuera de servicio", icono: "alerta" },
  { valor: "INACTIVA", etiqueta: "Inactiva", icono: "lock" },
];

export const etiquetaEstadoAula = (valor) =>
  valor === "RESERVADA"
    ? "Reservada"
    : ESTADOS_AULA.find((e) => e.valor === valor)?.etiqueta ?? String(valor ?? "").replaceAll("_", " ");

export const iconoEstadoAula = (valor) =>
  valor === "RESERVADA" ? "lock" : ESTADOS_AULA.find((e) => e.valor === valor)?.icono ?? "lock";
