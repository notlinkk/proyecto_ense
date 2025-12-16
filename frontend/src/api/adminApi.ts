import apiClient from './apiClient';
import { User, Lesson, Subscription, Ability, PageResponse } from '../types';

/**
 * API para endpoints de administración.
 * Solo accesibles para usuarios con rol ADMIN.
 */
export const adminApi = {
  // ==================== USER ADMIN ENDPOINTS ====================

  /**
   * Obtiene todos los usuarios (admin only).
   */
  getAllUsers: async (page = 0, size = 10): Promise<PageResponse<User>> => {
    const response = await apiClient.get<PageResponse<User>>('/users', {
      params: { page, size }
    });
    return response.data;
  },

  /**
   * Elimina un usuario por su username (admin only).
   */
  deleteUser: async (username: string): Promise<void> => {
    await apiClient.delete(`/users/${username}`);
  },

  /**
   * Actualiza parcialmente un usuario (admin only).
   * Envía un objeto con los campos a actualizar.
   */
  updateUser: async (username: string, data: Partial<User>): Promise<User> => {
    const response = await apiClient.patch<User>(`/users/${username}`, data);
    return response.data;
  },

  /**
   * Crea un nuevo usuario (admin only).
   */
  createUser: async (user: { username: string; password: string; name: string; surname1: string; surname2?: string; email: string }): Promise<User> => {
    const response = await apiClient.post<User>('/users', user);
    return response.data;
  },

  // ==================== LESSON ADMIN ENDPOINTS ====================

  /**
   * Obtiene todas las lecciones incluyendo las propias (admin only).
   */
  getAllLessons: async (page = 0, size = 10): Promise<PageResponse<Lesson>> => {
    // Use a special header or param to indicate admin request
    const response = await apiClient.get<PageResponse<Lesson>>('/lessons', {
      params: { page, size, admin: true }
    });
    return response.data;
  },

  // ==================== SUBSCRIPTION ADMIN ENDPOINTS ====================

  /**
   * Obtiene todas las suscripciones (admin only).
   */
  getAllSubscriptions: async (page = 0, size = 10): Promise<PageResponse<Subscription>> => {
    const response = await apiClient.get<PageResponse<Subscription>>('/subscriptions', {
      params: { page, size }
    });
    return response.data;
  },

  // ==================== ABILITY ADMIN ENDPOINTS ====================

  /**
   * Elimina una habilidad (admin only).
   */
  deleteAbility: async (name: string): Promise<void> => {
    await apiClient.delete(`/abilities/${name}`);
  },

  /**
   * Crea una nueva habilidad (admin only).
   */
  createAbility: async (ability: { name: string; description: string }): Promise<void> => {
    await apiClient.post('/abilities', ability);
  },

  /**
   * Actualiza parcialmente una habilidad (admin only).
   */
  updateAbility: async (name: string, data: Partial<Ability>): Promise<Ability> => {
    const response = await apiClient.patch<Ability>(`/abilities/${name}`, data);
    return response.data;
  },
};
