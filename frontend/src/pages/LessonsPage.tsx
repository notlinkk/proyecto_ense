import { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { protectedApi } from '../api';
import { Lesson } from '../types';
import { LessonCard, LoadingSpinner } from '../components';
import '../styles/LessonsPage.css';

/**
 * Página de listado de lecciones.
 * 
 * Muestra todas las lecciones disponibles con:
 * - Búsqueda por nombre
 * - Paginación
 * - Vista en grid
 */
function LessonsPage() {
  const [lessons, setLessons] = useState<Lesson[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [searchTerm, setSearchTerm] = useState('');
  const [currentPage, setCurrentPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const pageSize = 9;

  useEffect(() => {
    fetchLessons();
  }, [currentPage]);

  const fetchLessons = async (search?: string) => {
    try {
      setIsLoading(true);
      setError(null);
      
      const data = search 
        ? await protectedApi.searchLessons(search, currentPage, pageSize)
        : await protectedApi.getLessons(currentPage, pageSize);
      
      setLessons(data.content || []);
      setTotalPages(data.totalPages || 0);
    } catch (err) {
      console.error('Error al cargar lecciones:', err);
      setError('No se pudieron cargar las lecciones.');
    } finally {
      setIsLoading(false);
    }
  };

  const handleSearch = (e: React.FormEvent) => {
    e.preventDefault();
    setCurrentPage(0);
    fetchLessons(searchTerm || undefined);
  };

  const handlePageChange = (page: number) => {
    setCurrentPage(page);
    window.scrollTo({ top: 0, behavior: 'smooth' });
  };

  return (
    <div className="lessons-page">
      {/* Header */}
      <div className="page-header">
        <div className="header-content">
          <div>
            <h1>Lecciones</h1>
            <p>Explora todas las lecciones disponibles</p>
          </div>
          <Link to="/lessons/new" className="create-lesson-btn">
            ➕ Nueva Lección
          </Link>
        </div>
      </div>

      {/* Barra de búsqueda */}
      <form onSubmit={handleSearch} className="search-bar">
        <input
          type="text"
          placeholder="Buscar lecciones..."
          value={searchTerm}
          onChange={(e) => setSearchTerm(e.target.value)}
          className="search-input"
        />
        <button type="submit" className="search-button">
          🔍 Buscar
        </button>
      </form>

      {/* Contenido */}
      {isLoading ? (
        <LoadingSpinner message="Cargando lecciones..." />
      ) : error ? (
        <div className="error-container">
          <p className="error-message">{error}</p>
          <button onClick={() => fetchLessons()}>Reintentar</button>
        </div>
      ) : lessons.length > 0 ? (
        <>
          <div className="lessons-grid">
            {lessons.map((lesson) => (
              <LessonCard key={lesson.id} lesson={lesson} />
            ))}
          </div>

          {/* Paginación */}
          {totalPages > 1 && (
            <div className="pagination">
              <button
                onClick={() => handlePageChange(currentPage - 1)}
                disabled={currentPage === 0}
                className="page-btn"
              >
                ← Anterior
              </button>
              
              <div className="page-numbers">
                {Array.from({ length: totalPages }, (_, i) => (
                  <button
                    key={i}
                    onClick={() => handlePageChange(i)}
                    className={`page-number ${currentPage === i ? 'active' : ''}`}
                  >
                    {i + 1}
                  </button>
                ))}
              </div>

              <button
                onClick={() => handlePageChange(currentPage + 1)}
                disabled={currentPage === totalPages - 1}
                className="page-btn"
              >
                Siguiente →
              </button>
            </div>
          )}
        </>
      ) : (
        <div className="empty-state">
          <p>No se encontraron lecciones.</p>
          {searchTerm && (
            <button onClick={() => { setSearchTerm(''); fetchLessons(); }}>
              Ver todas las lecciones
            </button>
          )}
        </div>
      )}
    </div>
  );
}

export default LessonsPage;
