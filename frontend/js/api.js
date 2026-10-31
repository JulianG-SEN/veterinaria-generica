/* =====================================================================
   api.js  -  Comunicación con el backend Spring Boot y utilidades comunes
   ===================================================================== */

// Si el frontend lo sirve el propio Spring Boot (puerto 8095) se usan rutas relativas;
// si se abre desde otro origen (Live Server, file://) se apunta a localhost:8095.
const API_BASE = (location.protocol.startsWith('http') && location.port === '8095')
  ? ''
  : 'http://localhost:8095';

/**
 * Llama al backend y devuelve el JSON (o texto) de la respuesta.
 * Si la respuesta no es 2xx lanza un Error con:
 *   - message:  mensaje legible para el usuario
 *   - detalle:  objeto ErrorDetalle del backend (incluye erroresValidacion por campo)
 *   - status:   código HTTP
 */
async function api(ruta, opciones = {}) {
  const config = { headers: { 'Content-Type': 'application/json' }, ...opciones };
  if (config.body && typeof config.body !== 'string') {
    config.body = JSON.stringify(config.body);
  }

  let respuesta;
  try {
    respuesta = await fetch(API_BASE + ruta, config);
  } catch (e) {
    const err = new Error('No se pudo conectar con el servidor. Verifique que el backend esté en ejecución (puerto 8095).');
    err.status = 0;
    throw err;
  }

  const texto = await respuesta.text();
  let datos = null;
  if (texto) {
    try { datos = JSON.parse(texto); } catch (e) { datos = texto; }
  }

  if (!respuesta.ok) {
    const mensaje = (datos && datos.mensaje) || (typeof datos === 'string' && datos) || ('Error ' + respuesta.status);
    const err = new Error(mensaje);
    err.detalle = datos;
    err.status = respuesta.status;
    throw err;
  }
  return datos;
}

/* ---------- Utilidades ---------- */

/** Escapa texto para insertarlo de forma segura en HTML. */
function esc(valor) {
  return String(valor ?? '').replace(/[&<>"']/g, c => (
    { '&': '&amp;', '<': '&lt;', '>': '&gt;', '"': '&quot;', "'": '&#39;' }[c]
  ));
}

/** 2026-11-03 -> 03/11/2026  |  2026-11-03T09:00:00 -> 03/11/2026 09:00 (sin usar Date para evitar desfases de zona horaria). */
function formatoFecha(valor) {
  if (!valor) return '—';
  const m = String(valor).match(/^(\d{4})-(\d{2})-(\d{2})(?:[T ](\d{2}):(\d{2}))?/);
  if (!m) return String(valor);
  return m[4] ? `${m[3]}/${m[2]}/${m[1]} ${m[4]}:${m[5]}` : `${m[3]}/${m[2]}/${m[1]}`;
}

function formatoMoneda(valor) {
  if (valor === null || valor === undefined || valor === '') return '—';
  return new Intl.NumberFormat('es-CO', { style: 'currency', currency: 'COP', maximumFractionDigits: 0 }).format(valor);
}

function fechaHoyISO() {
  const d = new Date();
  const dos = n => String(n).padStart(2, '0');
  return `${d.getFullYear()}-${dos(d.getMonth() + 1)}-${dos(d.getDate())}`;
}

function toast(mensaje, tipo = 'ok') {
  const el = document.getElementById('toast');
  el.textContent = mensaje;
  el.className = 'toast toast--' + tipo + ' toast--visible';
  clearTimeout(toast._t);
  toast._t = setTimeout(() => { el.className = 'toast'; }, 3500);
}
