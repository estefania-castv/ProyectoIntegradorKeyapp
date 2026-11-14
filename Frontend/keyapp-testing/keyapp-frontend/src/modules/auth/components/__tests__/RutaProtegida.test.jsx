import { render, screen } from '@testing-library/react';
import { MemoryRouter, Route, Routes } from 'react-router-dom';
import RutaProtegida from '../RutaProtegida';
import { useAuth } from '@/modules/auth/hooks/useAuth';

jest.mock('@/modules/auth/hooks/useAuth');

function renderRutas() {
  return render(
    <MemoryRouter initialEntries={['/privado']}>
      <Routes>
        <Route path="/login" element={<p>Página de login</p>} />
        <Route path="/no-autorizado" element={<p>Sin autorización</p>} />
        <Route element={<RutaProtegida roles={['ADMINISTRADOR']} />}>
          <Route path="/privado" element={<p>Contenido privado</p>} />
        </Route>
      </Routes>
    </MemoryRouter>,
  );
}

describe('RutaProtegida', () => {
  test('redirige al login cuando no existe una sesión', () => {
    useAuth.mockReturnValue({ usuario: null });
    renderRutas();
    expect(screen.getByText('Página de login')).toBeInTheDocument();
  });

  test('impide el acceso cuando el rol no está autorizado', () => {
    useAuth.mockReturnValue({ usuario: { rol: 'PROFESOR' } });
    renderRutas();
    expect(screen.getByText('Sin autorización')).toBeInTheDocument();
  });

  test('permite el acceso al rol autorizado', () => {
    useAuth.mockReturnValue({ usuario: { rol: 'ADMINISTRADOR' } });
    renderRutas();
    expect(screen.getByText('Contenido privado')).toBeInTheDocument();
  });
});
