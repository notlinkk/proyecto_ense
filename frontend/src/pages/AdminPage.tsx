import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { protectedApi, adminApi } from '../api';
import { User, Lesson, Module, Subscription, Ability } from '../types';
import { LoadingSpinner } from '../components';
import { useAuth } from '../context/AuthContext';
import '../styles/AdminPage.css';

type ResourceType = 'users' | 'lessons' | 'modules' | 'subscriptions' | 'abilities';

interface TabConfig {
  id: ResourceType;
  label: string;
}

const TABS: TabConfig[] = [
  { id: 'users', label: 'Usuarios' },
  { id: 'lessons', label: 'Lecciones' },
  { id: 'modules', label: 'Módulos' },
  { id: 'subscriptions', label: 'Suscripciones' },
  { id: 'abilities', label: 'Habilidades' },
];

// Interfaces para modales de edición
interface EditUserData {
  name?: string;
  surname1?: string;
  surname2?: string;
  email?: string;
}

interface EditLessonData {
  name?: string;
  description?: string;
  price?: number;
}

interface EditModuleData {
  title?: string;
  description?: string;
  duration?: number;
}

interface EditAbilityData {
  description?: string;
}

// Interfaces para crear recursos
interface CreateLessonData {
  name: string;
  description: string;
  price: number;
  imageUrl: string;
  abilities: string[];
}

interface CreateModuleData {
  lessonId: string;
  title: string;
  description: string;
  content: string;
  duration: number;
}

interface CreateAbilityData {
  name: string;
  description: string;
}

interface CreateUserData {
  username: string;
  password: string;
  name: string;
  surname1: string;
  surname2: string;
  email: string;
}

/**
 * Página de administración.
 * Solo accesible para usuarios con rol ADMIN.
 * Muestra listas de todos los recursos y permite realizar operaciones CRUD.
 */
function AdminPage() {
  const navigate = useNavigate();
  const { user: authUser } = useAuth();
  
  const [activeTab, setActiveTab] = useState<ResourceType>('users');
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  
  // Data states
  const [users, setUsers] = useState<User[]>([]);
  const [lessons, setLessons] = useState<Lesson[]>([]);
  const [modules, setModules] = useState<Module[]>([]);
  const [subscriptions, setSubscriptions] = useState<Subscription[]>([]);
  const [abilities, setAbilities] = useState<Ability[]>([]);
  
  // Pagination
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const pageSize = 10;

  // Edit modal states
  const [editingUser, setEditingUser] = useState<User | null>(null);
  const [editingLesson, setEditingLesson] = useState<Lesson | null>(null);
  const [editingModule, setEditingModule] = useState<Module | null>(null);
  const [editingAbility, setEditingAbility] = useState<Ability | null>(null);
  const [editUserData, setEditUserData] = useState<EditUserData>({});
  const [editLessonData, setEditLessonData] = useState<EditLessonData>({});
  const [editModuleData, setEditModuleData] = useState<EditModuleData>({});
  const [editAbilityData, setEditAbilityData] = useState<EditAbilityData>({});

  // Create modal states
  const [showCreateLesson, setShowCreateLesson] = useState(false);
  const [showCreateModule, setShowCreateModule] = useState(false);
  const [showCreateAbility, setShowCreateAbility] = useState(false);
  const [showCreateUser, setShowCreateUser] = useState(false);
  const [createLessonData, setCreateLessonData] = useState<CreateLessonData>({
    name: '', description: '', price: 0, imageUrl: '', abilities: []
  });
  const [createModuleData, setCreateModuleData] = useState<CreateModuleData>({
    lessonId: '', title: '', description: '', content: '', duration: 0
  });
  const [createAbilityData, setCreateAbilityData] = useState<CreateAbilityData>({
    name: '', description: ''
  });
  const [createUserData, setCreateUserData] = useState<CreateUserData>({
    username: '', password: '', name: '', surname1: '', surname2: '', email: ''
  });
  const [availableAbilities, setAvailableAbilities] = useState<Ability[]>([]);
  const [allLessons, setAllLessons] = useState<Lesson[]>([]);

  // Check if user is admin
  const isAdmin = authUser?.roles?.some(role => role.rolename === 'ADMIN') ?? false;

  useEffect(() => {
    if (!isAdmin) {
      navigate('/home');
      return;
    }
    loadData();
  }, [activeTab, page, isAdmin, navigate]);

  const loadData = async () => {
    setIsLoading(true);
    setError(null);
    
    try {
      switch (activeTab) {
        case 'users':
          const usersData = await adminApi.getAllUsers(page, pageSize);
          setUsers(usersData.content);
          setTotalPages(usersData.totalPages);
          break;
        case 'lessons':
          const lessonsData = await adminApi.getAllLessons(page, pageSize);
          setLessons(lessonsData.content);
          setTotalPages(lessonsData.totalPages);
          break;
        case 'modules':
          const modulesData = await protectedApi.getModules(page, pageSize);
          setModules(modulesData.content);
          setTotalPages(modulesData.totalPages);
          break;
        case 'subscriptions':
          const subsData = await adminApi.getAllSubscriptions(page, pageSize);
          setSubscriptions(subsData.content);
          setTotalPages(subsData.totalPages);
          break;
        case 'abilities':
          const abilitiesData = await protectedApi.getAbilities(page, pageSize);
          setAbilities(abilitiesData.content);
          setTotalPages(abilitiesData.totalPages);
          break;
      }
    } catch (err) {
      console.error('Error loading data:', err);
      setError('Error al cargar los datos');
    } finally {
      setIsLoading(false);
    }
  };

  const handleTabChange = (tab: ResourceType) => {
    setActiveTab(tab);
    setPage(0);
  };

  const handleDelete = async (type: ResourceType, id: string) => {
    if (!confirm('¿Estás seguro de que quieres eliminar este recurso?')) return;
    
    try {
      switch (type) {
        case 'users':
          await adminApi.deleteUser(id);
          break;
        case 'lessons':
          await protectedApi.deleteLesson(id);
          break;
        case 'modules':
          await protectedApi.deleteModule(id);
          break;
        case 'subscriptions':
          await protectedApi.unsubscribe(id);
          break;
        case 'abilities':
          await adminApi.deleteAbility(id);
          break;
      }
      loadData();
    } catch (err) {
      console.error('Error deleting:', err);
      setError('Error al eliminar el recurso');
    }
  };

  // Edit handlers
  const openEditUser = (user: User) => {
    setEditingUser(user);
    setEditUserData({
      name: user.name,
      surname1: user.surname1,
      surname2: user.surname2,
      email: user.email,
    });
  };

  const openEditLesson = (lesson: Lesson) => {
    setEditingLesson(lesson);
    setEditLessonData({
      name: lesson.name,
      description: lesson.description,
      price: lesson.price,
    });
  };

  const openEditModule = (module: Module) => {
    setEditingModule(module);
    setEditModuleData({
      title: module.title,
      description: module.description,
      duration: module.duration,
    });
  };

  const openEditAbility = (ability: Ability) => {
    setEditingAbility(ability);
    setEditAbilityData({
      description: ability.description,
    });
  };

  const saveUser = async () => {
    if (!editingUser) return;
    try {
      await adminApi.updateUser(editingUser.username, editUserData);
      setEditingUser(null);
      loadData();
    } catch (err) {
      console.error('Error updating user:', err);
      setError('Error al actualizar el usuario');
    }
  };

  const saveLesson = async () => {
    if (!editingLesson) return;
    try {
      await protectedApi.updateLesson(editingLesson.id, editLessonData);
      setEditingLesson(null);
      loadData();
    } catch (err) {
      console.error('Error updating lesson:', err);
      setError('Error al actualizar la lección');
    }
  };

  const saveModule = async () => {
    if (!editingModule) return;
    try {
      await protectedApi.updateModule(editingModule.id, editModuleData);
      setEditingModule(null);
      loadData();
    } catch (err) {
      console.error('Error updating module:', err);
      setError('Error al actualizar el módulo');
    }
  };

  const saveAbility = async () => {
    if (!editingAbility) return;
    try {
      await adminApi.updateAbility(editingAbility.name, editAbilityData);
      setEditingAbility(null);
      loadData();
    } catch (err) {
      console.error('Error updating ability:', err);
      setError('Error al actualizar la habilidad');
    }
  };

  // Create handlers
  const openCreateUser = () => {
    setCreateUserData({ username: '', password: '', name: '', surname1: '', surname2: '', email: '' });
    setShowCreateUser(true);
  };

  const openCreateLesson = async () => {
    try {
      const abilitiesData = await protectedApi.getAbilities(0, 100);
      setAvailableAbilities(abilitiesData.content);
      setCreateLessonData({ name: '', description: '', price: 0, imageUrl: '', abilities: [] });
      setShowCreateLesson(true);
    } catch (err) {
      console.error('Error loading abilities:', err);
      setError('Error al cargar las habilidades');
    }
  };

  const openCreateModule = async () => {
    try {
      const lessonsData = await adminApi.getAllLessons(0, 100);
      setAllLessons(lessonsData.content);
      setCreateModuleData({ lessonId: '', title: '', description: '', content: '', duration: 0 });
      setShowCreateModule(true);
    } catch (err) {
      console.error('Error loading lessons:', err);
      setError('Error al cargar las lecciones');
    }
  };

  const openCreateAbility = () => {
    setCreateAbilityData({ name: '', description: '' });
    setShowCreateAbility(true);
  };

  const handleCreateLesson = async () => {
    try {
      await protectedApi.createLesson({
        name: createLessonData.name,
        description: createLessonData.description,
        price: createLessonData.price,
        imageUrl: createLessonData.imageUrl,
        abilities: createLessonData.abilities
      });
      setShowCreateLesson(false);
      loadData();
    } catch (err) {
      console.error('Error creating lesson:', err);
      setError('Error al crear la lección');
    }
  };

  const handleCreateModule = async () => {
    try {
      await protectedApi.createModule({
        lessonId: createModuleData.lessonId,
        title: createModuleData.title,
        description: createModuleData.description,
        content: createModuleData.content,
        duration: createModuleData.duration
      });
      setShowCreateModule(false);
      loadData();
    } catch (err) {
      console.error('Error creating module:', err);
      setError('Error al crear el módulo');
    }
  };

  const handleCreateAbility = async () => {
    try {
      await adminApi.createAbility({
        name: createAbilityData.name,
        description: createAbilityData.description
      });
      setShowCreateAbility(false);
      loadData();
    } catch (err) {
      console.error('Error creating ability:', err);
      setError('Error al crear la habilidad');
    }
  };

  const handleCreateUser = async () => {
    try {
      await adminApi.createUser({
        username: createUserData.username,
        password: createUserData.password,
        name: createUserData.name,
        surname1: createUserData.surname1,
        surname2: createUserData.surname2,
        email: createUserData.email
      });
      setShowCreateUser(false);
      loadData();
    } catch (err) {
      console.error('Error creating user:', err);
      setError('Error al crear el usuario');
    }
  };

  const toggleAbilitySelection = (abilityName: string) => {
    setCreateLessonData(prev => ({
      ...prev,
      abilities: prev.abilities.includes(abilityName)
        ? prev.abilities.filter(a => a !== abilityName)
        : [...prev.abilities, abilityName]
    }));
  };

  if (!isAdmin) {
    return null;
  }

  return (
    <div className="admin-page">
      <header className="admin-header">
        <h1>Panel de Administración</h1>
        <p className="admin-subtitle">Gestiona todos los recursos de la plataforma</p>
      </header>

      {/* Tabs de navegación */}
      <nav className="admin-tabs">
        {TABS.map(tab => (
          <button
            key={tab.id}
            className={`admin-tab ${activeTab === tab.id ? 'active' : ''}`}
            onClick={() => handleTabChange(tab.id)}
          >
            <span className="tab-label">{tab.label}</span>
          </button>
        ))}
      </nav>

      {/* Contenido principal */}
      <main className="admin-content">
        {error && (
          <div className="admin-error">
            <p>{error}</p>
            <button onClick={loadData}>Reintentar</button>
          </div>
        )}

        {isLoading ? (
          <LoadingSpinner message="Cargando datos..." />
        ) : (
          <>
            {/* Tabla de Usuarios */}
            {activeTab === 'users' && (
              <div className="admin-table-container">
                <div className="table-header">
                  <h2>Usuarios ({users.length})</h2>
                  <button className="btn-create" onClick={openCreateUser}>
                    Crear Usuario
                  </button>
                </div>
                <table className="admin-table">
                  <thead>
                    <tr>
                      <th>Username</th>
                      <th>Nombre</th>
                      <th>Email</th>
                      <th>Roles</th>
                      <th>Acciones</th>
                    </tr>
                  </thead>
                  <tbody>
                    {users.map(user => (
                      <tr key={user.username}>
                        <td className="cell-id">{user.username}</td>
                        <td>{user.name} {user.surname1}</td>
                        <td>{user.email}</td>
                        <td>
                          <div className="role-badges">
                            {user.roles?.map(role => (
                              <span key={role.rolename} className={`role-badge role-${role.rolename.toLowerCase()}`}>
                                {role.rolename}
                              </span>
                            ))}
                          </div>
                        </td>
                        <td className="cell-actions">
                          <button 
                            className="btn-edit"
                            onClick={() => openEditUser(user)}
                            title="Editar"
                          >
                            Editar
                          </button>
                          <button 
                            className="btn-delete"
                            onClick={() => handleDelete('users', user.username)}
                            disabled={user.username === authUser?.username}
                            title={user.username === authUser?.username ? 'No puedes eliminarte a ti mismo' : 'Eliminar'}
                          >
                            Eliminar
                          </button>
                        </td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>
            )}

            {/* Tabla de Lecciones */}
            {activeTab === 'lessons' && (
              <div className="admin-table-container">
                <div className="table-header">
                  <h2>Lecciones ({lessons.length})</h2>
                  <button className="btn-create" onClick={openCreateLesson}>
                    Crear Lección
                  </button>
                </div>
                <table className="admin-table">
                  <thead>
                    <tr>
                      <th>ID</th>
                      <th>Nombre</th>
                      <th>Propietario</th>
                      <th>Precio</th>
                      <th>Acciones</th>
                    </tr>
                  </thead>
                  <tbody>
                    {lessons.map(lesson => (
                      <tr key={lesson.id}>
                        <td className="cell-id">{lesson.id.substring(0, 8)}...</td>
                        <td>{lesson.name}</td>
                        <td>{lesson.ownerName || lesson.ownerId}</td>
                        <td>{lesson.price ? `${lesson.price}€` : 'Gratis'}</td>
                        <td className="cell-actions">
                          <button 
                            className="btn-view"
                            onClick={() => navigate(`/lessons/${lesson.id}`)}
                            title="Ver"
                          >
                            Ver
                          </button>
                          <button 
                            className="btn-edit"
                            onClick={() => openEditLesson(lesson)}
                            title="Editar"
                          >
                            Editar
                          </button>
                          <button 
                            className="btn-delete"
                            onClick={() => handleDelete('lessons', lesson.id)}
                            title="Eliminar"
                          >
                            Eliminar
                          </button>
                        </td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>
            )}

            {/* Tabla de Módulos */}
            {activeTab === 'modules' && (
              <div className="admin-table-container">
                <div className="table-header">
                  <h2>Módulos ({modules.length})</h2>
                  <button className="btn-create" onClick={openCreateModule}>
                    Crear Módulo
                  </button>
                </div>
                <table className="admin-table">
                  <thead>
                    <tr>
                      <th>ID</th>
                      <th>Título</th>
                      <th>Descripción</th>
                      <th>Duración</th>
                      <th>Acciones</th>
                    </tr>
                  </thead>
                  <tbody>
                    {modules.map(module => (
                      <tr key={module.id}>
                        <td className="cell-id">{module.id.substring(0, 8)}...</td>
                        <td>{module.title}</td>
                        <td className="cell-description">{module.description}</td>
                        <td>{module.duration} min</td>
                        <td className="cell-actions">
                          <button 
                            className="btn-edit"
                            onClick={() => openEditModule(module)}
                            title="Editar"
                          >
                            Editar
                          </button>
                          <button 
                            className="btn-delete"
                            onClick={() => handleDelete('modules', module.id)}
                            title="Eliminar"
                          >
                            Eliminar
                          </button>
                        </td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>
            )}

            {/* Tabla de Suscripciones */}
            {activeTab === 'subscriptions' && (
              <div className="admin-table-container">
                <div className="table-header">
                  <h2>Suscripciones ({subscriptions.length})</h2>
                </div>
                <table className="admin-table">
                  <thead>
                    <tr>
                      <th>ID</th>
                      <th>Lección</th>
                      <th>Fecha Inicio</th>
                      <th>Estado</th>
                      <th>Acciones</th>
                    </tr>
                  </thead>
                  <tbody>
                    {subscriptions.map(sub => (
                      <tr key={sub.id}>
                        <td className="cell-id">{sub.id.substring(0, 8)}...</td>
                        <td>{sub.lesson?.name || 'N/A'}</td>
                        <td>{sub.startDate}</td>
                        <td>
                          <span className={`status-badge ${sub.active ? 'active' : 'inactive'}`}>
                            {sub.active ? 'Activa' : 'Inactiva'}
                          </span>
                        </td>
                        <td className="cell-actions">
                          <button 
                            className="btn-delete"
                            onClick={() => handleDelete('subscriptions', sub.id)}
                            title="Cancelar"
                          >
                            Cancelar
                          </button>
                        </td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>
            )}

            {/* Tabla de Habilidades */}
            {activeTab === 'abilities' && (
              <div className="admin-table-container">
                <div className="table-header">
                  <h2>Habilidades ({abilities.length})</h2>
                  <button className="btn-create" onClick={openCreateAbility}>
                    Crear Habilidad
                  </button>
                </div>
                <table className="admin-table">
                  <thead>
                    <tr>
                      <th>Nombre</th>
                      <th>Descripción</th>
                      <th>Acciones</th>
                    </tr>
                  </thead>
                  <tbody>
                    {abilities.map(ability => (
                      <tr key={ability.name}>
                        <td className="cell-name">{ability.name}</td>
                        <td className="cell-description">{ability.description}</td>
                        <td className="cell-actions">
                          <button 
                            className="btn-edit"
                            onClick={() => openEditAbility(ability)}
                            title="Editar"
                          >
                            Editar
                          </button>
                          <button 
                            className="btn-delete"
                            onClick={() => handleDelete('abilities', ability.name)}
                            title="Eliminar"
                          >
                            Eliminar
                          </button>
                        </td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>
            )}

            {/* Paginación */}
            {totalPages > 1 && (
              <div className="admin-pagination">
                <button 
                  onClick={() => setPage(p => Math.max(0, p - 1))}
                  disabled={page === 0}
                >
                  Anterior
                </button>
                <span>Página {page + 1} de {totalPages}</span>
                <button 
                  onClick={() => setPage(p => Math.min(totalPages - 1, p + 1))}
                  disabled={page >= totalPages - 1}
                >
                  Siguiente
                </button>
              </div>
            )}
          </>
        )}
      </main>

      {/* Modal de edición de usuario */}
      {editingUser && (
        <div className="modal-overlay" onClick={() => setEditingUser(null)}>
          <div className="modal-content" onClick={e => e.stopPropagation()}>
            <h3>Editar Usuario: {editingUser.username}</h3>
            <div className="form-group">
              <label>Nombre</label>
              <input
                type="text"
                value={editUserData.name || ''}
                onChange={e => setEditUserData({...editUserData, name: e.target.value})}
              />
            </div>
            <div className="form-group">
              <label>Primer Apellido</label>
              <input
                type="text"
                value={editUserData.surname1 || ''}
                onChange={e => setEditUserData({...editUserData, surname1: e.target.value})}
              />
            </div>
            <div className="form-group">
              <label>Segundo Apellido</label>
              <input
                type="text"
                value={editUserData.surname2 || ''}
                onChange={e => setEditUserData({...editUserData, surname2: e.target.value})}
              />
            </div>
            <div className="form-group">
              <label>Email</label>
              <input
                type="email"
                value={editUserData.email || ''}
                onChange={e => setEditUserData({...editUserData, email: e.target.value})}
              />
            </div>
            <div className="modal-actions">
              <button className="btn-cancel" onClick={() => setEditingUser(null)}>Cancelar</button>
              <button className="btn-save" onClick={saveUser}>Guardar</button>
            </div>
          </div>
        </div>
      )}

      {/* Modal de edición de lección */}
      {editingLesson && (
        <div className="modal-overlay" onClick={() => setEditingLesson(null)}>
          <div className="modal-content" onClick={e => e.stopPropagation()}>
            <h3>Editar Lección: {editingLesson.name}</h3>
            <div className="form-group">
              <label>Nombre</label>
              <input
                type="text"
                value={editLessonData.name || ''}
                onChange={e => setEditLessonData({...editLessonData, name: e.target.value})}
              />
            </div>
            <div className="form-group">
              <label>Descripción</label>
              <textarea
                value={editLessonData.description || ''}
                onChange={e => setEditLessonData({...editLessonData, description: e.target.value})}
              />
            </div>
            <div className="form-group">
              <label>Precio</label>
              <input
                type="number"
                step="0.01"
                value={editLessonData.price || 0}
                onChange={e => setEditLessonData({...editLessonData, price: parseFloat(e.target.value)})}
              />
            </div>
            <div className="modal-actions">
              <button className="btn-cancel" onClick={() => setEditingLesson(null)}>Cancelar</button>
              <button className="btn-save" onClick={saveLesson}>Guardar</button>
            </div>
          </div>
        </div>
      )}

      {/* Modal de edición de módulo */}
      {editingModule && (
        <div className="modal-overlay" onClick={() => setEditingModule(null)}>
          <div className="modal-content" onClick={e => e.stopPropagation()}>
            <h3>Editar Módulo: {editingModule.title}</h3>
            <div className="form-group">
              <label>Título</label>
              <input
                type="text"
                value={editModuleData.title || ''}
                onChange={e => setEditModuleData({...editModuleData, title: e.target.value})}
              />
            </div>
            <div className="form-group">
              <label>Descripción</label>
              <textarea
                value={editModuleData.description || ''}
                onChange={e => setEditModuleData({...editModuleData, description: e.target.value})}
              />
            </div>
            <div className="form-group">
              <label>Duración (minutos)</label>
              <input
                type="number"
                value={editModuleData.duration || 0}
                onChange={e => setEditModuleData({...editModuleData, duration: parseInt(e.target.value)})}
              />
            </div>
            <div className="modal-actions">
              <button className="btn-cancel" onClick={() => setEditingModule(null)}>Cancelar</button>
              <button className="btn-save" onClick={saveModule}>Guardar</button>
            </div>
          </div>
        </div>
      )}

      {/* Modal de crear lección */}
      {showCreateLesson && (
        <div className="modal-overlay" onClick={() => setShowCreateLesson(false)}>
          <div className="modal-content modal-large" onClick={e => e.stopPropagation()}>
            <h3>Crear Nueva Lección</h3>
            <div className="form-group">
              <label>Nombre *</label>
              <input
                type="text"
                value={createLessonData.name}
                onChange={e => setCreateLessonData({...createLessonData, name: e.target.value})}
                placeholder="Nombre de la lección"
              />
            </div>
            <div className="form-group">
              <label>Descripción *</label>
              <textarea
                value={createLessonData.description}
                onChange={e => setCreateLessonData({...createLessonData, description: e.target.value})}
                placeholder="Descripción de la lección"
              />
            </div>
            <div className="form-group">
              <label>Precio</label>
              <input
                type="number"
                step="0.01"
                min="0"
                value={createLessonData.price}
                onChange={e => setCreateLessonData({...createLessonData, price: parseFloat(e.target.value) || 0})}
              />
            </div>
            <div className="form-group">
              <label>URL de Imagen</label>
              <input
                type="text"
                value={createLessonData.imageUrl}
                onChange={e => setCreateLessonData({...createLessonData, imageUrl: e.target.value})}
                placeholder="https://..."
              />
            </div>
            <div className="form-group">
              <label>Habilidades</label>
              <div className="abilities-grid">
                {availableAbilities.map(ability => (
                  <label key={ability.name} className="ability-checkbox">
                    <input
                      type="checkbox"
                      checked={createLessonData.abilities.includes(ability.name)}
                      onChange={() => toggleAbilitySelection(ability.name)}
                    />
                    <span>{ability.name}</span>
                  </label>
                ))}
              </div>
            </div>
            <div className="modal-actions">
              <button className="btn-cancel" onClick={() => setShowCreateLesson(false)}>Cancelar</button>
              <button 
                className="btn-save" 
                onClick={handleCreateLesson}
                disabled={!createLessonData.name || !createLessonData.description}
              >
                Crear
              </button>
            </div>
          </div>
        </div>
      )}

      {/* Modal de crear módulo */}
      {showCreateModule && (
        <div className="modal-overlay" onClick={() => setShowCreateModule(false)}>
          <div className="modal-content modal-large" onClick={e => e.stopPropagation()}>
            <h3>Crear Nuevo Módulo</h3>
            <div className="form-group">
              <label>Lección *</label>
              <select
                value={createModuleData.lessonId}
                onChange={e => setCreateModuleData({...createModuleData, lessonId: e.target.value})}
              >
                <option value="">Selecciona una lección</option>
                {allLessons.map(lesson => (
                  <option key={lesson.id} value={lesson.id}>
                    {lesson.name}
                  </option>
                ))}
              </select>
            </div>
            <div className="form-group">
              <label>Título *</label>
              <input
                type="text"
                value={createModuleData.title}
                onChange={e => setCreateModuleData({...createModuleData, title: e.target.value})}
                placeholder="Título del módulo"
              />
            </div>
            <div className="form-group">
              <label>Descripción *</label>
              <textarea
                value={createModuleData.description}
                onChange={e => setCreateModuleData({...createModuleData, description: e.target.value})}
                placeholder="Descripción del módulo"
              />
            </div>
            <div className="form-group">
              <label>Contenido</label>
              <textarea
                value={createModuleData.content}
                onChange={e => setCreateModuleData({...createModuleData, content: e.target.value})}
                placeholder="Contenido del módulo (puede ser HTML o Markdown)"
                className="content-textarea"
              />
            </div>
            <div className="form-group">
              <label>Duración (minutos)</label>
              <input
                type="number"
                min="0"
                value={createModuleData.duration}
                onChange={e => setCreateModuleData({...createModuleData, duration: parseInt(e.target.value) || 0})}
              />
            </div>
            <div className="modal-actions">
              <button className="btn-cancel" onClick={() => setShowCreateModule(false)}>Cancelar</button>
              <button 
                className="btn-save" 
                onClick={handleCreateModule}
                disabled={!createModuleData.lessonId || !createModuleData.title || !createModuleData.description}
              >
                Crear
              </button>
            </div>
          </div>
        </div>
      )}

      {/* Modal de crear habilidad */}
      {showCreateAbility && (
        <div className="modal-overlay" onClick={() => setShowCreateAbility(false)}>
          <div className="modal-content" onClick={e => e.stopPropagation()}>
            <h3>Crear Nueva Habilidad</h3>
            <div className="form-group">
              <label>Nombre *</label>
              <input
                type="text"
                value={createAbilityData.name}
                onChange={e => setCreateAbilityData({...createAbilityData, name: e.target.value})}
                placeholder="Nombre de la habilidad"
              />
            </div>
            <div className="form-group">
              <label>Descripción *</label>
              <textarea
                value={createAbilityData.description}
                onChange={e => setCreateAbilityData({...createAbilityData, description: e.target.value})}
                placeholder="Descripción de la habilidad"
              />
            </div>
            <div className="modal-actions">
              <button className="btn-cancel" onClick={() => setShowCreateAbility(false)}>Cancelar</button>
              <button 
                className="btn-save" 
                onClick={handleCreateAbility}
                disabled={!createAbilityData.name || !createAbilityData.description}
              >
                Crear
              </button>
            </div>
          </div>
        </div>
      )}

      {/* Modal de editar habilidad */}
      {editingAbility && (
        <div className="modal-overlay" onClick={() => setEditingAbility(null)}>
          <div className="modal-content" onClick={e => e.stopPropagation()}>
            <h3>Editar Habilidad: {editingAbility.name}</h3>
            <div className="form-group">
              <label>Descripción</label>
              <textarea
                value={editAbilityData.description || ''}
                onChange={e => setEditAbilityData({...editAbilityData, description: e.target.value})}
              />
            </div>
            <div className="modal-actions">
              <button className="btn-cancel" onClick={() => setEditingAbility(null)}>Cancelar</button>
              <button className="btn-save" onClick={saveAbility}>Guardar</button>
            </div>
          </div>
        </div>
      )}

      {/* Modal de crear usuario */}
      {showCreateUser && (
        <div className="modal-overlay" onClick={() => setShowCreateUser(false)}>
          <div className="modal-content modal-large" onClick={e => e.stopPropagation()}>
            <h3>Crear Nuevo Usuario</h3>
            <div className="form-group">
              <label>Username *</label>
              <input
                type="text"
                value={createUserData.username}
                onChange={e => setCreateUserData({...createUserData, username: e.target.value})}
                placeholder="Nombre de usuario"
              />
            </div>
            <div className="form-group">
              <label>Contraseña *</label>
              <input
                type="password"
                value={createUserData.password}
                onChange={e => setCreateUserData({...createUserData, password: e.target.value})}
                placeholder="Contraseña"
              />
            </div>
            <div className="form-group">
              <label>Nombre *</label>
              <input
                type="text"
                value={createUserData.name}
                onChange={e => setCreateUserData({...createUserData, name: e.target.value})}
                placeholder="Nombre"
              />
            </div>
            <div className="form-group">
              <label>Primer Apellido *</label>
              <input
                type="text"
                value={createUserData.surname1}
                onChange={e => setCreateUserData({...createUserData, surname1: e.target.value})}
                placeholder="Primer apellido"
              />
            </div>
            <div className="form-group">
              <label>Segundo Apellido</label>
              <input
                type="text"
                value={createUserData.surname2}
                onChange={e => setCreateUserData({...createUserData, surname2: e.target.value})}
                placeholder="Segundo apellido (opcional)"
              />
            </div>
            <div className="form-group">
              <label>Email *</label>
              <input
                type="email"
                value={createUserData.email}
                onChange={e => setCreateUserData({...createUserData, email: e.target.value})}
                placeholder="correo@ejemplo.com"
              />
            </div>
            <div className="modal-actions">
              <button className="btn-cancel" onClick={() => setShowCreateUser(false)}>Cancelar</button>
              <button 
                className="btn-save" 
                onClick={handleCreateUser}
                disabled={!createUserData.username || !createUserData.password || !createUserData.name || !createUserData.surname1 || !createUserData.email}
              >
                Crear
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}

export default AdminPage;
