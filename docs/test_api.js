/* =====================================================================
   test_api.js - Pruebas de la API REST de la Veterinaria (Node 18+)
   Uso:  node docs/test_api.js            (con el backend en http://localhost:8095)
   ===================================================================== */
const BASE = process.env.API_BASE || 'http://localhost:8095';
let ok = 0, fallos = 0;

async function llamar(metodo, ruta, cuerpo) {
  const r = await fetch(BASE + ruta, {
    method: metodo,
    headers: { 'Content-Type': 'application/json' },
    body: cuerpo ? JSON.stringify(cuerpo) : undefined
  });
  const t = await r.text();
  let d = t; try { d = JSON.parse(t); } catch (e) { /* texto plano */ }
  return { status: r.status, datos: d };
}
function verificar(nombre, condicion, detalle = '') {
  if (condicion) { ok++; console.log('  OK   ' + nombre); }
  else { fallos++; console.log('  FALLO ' + nombre + ' ' + detalle); }
}

(async () => {
  const sufijo = Date.now() % 100000;
  console.log('== Autenticación');
  let r = await llamar('POST', '/api/auth/login', { nombreUsuario: 'admin', password: 'admin123' });
  verificar('login correcto devuelve 200 y rol', r.status === 200 && r.datos.idRol.nombre === 'ADMINISTRADOR');
  verificar('la contraseña no se devuelve', r.datos.password === undefined);
  r = await llamar('POST', '/api/auth/login', { nombreUsuario: 'admin', password: 'mala' });
  verificar('login incorrecto devuelve 401', r.status === 401);

  console.log('== Clientes (CRUD y validaciones)');
  const cliente = { idTipoDocumento: { id: 1 }, numeroDocumento: 'T' + sufijo, nombre: 'Cliente Prueba', direccion: 'Calle 1', email: `prueba${sufijo}@mail.com`, telefono: '3000000000' };
  r = await llamar('POST', '/api/clientes/', cliente);
  verificar('crear cliente -> 201', r.status === 201, JSON.stringify(r.datos));
  const idCliente = r.datos.id;
  r = await llamar('POST', '/api/clientes/', cliente);
  verificar('documento/email duplicado -> 400', r.status === 400);
  r = await llamar('POST', '/api/clientes/', { ...cliente, nombre: '', email: 'malo' });
  verificar('campos inválidos -> 400 con erroresValidacion', r.status === 400 && r.datos.erroresValidacion && r.datos.erroresValidacion.nombre);
  r = await llamar('PUT', '/api/clientes/' + idCliente, { ...cliente, nombre: 'Cliente Editado' });
  verificar('actualizar cliente -> 200', r.status === 200 && r.datos.nombre === 'Cliente Editado');
  r = await llamar('GET', '/api/clientes/list/99999');
  verificar('cliente inexistente -> 404', r.status === 404);

  console.log('== Mascotas');
  const mascota = { idCliente: { id: idCliente }, idRaza: { id: 2 }, nombre: 'Toby', sexo: 'm', fechaNacimiento: '2022-05-01', pesoKg: 10.5, activo: true };
  r = await llamar('POST', '/api/mascotas/', mascota);
  verificar('crear mascota -> 201 y sexo en mayúscula', r.status === 201 && r.datos.sexo === 'M', JSON.stringify(r.datos));
  const idMascota = r.datos.id;
  r = await llamar('POST', '/api/mascotas/', { ...mascota, sexo: 'X' });
  verificar('sexo inválido -> 400', r.status === 400);
  r = await llamar('GET', '/api/mascotas/cliente/' + idCliente);
  verificar('mascotas por cliente', r.status === 200 && r.datos.length === 1);

  console.log('== Citas');
  const hora = `2027-0${(sufijo % 9) + 1}-15T10:${String(sufijo % 60).padStart(2, '0')}:00`;
  const cita = { idMascota: { id: idMascota }, idVeterinario: { id: 2 }, idServicio: { id: 1 }, idEstadoCita: { id: 1 }, fechaHora: hora, motivo: 'Control' };
  r = await llamar('POST', '/api/citas/', cita);
  verificar('agendar cita -> 201', r.status === 201, JSON.stringify(r.datos));
  const idCita = r.datos.id;
  r = await llamar('POST', '/api/citas/', cita);
  verificar('veterinario con otra cita a la misma hora -> 400', r.status === 400);
  r = await llamar('POST', '/api/citas/', { ...cita, idVeterinario: { id: 3 }, fechaHora: hora.replace('T10', 'T11') });
  verificar('usuario sin rol veterinario -> 400', r.status === 400);
  r = await llamar('GET', `/api/citas/agenda?veterinario=2&fecha=${hora.slice(0, 10)}`);
  verificar('agenda del veterinario', r.status === 200 && r.datos.some(c => c.id === idCita));

  console.log('== Historia clínica y vacunación');
  r = await llamar('POST', '/api/historias/', { idMascota: { id: idMascota }, idCita: { id: idCita }, idVeterinario: { id: 2 }, diagnostico: 'Sano', tratamiento: 'Ninguno' });
  verificar('registrar historia -> 201 con fecha automática', r.status === 201 && r.datos.fecha, JSON.stringify(r.datos));
  r = await llamar('POST', '/api/vacunaciones/', { idMascota: { id: idMascota }, idVacuna: { id: 3 }, idVeterinario: { id: 2 }, fechaAplicacion: '2026-10-31' });
  verificar('vacuna de otra especie -> 400', r.status === 400);
  r = await llamar('POST', '/api/vacunaciones/', { idMascota: { id: idMascota }, idVacuna: { id: 1 }, idVeterinario: { id: 2 }, fechaAplicacion: '2026-10-31', proximaDosis: '2027-10-31' });
  verificar('registrar vacunación -> 201', r.status === 201, JSON.stringify(r.datos));

  console.log('== Integridad al eliminar');
  r = await llamar('DELETE', '/api/clientes/' + idCliente);
  verificar('cliente con mascotas no se puede eliminar -> 400', r.status === 400);

  console.log(`\nResultado: ${ok} correctas, ${fallos} fallidas`);
  process.exit(fallos ? 1 : 0);
})().catch(e => { console.error('No se pudo ejecutar:', e.message); process.exit(1); });
