import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { protectedApi } from '../api';
import { LoadingSpinner } from '../components';
import '../styles/CreateLessonPage.css';

/**
 * Página para crear una nueva lección.
 * Formulario con campos para nombre y descripción.
 */
export function CreateLessonPage() {
  const navigate = useNavigate();
  const [name, setName] = useState('');
  const [description, setDescription] = useState('');
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    
    if (!name.trim()) {
      setError('El nombre es obligatorio');
      return;
    }

    setLoading(true);
    setError(null);

    try {
      const newLesson = await protectedApi.createLesson({
        name: name.trim(),
        description: description.trim()
      });
      
      // Navegar a la página de la lección creada
      navigate(`/lessons/${newLesson.id}`);
    } catch (err) {
      console.error('Error creating lesson:', err);
      setError('Error al crear la lección. Por favor, intenta de nuevo.');
    } finally {
      setLoading(false);
    }
  };

  const handleCancel = () => {
    navigate('/lessons');
  };

  return (
    <div className="create-lesson-page">
      <div className="create-lesson-container">
        <h1>Crear Nueva Lección</h1>
        
        <form onSubmit={handleSubmit} className="create-lesson-form">
          {error && (
            <div className="form-error">
              {error}
            </div>
          )}

          <div className="form-group">
            <label htmlFor="name">Nombre de la Lección *</label>
            <input
              type="text"
              id="name"
              value={name}
              onChange={(e) => setName(e.target.value)}
              placeholder="Ej: Introducción a React"
              disabled={loading}
              autoFocus
            />
          </div>

          <div className="form-group">
            <label htmlFor="description">Descripción</label>
            <textarea
              id="description"
              value={description}
              onChange={(e) => setDescription(e.target.value)}
              placeholder="Describe el contenido y objetivos de esta lección..."
              rows={4}
              disabled={loading}
            />
          </div>

          <div className="form-actions">
            <button 
              type="button" 
              onClick={handleCancel}
              className="btn-secondary"
              disabled={loading}
            >
              Cancelar
            </button>
            <button 
              type="submit" 
              className="btn-primary"
              disabled={loading}
            >
              {loading ? <LoadingSpinner /> : 'Crear Lección'}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}
