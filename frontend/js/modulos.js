/* =====================================================================
   modulos.js  -  Configuración de cada módulo del sistema
   Para agregar un módulo nuevo basta con añadir un objeto a MODULOS.

   Propiedades:
     clave, titulo, singular, icono, grupo   -> menú y textos
     endpoint                                -> ruta base del backend (/api/...)
     roles                                   -> roles que ven el módulo
     editan / eliminan                       -> roles que pueden editar / eliminar (por defecto: todos los que lo ven)
     columnas[]  { titulo, campo, formato(valor, fila), html }
     campos[]    { campo, etiqueta, tipo, requerido, origen, texto(o), opciones, ... }
   ===================================================================== */

const TODOS = ['ADMINISTRADOR', 'VETERINARIO', 'RECEPCIONISTA'];
const ADMIN = ['ADMINISTRADOR'];
const CLINICA = ['ADMINISTRADOR', 'VETERINARIO'];

const nombreDe = o => (o ? o.nombre : '—');
const claseEstadoCita = nombre => ({ PROGRAMADA: 'azul', CONFIRMADA: 'verde', ATENDIDA: 'gris', CANCELADA: 'rojo' }[String(nombre).toUpperCase()] || 'gris');
const insigniaActivo = (v, si = 'Activo', no = 'Inactivo') => insignia(v ? si : no, v ? 'verde' : 'rojo');
const sexoTexto = v => (v === 'M' ? 'Macho' : v === 'H' ? 'Hembra' : '—');

const tipoDocumentoCampo = {
  campo: 'idTipoDocumento', etiqueta: 'Tipo de documento', tipo: 'select', requerido: true,
  origen: '/api/tipodocumentos/list', texto: o => o.tipo
};
const mascotaCampo = {
  campo: 'idMascota', etiqueta: 'Mascota', tipo: 'select', requerido: true,
  origen: '/api/mascotas/list', texto: o => `${o.nombre} — ${o.idCliente ? o.idCliente.nombre : ''}`
};
const veterinarioCampo = {
  campo: 'idVeterinario', etiqueta: 'Veterinario', tipo: 'select', requerido: true,
  origen: '/api/usuarios/veterinarios', texto: o => o.nombre
};

const MODULOS = [
  /* ---------------- Inicio ---------------- */
  { clave: 'panel', titulo: 'Panel', icono: '🏠', grupo: 'Inicio', roles: TODOS, especial: 'panel' },

  /* ---------------- Gestión ---------------- */
  {
    clave: 'clientes', titulo: 'Clientes', singular: 'Cliente', icono: '👤', grupo: 'Gestión', roles: TODOS,
    eliminan: ADMIN,
    descripcion: 'Dueños de las mascotas.',
    endpoint: '/api/clientes',
    nombreRegistro: r => r.nombre,
    columnas: [
      { titulo: 'Documento', formato: (v, f) => `${f.idTipoDocumento ? f.idTipoDocumento.tipo : ''} ${f.numeroDocumento}` },
      { titulo: 'Nombre', campo: 'nombre' },
      { titulo: 'Correo', campo: 'email' },
      { titulo: 'Teléfono', campo: 'telefono' },
      { titulo: 'Dirección', campo: 'direccion' }
    ],
    campos: [
      tipoDocumentoCampo,
      { campo: 'numeroDocumento', etiqueta: 'Número de documento', tipo: 'text', requerido: true, max: 30 },
      { campo: 'nombre', etiqueta: 'Nombre completo', tipo: 'text', requerido: true, max: 200 },
      { campo: 'direccion', etiqueta: 'Dirección', tipo: 'text', requerido: true, max: 100 },
      { campo: 'email', etiqueta: 'Correo electrónico', tipo: 'email', requerido: true, max: 100 },
      { campo: 'telefono', etiqueta: 'Teléfono', tipo: 'text', requerido: true, max: 20 }
    ]
  },
  {
    clave: 'mascotas', titulo: 'Mascotas', singular: 'Mascota', femenino: true, icono: '🐶', grupo: 'Gestión', roles: TODOS,
    eliminan: ADMIN,
    descripcion: 'Pacientes de la veterinaria, asociados a su dueño.',
    endpoint: '/api/mascotas',
    nombreRegistro: r => r.nombre,
    columnas: [
      { titulo: 'Nombre', campo: 'nombre' },
      { titulo: 'Dueño', campo: 'idCliente', formato: v => nombreDe(v) },
      { titulo: 'Especie / Raza', campo: 'idRaza', formato: v => (v ? `${v.idEspecie ? v.idEspecie.nombre : ''} · ${v.nombre}` : '—') },
      { titulo: 'Sexo', campo: 'sexo', formato: v => sexoTexto(v) },
      { titulo: 'Nacimiento', campo: 'fechaNacimiento', formato: v => formatoFecha(v) },
      { titulo: 'Peso (kg)', campo: 'pesoKg' },
      { titulo: 'Estado', campo: 'activo', html: true, formato: v => insigniaActivo(v, 'Activa', 'Inactiva') }
    ],
    campos: [
      { campo: 'idCliente', etiqueta: 'Dueño (cliente)', tipo: 'select', requerido: true, origen: '/api/clientes/list', texto: o => `${o.nombre} (${o.numeroDocumento})` },
      { campo: 'idRaza', etiqueta: 'Raza', tipo: 'select', requerido: true, origen: '/api/razas/list', texto: o => `${o.idEspecie ? o.idEspecie.nombre : ''} · ${o.nombre}` },
      { campo: 'nombre', etiqueta: 'Nombre de la mascota', tipo: 'text', requerido: true, max: 80 },
      { campo: 'sexo', etiqueta: 'Sexo', tipo: 'opciones', requerido: true, opciones: [{ valor: 'M', texto: 'Macho' }, { valor: 'H', texto: 'Hembra' }] },
      { campo: 'fechaNacimiento', etiqueta: 'Fecha de nacimiento', tipo: 'date' },
      { campo: 'pesoKg', etiqueta: 'Peso (kg)', tipo: 'number', paso: 0.01, min: 0 },
      { campo: 'color', etiqueta: 'Color', tipo: 'text', max: 50 },
      { campo: 'activo', etiqueta: 'Mascota activa', tipo: 'checkbox', valorInicial: true }
    ]
  },
  {
    clave: 'citas', titulo: 'Citas', singular: 'Cita', femenino: true, icono: '📅', grupo: 'Gestión', roles: TODOS,
    eliminan: ADMIN,
    descripcion: 'Agenda de la clínica. Un veterinario no puede tener dos citas a la misma hora.',
    endpoint: '/api/citas',
    ordenar: (a, b) => String(b.fechaHora).localeCompare(String(a.fechaHora)),
    nombreRegistro: r => `${formatoFecha(r.fechaHora)} - ${nombreDe(r.idMascota)}`,
    columnas: [
      { titulo: 'Fecha y hora', campo: 'fechaHora', formato: v => formatoFecha(v) },
      { titulo: 'Mascota', campo: 'idMascota', formato: (v, f) => (v ? `${v.nombre} (${nombreDe(v.idCliente)})` : '—') },
      { titulo: 'Veterinario', campo: 'idVeterinario', formato: v => nombreDe(v) },
      { titulo: 'Servicio', campo: 'idServicio', formato: v => nombreDe(v) },
      { titulo: 'Estado', campo: 'idEstadoCita', html: true, formato: v => (v ? insignia(v.nombre, claseEstadoCita(v.nombre)) : '—') },
      { titulo: 'Motivo', campo: 'motivo' }
    ],
    campos: [
      mascotaCampo,
      { ...veterinarioCampo },
      { campo: 'idServicio', etiqueta: 'Servicio', tipo: 'select', requerido: true, origen: '/api/servicios/list', texto: o => `${o.nombre} (${formatoMoneda(o.precio)})` },
      { campo: 'fechaHora', etiqueta: 'Fecha y hora', tipo: 'datetime-local', requerido: true },
      { campo: 'idEstadoCita', etiqueta: 'Estado de la cita', tipo: 'select', requerido: true, origen: '/api/estadocitas/list', texto: o => o.nombre },
      { campo: 'motivo', etiqueta: 'Motivo de la consulta', tipo: 'textarea', max: 255 }
    ]
  },

  /* ---------------- Clínica ---------------- */
  {
    clave: 'historias', titulo: 'Historia clínica', singular: 'Historia clínica', femenino: true, icono: '🩺', grupo: 'Clínica', roles: CLINICA,
    eliminan: ADMIN,
    descripcion: 'Diagnósticos y tratamientos. La cita es opcional (atención de urgencia).',
    endpoint: '/api/historias',
    ordenar: (a, b) => String(b.fecha).localeCompare(String(a.fecha)),
    nombreRegistro: r => `${formatoFecha(r.fecha)} - ${nombreDe(r.idMascota)}`,
    columnas: [
      { titulo: 'Fecha', campo: 'fecha', formato: v => formatoFecha(v) },
      { titulo: 'Mascota', campo: 'idMascota', formato: v => nombreDe(v) },
      { titulo: 'Veterinario', campo: 'idVeterinario', formato: v => nombreDe(v) },
      { titulo: 'Diagnóstico', campo: 'diagnostico' },
      { titulo: 'Tratamiento', campo: 'tratamiento' }
    ],
    campos: [
      mascotaCampo,
      { campo: 'idCita', etiqueta: 'Cita asociada', tipo: 'select', origen: '/api/citas/list', ayuda: 'Opcional. Debe ser una cita de la misma mascota.',
        texto: o => `${formatoFecha(o.fechaHora)} · ${nombreDe(o.idMascota)} · ${nombreDe(o.idServicio)}` },
      { ...veterinarioCampo },
      { campo: 'fecha', etiqueta: 'Fecha y hora de la atención', tipo: 'datetime-local', ayuda: 'Si se deja vacío se usa la fecha y hora actual.' },
      { campo: 'diagnostico', etiqueta: 'Diagnóstico', tipo: 'textarea', requerido: true, max: 500 },
      { campo: 'tratamiento', etiqueta: 'Tratamiento', tipo: 'textarea', max: 500 },
      { campo: 'observaciones', etiqueta: 'Observaciones', tipo: 'textarea', max: 500 }
    ]
  },
  {
    clave: 'vacunaciones', titulo: 'Vacunación', singular: 'Vacunación', femenino: true, icono: '💉', grupo: 'Clínica', roles: CLINICA,
    eliminan: ADMIN,
    descripcion: 'Carné de vacunación. La vacuna debe corresponder a la especie de la mascota.',
    endpoint: '/api/vacunaciones',
    ordenar: (a, b) => String(b.fechaAplicacion).localeCompare(String(a.fechaAplicacion)),
    nombreRegistro: r => `${nombreDe(r.idMascota)} - ${nombreDe(r.idVacuna)}`,
    columnas: [
      { titulo: 'Mascota', campo: 'idMascota', formato: v => nombreDe(v) },
      { titulo: 'Vacuna', campo: 'idVacuna', formato: v => nombreDe(v) },
      { titulo: 'Veterinario', campo: 'idVeterinario', formato: v => nombreDe(v) },
      { titulo: 'Aplicada', campo: 'fechaAplicacion', formato: v => formatoFecha(v) },
      { titulo: 'Próxima dosis', campo: 'proximaDosis', formato: v => formatoFecha(v) }
    ],
    campos: [
      mascotaCampo,
      { campo: 'idVacuna', etiqueta: 'Vacuna', tipo: 'select', requerido: true, origen: '/api/vacunas/list', texto: o => `${o.nombre} (${nombreDe(o.idEspecie)})` },
      { ...veterinarioCampo },
      { campo: 'fechaAplicacion', etiqueta: 'Fecha de aplicación', tipo: 'date', requerido: true },
      { campo: 'proximaDosis', etiqueta: 'Próxima dosis', tipo: 'date' }
    ]
  },
  {
    clave: 'servicios', titulo: 'Servicios', singular: 'Servicio', icono: '✂️', grupo: 'Clínica', roles: TODOS,
    editan: ADMIN, eliminan: ADMIN,
    descripcion: 'Servicios que ofrece la veterinaria.',
    endpoint: '/api/servicios',
    nombreRegistro: r => r.nombre,
    columnas: [
      { titulo: 'Servicio', campo: 'nombre' },
      { titulo: 'Descripción', campo: 'descripcion' },
      { titulo: 'Precio', campo: 'precio', formato: v => formatoMoneda(v) },
      { titulo: 'Duración (min)', campo: 'duracionMin' },
      { titulo: 'Estado', campo: 'activo', html: true, formato: v => insigniaActivo(v) }
    ],
    campos: [
      { campo: 'nombre', etiqueta: 'Nombre del servicio', tipo: 'text', requerido: true, max: 100 },
      { campo: 'descripcion', etiqueta: 'Descripción', tipo: 'text', max: 255 },
      { campo: 'precio', etiqueta: 'Precio (COP)', tipo: 'number', requerido: true, paso: 1, min: 0 },
      { campo: 'duracionMin', etiqueta: 'Duración (minutos)', tipo: 'number', requerido: true, paso: 5, min: 1, valorInicial: 30 },
      { campo: 'activo', etiqueta: 'Servicio activo', tipo: 'checkbox', valorInicial: true }
    ]
  },

  /* ---------------- Administración ---------------- */
  {
    clave: 'usuarios', titulo: 'Usuarios', singular: 'Usuario', icono: '🔐', grupo: 'Administración', roles: ADMIN,
    descripcion: 'Personal que inicia sesión. Las contraseñas se guardan cifradas (BCrypt).',
    endpoint: '/api/usuarios',
    nombreRegistro: r => r.nombreUsuario,
    columnas: [
      { titulo: 'Nombre', campo: 'nombre' },
      { titulo: 'Usuario', campo: 'nombreUsuario' },
      { titulo: 'Documento', formato: (v, f) => `${f.idTipoDocumento ? f.idTipoDocumento.tipo : ''} ${f.numeroDocumento}` },
      { titulo: 'Correo', campo: 'email' },
      { titulo: 'Rol', campo: 'idRol', html: true, formato: v => (v ? insignia(v.nombre, 'azul') : '—') },
      { titulo: 'Estado', campo: 'activo', html: true, formato: v => insigniaActivo(v) }
    ],
    campos: [
      tipoDocumentoCampo,
      { campo: 'numeroDocumento', etiqueta: 'Número de documento', tipo: 'text', requerido: true, max: 30 },
      { campo: 'nombre', etiqueta: 'Nombre completo', tipo: 'text', requerido: true, max: 200 },
      { campo: 'nombreUsuario', etiqueta: 'Nombre de usuario', tipo: 'text', requerido: true, max: 50 },
      { campo: 'password', etiqueta: 'Contraseña', tipo: 'password', requerido: true, ayuda: 'Mínimo 6 caracteres. Al editar, déjela vacía para conservar la actual.' },
      { campo: 'email', etiqueta: 'Correo electrónico', tipo: 'email', requerido: true, max: 100 },
      { campo: 'idRol', etiqueta: 'Rol', tipo: 'select', requerido: true, origen: '/api/roles/list', texto: o => o.nombre },
      { campo: 'activo', etiqueta: 'Usuario activo', tipo: 'checkbox', valorInicial: true }
    ]
  },
  {
    clave: 'razas', titulo: 'Razas', singular: 'Raza', femenino: true, icono: '🐕', grupo: 'Catálogos', roles: ADMIN,
    endpoint: '/api/razas', nombreRegistro: r => r.nombre,
    columnas: [
      { titulo: 'Especie', campo: 'idEspecie', formato: v => nombreDe(v) },
      { titulo: 'Raza', campo: 'nombre' }
    ],
    campos: [
      { campo: 'idEspecie', etiqueta: 'Especie', tipo: 'select', requerido: true, origen: '/api/especies/list', texto: o => o.nombre },
      { campo: 'nombre', etiqueta: 'Nombre de la raza', tipo: 'text', requerido: true, max: 80 }
    ]
  },
  {
    clave: 'especies', titulo: 'Especies', singular: 'Especie', femenino: true, icono: '🐾', grupo: 'Catálogos', roles: ADMIN,
    endpoint: '/api/especies', nombreRegistro: r => r.nombre,
    columnas: [{ titulo: 'Especie', campo: 'nombre' }],
    campos: [{ campo: 'nombre', etiqueta: 'Nombre de la especie', tipo: 'text', requerido: true, max: 50 }]
  },
  {
    clave: 'vacunas', titulo: 'Vacunas', singular: 'Vacuna', femenino: true, icono: '🧪', grupo: 'Catálogos', roles: ADMIN,
    endpoint: '/api/vacunas', nombreRegistro: r => r.nombre,
    columnas: [
      { titulo: 'Vacuna', campo: 'nombre' },
      { titulo: 'Especie', campo: 'idEspecie', formato: v => nombreDe(v) },
      { titulo: 'Descripción', campo: 'descripcion' }
    ],
    campos: [
      { campo: 'nombre', etiqueta: 'Nombre de la vacuna', tipo: 'text', requerido: true, max: 100 },
      { campo: 'idEspecie', etiqueta: 'Especie', tipo: 'select', requerido: true, origen: '/api/especies/list', texto: o => o.nombre },
      { campo: 'descripcion', etiqueta: 'Descripción', tipo: 'text', max: 255 }
    ]
  },
  {
    clave: 'tipodocumentos', titulo: 'Tipos de documento', singular: 'Tipo de documento', icono: '🪪', grupo: 'Catálogos', roles: ADMIN,
    endpoint: '/api/tipodocumentos', nombreRegistro: r => r.tipo,
    columnas: [{ titulo: 'Tipo', campo: 'tipo' }],
    campos: [{ campo: 'tipo', etiqueta: 'Tipo de documento', tipo: 'text', requerido: true, max: 30 }]
  },
  {
    clave: 'roles', titulo: 'Roles', singular: 'Rol', icono: '🛡️', grupo: 'Catálogos', roles: ADMIN,
    endpoint: '/api/roles', nombreRegistro: r => r.nombre,
    columnas: [{ titulo: 'Rol', campo: 'nombre' }],
    campos: [{ campo: 'nombre', etiqueta: 'Nombre del rol', tipo: 'text', requerido: true, max: 30,
               ayuda: 'El rol VETERINARIO es el que permite atender citas.' }]
  },
  {
    clave: 'estadocitas', titulo: 'Estados de cita', singular: 'Estado de cita', icono: '🚦', grupo: 'Catálogos', roles: ADMIN,
    endpoint: '/api/estadocitas', nombreRegistro: r => r.nombre,
    columnas: [{ titulo: 'Estado', campo: 'nombre' }],
    campos: [{ campo: 'nombre', etiqueta: 'Nombre del estado', tipo: 'text', requerido: true, max: 30 }]
  }
];
