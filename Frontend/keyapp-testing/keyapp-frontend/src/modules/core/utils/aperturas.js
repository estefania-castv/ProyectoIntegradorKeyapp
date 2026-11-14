// Regla de apertura: se puede abrir desde la hora de inicio hasta 20 minutos después (sin pasar de la hora de fin).
// El backend es quien decide de verdad; esto solo sirve para mostrar u ocultar el botón.
export const MINUTOS_PARA_ABRIR = 20;

const dos = (n) => String(n).padStart(2, "0");
const aMinutos = (hora) => {
  const [h, m] = String(hora ?? "0:0").split(":");
  return Number(h) * 60 + Number(m);
};
const aTexto = (min) => `${dos(Math.floor(min / 60))}:${dos(min % 60)}`;

export function hoyLocal(ahora = new Date()) {
  return `${ahora.getFullYear()}-${dos(ahora.getMonth() + 1)}-${dos(ahora.getDate())}`;
}

// La reserva ya terminó (su hora de fin pasó): el aula quedó liberada.
export function reservaTerminada(reserva, ahora = new Date()) {
  const hoy = hoyLocal(ahora);
  if (reserva.fecha < hoy) return true;
  if (reserva.fecha > hoy) return false;
  return ahora.getHours() * 60 + ahora.getMinutes() >= aMinutos(reserva.horaFin);
}

// -> { estado: "futura" | "antes" | "abierta" | "vencida", desde?, hasta? }
export function ventanaApertura(reserva, ahora = new Date()) {
  const hoy = hoyLocal(ahora);
  if (reserva.fecha < hoy) return { estado: "vencida" };
  if (reserva.fecha > hoy) return { estado: "futura" };

  const inicio = aMinutos(reserva.horaInicio);
  const fin = aMinutos(reserva.horaFin);
  const limite = Math.min(inicio + MINUTOS_PARA_ABRIR, fin);
  const actual = ahora.getHours() * 60 + ahora.getMinutes();

  if (actual < inicio) return { estado: "antes", desde: aTexto(inicio) };
  if (actual < limite) return { estado: "abierta", hasta: aTexto(limite) };
  return { estado: "vencida" };
}
