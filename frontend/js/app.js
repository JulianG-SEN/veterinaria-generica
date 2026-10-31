/* =====================================================================
   app.js  -  Inicio de sesión, menú por rol, navegación y panel principal
   Nota: la restricción por rol de esta pantalla es solo de interfaz;
   igual que en el proyecto base, la API REST no exige autenticación.
   ===================================================================== */

const CLAVE_SESION = 'vet_usuario';
let usuarioActual = null;

const $ = id => document.getElementById(id);
const rolActual = () => (usuarioActual && usuarioActual.idRol ? String(usuarioActual.idRol.nombre).toUpperCase() : '');
const modulosVisibles = () => MODULOS.filter(m => m.roles.includes(rolActual()));

/* ---------- Sesión ---------- */
function guardarSesion(usuario) {
  usuarioActual = usuario;
  try { sessionStorage.setItem(CLAVE_SESION, JSON.stringify(usuario)); } catch (e) { /* almacenamiento no disponible */ }
}

function cargarSesion() {
  try {
    const guardado = sessionStorage.getItem(CLAVE_SESION);
    return guardado ? JSON.parse(guardado) : null;
  } catch (e) {
    return null;
  }
}

function cerrarSesion() {
  usuarioActual = null;
  try { sessionStorage.removeItem(CLAVE_SESION); } catch (e) { /* ignorar */ }
  location.hash = '';
  $('app').hidden = true;
  $('login').hidden = false;
  $('formLogin').reset();
  $('loginUsuario').focus();
}

async function iniciarSesion(ev) {
  ev.preventDefault();
  const error = $('loginError');
  error.hidden = true;
  try {
    const usuario = await api('/api/auth/login', {
      method: 'POST',
      body: { nombreUsuario: $('loginUsuario').value.trim(), password: $('loginPassword').value }
    });
    guardarSesion(usuario);
    mostrarApp();
  } catch (e) {
    error.textContent = e.message;
    error.hidden = false;
  }
}

/* ---------- Aplicación ---------- */
function mostrarApp() {
  $('login').hidden = true;
  $('app').hidden = false;
  $('infoUsuario').textContent = `${usuarioActual.nombre} · ${rolActual()}`;
  construirMenu();
  navegar();
}

function construirMenu() {
  const grupos = [];
  modulosVisibles().forEach(m => {
    let g = grupos.find(x => x.nombre === m.grupo);
    if (!g) { g = { nombre: m.grupo, items: [] }; grupos.push(g); }
    g.items.push(m);
  });
  $('menuNav').innerHTML = grupos.map(g => `
    <div class="menu__grupo">${esc(g.nombre)}</div>
    ${g.items.map(m => `<a href="#${m.clave}" data-clave="${m.clave}"><span class="menu__icono">${m.icono}</span>${esc(m.titulo)}</a>`).join('')}
  `).join('');
}

function navegar() {
  if (!usuarioActual) return;
  const visibles = modulosVisibles();
  const clave = location.hash.slice(1);
  const modulo = visibles.find(m => m.clave === clave) || visibles[0];

  document.querySelectorAll('#menuNav a').forEach(a => a.classList.toggle('activo', a.dataset.clave === modulo.clave));
  $('tituloModulo').textContent = modulo.titulo;
  $('menu').classList.remove('menu--abierto');

  // Un contenedor nuevo por navegación evita acumular escuchadores de eventos
  const contenedor = document.createElement('div');
  $('contenido').replaceChildren(contenedor);

  if (modulo.especial === 'panel') {
    pintarPanel(contenedor);
    return;
  }
  const config = {
    ...modulo,
    soloLectura: !!modulo.editan && !modulo.editan.includes(rolActual()),
    sinEliminar: !!modulo.eliminan && !modulo.eliminan.includes(rolActual())
  };
  new ModuloCrud(config, contenedor).iniciar();
}

/* ---------- Panel principal ---------- */
async function pintarPanel(cont) {
  cont.innerHTML = '<p class="cargando">Cargando panel…</p>';
  try {
    const [clientes, mascotas, citas, veterinarios] = await Promise.all([
      api('/api/clientes/list'), api('/api/mascotas/list'), api('/api/citas/list'), api('/api/usuarios/veterinarios')
    ]);
    const hoy = fechaHoyISO();
    const ahora = new Date();
    const dos = n => String(n).padStart(2, '0');
    const ahoraISO = `${hoy}T${dos(ahora.getHours())}:${dos(ahora.getMinutes())}`;
    const citasHoy = citas.filter(c => String(c.fechaHora).slice(0, 10) === hoy);
    const activa = c => !['CANCELADA', 'ATENDIDA'].includes(String(c.idEstadoCita ? c.idEstadoCita.nombre : '').toUpperCase());
    const proximas = citas
      .filter(c => String(c.fechaHora) >= ahoraISO && activa(c))
      .sort((a, b) => String(a.fechaHora).localeCompare(String(b.fechaHora)))
      .slice(0, 6);

    const tarjeta = (icono, valor, texto, destino) =>
      `<a class="tarjeta" href="#${destino}"><span class="tarjeta__icono">${icono}</span><strong>${valor}</strong><span>${texto}</span></a>`;
    const permitido = clave => modulosVisibles().some(m => m.clave === clave);

    cont.innerHTML = `
      <p class="saludo">Hola, <strong>${esc(usuarioActual.nombre)}</strong>. Resumen de la clínica:</p>
      <div class="tarjetas">
        ${tarjeta('👤', clientes.length, 'Clientes', 'clientes')}
        ${tarjeta('🐶', mascotas.filter(m => m.activo).length, 'Mascotas activas', 'mascotas')}
        ${tarjeta('📅', citasHoy.length, 'Citas de hoy', 'citas')}
        ${tarjeta('🩺', veterinarios.length, 'Veterinarios activos', permitido('usuarios') ? 'usuarios' : 'citas')}
      </div>
      <h3 class="subtitulo">Próximas citas</h3>
      <div class="tabla-cont">
        <table class="tabla">
          <thead><tr><th>Fecha y hora</th><th>Mascota</th><th>Veterinario</th><th>Servicio</th><th>Estado</th></tr></thead>
          <tbody>
            ${proximas.length ? proximas.map(c => `
              <tr>
                <td data-etiqueta="Fecha y hora">${esc(formatoFecha(c.fechaHora))}</td>
                <td data-etiqueta="Mascota">${esc(c.idMascota ? c.idMascota.nombre : '—')}</td>
                <td data-etiqueta="Veterinario">${esc(c.idVeterinario ? c.idVeterinario.nombre : '—')}</td>
                <td data-etiqueta="Servicio">${esc(c.idServicio ? c.idServicio.nombre : '—')}</td>
                <td data-etiqueta="Estado">${c.idEstadoCita ? insignia(c.idEstadoCita.nombre, claseEstadoCita(c.idEstadoCita.nombre)) : '—'}</td>
              </tr>`).join('') : '<tr><td class="vacio" colspan="5">No hay citas próximas</td></tr>'}
          </tbody>
        </table>
      </div>`;
  } catch (e) {
    cont.innerHTML = `<div class="aviso aviso--error">${esc(e.message)}</div>`;
  }
}

/* ---------- Arranque ---------- */
document.addEventListener('DOMContentLoaded', () => {
  $('formLogin').addEventListener('submit', iniciarSesion);
  $('btnSalir').addEventListener('click', cerrarSesion);
  $('btnMenu').addEventListener('click', () => $('menu').classList.toggle('menu--abierto'));
  window.addEventListener('hashchange', navegar);

  const sesion = cargarSesion();
  if (sesion) {
    usuarioActual = sesion;
    mostrarApp();
  }
});
