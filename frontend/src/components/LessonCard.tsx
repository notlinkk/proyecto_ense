import { Link } from 'react-router-dom';
import { Lesson } from '../types';
import '../styles/LessonCard.css';

interface LessonCardProps {
  lesson: Lesson;
}

/**
 * Tarjeta para mostrar un resumen de una lección.
 * Se usa en la página de inicio y en la lista de lecciones.
 */
function LessonCard({ lesson }: LessonCardProps) {
  const moduleCount = lesson.modules?.length ?? 0;
  const abilityCount = lesson.abilities?.length ?? 0;

  return (
    <Link to={`/lessons/${lesson.id}`} className="lesson-card">
      {lesson.imageUrl && (
        <div className="lesson-card-image">
          <img src={lesson.imageUrl} alt={lesson.name} />
        </div>
      )}
      <div className="lesson-card-content">
        <div className="lesson-card-header">
          <h3 className="lesson-title">{lesson.name}</h3>
          {lesson.price !== undefined && lesson.price > 0 && (
            <span className="lesson-price">${lesson.price.toFixed(2)}</span>
          )}
        </div>
        
        <p className="lesson-description">
          {lesson.description || 'Sin descripción disponible'}
        </p>

        <div className="lesson-meta">
          <span className="meta-item">
            {moduleCount} {moduleCount === 1 ? 'módulo' : 'módulos'}
          </span>
          {abilityCount > 0 && (
            <span className="meta-item">
              {abilityCount} {abilityCount === 1 ? 'habilidad' : 'habilidades'}
            </span>
          )}
        </div>

        {lesson.abilities && lesson.abilities.length > 0 && (
          <div className="lesson-abilities">
            {lesson.abilities.slice(0, 3).map((ability) => (
              <span key={ability.name} className="ability-tag">
                {ability.name}
              </span>
            ))}
            {lesson.abilities.length > 3 && (
              <span className="ability-tag more">+{lesson.abilities.length - 3}</span>
            )}
          </div>
        )}
      </div>
    </Link>
  );
}

export default LessonCard;
