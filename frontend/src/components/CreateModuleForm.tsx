import { useState } from 'react';
import { protectedApi } from '../api';
import { Module } from '../types';
import { LoadingSpinner } from '../components';
import '../styles/CreateModuleForm.css';

interface CreateModuleFormProps {
  lessonId: string;
  onModuleCreated: (module: Module) => void;
  onCancel: () => void;
}

/**
 * Formulario para crear un nuevo módulo dentro de una lección.
 */
export function CreateModuleForm({ lessonId, onModuleCreated, onCancel }: CreateModuleFormProps) {
  const [title, setTitle] = useState('');
  const [description, setDescription] = useState('');
  const [content, setContent] = useState('');
  const [duration, setDuration] = useState(30);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    
    if (!title.trim()) {
      setError('El título es obligatorio');
      return;
    }

    setLoading(true);
    setError(null);

    try {
      const newModule = await protectedApi.createModule({
        title: title.trim(),
        description: description.trim(),
        content: content.trim(),
        duration,
        lessonId
      });
      
      onModuleCreated(newModule);
    } catch (err) {
      console.error('Error creating module:', err);
      setError('Error al crear el módulo. Por favor, intenta de nuevo.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="create-module-form-container">
      <h3>Añadir Nuevo Módulo</h3>
      
      <form onSubmit={handleSubmit} className="create-module-form">
        {error && (
          <div className="form-error">
            {error}
          </div>
        )}

        <div className="form-row">
          <div className="form-group flex-2">
            <label htmlFor="module-title">Título *</label>
            <input
              type="text"
              id="module-title"
              value={title}
              onChange={(e) => setTitle(e.target.value)}
              placeholder="Ej: Introducción"
              disabled={loading}
              autoFocus
            />
          </div>

          <div className="form-group flex-1">
            <label htmlFor="module-duration">Duración (min)</label>
            <input
              type="number"
              id="module-duration"
              value={duration}
              onChange={(e) => setDuration(parseInt(e.target.value) || 0)}
              min={1}
              disabled={loading}
            />
          </div>
        </div>

        <div className="form-group">
          <label htmlFor="module-description">Descripción</label>
          <textarea
            id="module-description"
            value={description}
            onChange={(e) => setDescription(e.target.value)}
            placeholder="Breve descripción del módulo..."
            rows={2}
            disabled={loading}
          />
        </div>

        <div className="form-group">
          <label htmlFor="module-content">Contenido</label>
          <textarea
            id="module-content"
            value={content}
            onChange={(e) => setContent(e.target.value)}
            placeholder="Contenido del módulo (texto, URL de video, etc.)..."
            rows={4}
            disabled={loading}
          />
        </div>

        <div className="form-actions">
          <button 
            type="button" 
            onClick={onCancel}
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
            {loading ? <LoadingSpinner /> : 'Añadir Módulo'}
          </button>
        </div>
      </form>
    </div>
  );
}
