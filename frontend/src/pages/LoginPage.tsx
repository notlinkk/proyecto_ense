import { useState, FormEvent, useEffect } from 'react';
import { useNavigate, useLocation } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { authApi } from '../api';
import { RegisterData } from '../types';
import '../styles/LoginPage.css';

/**
 * Página de inicio de sesión y registro.
 * 
 * Funcionalidades:
 * - Formulario con username y password para login
 * - Formulario de registro con todos los campos necesarios
 * - Validación básica de campos
 * - Manejo de errores de autenticación
 * - Redirección a la página original después del login
 */
function LoginPage() {
  const [isRegisterMode, setIsRegisterMode] = useState(false);
  
  // Login state
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  
  // Register state
  const [registerData, setRegisterData] = useState<RegisterData>({
    username: '',
    name: '',
    surname1: '',
    surname2: '',
    email: '',
    password: '',
  });
  const [confirmPassword, setConfirmPassword] = useState('');
  
  const [error, setError] = useState<string | null>(null);
  const [success, setSuccess] = useState<string | null>(null);
  const [isSubmitting, setIsSubmitting] = useState(false);

  const { login, isAuthenticated } = useAuth();
  const navigate = useNavigate();
  const location = useLocation();

  // Obtener la página de origen (si venimos de una redirección)
  const from = (location.state as { from?: Location })?.from?.pathname || '/home';

  // Si ya está autenticado, redirigir
  useEffect(() => {
    if (isAuthenticated) {
      navigate(from, { replace: true });
    }
  }, [isAuthenticated, navigate, from]);

  /**
   * Manejar el envío del formulario de login.
   */
  const handleLoginSubmit = async (e: FormEvent) => {
    e.preventDefault();
    
    // Validación básica
    if (!username.trim() || !password.trim()) {
      setError('Por favor, completa todos los campos');
      return;
    }

    setError(null);
    setIsSubmitting(true);

    try {
      await login({ username, password });
    } catch (err) {
      if (err instanceof Error) {
        if (err.message.includes('401') || err.message.includes('Unauthorized')) {
          setError('Usuario o contraseña incorrectos');
        } else if (err.message.includes('Network Error')) {
          setError('Error de conexión. Verifica que el servidor esté activo.');
        } else {
          setError('Error al iniciar sesión. Inténtalo de nuevo.');
        }
      } else {
        setError('Error inesperado. Inténtalo de nuevo.');
      }
      console.error('Error de login:', err);
    } finally {
      setIsSubmitting(false);
    }
  };

  /**
   * Manejar el envío del formulario de registro.
   */
  const handleRegisterSubmit = async (e: FormEvent) => {
    e.preventDefault();
    
    // Validación básica
    if (!registerData.username.trim() || !registerData.name.trim() || 
        !registerData.surname1.trim() || !registerData.email.trim() || 
        !registerData.password.trim()) {
      setError('Por favor, completa todos los campos obligatorios');
      return;
    }

    if (registerData.password !== confirmPassword) {
      setError('Las contraseñas no coinciden');
      return;
    }

    if (registerData.password.length < 6) {
      setError('La contraseña debe tener al menos 6 caracteres');
      return;
    }

    setError(null);
    setSuccess(null);
    setIsSubmitting(true);

    try {
      await authApi.register(registerData);
      setSuccess('¡Registro exitoso! Ahora puedes iniciar sesión.');
      // Switch to login mode
      setTimeout(() => {
        setIsRegisterMode(false);
        setUsername(registerData.username);
        setPassword('');
        setSuccess(null);
      }, 2000);
    } catch (err) {
      if (err instanceof Error) {
        if (err.message.includes('409') || err.message.includes('Conflict')) {
          setError('El usuario o email ya existe');
        } else if (err.message.includes('Network Error')) {
          setError('Error de conexión. Verifica que el servidor esté activo.');
        } else {
          setError('Error al registrar. Inténtalo de nuevo.');
        }
      } else {
        setError('Error inesperado. Inténtalo de nuevo.');
      }
      console.error('Error de registro:', err);
    } finally {
      setIsSubmitting(false);
    }
  };

  const handleRegisterInputChange = (field: keyof RegisterData, value: string) => {
    setRegisterData(prev => ({ ...prev, [field]: value }));
  };

  const toggleMode = () => {
    setIsRegisterMode(!isRegisterMode);
    setError(null);
    setSuccess(null);
  };

  return (
    <div className="login-container">
      <div className="login-card">
        <div className="login-header">
          <h1>EnSe</h1>
          <p>{isRegisterMode ? 'Crear cuenta' : 'Sistema de Enseñanza'}</p>
        </div>

        {/* Mensaje de éxito */}
        {success && (
          <div className="success-message" role="alert">
            {success}
          </div>
        )}

        {/* Mensaje de error */}
        {error && (
          <div className="error-message" role="alert">
            {error}
          </div>
        )}

        {!isRegisterMode ? (
          /* Formulario de Login */
          <form onSubmit={handleLoginSubmit} className="login-form">
            <div className="form-group">
              <label htmlFor="username">Usuario</label>
              <input
                type="text"
                id="username"
                value={username}
                onChange={(e) => setUsername(e.target.value)}
                placeholder="Ingresa tu usuario"
                disabled={isSubmitting}
                autoComplete="username"
                autoFocus
              />
            </div>

            <div className="form-group">
              <label htmlFor="password">Contraseña</label>
              <input
                type="password"
                id="password"
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                placeholder="Ingresa tu contraseña"
                disabled={isSubmitting}
                autoComplete="current-password"
              />
            </div>

            <button
              type="submit"
              className="login-button"
              disabled={isSubmitting}
            >
              {isSubmitting ? 'Iniciando sesión...' : 'Iniciar Sesión'}
            </button>
          </form>
        ) : (
          /* Formulario de Registro */
          <form onSubmit={handleRegisterSubmit} className="login-form">
            <div className="form-group">
              <label htmlFor="reg-username">Usuario *</label>
              <input
                type="text"
                id="reg-username"
                value={registerData.username}
                onChange={(e) => handleRegisterInputChange('username', e.target.value)}
                placeholder="Nombre de usuario"
                disabled={isSubmitting}
                autoComplete="username"
                autoFocus
              />
            </div>

            <div className="form-row">
              <div className="form-group">
                <label htmlFor="reg-name">Nombre *</label>
                <input
                  type="text"
                  id="reg-name"
                  value={registerData.name}
                  onChange={(e) => handleRegisterInputChange('name', e.target.value)}
                  placeholder="Tu nombre"
                  disabled={isSubmitting}
                />
              </div>
              <div className="form-group">
                <label htmlFor="reg-surname1">Primer apellido *</label>
                <input
                  type="text"
                  id="reg-surname1"
                  value={registerData.surname1}
                  onChange={(e) => handleRegisterInputChange('surname1', e.target.value)}
                  placeholder="Primer apellido"
                  disabled={isSubmitting}
                />
              </div>
            </div>

            <div className="form-group">
              <label htmlFor="reg-surname2">Segundo apellido</label>
              <input
                type="text"
                id="reg-surname2"
                value={registerData.surname2}
                onChange={(e) => handleRegisterInputChange('surname2', e.target.value)}
                placeholder="Segundo apellido (opcional)"
                disabled={isSubmitting}
              />
            </div>

            <div className="form-group">
              <label htmlFor="reg-email">Correo electrónico *</label>
              <input
                type="email"
                id="reg-email"
                value={registerData.email}
                onChange={(e) => handleRegisterInputChange('email', e.target.value)}
                placeholder="tu@email.com"
                disabled={isSubmitting}
                autoComplete="email"
              />
            </div>

            <div className="form-group">
              <label htmlFor="reg-password">Contraseña *</label>
              <input
                type="password"
                id="reg-password"
                value={registerData.password}
                onChange={(e) => handleRegisterInputChange('password', e.target.value)}
                placeholder="Mínimo 6 caracteres"
                disabled={isSubmitting}
                autoComplete="new-password"
              />
            </div>

            <div className="form-group">
              <label htmlFor="reg-confirm-password">Confirmar contraseña *</label>
              <input
                type="password"
                id="reg-confirm-password"
                value={confirmPassword}
                onChange={(e) => setConfirmPassword(e.target.value)}
                placeholder="Repite tu contraseña"
                disabled={isSubmitting}
                autoComplete="new-password"
              />
            </div>

            <button
              type="submit"
              className="login-button"
              disabled={isSubmitting}
            >
              {isSubmitting ? 'Registrando...' : 'Crear Cuenta'}
            </button>
          </form>
        )}

        {/* Toggle entre login y registro */}
        <div className="auth-toggle">
          <span>
            {isRegisterMode 
              ? '¿Ya tienes cuenta?' 
              : '¿No tienes cuenta?'}
          </span>
          <button 
            type="button" 
            onClick={toggleMode}
            className="toggle-button"
            disabled={isSubmitting}
          >
            {isRegisterMode ? 'Iniciar Sesión' : 'Registrarse'}
          </button>
        </div>

        {/* Información de seguridad */}
        <div className="security-info">
          <small>
            🔒 Tu sesión está protegida con JWT y cookies HttpOnly
          </small>
        </div>
      </div>
    </div>
  );
}

export default LoginPage;
