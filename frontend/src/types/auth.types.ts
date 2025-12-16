/**
 * Tipos e interfaces para la autenticación.
 * 
 * Separamos los tipos para mantener el código organizado
 * y facilitar la reutilización en diferentes partes de la app.
 */

/**
 * Credenciales para el login.
 */
export interface LoginCredentials {
  username: string;
  password: string;
}

/**
 * Datos para el registro de un nuevo usuario.
 */
export interface RegisterData {
  username: string;
  name: string;
  surname1: string;
  surname2?: string;
  email: string;
  password: string;
}

/**
 * Respuesta del endpoint de login.
 * El JWT viene en el header Authorization, no en el body.
 * El refresh token viene como cookie HttpOnly (no accesible desde JS).
 */
export interface LoginResponse {
  // El backend puede devolver datos adicionales en el body si es necesario
  message?: string;
}

/**
 * Estado del contexto de autenticación.
 */
export interface AuthState {
  /** Token JWT en memoria (NUNCA en localStorage por seguridad) */
  accessToken: string | null;
  /** Indica si el usuario está autenticado */
  isAuthenticated: boolean;
  /** Indica si se está verificando la autenticación */
  isLoading: boolean;
}

import { User } from './entities.types';

/**
 * Acciones disponibles en el contexto de autenticación.
 */
export interface AuthContextType extends AuthState {
  /** Usuario actual autenticado */
  user: User | null;
  /** Realiza el login con las credenciales proporcionadas */
  login: (credentials: LoginCredentials) => Promise<void>;
  /** Cierra la sesión del usuario */
  logout: () => void;
  /** Actualiza el access token (usado internamente por el interceptor de axios) */
  setAccessToken: (token: string | null) => void;
}

/**
 * Información decodificada del JWT.
 * Útil para obtener información del usuario sin hacer una llamada al backend.
 */
export interface JWTPayload {
  sub: string;          // Subject (normalmente el username o userId)
  exp: number;          // Timestamp de expiración
  iat: number;          // Timestamp de emisión
  // Añade más campos según lo que tu backend incluya en el JWT
  roles?: string[];
}
