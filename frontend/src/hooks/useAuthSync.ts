import { useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { setAuthToken, setOnRefreshTokenFailed } from '../api/apiClient';

/**
 * Hook para sincronizar el estado de autenticación con el cliente HTTP.
 * 
 * Este hook resuelve un problema arquitectónico importante:
 * - El token JWT está en el Context de React (memoria)
 * - Los interceptores de axios están fuera del ciclo de React
 * - Necesitamos una forma de comunicar el token a axios
 * 
 * Solución:
 * - Usar un efecto que actualiza una referencia global en apiClient
 * - Cada vez que el token cambia en el Context, se actualiza en apiClient
 * 
 * También configura el callback para cuando el refresh token falla,
 * permitiendo redirigir al usuario a /login.
 */
export function useAuthSync() {
  const { accessToken, logout } = useAuth();
  const navigate = useNavigate();

  // Sincronizar el token con el cliente HTTP
  useEffect(() => {
    setAuthToken(accessToken);
  }, [accessToken]);

  // Configurar el callback para cuando el refresh falla
  useEffect(() => {
    setOnRefreshTokenFailed(() => {
      logout();
      navigate('/login', { replace: true });
    });
  }, [logout, navigate]);
}

export default useAuthSync;
