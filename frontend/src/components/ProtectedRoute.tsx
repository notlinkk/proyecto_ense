import { Navigate, Outlet, useLocation } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

/**
 * Componente de ruta protegida.
 * 
 * Este componente actúa como un "guardián" para las rutas que requieren autenticación.
 * Si el usuario no está autenticado, lo redirige a /login.
 * 
 * Uso en App.tsx:
 * <Route element={<ProtectedRoute />}>
 *   <Route path="/home" element={<HomePage />} />
 *   <Route path="/dashboard" element={<Dashboard />} />
 * </Route>
 * 
 * ¿Por qué usar Outlet?
 * - Outlet renderiza los componentes hijos de la ruta
 * - Permite envolver múltiples rutas protegidas con un solo ProtectedRoute
 * - Es el patrón recomendado por React Router v6
 */
function ProtectedRoute() {
  const { isAuthenticated, isLoading } = useAuth();
  const location = useLocation();

  // Mientras se verifica la autenticación (ej: refresh token al cargar),
  // mostramos un indicador de carga
  if (isLoading) {
    return (
      <div className="loading-container">
        <div className="loading-spinner"></div>
        <p>Verificando sesión...</p>
      </div>
    );
  }

  // Si no está autenticado, redirigir a login
  // Guardamos la ubicación actual para redirigir después del login
  if (!isAuthenticated) {
    return <Navigate to="/login" state={{ from: location }} replace />;
  }

  // Si está autenticado, renderizar las rutas hijas
  return <Outlet />;
}

export default ProtectedRoute;
