import { useState, useEffect } from 'react';
import { useParams, Link, useNavigate } from 'react-router-dom';
import { protectedApi } from '../api';
import { Lesson, Module, User } from '../types';
import { ModuleItem, LoadingSpinner, CreateModuleForm } from '../components';
import '../styles/LessonDetailPage.css';

/**
 * Página de detalle de una lección.
 * 
 * Muestra:
 * - Información completa de la lección
 * - Lista de módulos expandibles (solo si tiene acceso)
 * - Habilidades asociadas
 * - Botón de suscripción si no tiene acceso
 * - Formulario para añadir nuevos módulos (solo si es owner)
 */
function LessonDetailPage() {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  
  const [lesson, setLesson] = useState<Lesson | null>(null);
  const [currentUser, setCurrentUser] = useState<User | null>(null);
  const [hasAccess, setHasAccess] = useState(false);
  const [isLoading, setIsLoading] = useState(true);
  const [isSubscribing, setIsSubscribing] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [expandedModule, setExpandedModule] = useState<number | null>(0);
  const [showAddModule, setShowAddModule] = useState(false);

  const isOwner = currentUser && lesson && lesson.ownerId === currentUser.username;

  useEffect(() => {
    const fetchData = async () => {
      if (!id) return;
      
      try {
        setIsLoading(true);
        
        // Fetch lesson, user, and access status in parallel
        const [lessonData, userData, accessStatus] = await Promise.all([
          protectedApi.getLesson(id),
          protectedApi.getCurrentUser(),
          protectedApi.checkLessonAccess(id)
        ]);
        
        setLesson(lessonData);
        setCurrentUser(userData);
        setHasAccess(accessStatus);
      } catch (err) {
        console.error('Error al cargar la lección:', err);
        setError('No se pudo cargar la lección.');
      } finally {
        setIsLoading(false);
      }
    };

    fetchData();
  }, [id]);

  const handleSubscribe = async () => {
    if (!id) return;
    
    try {
      setIsSubscribing(true);
      await protectedApi.subscribeToLesson(id);
      setHasAccess(true);
    } catch (err) {
      console.error('Error al suscribirse:', err);
      setError('No se pudo completar la suscripción.');
    } finally {
      setIsSubscribing(false);
    }
  };

  const handleModuleToggle = (index: number) => {
    setExpandedModule(expandedModule === index ? null : index);
  };

  if (isLoading) {
    return <LoadingSpinner message="Cargando lección..." />;
  }

  if (error || !lesson) {
    return (
      <div className="error-container">
        <p className="error-message">{error || 'Lección no encontrada'}</p>
        <button onClick={() => navigate('/lessons')}>Volver a lecciones</button>
      </div>
    );
  }

  // Ordenar módulos por posición
  const sortedModules = [...(lesson.modules || [])].sort((a, b) => a.position - b.position);
  const totalDuration = sortedModules.reduce((acc, m) => acc + m.duration, 0);

  return (
    <div className="lesson-detail-page">
      {/* Breadcrumb */}
      <nav className="breadcrumb">
        <Link to="/lessons">Lecciones</Link>
        <span className="separator">/</span>
        <span className="current">{lesson.name}</span>
      </nav>

      {/* Header de la lección */}
      <header className="lesson-header">
        <div className="lesson-header-content">
          <div>
            <h1>{lesson.name}</h1>
            <p className="lesson-description">{lesson.description}</p>
            
            <div className="lesson-stats">
              <span className="stat">
                📚 {sortedModules.length} {sortedModules.length === 1 ? 'módulo' : 'módulos'}
              </span>
              <span className="stat">
                ⏱️ {totalDuration} min en total
              </span>
              {isOwner && (
                <span className="stat owner-badge">
                  👑 Eres el creador
                </span>
              )}
            </div>
          </div>

          {/* Subscribe button for non-owners without access */}
          {!isOwner && !hasAccess && (
            <button 
              className="subscribe-button"
              onClick={handleSubscribe}
              disabled={isSubscribing}
            >
              {isSubscribing ? '⏳ Suscribiendo...' : '🔔 Suscribirse'}
            </button>
          )}

          {!isOwner && hasAccess && (
            <span className="subscribed-badge">✅ Suscrito</span>
          )}
        </div>

        {/* Habilidades */}
        {lesson.abilities && lesson.abilities.length > 0 && (
          <div className="abilities-section">
            <h3>Habilidades que desarrollarás:</h3>
            <div className="abilities-list">
              {lesson.abilities.map((ability) => (
                <span key={ability.name} className="ability-badge">
                  🎯 {ability.name}
                </span>
              ))}
            </div>
          </div>
        )}
      </header>

      {/* Contenido - Módulos (solo visible si tiene acceso) */}
      {hasAccess ? (
        <section className="modules-section">
          <div className="modules-header">
            <h2>Contenido del Curso</h2>
            {isOwner && !showAddModule && (
              <button 
                className="add-module-btn"
                onClick={() => setShowAddModule(true)}
              >
                ➕ Añadir Módulo
              </button>
            )}
          </div>
          
          {sortedModules.length > 0 ? (
            <div className="modules-list">
              {sortedModules.map((module, index) => (
                <ModuleItem
                  key={module.id}
                  module={module}
                  index={index}
                  isExpanded={expandedModule === index}
                  onToggle={() => handleModuleToggle(index)}
                />
              ))}
            </div>
          ) : (
            <div className="empty-modules">
              <p>Esta lección aún no tiene módulos.</p>
            </div>
          )}

          {/* Formulario para añadir módulo (solo owner) */}
          {isOwner && showAddModule && id && (
            <CreateModuleForm
              lessonId={id}
              onModuleCreated={(newModule: Module) => {
                setLesson(prev => prev ? {
                  ...prev,
                  modules: [...(prev.modules || []), newModule]
                } : null);
                setShowAddModule(false);
              }}
              onCancel={() => setShowAddModule(false)}
            />
          )}
        </section>
      ) : (
        <section className="modules-section locked-section">
          <div className="locked-content">
            <span className="lock-icon">🔒</span>
            <h2>Contenido Bloqueado</h2>
            <p>Suscríbete a esta lección para acceder a los {sortedModules.length} módulos.</p>
            <button 
              className="subscribe-button-large"
              onClick={handleSubscribe}
              disabled={isSubscribing}
            >
              {isSubscribing ? '⏳ Suscribiendo...' : '🔔 Suscribirse para ver el contenido'}
            </button>
          </div>
        </section>
      )}

      {/* Botón de acción */}
      <div className="lesson-actions">
        {hasAccess && sortedModules.length > 0 && (
          <button 
            className="start-button"
            onClick={() => setExpandedModule(0)}
          >
            🚀 Comenzar Lección
          </button>
        )}
        <Link to="/lessons" className="back-link">
          ← Volver a lecciones
        </Link>
      </div>
    </div>
  );
}

export default LessonDetailPage;
