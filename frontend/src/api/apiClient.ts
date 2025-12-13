import axios, { AxiosError, InternalAxiosRequestConfig } from 'axios';

/**
 * Cliente HTTP base configurado con axios.
 * 
 * CONFIGURACIÓN DE SEGURIDAD IMPORTANTE:
 * 
 * withCredentials: true
 * - Permite que las cookies HttpOnly se envíen automáticamente
 * - Necesario para que el refresh token (cookie) llegue al backend
 * - Sin esto, el navegador no incluiría la cookie en las requests
 * 
 * La URL base apunta al proxy de Vite en desarrollo,
 * que redirige a http://localhost:8080 (backend Spring Boot)
 */
const apiClient = axios.create({
  baseURL: '/api', // Usa el proxy de Vite en desarrollo
  headers: {
    'Content-Type': 'application/json',
    'API-Version': '0', // Use version 0 which returns standard Page format
  },
  // CRÍTICO: Necesario para enviar/recibir cookies HttpOnly
  withCredentials: true,
});

/**
 * Referencia al token de acceso actual.
 * Se actualiza desde el AuthContext cuando el usuario hace login.
 * 
 * ¿Por qué una variable global en lugar de leerlo del Context?
 * - Los interceptores de axios se ejecutan fuera del ciclo de React
 * - No podemos usar hooks (useAuth) dentro de los interceptores
 * - Esta referencia nos permite acceder al token desde los interceptores
 */
let currentAccessToken: string | null = null;

/**
 * Función para actualizar el token de acceso.
 * Llamada desde el AuthContext cuando el token cambia.
 */
export const setAuthToken = (token: string | null) => {
  currentAccessToken = token;
};

/**
 * Función para obtener el token actual.
 * Útil para debugging o verificaciones.
 */
export const getAuthToken = () => currentAccessToken;

/**
 * Callback para manejar cuando el refresh token falla.
 * Se configura desde la aplicación para ejecutar el logout.
 */
let onRefreshTokenFailed: (() => void) | null = null;

export const setOnRefreshTokenFailed = (callback: () => void) => {
  onRefreshTokenFailed = callback;
};

/**
 * Flag para evitar múltiples intentos de refresh simultáneos.
 * 
 * Escenario: Si varias requests fallan con 401 al mismo tiempo,
 * sin este flag, cada una intentaría hacer refresh por separado.
 * Con el flag, solo la primera hace refresh, las demás esperan.
 */
let isRefreshing = false;

/**
 * Cola de requests pendientes mientras se hace refresh.
 * 
 * Cuando una request falla con 401 y ya hay un refresh en progreso,
 * la request se añade a esta cola y espera a que el refresh termine.
 */
let failedQueue: Array<{
  resolve: (token: string) => void;
  reject: (error: Error) => void;
}> = [];

/**
 * Procesa la cola de requests pendientes después del refresh.
 * 
 * @param error - Error si el refresh falló
 * @param token - Nuevo token si el refresh tuvo éxito
 */
const processQueue = (error: Error | null, token: string | null = null) => {
  failedQueue.forEach(prom => {
    if (error) {
      prom.reject(error);
    } else if (token) {
      prom.resolve(token);
    }
  });
  failedQueue = [];
};

/**
 * INTERCEPTOR DE REQUEST
 * 
 * Se ejecuta ANTES de cada request.
 * Añade automáticamente el header Authorization con el JWT.
 * 
 * ¿Por qué no guardamos el JWT en localStorage?
 * - localStorage es accesible desde cualquier script JavaScript
 * - Un ataque XSS podría robar el token
 * - La memoria de React es más segura (no persiste, no es accesible)
 */
apiClient.interceptors.request.use(
  (config: InternalAxiosRequestConfig) => {
    // Si hay un token en memoria, añadirlo al header
    if (currentAccessToken) {
      config.headers.Authorization = `Bearer ${currentAccessToken}`;
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

/**
 * INTERCEPTOR DE RESPONSE
 * 
 * Se ejecuta DESPUÉS de cada response.
 * Maneja automáticamente los errores 401 (no autorizado).
 * 
 * Flujo cuando recibimos un 401:
 * 1. Verificar si ya hay un refresh en progreso
 * 2. Si no, iniciar el refresh
 * 3. Si el refresh tiene éxito, reintentar la request original
 * 4. Si el refresh falla, redirigir a /login
 * 
 * ¿Por qué el refresh token funciona automáticamente?
 * - Está en una cookie HttpOnly con withCredentials: true
 * - El navegador la envía automáticamente al backend
 * - El backend la valida y devuelve un nuevo JWT en el header
 */
apiClient.interceptors.response.use(
  // Las respuestas exitosas pasan sin modificación
  (response) => response,
  
  // Manejar errores
  async (error: AxiosError) => {
    const originalRequest = error.config as InternalAxiosRequestConfig & { _retry?: boolean };

    // Solo manejar errores 401 (no autorizado)
    // Y solo si no es un reintento (evitar bucles infinitos)
    if (error.response?.status === 401 && !originalRequest._retry) {
      // Si la request que falló es el refresh, no reintentar
      if (originalRequest.url === '/auth/refresh') {
        if (onRefreshTokenFailed) {
          onRefreshTokenFailed();
        }
        return Promise.reject(error);
      }

      // Si ya hay un refresh en progreso, esperar a que termine
      if (isRefreshing) {
        return new Promise((resolve, reject) => {
          failedQueue.push({
            resolve: (token: string) => {
              originalRequest.headers.Authorization = `Bearer ${token}`;
              resolve(apiClient(originalRequest));
            },
            reject: (err: Error) => {
              reject(err);
            },
          });
        });
      }

      // Marcar la request como reintento
      originalRequest._retry = true;
      isRefreshing = true;

      try {
        // Intentar obtener un nuevo token usando el refresh token (cookie)
        const response = await apiClient.post('/auth/refresh');
        
        // Extraer el nuevo JWT del header Authorization (case-insensitive)
        const authHeader = response.headers['authorization'] || response.headers['Authorization'];
        const newToken = authHeader?.replace('Bearer ', '');

        if (newToken) {
          // Actualizar el token en memoria
          currentAccessToken = newToken;
          
          // Procesar las requests que estaban esperando
          processQueue(null, newToken);
          
          // Reintentar la request original con el nuevo token
          originalRequest.headers.Authorization = `Bearer ${newToken}`;
          return apiClient(originalRequest);
        } else {
          throw new Error('No se recibió un nuevo token');
        }
      } catch (refreshError) {
        // El refresh falló - el usuario debe hacer login de nuevo
        processQueue(refreshError as Error, null);
        currentAccessToken = null;
        
        // Notificar a la aplicación para que haga logout
        if (onRefreshTokenFailed) {
          onRefreshTokenFailed();
        }
        
        return Promise.reject(refreshError);
      } finally {
        isRefreshing = false;
      }
    }

    // Para otros errores, simplemente rechazar la promesa
    return Promise.reject(error);
  }
);

export default apiClient;
