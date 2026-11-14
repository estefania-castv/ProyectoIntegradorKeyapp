import { normalizar } from '../texto';

describe('normalizar', () => {
  test('convierte el texto a minúsculas y elimina tildes', () => {
    expect(normalizar('Matemáticas')).toBe('matematicas');
  });

  test('acepta valores nulos sin generar errores', () => {
    expect(normalizar(null)).toBe('');
  });
});
