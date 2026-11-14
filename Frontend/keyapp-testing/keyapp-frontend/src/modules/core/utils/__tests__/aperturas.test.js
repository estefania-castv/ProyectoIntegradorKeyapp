import { hoyLocal, reservaTerminada, ventanaApertura } from '../aperturas';

describe('reglas de apertura de KeyApp', () => {
  const ahora = new Date(2026, 10, 13, 10, 5);
  const reserva = {
    fecha: '2026-11-13',
    horaInicio: '10:00:00',
    horaFin: '11:00:00',
  };

  test('obtiene la fecha local en formato AAAA-MM-DD', () => {
    expect(hoyLocal(ahora)).toBe('2026-11-13');
  });

  test('permite mostrar la ventana durante los primeros 20 minutos', () => {
    expect(ventanaApertura(reserva, ahora)).toEqual({
      estado: 'abierta',
      hasta: '10:20',
    });
  });

  test('indica que la reserva es futura antes de su fecha', () => {
    expect(ventanaApertura({ ...reserva, fecha: '2026-11-14' }, ahora)).toEqual({
      estado: 'futura',
    });
  });

  test('identifica una reserva terminada al llegar la hora de fin', () => {
    const despuesDelFin = new Date(2026, 10, 13, 11, 0);
    expect(reservaTerminada(reserva, despuesDelFin)).toBe(true);
  });
});
