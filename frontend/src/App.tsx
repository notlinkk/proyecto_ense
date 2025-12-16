import { Routes, Route, Navigate } from 'react-router-dom'
import LoginPage from './pages/LoginPage'
import HomePage from './pages/HomePage'
import LessonsPage from './pages/LessonsPage'
import LessonDetailPage from './pages/LessonDetailPage'
import ProfilePage from './pages/ProfilePage'
import { CreateLessonPage, AdminPage } from './pages'
import ProtectedRoute from './components/ProtectedRoute'
import Layout from './components/Layout'
import { useAuthSync } from './hooks'
import './styles/App.css'

/**
 * Componente principal de la aplicación.
 * 
 * Define las rutas de la aplicación:
 * - /login: Página pública de inicio de sesión
 * - /home: Página de inicio protegida
 * - /lessons: Lista de lecciones
 * - /lessons/new: Crear nueva lección
 * - /lessons/:id: Detalle de una lección
 * - /profile: Perfil del usuario
 */
function App() {
  // Sincronizar el token de autenticación con el cliente HTTP
  useAuthSync();

  return (
    <div className="app">
      <Routes>
        {/* Ruta pública - Login */}
        <Route path="/login" element={<LoginPage />} />
        
        {/* Rutas protegidas - requieren autenticación */}
        <Route element={<ProtectedRoute />}>
          {/* Layout con navegación para todas las páginas protegidas */}
          <Route element={<Layout />}>
            <Route path="/home" element={<HomePage />} />
            <Route path="/lessons" element={<LessonsPage />} />
            <Route path="/lessons/new" element={<CreateLessonPage />} />
            <Route path="/lessons/:id" element={<LessonDetailPage />} />
            <Route path="/profile" element={<ProfilePage />} />
            <Route path="/admin" element={<AdminPage />} />
          </Route>
        </Route>
        
        {/* Redirigir la raíz a /home */}
        <Route path="/" element={<Navigate to="/home" replace />} />
        
        {/* Cualquier otra ruta redirige a /home */}
        <Route path="*" element={<Navigate to="/home" replace />} />
      </Routes>
    </div>
  )
}

export default App
