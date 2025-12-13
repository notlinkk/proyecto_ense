import '../styles/LoadingSpinner.css';

interface LoadingSpinnerProps {
  message?: string;
}

/**
 * Componente de carga reutilizable.
 */
function LoadingSpinner({ message = 'Cargando...' }: LoadingSpinnerProps) {
  return (
    <div className="loading-spinner-container">
      <div className="spinner"></div>
      <p>{message}</p>
    </div>
  );
}

export default LoadingSpinner;
