# EnSe Frontend

Frontend React + Vite + TypeScript para el sistema de enseñanza EnSe.

## 🔐 Arquitectura de Autenticación

Este proyecto implementa una autenticación segura con JWT y Refresh Token:

### Flujo de Autenticación

```
┌─────────────────────────────────────────────────────────────────┐
│                    FLUJO DE LOGIN                                │
├─────────────────────────────────────────────────────────────────┤
│                                                                  │
│  Usuario                Frontend              Backend            │
│    │                      │                      │               │
│    │─── Login ───────────>│                      │               │
│    │                      │─── POST /auth/login ─>│               │
│    │                      │    {username, pwd}   │               │
│    │                      │                      │               │
│    │                      │<─ Header: Auth ──────│               │
│    │                      │   Cookie: HttpOnly   │               │
│    │                      │                      │               │
│    │                      │ JWT → Context        │               │
│    │                      │ (memoria)            │               │
│    │<─── Redirect ────────│                      │               │
│    │     /home            │                      │               │
│                                                                  │
└─────────────────────────────────────────────────────────────────┘
```

### Almacenamiento de Tokens

| Token | Almacenamiento | Accesible desde JS | Seguridad |
|-------|---------------|-------------------|-----------|
| JWT (Access Token) | Memoria (React Context) | Sí (limitado) | Alto - Se pierde al cerrar pestaña |
| Refresh Token | Cookie HttpOnly | No | Muy Alto - Protegido contra XSS |

### Renovación Automática

Cuando el JWT expira:
1. El interceptor de axios detecta el error 401
2. Automáticamente llama a `POST /auth/refresh`
3. El refresh token (cookie) se envía automáticamente
4. El backend valida y devuelve un nuevo JWT
5. Se reintenta la request original con el nuevo JWT

## 📁 Estructura del Proyecto

```
frontend/
├── src/
│   ├── api/
│   │   ├── apiClient.ts      # Cliente axios con interceptores
│   │   ├── authApi.ts        # Endpoints de autenticación
│   │   ├── protectedApi.ts   # Endpoints protegidos
│   │   └── index.ts
│   ├── components/
│   │   ├── ProtectedRoute.tsx # Componente de ruta protegida
│   │   └── index.ts
│   ├── context/
│   │   ├── AuthContext.tsx   # Contexto de autenticación
│   │   └── index.ts
│   ├── hooks/
│   │   ├── useAuthSync.ts    # Sincronizar token con axios
│   │   └── index.ts
│   ├── pages/
│   │   ├── LoginPage.tsx     # Página de login
│   │   ├── HomePage.tsx      # Página principal (protegida)
│   │   └── index.ts
│   ├── styles/
│   │   ├── index.css         # Estilos globales
│   │   ├── App.css
│   │   ├── LoginPage.css
│   │   └── HomePage.css
│   ├── types/
│   │   ├── auth.types.ts     # Tipos de autenticación
│   │   └── index.ts
│   ├── App.tsx               # Componente principal
│   ├── main.tsx              # Punto de entrada
│   └── vite-env.d.ts
├── package.json
├── tsconfig.json
├── vite.config.ts
└── README.md
```

## 🚀 Instalación y Ejecución

### Requisitos Previos
- Node.js 18+ 
- npm o yarn

### Instalación

```bash
cd frontend
npm install
```

### Desarrollo

```bash
npm run dev
```

La aplicación estará disponible en `http://localhost:3000`.

El proxy de Vite redirigirá las llamadas `/api/*` a `http://localhost:8080` (backend Spring Boot).

### Producción

```bash
npm run build
npm run preview
```

## ⚙️ Configuración

### Proxy del Backend

En `vite.config.ts`:

```typescript
server: {
  proxy: {
    '/api': {
      target: 'http://localhost:8080',
      changeOrigin: true,
      rewrite: (path) => path.replace(/^\/api/, ''),
    },
  },
}
```

### Variables de Entorno

Crear un archivo `.env` para configuración:

```env
VITE_API_URL=http://localhost:8080
```

## 🔧 Requisitos del Backend

El backend Spring Boot debe:

1. **POST /auth/login**
   - Aceptar `{ username, password }`
   - Devolver JWT en header `Authorization: Bearer <token>`
   - Establecer cookie HttpOnly con refresh token

2. **POST /auth/refresh**
   - Leer el refresh token de la cookie
   - Devolver nuevo JWT en header `Authorization`

3. **Configurar CORS**
   ```java
   @Configuration
   public class CorsConfig {
       @Bean
       public CorsFilter corsFilter() {
           CorsConfiguration config = new CorsConfiguration();
           config.setAllowCredentials(true); // ¡Importante!
           config.addAllowedOrigin("http://localhost:3000");
           config.addAllowedHeader("*");
           config.addExposedHeader("Authorization");
           config.addAllowedMethod("*");
           // ...
       }
   }
   ```

## 🛡️ Seguridad

### ¿Por qué JWT en memoria?

- **localStorage** es vulnerable a XSS (cualquier script puede leerlo)
- **Memoria de React** no es accesible desde scripts externos
- Al cerrar la pestaña, el token desaparece automáticamente

### ¿Por qué Refresh Token en Cookie HttpOnly?

- No es accesible desde JavaScript (protección contra XSS)
- Se envía automáticamente con cada request
- El servidor controla su expiración
- Más difícil de robar en un ataque XSS

### Flujo de Refresh

```
Request → 401 → ¿Ya hay refresh en progreso?
                     │
         ┌───── No ──┴─── Sí ─────┐
         │                         │
   POST /refresh           Añadir a cola
         │                         │
   ¿Éxito?                 Esperar resultado
      │
   ┌──┴──┐
  Sí     No
   │      │
Reintentar  Logout
  request   + /login
```

## 📝 Licencia

MIT
