import { Module } from '../types';
import '../styles/ModuleItem.css';

interface ModuleItemProps {
  module: Module;
  index: number;
  isExpanded: boolean;
  onToggle: () => void;
}

/**
 * Componente para mostrar un módulo dentro de una lección.
 * Muestra título, descripción y contenido expandible.
 */
function ModuleItem({ module, index, isExpanded, onToggle }: ModuleItemProps) {
  return (
    <div className={`module-item ${isExpanded ? 'expanded' : ''}`}>
      <button className="module-header" onClick={onToggle}>
        <div className="module-number">{index + 1}</div>
        <div className="module-info">
          <h4 className="module-title">{module.title}</h4>
          <span className="module-duration">{module.duration} min</span>
        </div>
        <span className="expand-icon">{isExpanded ? '▲' : '▼'}</span>
      </button>

      {isExpanded && (
        <div className="module-content">
          <p className="module-description">{module.description}</p>
          
          {module.content && (
            <div className="module-material">
              <h5>Contenido:</h5>
              <div className="content-preview">
                {/* Si el contenido es una URL, mostrar como enlace */}
                {module.content.startsWith('http') ? (
                  <a 
                    href={module.content} 
                    target="_blank" 
                    rel="noopener noreferrer"
                    className="content-link"
                  >
                    Ver recurso externo
                  </a>
                ) : (
                  <p>{module.content}</p>
                )}
              </div>
            </div>
          )}
        </div>
      )}
    </div>
  );
}

export default ModuleItem;
