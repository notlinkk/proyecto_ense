import apiClient from './apiClient';
import { 
  User, 
  Lesson, 
  Module, 
  PageResponse, 
  CreateLessonDTO, 
  CreateModuleDTO,
  Subscription,
  CreateSubscriptionDTO,
  AccessCheckResponse
} from '../types';

/**
 * API para endpoints protegidos.
 * 
 * Estos endpoints requieren autenticación (JWT válido).
 * El interceptor de apiClient añade automáticamente el header Authorization.
 */
export const protectedApi = {
  // ==================== USER ENDPOINTS ====================

  /**
   * Obtiene la información del usuario actual autenticado.
   */
  getCurrentUser: async (): Promise<User> => {
    const response = await apiClient.get<User>('/users/me');
    return response.data;
  },

  /**
   * Obtiene un usuario por su username.
   */
  getUser: async (username: string): Promise<User> => {
    const response = await apiClient.get<User>(`/users/${username}`);
    return response.data;
  },

  /**
   * Obtiene las lecciones creadas por el usuario actual.
   * El count se puede obtener del campo totalElements de la respuesta.
   */
  getMyLessons: async (page = 0, size = 10): Promise<PageResponse<Lesson>> => {
    const response = await apiClient.get<PageResponse<Lesson>>('/users/me/lessons', {
      params: { page, size }
    });
    return response.data;
  },

  /**
   * Obtener las suscripciones del usuario actual.
   */
  getMySubscriptions: async (page = 0, size = 10): Promise<PageResponse<Subscription>> => {
    const response = await apiClient.get<PageResponse<Subscription>>('/users/me/subscriptions', {
      params: { page, size }
    });
    return response.data;
  },

  // ==================== LESSON ENDPOINTS ====================

  /**
   * Obtiene todas las lecciones con paginación.
   */
  getLessons: async (page = 0, size = 10): Promise<PageResponse<Lesson>> => {
    const response = await apiClient.get<PageResponse<Lesson>>('/lessons', {
      params: { page, size }
    });
    return response.data;
  },

  /**
   * Obtiene una lección por su ID.
   */
  getLesson: async (id: string): Promise<Lesson> => {
    const response = await apiClient.get<Lesson>(`/lessons/${id}`);
    return response.data;
  },

  /**
   * Busca lecciones por nombre.
   */
  searchLessons: async (nombre: string, page = 0, size = 10): Promise<PageResponse<Lesson>> => {
    const response = await apiClient.get<PageResponse<Lesson>>('/lessons', {
      params: { nombre, page, size }
    });
    return response.data;
  },

  /**
   * Crea una nueva lección.
   */
  createLesson: async (lesson: CreateLessonDTO): Promise<Lesson> => {
    const response = await apiClient.post<Lesson>('/lessons', lesson);
    return response.data;
  },

  /**
   * Elimina una lección por su ID.
   */
  deleteLesson: async (id: string): Promise<void> => {
    await apiClient.delete(`/lessons/${id}`);
  },

  // ==================== MODULE ENDPOINTS ====================

  /**
   * Obtiene todos los módulos con paginación.
   */
  getModules: async (page = 0, size = 10): Promise<PageResponse<Module>> => {
    const response = await apiClient.get<PageResponse<Module>>('/modules', {
      params: { page, size }
    });
    return response.data;
  },

  /**
   * Obtiene un módulo por su ID.
   */
  getModule: async (id: string): Promise<Module> => {
    const response = await apiClient.get<Module>(`/modules/${id}`);
    return response.data;
  },

  /**
   * Crea un nuevo módulo.
   */
  createModule: async (module: CreateModuleDTO): Promise<Module> => {
    const response = await apiClient.post<Module>('/modules', module);
    return response.data;
  },

  /**
   * Elimina un módulo por su ID.
   */
  deleteModule: async (id: string): Promise<void> => {
    await apiClient.delete(`/modules/${id}`);
  },

  // ==================== SUBSCRIPTION ENDPOINTS ====================

  /**
   * Suscribirse a una lección.
   */
  subscribeToLesson: async (lessonId: string): Promise<Subscription> => {
    const dto: CreateSubscriptionDTO = { lessonId };
    const response = await apiClient.post<Subscription>('/subscriptions', dto);
    return response.data;
  },

  /**
   * Verificar si tengo acceso a una lección (soy owner o estoy suscrito).
   */
  checkLessonAccess: async (lessonId: string): Promise<boolean> => {
    const response = await apiClient.get<AccessCheckResponse>(`/subscriptions/check/${lessonId}`);
    return response.data.hasAccess;
  },

  /**
   * Cancelar suscripción.
   */
  unsubscribe: async (subscriptionId: string): Promise<void> => {
    await apiClient.delete(`/subscriptions/${subscriptionId}`);
  },
};
