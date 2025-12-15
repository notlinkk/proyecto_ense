-- ============================================
-- INITIAL DATA FOR MENTORY EDUCATIONAL PLATFORM
-- ============================================
-- This script clears existing data and inserts fresh test data
-- Run order is important due to foreign key constraints

-- ============================================
-- STEP 1: CLEAR ALL EXISTING DATA
-- ============================================
DELETE FROM subscriptions WHERE true;
DELETE FROM modules WHERE true;
DELETE FROM lesson_ability WHERE true;
DELETE FROM user_lessons WHERE true;
DELETE FROM lessons WHERE true;
DELETE FROM abilities WHERE true;
DELETE FROM user_roles WHERE true;
DELETE FROM role_permissions WHERE true;
DELETE FROM role_hierarchy WHERE true;
DELETE FROM refresh_tokens WHERE true;
DELETE FROM users WHERE true;
DELETE FROM roles WHERE true;
DELETE FROM permissions WHERE true;

-- ============================================
-- STEP 2: PERMISSIONS
-- ============================================
INSERT INTO permissions (id, resource, action) VALUES
('users:read', 'users', 'read'),
('users:write', 'users', 'write'),
('users:update', 'users', 'update'),
('users:delete', 'users', 'delete'),
('abilities:read', 'abilities', 'read'),
('abilities:write', 'abilities', 'write'),
('abilities:update', 'abilities', 'update'),
('abilities:delete', 'abilities', 'delete'),
('lessons:read', 'lessons', 'read'),
('lessons:write', 'lessons', 'write'),
('lessons:update', 'lessons', 'update'),
('lessons:delete', 'lessons', 'delete'),
('modules:read', 'modules', 'read'),
('modules:write', 'modules', 'write'),
('modules:update', 'modules', 'update'),
('modules:delete', 'modules', 'delete'),
('subscriptions:read', 'subscriptions', 'read'),
('subscriptions:write', 'subscriptions', 'write'),
('subscriptions:update', 'subscriptions', 'update'),
('subscriptions:delete', 'subscriptions', 'delete');

-- ============================================
-- STEP 3: ROLES
-- ============================================
INSERT INTO roles (rolename) VALUES
('ADMIN'),
('USER'),
('TEACHER');

-- ============================================
-- STEP 4: ROLE HIERARCHY
-- ============================================
INSERT INTO role_hierarchy(role, included_role) VALUES
('ADMIN', 'TEACHER'),
('TEACHER', 'USER');

-- ============================================
-- STEP 5: ROLE PERMISSIONS
-- ============================================
INSERT INTO role_permissions (role, permission) VALUES
('ADMIN', 'users:read'),
('ADMIN', 'users:write'),
('ADMIN', 'users:update'),
('ADMIN', 'users:delete'),
('ADMIN', 'abilities:read'),
('ADMIN', 'abilities:write'),
('ADMIN', 'abilities:update'),
('ADMIN', 'abilities:delete'),
('ADMIN', 'lessons:read'),
('ADMIN', 'lessons:write'),
('ADMIN', 'lessons:update'),
('ADMIN', 'lessons:delete'),
('ADMIN', 'modules:read'),
('ADMIN', 'modules:write'),
('ADMIN', 'modules:update'),
('ADMIN', 'modules:delete'),
('ADMIN', 'subscriptions:read'),
('ADMIN', 'subscriptions:write'),
('ADMIN', 'subscriptions:update'),
('ADMIN', 'subscriptions:delete'),
('TEACHER', 'lessons:read'),
('TEACHER', 'lessons:write'),
('TEACHER', 'lessons:update'),
('TEACHER', 'modules:read'),
('TEACHER', 'modules:write'),
('TEACHER', 'modules:update'),
('TEACHER', 'abilities:read'),
('USER', 'lessons:read'),
('USER', 'modules:read'),
('USER', 'abilities:read'),
('USER', 'subscriptions:read'),
('USER', 'subscriptions:write');

-- ============================================
-- STEP 6: ABILITIES (Habilidades del mundo real)
-- ============================================
INSERT INTO abilities (name, description) VALUES
-- Música
('Piano', 'Tocar piano e instrumentos de teclado'),
('Guitar', 'Técnicas de guitarra acústica y eléctrica'),
('Music Theory', 'Entender escalas, acordes y composición'),
('Singing', 'Técnicas vocales e interpretación'),
('Drums', 'Percusión y fundamentos de ritmo'),

-- Gaming y Digital
('Minecraft Redstone', 'Construir circuitos complejos en Minecraft'),
('Chess', 'Pensamiento estratégico y juego de ajedrez'),
('Speedcubing', 'Resolver cubo de Rubik y otros puzzles rápidamente'),
('Game Design', 'Diseñar mecánicas de videojuegos atractivas'),
('Pixel Art', 'Crear arte digital en estilo pixel'),

-- Artes y Manualidades
('Drawing', 'Bocetos y fundamentos de ilustración'),
('Watercolor Painting', 'Técnicas de acuarela y composición'),
('Origami', 'Arte japonés de plegado de papel'),
('Knitting', 'Crear tejidos con lana'),
('Calligraphy', 'Escritura decorativa y lettering'),

-- Cocina y Repostería
('Baking', 'Preparación de pan, pastelería y postres'),
('Italian Cuisine', 'Técnicas tradicionales de cocina italiana'),
('Sushi Making', 'Preparación y presentación de sushi japonés'),
('Coffee Brewing', 'Espresso, pour-over y café de especialidad'),

-- Idiomas
('Spanish', 'Conversación y gramática del idioma español'),
('Japanese', 'Idioma japonés incluyendo hiragana y kanji'),
('Sign Language', 'Comunicación en lengua de señas americana'),

-- Deportes y Fitness
('Yoga', 'Bienestar físico y mental a través del yoga'),
('Swimming', 'Técnicas de natación y seguridad acuática'),
('Skateboarding', 'Trucos y técnicas de skateboard'),

-- Habilidades de Vida
('Photography', 'Técnicas de cámara y composición'),
('Public Speaking', 'Presentación y comunicación con confianza'),
('Gardening', 'Cuidado de plantas y diseño de jardines'),
('First Aid', 'Fundamentos de respuesta médica de emergencia');

-- ============================================
-- STEP 7: USERS
-- ============================================
INSERT INTO users (username, name, surname1, surname2, email, password) VALUES
('admin', 'Administrator', 'System', '', 'admin@mentory.com', '{bcrypt}$2a$10$aRBN4Mi5tTnxLArhkiXCQuvC2XfUDwLe412RKnO9O.D8LPuLQDdcW'),
('teacher1', 'Elena', 'Martinez', 'Piano', 'elena.martinez@mentory.com', '{bcrypt}$2a$10$aRBN4Mi5tTnxLArhkiXCQuvC2XfUDwLe412RKnO9O.D8LPuLQDdcW'),
('teacher2', 'Marcus', 'Chen', 'Gaming', 'marcus.chen@mentory.com', '{bcrypt}$2a$10$aRBN4Mi5tTnxLArhkiXCQuvC2XfUDwLe412RKnO9O.D8LPuLQDdcW'),
('teacher3', 'Sofia', 'Rossi', 'Cooking', 'sofia.rossi@mentory.com', '{bcrypt}$2a$10$aRBN4Mi5tTnxLArhkiXCQuvC2XfUDwLe412RKnO9O.D8LPuLQDdcW'),
('student1', 'Ana', 'Martinez', 'Sanchez', 'ana.martinez@email.com', '{bcrypt}$2a$10$aRBN4Mi5tTnxLArhkiXCQuvC2XfUDwLe412RKnO9O.D8LPuLQDdcW'),
('student2', 'Pedro', 'Lopez', 'Fernandez', 'pedro.lopez@email.com', '{bcrypt}$2a$10$aRBN4Mi5tTnxLArhkiXCQuvC2XfUDwLe412RKnO9O.D8LPuLQDdcW'),
('student3', 'Laura', 'Hernandez', 'Diaz', 'laura.hernandez@email.com', '{bcrypt}$2a$10$aRBN4Mi5tTnxLArhkiXCQuvC2XfUDwLe412RKnO9O.D8LPuLQDdcW');

-- ============================================
-- STEP 8: USER ROLES
-- ============================================
INSERT INTO user_roles (username, rolename) VALUES
('admin', 'ADMIN'),
('teacher1', 'TEACHER'),
('teacher2', 'TEACHER'),
('teacher3', 'TEACHER'),
('student1', 'USER'),
('student2', 'USER'),
('student3', 'USER');

-- ============================================
-- STEP 9: LECCIONES
-- ============================================
INSERT INTO lessons (id, name, description, owner_id, price, image_url) VALUES
-- Lecciones de Música (teacher1)
('lesson-piano-beginners', 'Piano para Principiantes', 'Aprende a tocar el piano desde cero. Este curso completo cubre posición de manos, lectura de partituras, acordes básicos y tus primeras canciones. Perfecto para principiantes que quieren iniciar su viaje musical.', 'teacher1', 49.99, 'https://images.unsplash.com/photo-1552422535-c45813c61732?w=800'),
('lesson-music-theory', 'Fundamentos de Teoría Musical', 'Comprende los bloques de construcción de la música: escalas, intervalos, progresiones de acordes y ritmo. Conocimiento esencial para cualquier músico que quiera componer, improvisar o entender mejor sus canciones favoritas.', 'teacher1', 39.99, 'https://images.unsplash.com/photo-1507838153414-b4b713384a76?w=800'),
('lesson-guitar-acoustic', 'Guitarra Acústica Esencial', 'Domina la guitarra acústica con posición correcta de dedos, patrones de rasgueo y progresiones de acordes populares. Aprende a tocar canciones de fogata y mucho más con confianza.', 'teacher1', 44.99, 'https://images.unsplash.com/photo-1510915361894-db8b60106cb1?w=800'),

-- Lecciones de Gaming y Tecnología (teacher2)
('lesson-redstone-mastery', 'Ingeniería Redstone en Minecraft', 'Conviértete en ingeniero de redstone! Aprende a construir granjas automáticas, puertas ocultas, calculadoras y mecanismos complejos. Desde circuitos básicos hasta lógica de computadora en Minecraft.', 'teacher2', 29.99, 'https://images.unsplash.com/photo-1587573089734-09cb69c0f2b4?w=800'),
('lesson-chess-strategy', 'Estrategia y Tácticas de Ajedrez', 'Eleva tu juego de ajedrez con principios de apertura, tácticas de medio juego y técnicas de final. Aprende de partidas de grandes maestros y desarrolla tu pensamiento estratégico.', 'teacher2', 34.99, 'https://images.unsplash.com/photo-1529699211952-734e80c4d42b?w=800'),
('lesson-speedcubing', 'Speedcubing: Resuelve el Cubo de Rubik', 'Aprende a resolver el cubo de Rubik en menos de 2 minutos! Domina el método principiante, luego avanza a CFOP para tiempos sub-30 segundos. Incluye finger tricks y rutinas de práctica.', 'teacher2', 24.99, 'https://images.unsplash.com/photo-1577401239170-897942555fb3?w=800'),

-- Lecciones de Cocina (teacher3)
('lesson-italian-cooking', 'Cocina Italiana Auténtica', 'Cocina como un verdadero italiano! Aprende a hacer pasta fresca desde cero, salsas auténticas, risotto y postres clásicos como el tiramisú. Técnicas transmitidas por generaciones.', 'teacher3', 54.99, 'https://images.unsplash.com/photo-1498579150354-977475b7ea0b?w=800'),
('lesson-sushi-making', 'El Arte del Sushi', 'Domina el arte de hacer sushi en casa. Aprende preparación correcta del arroz, selección y manejo del pescado, técnicas de enrollado para maki, presentación de nigiri y acompañamientos tradicionales.', 'teacher3', 59.99, 'https://images.unsplash.com/photo-1579871494447-9811cf80d66c?w=800'),
('lesson-bread-baking', 'Panadería Artesanal', 'Hornea pan de calidad profesional en casa. Desde pan de masa madre crujiente hasta brioche suave, aprende fermentación, formado, cortes y cómo lograr la corteza y miga perfectas.', 'teacher3', 44.99, 'https://images.unsplash.com/photo-1509440159596-0249088772ff?w=800');

-- ============================================
-- STEP 10: LESSON-ABILITY ASSOCIATIONS
-- ============================================
INSERT INTO lesson_ability (lesson_id, ability_name) VALUES
('lesson-piano-beginners', 'Piano'),
('lesson-piano-beginners', 'Music Theory'),
('lesson-music-theory', 'Music Theory'),
('lesson-music-theory', 'Piano'),
('lesson-guitar-acoustic', 'Guitar'),
('lesson-guitar-acoustic', 'Music Theory'),
('lesson-redstone-mastery', 'Minecraft Redstone'),
('lesson-redstone-mastery', 'Game Design'),
('lesson-chess-strategy', 'Chess'),
('lesson-speedcubing', 'Speedcubing'),
('lesson-italian-cooking', 'Italian Cuisine'),
('lesson-italian-cooking', 'Baking'),
('lesson-sushi-making', 'Sushi Making'),
('lesson-bread-baking', 'Baking');

-- ============================================
-- STEP 11: USER-LESSON ASSOCIATIONS (Creators)
-- ============================================
INSERT INTO user_lessons (username, lesson_id) VALUES
('teacher1', 'lesson-piano-beginners'),
('teacher1', 'lesson-music-theory'),
('teacher1', 'lesson-guitar-acoustic'),
('teacher2', 'lesson-redstone-mastery'),
('teacher2', 'lesson-chess-strategy'),
('teacher2', 'lesson-speedcubing'),
('teacher3', 'lesson-italian-cooking'),
('teacher3', 'lesson-sushi-making'),
('teacher3', 'lesson-bread-baking');

-- ============================================
-- STEP 12: MÓDULOS
-- ============================================
INSERT INTO modules (id, title, description, content, duration, position, lesson_id) VALUES
-- Módulos de Piano
('mod-piano-1', 'Introducción al Piano', 'Conoce el teclado y la postura correcta', 'Bienvenido a tu viaje musical con el piano! El piano tiene 88 teclas en un patrón repetitivo. Las teclas blancas son notas naturales (Do, Re, Mi, Fa, Sol, La, Si) y las negras son sostenidos/bemoles. Encuentra el Do Central - es la tecla blanca justo a la izquierda de las dos teclas negras del centro. Siéntate centrado con tu ombligo alineado al Do Central. Mantén la espalda recta pero relajada, pies planos en el suelo. Tus dedos se numeran 1-5, siendo el pulgar el 1.', 45, 1, 'lesson-piano-beginners'),
('mod-piano-2', 'Lectura de Partituras', 'Aprende a leer notación musical y ritmo', 'La música se escribe en cinco líneas horizontales llamadas pentagrama. La clave de Sol es para tu mano derecha - las líneas son Mi-Sol-Si-Re-Fa, los espacios son Fa-La-Do-Mi. La clave de Fa es para tu mano izquierda. Valores de notas: Redonda = 4 tiempos, Blanca = 2 tiempos, Negra = 1 tiempo, Corchea = 1/2 tiempo. Los compases como 4/4 indican cuántos tiempos por compás.', 60, 2, 'lesson-piano-beginners'),
('mod-piano-3', 'Acordes Básicos y Armonía', 'Aprende triadas mayores y menores', 'Los acordes son tres o más notas tocadas juntas. Acordes mayores suenan alegres: Do Mayor = Do-Mi-Sol, Sol Mayor = Sol-Si-Re, Fa Mayor = Fa-La-Do. Acordes menores suenan tristes: La menor = La-Do-Mi, Re menor = Re-Fa-La. La progresión I-IV-V en Do usa acordes Do-Fa-Sol - miles de canciones usan solo estos tres acordes! Practica cambiar entre ellos suavemente.', 55, 3, 'lesson-piano-beginners'),
('mod-piano-4', 'Tus Primeras Canciones', 'Pon todo junto y toca piezas completas', 'Ahora tocamos música real! Himno a la Alegría: Mi-Mi-Fa-Sol-Sol-Fa-Mi-Re-Do-Do-Re-Mi-Mi-Re-Re. Estrellita: Do-Do-Sol-Sol-La-La-Sol, Fa-Fa-Mi-Mi-Re-Re-Do. Practica manos separadas primero, luego combina. Usa un metrónomo empezando a 60 BPM. Grábate para identificar áreas de mejora.', 50, 4, 'lesson-piano-beginners'),
-- Módulos de Teoría Musical
('mod-theory-1', 'Fundamentos de Escalas', 'Entiende escalas mayores y menores', 'Una escala es una secuencia de notas en orden ascendente o descendente. La escala mayor sigue el patrón: Tono-Tono-Semitono-Tono-Tono-Tono-Semitono. Do Mayor: Do-Re-Mi-Fa-Sol-La-Si-Do (solo teclas blancas). La escala menor natural tiene un patrón diferente creando un sonido más oscuro. La menor: La-Si-Do-Re-Mi-Fa-Sol-La.', 50, 1, 'lesson-music-theory'),
('mod-theory-2', 'Intervalos y Armonía', 'Aprende las distancias entre notas', 'Un intervalo es la distancia entre dos notas. Segunda menor = 1 semitono, Segunda mayor = 2 semitonos, Tercera menor = 3 semitonos, Tercera mayor = 4 semitonos. La quinta perfecta (7 semitonos) es la base de los power chords. Entrena tu oído cantando intervalos y reconociéndolos en canciones.', 55, 2, 'lesson-music-theory'),
('mod-theory-3', 'Progresiones de Acordes', 'Construye secuencias de acordes efectivas', 'Los acordes se construyen apilando terceras. I-IV-V-I es la progresión más común en música occidental. En Do: Do-Fa-Sol-Do. I-V-vi-IV es el "progression de 4 acordes" usado en cientos de hits pop. ii-V-I es esencial en jazz. Analiza tus canciones favoritas para encontrar estos patrones.', 60, 3, 'lesson-music-theory'),
('mod-theory-4', 'Ritmo y Compás', 'Domina patrones rítmicos y métricas', 'El compás organiza la música en grupos de tiempos. 4/4 tiene 4 negras por compás (el más común). 3/4 es el vals con 3 tiempos. 6/8 tiene sensación de balanceo. Practica con metrónomo, palmea ritmos, y cuenta en voz alta. La síncopa acentúa tiempos débiles creando groove.', 45, 4, 'lesson-music-theory'),
-- Módulos de Guitarra
('mod-guitar-1', 'Anatomía y Afinación', 'Conoce tu guitarra y afínala correctamente', 'Las cuerdas de la guitarra de grave a agudo son: Mi-La-Re-Sol-Si-Mi (E-A-D-G-B-E). Memoriza con: "Mi Amigo Diego Solo Bebe Estrellas". Usa un afinador electrónico o app. Los trastes dividen el diapasón - cada traste sube un semitono. Aprende las partes: clavijero, cejuela, mástil, cuerpo, boca, puente.', 40, 1, 'lesson-guitar-acoustic'),
('mod-guitar-2', 'Acordes Abiertos Básicos', 'Domina los acordes esenciales', 'Los acordes abiertos usan cuerdas al aire. Mi Mayor: dedos en trastes 1-2-2 de cuerdas 3-5-4. La Mayor: dedos en traste 2 de cuerdas 2-3-4. Re Mayor: forma triangular en las 3 primeras cuerdas. Do Mayor: forma escalonada. Sol Mayor: usa los 4 dedos. Practica cambios lentamente hasta que sean automáticos.', 55, 2, 'lesson-guitar-acoustic'),
('mod-guitar-3', 'Patrones de Rasgueo', 'Desarrolla tu mano derecha', 'El rasgueo básico: abajo en cada tiempo. Patrón folk: Abajo-Abajo-Arriba-Arriba-Abajo-Arriba. Mantén el movimiento del brazo constante como un péndulo. Acentúa el tiempo 2 y 4 para sensación de rock/pop. Practica con metrónomo empezando a 60 BPM, sube gradualmente.', 50, 3, 'lesson-guitar-acoustic'),
('mod-guitar-4', 'Tus Primeras Canciones', 'Toca canciones completas', 'Canciones con 2 acordes: "Horse With No Name" (Mi menor, Re6/9). Canciones con 3 acordes: "Twist and Shout" (Re-Sol-La). Canciones con 4 acordes: "Wonderwall" (Mi menor-Sol-Re-La). Practica los cambios de acordes antes de añadir el rasgueo. Canta mientras tocas para mejorar el timing.', 60, 4, 'lesson-guitar-acoustic'),
-- Módulos de Redstone
('mod-redstone-1', 'Fundamentos de Redstone', 'Entiende polvo, antorchas y energía', 'Redstone es la electricidad de Minecraft. Fuentes de energía: Antorcha Redstone (siempre encendida), Palanca (interruptor), Botón (pulso breve), Placa de presión (activada por peso). La energía viaja por el polvo con intensidad decreciente - empieza en 15, pierde 1 por bloque. Usa repetidores para refrescar la señal.', 40, 1, 'lesson-redstone-mastery'),
('mod-redstone-2', 'Puertas Lógicas', 'Construye puertas AND, OR, NOT y XOR', 'Las puertas lógicas son bloques de computación. Puerta NOT: Coloca una antorcha en un bloque - al energizar el bloque la antorcha se apaga. Puerta OR: Conecta dos entradas que convergen en una salida. Puerta AND: Usa dos antorchas en serie - solo cuando ambas entradas están ON la salida está ON.', 55, 2, 'lesson-redstone-mastery'),
('mod-redstone-3', 'Pistones y Máquinas Voladoras', 'Crea movimiento con pistones pegajosos', 'Los pistones normales empujan bloques, los pegajosos pueden empujar Y tirar. Los pistones mueven hasta 12 bloques. Los bloques de slime se pegan a bloques adyacentes. Máquina voladora: 2 observadores, 2 pistones pegajosos, 2 bloques de slime - crea un loop auto-perpetuante!', 65, 3, 'lesson-redstone-mastery'),
('mod-redstone-4', 'Granjas Automáticas', 'Construye granjas auto-cosechadoras', 'Granja de cultivos: área 9x9 con agua central, observadores mirando cultivos, pistones empujan agua cuando crecen, tolvas recolectan en cofre. Granja de caña: Planta junto al agua, pistones a altura 2, reloj de observadores activa cosecha. Granja de hierro: 20+ aldeanos con camas, zombie atrapado los asusta, golems aparecen y caen en lava.', 75, 4, 'lesson-redstone-mastery'),
-- Módulos de Ajedrez
('mod-chess-1', 'Principios de Apertura', 'Comienza tus partidas con ventaja', 'Los tres principios de apertura: 1) Controla el centro con peones (e4, d4). 2) Desarrolla piezas menores (caballos y alfiles) hacia casillas activas. 3) Enroca temprano para proteger tu rey. Evita mover la misma pieza dos veces y no saques la dama muy pronto. Estudia aperturas básicas: Italiana, Española, Defensa Siciliana.', 50, 1, 'lesson-chess-strategy'),
('mod-chess-2', 'Tácticas Fundamentales', 'Reconoce patrones ganadores', 'Clavada: una pieza no puede moverse porque expondría una pieza más valiosa. Horquilla: una pieza ataca dos objetivos simultáneamente (especialmente caballos). Ataque descubierto: al mover una pieza, otra ataca. Jaque doble: el rey recibe jaque de dos piezas. Practica puzzles tácticos diariamente.', 60, 2, 'lesson-chess-strategy'),
('mod-chess-3', 'Estrategia de Medio Juego', 'Planifica y ejecuta tu estrategia', 'Evalúa la posición: material, seguridad del rey, estructura de peones, actividad de piezas. Crea debilidades en la posición rival: peones doblados, aislados o retrasados. Domina columnas y diagonales abiertas con torres y alfiles. Coordina tus piezas para ataques al rey o ventajas posicionales.', 55, 3, 'lesson-chess-strategy'),
('mod-chess-4', 'Técnicas de Final', 'Convierte ventajas en victorias', 'Rey y torre vs rey: lleva al rey rival al borde del tablero. Rey y dama vs rey: usa el método de la escalera. Finales de peones: la regla del cuadrado determina si un peón corona. Oposición: cuando los reyes se enfrentan con una casilla entre ellos, quien NO mueve tiene ventaja. Practica finales básicos hasta dominarlos.', 50, 4, 'lesson-chess-strategy'),
-- Módulos de Speedcubing
('mod-cube-1', 'Conoce tu Cubo', 'Entiende la mecánica y notación', 'El cubo 3x3 tiene 6 caras: Blanca (D-abajo), Amarilla (U-arriba), Roja (R-derecha), Naranja (L-izquierda), Azul (B-atrás), Verde (F-frente). Notación: R = rotar derecha horario, R'' = antihorario, R2 = doble giro. Los centros no se mueven y definen el color de cada cara. Las esquinas tienen 3 colores, las aristas tienen 2.', 35, 1, 'lesson-speedcubing'),
('mod-cube-2', 'Método Principiante - Cruz', 'Resuelve la primera capa', 'Paso 1: Forma la cruz blanca. Encuentra aristas blancas y posiciónalas correctamente alineando también el color lateral con su centro. Paso 2: Inserta las esquinas blancas usando el algoritmo: R U R'' U'' (repite hasta que la esquina esté en su lugar). La primera capa debe quedar completa y correcta.', 45, 2, 'lesson-speedcubing'),
('mod-cube-3', 'Método Principiante - F2L y OLL', 'Completa dos capas y orienta la última', 'Segunda capa: Encuentra aristas sin amarillo, posiciona arriba del centro correcto, usa: U R U'' R'' U'' F'' U F (derecha) o U'' L'' U L U F U'' F'' (izquierda). OLL: Forma la cruz amarilla, luego orienta las esquinas. Algoritmo de cruz: F R U R'' U'' F''. Puede requerir 1-3 aplicaciones.', 55, 3, 'lesson-speedcubing'),
('mod-cube-4', 'PLL y Finger Tricks', 'Permuta la última capa con velocidad', 'PLL posiciona las piezas amarillas correctamente. Algoritmo T-perm: R U R'' U'' R'' F R2 U'' R'' U'' R U R'' F''. Practica finger tricks: usa los dedos índice y pulgar para giros rápidos. Practica algoritmos lentamente primero, luego aumenta velocidad. Meta: sub-2 minutos con práctica constante!', 50, 4, 'lesson-speedcubing'),
-- Módulos de Cocina Italiana
('mod-italian-1', 'Pasta Fresca desde Cero', 'Domina la masa de pasta artesanal', 'La pasta fresca usa proporción 100g harina por huevo. Haz un volcán con la harina, huevos al centro, mezcla con tenedor gradualmente. Amasa 10 minutos hasta que esté suave y elástica. Reposa 30 minutos envuelta en film. Estira con rodillo o máquina de pasta hasta ver tu mano a través. Corta en tagliatelle, fettuccine o lasaña.', 60, 1, 'lesson-italian-cooking'),
('mod-italian-2', 'Salsas Madre Italianas', 'Aprende las bases de la cocina italiana', 'Pomodoro: Sofríe ajo en aceite de oliva, añade tomates San Marzano triturados, albahaca fresca, sal. Cocina 20-30 min. Ragù alla Bolognese: sofrito de cebolla, zanahoria, apio, carne picada, vino tinto, tomate, leche. Cocción lenta 3+ horas. Aglio e Olio: ajo dorado en aceite abundante, peperoncino, perejil. Simple pero perfecta.', 55, 2, 'lesson-italian-cooking'),
('mod-italian-3', 'Risotto Perfecto', 'Técnica cremosa sin crema', 'Usa arroz Arborio, Carnaroli o Vialone Nano. Sofríe cebolla en mantequilla, tuesta el arroz 2 min. Añade vino blanco hasta evaporar. Agrega caldo caliente cucharón a cucharón, removiendo constantemente. Total 18-20 minutos. Mantecatura final: retira del fuego, añade mantequilla fría y parmesano. Debe quedar all''onda (fluido).', 50, 3, 'lesson-italian-cooking'),
('mod-italian-4', 'Tiramisú Clásico', 'El postre italiano más famoso', 'Separa 4 huevos. Bate yemas con 100g azúcar hasta blanquear. Incorpora 500g mascarpone suavemente. Monta claras a punto de nieve, incorpora con movimientos envolventes. Moja savoiardi en café espresso frío (con amaretto opcional). Capas: crema, bizcochos, crema. Refrigera mínimo 4 horas, mejor toda la noche. Espolvorea cacao antes de servir.', 65, 4, 'lesson-italian-cooking'),
-- Módulos de Sushi
('mod-sushi-1', 'El Arroz Perfecto', 'Base fundamental del sushi', 'Usa arroz japonés de grano corto. Lava hasta que el agua salga clara (5-6 veces). Proporción: 1 taza arroz, 1.1 tazas agua. Cocina y reposa 10 min. Sushi-zu: 3 cda vinagre de arroz, 1 cda azúcar, 1/2 cdta sal. Mezcla con el arroz caliente usando movimientos de corte, abanica para enfriar. El arroz debe brillar, no estar pegajoso.', 50, 1, 'lesson-sushi-making'),
('mod-sushi-2', 'Selección de Pescado', 'Calidad y seguridad ante todo', 'Compra pescado "sushi-grade" o congelado a -20°C por 7 días para eliminar parásitos. Atún: rojo brillante, sin olor. Salmón: color naranja intenso, grasa visible. Evita pescado opaco o con olor fuerte. Corta siempre contra la fibra en ángulo de 45°. Mantén refrigerado hasta el momento de servir. La frescura es todo en el sushi.', 45, 2, 'lesson-sushi-making'),
('mod-sushi-3', 'Técnicas de Maki', 'Enrolla como un profesional', 'Coloca el nori (lado brillante abajo) sobre la esterilla de bambú. Extiende arroz fino dejando 2cm libres arriba. Añade ingredientes en línea al centro. Enrolla: levanta la esterilla, cubre los ingredientes, presiona suavemente, continúa enrollando. Humedece el borde del nori para sellar. Corta con cuchillo mojado en 6-8 piezas.', 55, 3, 'lesson-sushi-making'),
('mod-sushi-4', 'Nigiri y Presentación', 'El arte del sushi a mano', 'Moja tus manos en agua con vinagre. Toma 20g de arroz, forma óvalo suave presionando. Coloca el pescado en tus dedos, añade wasabi mínimo, pon el arroz encima, voltea. Ajusta la forma con ambas manos. Presentación: usa platos minimalistas, deja espacio entre piezas, añade gari (jengibre) y wasabi como acompañamiento, no sobre el sushi.', 60, 4, 'lesson-sushi-making'),
-- Módulos de Panadería
('mod-bread-1', 'Entendiendo la Fermentación', 'La ciencia detrás del pan', 'La levadura consume azúcares y produce CO2 y alcohol. Levadura fresca: 42g = 14g levadura seca. Temperatura ideal: 24-27°C. Autolyse: mezcla harina y agua, reposa 30 min antes de añadir levadura y sal. Esto desarrolla el gluten. Fermentación en bloque: la masa dobla su tamaño (1-2 horas). Masa madre: cultivo de levaduras silvestres, sabor más complejo.', 55, 1, 'lesson-bread-baking'),
('mod-bread-2', 'Técnicas de Amasado', 'Desarrolla el gluten correctamente', 'Amasado tradicional: estira la masa, dobla, gira 90°, repite 10-15 min hasta superficie suave. Método stretch and fold: estira un lado, dobla al centro, repite 4 lados. Hazlo cada 30 min durante la fermentación. Prueba de ventana: estira un trozo; si forma membrana translúcida sin romperse, el gluten está desarrollado. Reposo entre técnicas.', 50, 2, 'lesson-bread-baking'),
('mod-bread-3', 'Formado y Cortes', 'Da forma a tu pan', 'Preforma: desgasifica suavemente, forma bola con tensión superficial, reposa 20 min. Forma final: para hogaza redonda (boule), dobla los bordes al centro creando tensión. Para baguette, enrolla aplicando presión uniforme. Los cortes (greñas) controlan la expansión: usa cuchilla o lame afilada, corta a 45° con decisión. Patrones decorativos opcionales.', 60, 3, 'lesson-bread-baking'),
('mod-bread-4', 'Horneado Magistral', 'Consigue la corteza perfecta', 'Precalienta el horno con piedra o bandeja pesada a 250°C mínimo 45 min. Vapor los primeros 15 min: rocía agua, usa bandeja con hielo, o tapa con olla de hierro (Dutch oven). Baja a 230°C después del vapor. El pan está listo cuando suena hueco al golpear la base (temp interna 95°C). Enfría completamente sobre rejilla antes de cortar - mínimo 1 hora!', 65, 4, 'lesson-bread-baking');

-- ============================================
-- STEP 13: SUBSCRIPTIONS
-- ============================================
INSERT INTO subscriptions (id, start_date, end_date, active, username, lesson_id) VALUES
('sub-s1-piano', '2025-01-01', '2026-01-01', true, 'student1', 'lesson-piano-beginners'),
('sub-s1-redstone', '2025-01-15', '2026-01-15', true, 'student1', 'lesson-redstone-mastery'),
('sub-s2-chess', '2025-01-10', '2026-01-10', true, 'student2', 'lesson-chess-strategy'),
('sub-s2-italian', '2025-02-15', '2026-02-15', true, 'student2', 'lesson-italian-cooking'),
('sub-s3-sushi', '2025-02-01', '2026-02-01', true, 'student3', 'lesson-sushi-making'),
('sub-s3-bread', '2025-03-01', '2026-03-01', true, 'student3', 'lesson-bread-baking');
