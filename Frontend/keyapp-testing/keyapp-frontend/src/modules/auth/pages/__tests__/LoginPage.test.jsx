import { fireEvent, render, screen, waitFor } from '@testing-library/react';
import { MemoryRouter } from 'react-router-dom';
import LoginPage from '../LoginPage';
import { useAuth } from '@/modules/auth/hooks/useAuth';

jest.mock('@/modules/auth/hooks/useAuth');
jest.mock('@/modules/core/design-system', () => ({
  Icono: () => null,
}));

function renderLogin() {
  return render(
    <MemoryRouter initialEntries={['/login']}>
      <LoginPage />
    </MemoryRouter>,
  );
}

describe('LoginPage', () => {
  test('muestra el formulario de inicio de sesión', () => {
    useAuth.mockReturnValue({ usuario: null, login: jest.fn() });
    renderLogin();

    expect(screen.getByRole('heading', { name: 'Iniciar sesión' })).toBeInTheDocument();
    expect(screen.getByRole('button', { name: 'Entrar' })).toBeInTheDocument();
  });

  test('envía el correo y la contraseña al método login', async () => {
    const login = jest.fn().mockResolvedValue({ idUsuario: 1 });
    useAuth.mockReturnValue({ usuario: null, login });
    const { container } = renderLogin();

    const inputs = container.querySelectorAll('input');
    fireEvent.change(inputs[0], { target: { value: 'profesor@keyapp.com' } });
    fireEvent.change(inputs[1], { target: { value: 'ClaveSegura123' } });
    fireEvent.click(screen.getByRole('button', { name: 'Entrar' }));

    await waitFor(() => {
      expect(login).toHaveBeenCalledWith('profesor@keyapp.com', 'ClaveSegura123');
    });
  });

  test('muestra el mensaje cuando el inicio de sesión falla', async () => {
    const login = jest.fn().mockRejectedValue(new Error('Correo o contraseña incorrectos'));
    useAuth.mockReturnValue({ usuario: null, login });
    const { container } = renderLogin();

    const inputs = container.querySelectorAll('input');
    fireEvent.change(inputs[0], { target: { value: 'incorrecto@keyapp.com' } });
    fireEvent.change(inputs[1], { target: { value: 'incorrecta' } });
    fireEvent.click(screen.getByRole('button', { name: 'Entrar' }));

    expect(await screen.findByText('Correo o contraseña incorrectos')).toBeInTheDocument();
  });
});
