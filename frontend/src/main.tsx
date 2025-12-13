import React from 'react'
import ReactDOM from 'react-dom/client'
import { BrowserRouter } from 'react-router-dom'
import App from './App'
import { AuthProvider } from './context/AuthContext'
import './styles/index.css'

/**
 * Punto de entrada de la aplicación React.
 * 
 * Estructura de proveedores:
 * - BrowserRouter: Habilita el enrutamiento con React Router
 * - AuthProvider: Provee el contexto de autenticación a toda la app
 *   (maneja el JWT en memoria y el estado de autenticación)
 */
ReactDOM.createRoot(document.getElementById('root')!).render(
  <React.StrictMode>
    <BrowserRouter>
      <AuthProvider>
        <App />
      </AuthProvider>
    </BrowserRouter>
  </React.StrictMode>,
)
