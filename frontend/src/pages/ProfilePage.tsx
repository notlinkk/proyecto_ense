import { useState, useEffect, useCallback } from 'react';
import { useNavigate, Link, useLocation } from 'react-router-dom';
import { protectedApi } from '../api';
import { useAuth } from '../context/AuthContext';
import { User, Subscription, Lesson } from '../types';
import { LoadingSpinner } from '../components';
import '../styles/ProfilePage.css';

/**
 * Página de perfil del usuario.
 * 
 * Muestra:
 * - Información personal del usuario
 * - Estadísticas de aprendizaje
 * - Opción de convertirse en profesor
 * - Opción de cerrar sesión
 */
function ProfilePage() {
  const { logout } = useAuth();
  const navigate = useNavigate();
  const location = useLocation();
  
  const [user, setUser] = useState<User | null>(null);
  const [subscriptions, setSubscriptions] = useState<Subscription[]>([]);
  const [myLessons, setMyLessons] = useState<Lesson[]>([]);
  const [lessonsCount, setLessonsCount] = useState(0);
  const [isLoading, setIsLoading] = useState(true);
  const [isBecomingTeacher, setIsBecomingTeacher] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const fetchData = useCallback(async () => {
    try {
      setIsLoading(true);
      const [userData, subsData, lessonsData] = await Promise.all([
        protectedApi.getCurrentUser(),
        protectedApi.getMySubscriptions(0, 10),
        protectedApi.getMyLessons(0, 10)
      ]);
      setUser(userData);
      setSubscriptions(subsData.content || []);
      setMyLessons(lessonsData.content || []);
      setLessonsCount(lessonsData.totalElements || 0);
    } catch (err) {
      console.error('Error al cargar perfil:', err);
      setError('No se pudo cargar tu perfil.');
    } finally {
      setIsLoading(false);
    }
  }, []);

  // Refetch data every time the page is visited
  useEffect(() => {
    fetchData();
  }, [fetchData, location.key]);

  const handleLogout = () => {
    logout();
    navigate('/login', { replace: true });
  };

  // Check if user has TEACHER or ADMIN role
  const isTeacher = user?.roles?.some(
    role => role.rolename === 'TEACHER' || role.rolename === 'ADMIN'
  ) ?? false;

  const handleBecomeTeacher = async () => {
    try {
      setIsBecomingTeacher(true);
      const updatedUser = await protectedApi.becomeTeacher();
      setUser(updatedUser);
    } catch (err) {
      console.error('Error al convertirse en profesor:', err);
      setError('No se pudo completar la solicitud. Inténtalo de nuevo.');
    } finally {
      setIsBecomingTeacher(false);
    }
  };

  if (isLoading) {
    return <LoadingSpinner message="Cargando tu perfil..." />;
  }

  if (error) {
    return (
      <div className="error-container">
        <p className="error-message">{error}</p>
        <button onClick={() => window.location.reload()}>Reintentar</button>
      </div>
    );
  }

  return (
    <div className="profile-page">
      <div className="profile-container">
        {/* Avatar y nombre */}
        <div className="profile-header">
          <div className="avatar">
            <span className="avatar-text">
              {user?.name?.charAt(0).toUpperCase() || 'U'}
            </span>
          </div>
          <h1 className="user-fullname">
            {user?.name} {user?.surname1} {user?.surname2 || ''}
          </h1>
          <span className="username">@{user?.username}</span>
        </div>

        {/* Información del usuario */}
        <section className="profile-section">
          <h2>Información Personal</h2>
          <div className="info-grid">
            <div className="info-item">
              <span className="info-label">Nombre de usuario</span>
              <span className="info-value">{user?.username}</span>
            </div>
            <div className="info-item">
              <span className="info-label">Correo electrónico</span>
              <span className="info-value">{user?.email}</span>
            </div>
            <div className="info-item">
              <span className="info-label">Nombre completo</span>
              <span className="info-value">
                {user?.name} {user?.surname1} {user?.surname2 || ''}
              </span>
            </div>
          </div>
        </section>

        {/* Estadísticas */}
        <section className="profile-section">
          <h2>Actividad</h2>
          <div className="stats-grid">
            <div className="stat-item">
              <span className="stat-number">{lessonsCount}</span>
              <span className="stat-label">Lecciones creadas</span>
            </div>
            <div className="stat-item">
              <span className="stat-number">{subscriptions.length}</span>
              <span className="stat-label">Suscripciones activas</span>
            </div>
          </div>
        </section>

        {/* Convertirse en Profesor */}
        {!isTeacher && (
          <section className="profile-section teacher-section">
            <h2>Conviértete en Profesor</h2>
            <p className="teacher-info">
              ¿Quieres compartir tus conocimientos? Conviértete en profesor y podrás 
              crear tus propias lecciones.
            </p>
            <button 
              onClick={handleBecomeTeacher} 
              className="become-teacher-button"
              disabled={isBecomingTeacher}
            >
              {isBecomingTeacher ? 'Procesando...' : 'Convertirme en Profesor'}
            </button>
          </section>
        )}

        {/* Estado de Profesor */}
        {isTeacher && (
          <section className="profile-section teacher-section teacher-active">
            <h2>Eres Profesor</h2>
            <p className="teacher-info">
              Tienes permisos de profesor. Puedes crear y gestionar lecciones.
            </p>
            <Link to="/lessons/new" className="create-lesson-link">
              Crear Nueva Lección
            </Link>
          </section>
        )}

        {/* Lecciones del usuario */}
        {myLessons.length > 0 && (
          <section className="profile-section">
            <h2>Mis Lecciones Creadas</h2>
            <div className="user-lessons">
              {myLessons.map((lesson) => (
                <Link to={`/lessons/${lesson.id}`} key={lesson.id} className="user-lesson-item">
                  <span className="lesson-name">{lesson.name}</span>
                  <span className="lesson-description">{lesson.description}</span>
                </Link>
              ))}
            </div>
          </section>
        )}

        {/* Suscripciones */}
        {subscriptions.length > 0 && (
          <section className="profile-section">
            <h2>Mis Suscripciones</h2>
            <div className="user-lessons">
              {subscriptions.map((sub) => (
                <Link to={`/lessons/${sub.lesson?.id}`} key={sub.id} className="user-lesson-item subscription-item">
                  <span className="lesson-name">{sub.lesson?.name || 'Lección'}</span>
                  <span className="subscription-date">Desde: {sub.startDate}</span>
                </Link>
              ))}
            </div>
          </section>
        )}

        {/* Acciones */}
        <section className="profile-section">
          <button onClick={handleLogout} className="logout-button">
            Cerrar Sesión
          </button>
        </section>
      </div>
    </div>
  );
}

export default ProfilePage;
