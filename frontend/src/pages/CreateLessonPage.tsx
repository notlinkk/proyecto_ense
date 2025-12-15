import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { protectedApi } from '../api';
import { LoadingSpinner } from '../components';
import { Ability } from '../types';
import '../styles/CreateLessonPage.css';

/**
 * Página para crear una nueva lección.
 * Formulario con campos para nombre, descripción y habilidades.
 */
export function CreateLessonPage() {
  const navigate = useNavigate();
  const [name, setName] = useState('');
  const [description, setDescription] = useState('');
  const [price, setPrice] = useState('');
  const [imageUrl, setImageUrl] = useState('');
  const [selectedAbilities, setSelectedAbilities] = useState<string[]>([]);
  const [availableAbilities, setAvailableAbilities] = useState<Ability[]>([]);
  const [loading, setLoading] = useState(false);
  const [loadingAbilities, setLoadingAbilities] = useState(true);
  const [error, setError] = useState<string | null>(null);

  // Cargar habilidades disponibles al montar
  useEffect(() => {
    const fetchAbilities = async () => {
      try {
        const response = await protectedApi.getAbilities();
        setAvailableAbilities(response.content);
      } catch (err) {
        console.error('Error loading abilities:', err);
        setError('Error al cargar las habilidades');
      } finally {
        setLoadingAbilities(false);
      }
    };
    fetchAbilities();
  }, []);

  const toggleAbility = (abilityName: string) => {
    setSelectedAbilities(prev => 
      prev.includes(abilityName)
        ? prev.filter(a => a !== abilityName)
        : [...prev, abilityName]
    );
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    
    if (!name.trim()) {
      setError('El nombre es obligatorio');
      return;
    }

    if (!price.trim() || isNaN(parseFloat(price)) || parseFloat(price) < 0) {
      setError('El precio debe ser un número válido');
      return;
    }

    if (!imageUrl.trim()) {
      setError('La imagen es obligatoria');
      return;
    }

    if (selectedAbilities.length === 0) {
      setError('Debes seleccionar al menos una habilidad');
      return;
    }

    setLoading(true);
    setError(null);

    try {
      const newLesson = await protectedApi.createLesson({
        name: name.trim(),
        description: description.trim(),
        price: parseFloat(price),
        imageUrl: imageUrl.trim(),
        abilities: selectedAbilities
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

  if (loadingAbilities) {
    return (
      <div className="create-lesson-page">
        <div className="create-lesson-container">
          <LoadingSpinner />
        </div>
      </div>
    );
  }

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

          <div className="form-row">
            <div className="form-group">
              <label htmlFor="price">Precio (€) *</label>
              <input
                type="number"
                id="price"
                value={price}
                onChange={(e) => setPrice(e.target.value)}
                placeholder="29.99"
                min="0"
                step="0.01"
                disabled={loading}
              />
            </div>

            <div className="form-group">
              <label htmlFor="imageUrl">URL de la Imagen *</label>
              <input
                type="url"
                id="imageUrl"
                value={imageUrl}
                onChange={(e) => setImageUrl(e.target.value)}
                placeholder="https://ejemplo.com/imagen.jpg"
                disabled={loading}
              />
            </div>
          </div>

          {imageUrl && (
            <div className="image-preview">
              <label>Vista previa de la imagen</label>
              <img 
                src={imageUrl} 
                alt="Vista previa" 
                onError={(e) => {
                  (e.target as HTMLImageElement).style.display = 'none';
                }}
                onLoad={(e) => {
                  (e.target as HTMLImageElement).style.display = 'block';
                }}
              />
            </div>
          )}

          <div className="form-group">
            <label>Habilidades * <span className="label-hint">(selecciona al menos una)</span></label>
            <div className="abilities-grid">
              {availableAbilities.map(ability => (
                <button
                  key={ability.name}
                  type="button"
                  className={`ability-tag ${selectedAbilities.includes(ability.name) ? 'selected' : ''}`}
                  onClick={() => toggleAbility(ability.name)}
                  disabled={loading}
                  title={ability.description}
                >
                  {ability.name}
                </button>
              ))}
            </div>
            {selectedAbilities.length > 0 && (
              <div className="selected-count">
                {selectedAbilities.length} habilidad{selectedAbilities.length > 1 ? 'es' : ''} seleccionada{selectedAbilities.length > 1 ? 's' : ''}
              </div>
            )}
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
