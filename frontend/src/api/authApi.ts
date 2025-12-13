import apiClient from './apiClient';
import { LoginCredentials, RegisterData } from '../types';

/**
 * API de autenticación.
 * 
 * Encapsula todas las llamadas relacionadas con autenticación.
 * Esto mantiene la lógica de autenticación separada del resto de la app.
 */
export const authApi = {
  /**
   * Realiza el login con las credenciales proporcionadas.
   * 
   * @param credentials - Username y password
   * @returns El JWT extraído del header Authorization
   * 
   * Flujo:
   * 1. Envía POST /auth/login con { username, password }
   * 2. El backend valida las credenciales
   * 3. Si son válidas:
   *    - Devuelve el JWT en el header Authorization
   *    - Establece una cookie HttpOnly con el refresh token
   * 4. Extraemos el JWT del header y lo devolvemos
   * 
   * IMPORTANTE: El refresh token queda en una cookie HttpOnly
   * y NO es accesible desde JavaScript (protección contra XSS)
   */
  login: async (credentials: LoginCredentials): Promise<string> => {
    const response = await apiClient.post('/auth/login', credentials);
    
    // El JWT viene en el header Authorization
    // Formato: "Bearer <token>" - Spring Boot usa setBearerAuth()
    // Nota: El header puede venir en minúsculas dependiendo del navegador
    const authHeader = response.headers['authorization'] || response.headers['Authorization'];
    
    if (!authHeader) {
      throw new Error('No se recibió el token de autenticación');
    }
    
    // Quitar el prefijo "Bearer " para obtener solo el token
    const token = authHeader.replace('Bearer ', '');
    
    return token;
  },

  /**
   * Intenta obtener un nuevo JWT usando el refresh token.
   * 
   * @returns El nuevo JWT o null si no hay sesión activa
   * 
   * Este endpoint se llama:
   * 1. Al cargar la aplicación (para recuperar la sesión)
   * 2. Cuando un request devuelve 401 (token expirado)
   * 
   * ¿Cómo funciona?
   * - La cookie HttpOnly con el refresh token se envía automáticamente
   * - El backend valida el refresh token
   * - Si es válido, devuelve un nuevo JWT en el header Authorization
   * - Si no es válido, devuelve 401 y el usuario debe hacer login
   */
  refreshToken: async (): Promise<string | null> => {
    try {
      const response = await apiClient.post('/auth/refresh');
      
      // Buscar el header Authorization (case-insensitive)
      const authHeader = response.headers['authorization'] || response.headers['Authorization'];
      
      if (!authHeader) {
        return null;
      }
      
      return authHeader.replace('Bearer ', '');
    } catch (error) {
      // Si falla, probablemente no hay sesión activa
      return null;
    }
  },

  /**
   * Registra un nuevo usuario.
   * 
   * @param data - Datos del nuevo usuario
   * @returns El usuario creado
   */
  register: async (data: RegisterData): Promise<void> => {
    await apiClient.post('/auth/register', data);
  },

  /**
   * Cierra la sesión del usuario.
   * 
   * Opcional: Puedes implementar un endpoint en el backend
   * que invalide el refresh token en el servidor.
   * 
   * Aunque el JWT en memoria se borre, el refresh token
   * seguiría siendo válido hasta que expire.
   * Un endpoint de logout en el backend puede:
   * - Añadir el refresh token a una blacklist
   * - Borrar la cookie del refresh token
   */
  logout: async (): Promise<void> => {
    try {
      await apiClient.post('/auth/logout');
    } catch (error) {
      // Ignorar errores - el logout local ya se hizo
      console.error('Error al hacer logout en el servidor:', error);
    }
  },
};
