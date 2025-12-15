import { createContext, useContext, useState, useCallback, useEffect, ReactNode } from 'react';
import { AuthContextType, LoginCredentials } from '../types';
import { authApi } from '../api/authApi';

/**
 * Contexto de autenticación.
 * 
 * ¿Por qué usamos Context en lugar de localStorage para el JWT?
 * 
 * SEGURIDAD: El JWT se almacena solo en memoria (estado de React).
 * - localStorage es vulnerable a ataques XSS (cualquier script puede leerlo)
 * - La memoria de React no es accesible desde scripts maliciosos
 * - Al cerrar la pestaña, el token desaparece automáticamente
 * 
 * El refresh token está en una cookie HttpOnly:
 * - No es accesible desde JavaScript (protección contra XSS)
 * - Se envía automáticamente en cada request al backend
 * - El backend valida la cookie y emite un nuevo JWT si es válida
 */

// Crear el contexto con un valor inicial undefined
const AuthContext = createContext<AuthContextType | undefined>(undefined);

interface AuthProviderProps {
  children: ReactNode;
}

/**
 * Proveedor del contexto de autenticación.
 * Envuelve toda la aplicación para proveer el estado de autenticación.
 */
export function AuthProvider({ children }: AuthProviderProps) {
  // El JWT se almacena SOLO en memoria (estado de React)
  // NUNCA en localStorage o sessionStorage por seguridad
  const [accessToken, setAccessToken] = useState<string | null>(null);
  const [isLoading, setIsLoading] = useState(true);

  /**
   * Al montar la aplicación, intentamos obtener un nuevo JWT
   * usando el refresh token (si existe en la cookie).
   * 
   * Esto permite mantener la sesión activa al recargar la página,
   * sin necesidad de almacenar el JWT en localStorage.
   */
  useEffect(() => {
    const initializeAuth = async () => {
      try {
        // Intentar obtener un nuevo JWT usando el refresh token de la cookie
        const newToken = await authApi.refreshToken();
        if (newToken) {
          setAccessToken(newToken);
        }
      } catch (error) {
        // Si falla, el usuario no está autenticado
        // Esto es normal si no hay sesión activa
        console.log('No hay sesión activa o el refresh token expiró');
      } finally {
        setIsLoading(false);
      }
    };

    initializeAuth();
  }, []);

  /**
   * Función de login.
   * Envía las credenciales al backend y almacena el JWT en memoria.
   */
  const login = useCallback(async (credentials: LoginCredentials) => {
    setIsLoading(true);
    try {
      // authApi.login extrae el JWT del header Authorization
      const token = await authApi.login(credentials);
      setAccessToken(token);
    } finally {
      setIsLoading(false);
    }
  }, []);

  /**
   * Función de logout.
   * Limpia el JWT de memoria y notifica al backend para invalidar el refresh token.
   */
  const logout = useCallback(async () => {
    // Primero llamar al backend para invalidar el refresh token y borrar la cookie
    try {
      await authApi.logout();
    } catch (error) {
      console.error('Error al hacer logout en el servidor:', error);
    }
    // Luego limpiar el token de memoria
    setAccessToken(null);
  }, []);

  // Determinar si el usuario está autenticado basándose en la presencia del JWT
  const isAuthenticated = accessToken !== null;

  const value: AuthContextType = {
    accessToken,
    isAuthenticated,
    isLoading,
    login,
    logout,
    setAccessToken,
  };

  return (
    <AuthContext.Provider value={value}>
      {children}
    </AuthContext.Provider>
  );
}

/**
 * Hook personalizado para acceder al contexto de autenticación.
 * 
 * Uso:
 * const { isAuthenticated, login, logout } = useAuth();
 */
export function useAuth(): AuthContextType {
  const context = useContext(AuthContext);
  if (context === undefined) {
    throw new Error('useAuth debe ser usado dentro de un AuthProvider');
  }
  return context;
}
