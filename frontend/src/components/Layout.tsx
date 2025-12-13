import { Outlet, NavLink, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import '../styles/Layout.css';

/**
 * Layout principal de la aplicación.
 * 
 * Proporciona:
 * - Header con navegación
 * - Área de contenido principal (Outlet para rutas hijas)
 * - Footer
 * 
 * Se usa como wrapper para todas las páginas protegidas.
 */
function Layout() {
  const { logout } = useAuth();
  const navigate = useNavigate();

  const handleLogout = () => {
    logout();
    navigate('/login', { replace: true });
  };

  return (
    <div className="layout">
      {/* Header con navegación */}
      <header className="layout-header">
        <div className="header-container">
          <NavLink to="/home" className="logo">
            <h1>EnSe</h1>
          </NavLink>

          <nav className="main-nav">
            <NavLink 
              to="/home" 
              className={({ isActive }) => isActive ? 'nav-link active' : 'nav-link'}
            >
              Inicio
            </NavLink>
            <NavLink 
              to="/lessons" 
              className={({ isActive }) => isActive ? 'nav-link active' : 'nav-link'}
            >
              Lecciones
            </NavLink>
            <NavLink 
              to="/profile" 
              className={({ isActive }) => isActive ? 'nav-link active' : 'nav-link'}
            >
              Perfil
            </NavLink>
          </nav>

          <button onClick={handleLogout} className="logout-btn">
            Cerrar Sesión
          </button>
        </div>
      </header>

      {/* Contenido principal - las rutas hijas se renderizan aquí */}
      <main className="layout-main">
        <Outlet />
      </main>

      {/* Footer */}
      <footer className="layout-footer">
        <p>EnSe - Sistema de Enseñanza © {new Date().getFullYear()}</p>
      </footer>
    </div>
  );
}

export default Layout;
