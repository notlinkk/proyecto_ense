/**
 * Tipos para las entidades del sistema de enseñanza.
 * Basados en las entidades del backend Spring Boot.
 */

/**
 * Representa una habilidad/competencia que se puede adquirir.
 */
export interface Ability {
  name: string;
  description?: string;
}

/**
 * Representa un módulo dentro de una lección.
 * Los módulos son las unidades de contenido que componen una lección.
 */
export interface Module {
  id: string;
  title: string;
  description: string;
  content: string;      // Puede ser texto, URL de video, etc.
  duration: number;     // Duración en minutos
  position: number;     // Orden del módulo dentro de la lección
}

/**
 * Representa una lección del sistema.
 * Una lección contiene múltiples módulos y está asociada a habilidades.
 */
export interface Lesson {
  id: string;
  name: string;
  description: string;
  ownerId: string;
  ownerName?: string;
  price?: number;
  imageUrl?: string;
  modules?: Module[];
  abilities?: Ability[];
}

/**
 * Representa un rol del sistema.
 */
export interface Role {
  rolename: string;
}

/**
 * Representa un usuario del sistema.
 */
export interface User {
  username: string;
  name: string;
  surname1: string;
  surname2?: string;
  email: string;
  lessons?: Lesson[];
  roles?: Role[];
}

/**
 * Respuesta paginada del backend.
 */
export interface PageResponse<T> {
  content: T[];
  pageable: {
    pageNumber: number;
    pageSize: number;
  };
  totalElements: number;
  totalPages: number;
  first: boolean;
  last: boolean;
  empty: boolean;
}

/**
 * DTO para crear una nueva lección.
 * Solo contiene los atributos simples necesarios.
 * Debe incluir al menos una habilidad.
 */
export interface CreateLessonDTO {
  name: string;
  description: string;
  imageUrl?: string;
  abilities: string[];  // Nombres de las habilidades
}

/**
 * DTO para crear un nuevo módulo.
 * Solo contiene los atributos simples necesarios.
 */
export interface CreateModuleDTO {
  title: string;
  description: string;
  content: string;
  duration: number;
  position: number;
  lessonId: string;
}

/**
 * Representa una suscripción a una lección.
 */
export interface Subscription {
  id: string;
  startDate: string;
  endDate?: string;
  active: boolean;
  lesson?: Lesson;
}

/**
 * DTO para crear una suscripción.
 */
export interface CreateSubscriptionDTO {
  lessonId: string;
}

/**
 * Respuesta de verificación de acceso a una lección.
 */
export interface AccessCheckResponse {
  hasAccess: boolean;
}
