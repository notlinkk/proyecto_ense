import { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { protectedApi } from '../api';
import { Lesson, User } from '../types';
import { LessonCard, LoadingSpinner } from '../components';
import '../styles/HomePage.css';

/**
 * Página de inicio.
 * 
 * Muestra:
 * - Saludo personalizado al usuario
 * - Lista de lecciones destacadas
 * - Accesos rápidos a las principales secciones
 */
function HomePage() {
  const [user, setUser] = useState<User | null>(null);
  const [lessons, setLessons] = useState<Lesson[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    const fetchData = async () => {
      try {
        setIsLoading(true);
        
        // Cargar datos del usuario y lecciones en paralelo
        const [userData, lessonsData] = await Promise.all([
          protectedApi.getCurrentUser(),
          protectedApi.getLessons(0, 6) // Mostrar las primeras 6 lecciones
        ]);

        setUser(userData);
        setLessons(lessonsData.content || []);
      } catch (err) {
        console.error('Error al cargar datos:', err);
        setError('No se pudieron cargar los datos. Por favor, recarga la página.');
      } finally {
        setIsLoading(false);
      }
    };

    fetchData();
  }, []);

  if (isLoading) {
    return <LoadingSpinner message="Cargando tu espacio de aprendizaje..." />;
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
    <div className="home-page">
      {/* Hero Section - Saludo */}
      <section className="hero-section">
        <div className="hero-content">
          <h1>
            ¡Hola, <span className="user-name">{user?.name || 'Estudiante'}</span>!
          </h1>
          <p>Bienvenido a tu espacio de aprendizaje. ¿Qué te gustaría aprender hoy?</p>
        </div>
      </section>

      {/* Quick Stats */}
      <section className="stats-section">
        <div className="stat-card">
          <span className="stat-icon">📚</span>
          <div className="stat-info">
            <span className="stat-number">{lessons.length}</span>
            <span className="stat-label">Lecciones disponibles</span>
          </div>
        </div>
        <div className="stat-card">
          <span className="stat-icon">🎯</span>
          <div className="stat-info">
            <span className="stat-number">{user?.lessons?.length || 0}</span>
            <span className="stat-label">Mis lecciones</span>
          </div>
        </div>
      </section>

      {/* Lecciones destacadas */}
      <section className="lessons-section">
        <div className="section-header">
          <h2>Lecciones Destacadas</h2>
          <Link to="/lessons" className="view-all-link">
            Ver todas →
          </Link>
        </div>

        {lessons.length > 0 ? (
          <div className="lessons-grid">
            {lessons.map((lesson) => (
              <LessonCard key={lesson.id} lesson={lesson} />
            ))}
          </div>
        ) : (
          <div className="empty-state">
            <p>No hay lecciones disponibles en este momento.</p>
          </div>
        )}
      </section>

      {/* Quick Actions */}
      <section className="actions-section">
        <h2>Accesos Rápidos</h2>
        <div className="actions-grid">
          <Link to="/lessons" className="action-card">
            <span className="action-icon">📖</span>
            <span className="action-label">Explorar Lecciones</span>
          </Link>
          <Link to="/profile" className="action-card">
            <span className="action-icon">👤</span>
            <span className="action-label">Mi Perfil</span>
          </Link>
        </div>
      </section>
    </div>
  );
}

export default HomePage;
