/* =====================================================================
   crud.js  -  Motor genérico de CRUD
   Cada módulo (clientes, mascotas, citas...) se describe con un objeto de
   configuración en modulos.js; este motor pinta la tabla, el formulario
   modal, el buscador y llama al backend.  ("JavaScript genérico")
   ===================================================================== */

const sinTildes = s => String(s ?? '').normalize('NFD').replace(/[\u0300-\u036f]/g, '').toLowerCase();

/** Pinta un texto como etiqueta de color (para estados, activo/inactivo...). */
function insignia(texto, clase = 'gris') {
  return `<span class="insignia insignia--${clase}">${esc(texto)}</span>`;
}

/** Valor por defecto con el que se muestra una celda. */
function valorPorDefecto(v) {
  if (v === null || v === undefined || v === '') return '—';
  if (typeof v === 'boolean') return v ? 'Sí' : 'No';
  if (typeof v === 'object') return v.nombre ?? v.tipo ?? v.id ?? '—';
  return String(v);
}

class ModuloCrud {
  constructor(config, contenedor) {
    this.cfg = config;
    this.cont = contenedor;
    this.datos = [];
    this.filtro = '';
    this.editando = null;   // registro que se edita (null = nuevo)
  }

  /* ---------- Ciclo de vida ---------- */
  async iniciar() {
    const c = this.cfg;
    const puedeCrear = !c.soloLectura;
    this.cont.innerHTML = `
      <div class="herramientas">
        <input type="search" class="buscador" placeholder="Buscar en ${esc(c.titulo.toLowerCase())}..." aria-label="Buscar">
        <span class="contador"></span>
        ${puedeCrear ? `<button class="btn btn--primario" data-accion="nuevo">＋ Nuevo</button>` : ''}
      </div>
      ${c.descripcion ? `<p class="descripcion">${esc(c.descripcion)}</p>` : ''}
      <div class="aviso aviso--error" hidden></div>
      <div class="tabla-cont"></div>`;

    this.cont.querySelector('.buscador').addEventListener('input', e => {
      this.filtro = sinTildes(e.target.value);
      this.pintarTabla();
    });
    this.cont.addEventListener('click', e => this.alHacerClic(e));
    await this.cargar();
  }

  async cargar() {
    const aviso = this.cont.querySelector('.aviso');
    try {
      this.datos = await api(this.cfg.endpoint + '/list');
      if (this.cfg.ordenar) this.datos.sort(this.cfg.ordenar);
      aviso.hidden = true;
    } catch (e) {
      this.datos = [];
      aviso.textContent = e.message;
      aviso.hidden = false;
    }
    this.pintarTabla();
  }

  /* ---------- Tabla ---------- */
  pintarTabla() {
    const c = this.cfg;
    const filas = this.datos.map(fila => {
      const celdas = c.columnas.map(col => {
        const bruto = col.campo ? fila[col.campo] : undefined;
        const resultado = col.formato ? col.formato(bruto, fila) : valorPorDefecto(bruto);
        return { html: col.html ? String(resultado) : esc(resultado), texto: col.html ? String(resultado).replace(/<[^>]*>/g, '') : String(resultado) };
      });
      return { fila, celdas };
    }).filter(f => !this.filtro || sinTildes(f.celdas.map(x => x.texto).join(' ')).includes(this.filtro));

    this.cont.querySelector('.contador').textContent = `${filas.length} de ${this.datos.length} registros`;

    const puedeEditar = !c.soloLectura;
    const puedeEliminar = !c.soloLectura && !c.sinEliminar;
    const cabecera = c.columnas.map(col => `<th>${esc(col.titulo)}</th>`).join('')
      + (puedeEditar || puedeEliminar ? '<th class="col-acciones">Acciones</th>' : '');

    const cuerpo = filas.length
      ? filas.map(({ fila, celdas }) => `
          <tr>
            ${celdas.map((x, i) => `<td data-etiqueta="${esc(c.columnas[i].titulo)}">${x.html}</td>`).join('')}
            ${puedeEditar || puedeEliminar ? `
              <td class="col-acciones">
                ${puedeEditar ? `<button class="btn btn--mini" data-accion="editar" data-id="${fila.id}">Editar</button>` : ''}
                ${puedeEliminar ? `<button class="btn btn--mini btn--peligro" data-accion="eliminar" data-id="${fila.id}">Eliminar</button>` : ''}
              </td>` : ''}
          </tr>`).join('')
      : `<tr><td class="vacio" colspan="${c.columnas.length + 1}">No hay registros para mostrar</td></tr>`;

    this.cont.querySelector('.tabla-cont').innerHTML = `
      <table class="tabla">
        <thead><tr>${cabecera}</tr></thead>
        <tbody>${cuerpo}</tbody>
      </table>`;
  }

  alHacerClic(e) {
    const boton = e.target.closest('[data-accion]');
    if (!boton || !this.cont.contains(boton)) return;
    const accion = boton.dataset.accion;
    const registro = this.datos.find(d => String(d.id) === boton.dataset.id);
    if (accion === 'nuevo') this.abrirFormulario(null);
    if (accion === 'editar' && registro) this.abrirFormulario(registro);
    if (accion === 'eliminar' && registro) this.eliminar(registro);
  }

  /* ---------- Formulario modal ---------- */
  async abrirFormulario(registro) {
    this.editando = registro;
    const c = this.cfg;
    const modal = document.getElementById('modal');
    const fem = !!c.femenino;
    document.getElementById('modalTitulo').textContent = registro ? `Editar ${c.singular.toLowerCase()}` : `${fem ? 'Nueva' : 'Nuevo'} ${c.singular.toLowerCase()}`;
    document.getElementById('modalError').hidden = true;

    // Carga las listas de los campos tipo "select" (una vez por apertura)
    const listas = {};
    await Promise.all(c.campos.filter(f => f.tipo === 'select').map(async f => {
      try { listas[f.campo] = await api(f.origen); }
      catch (e) { listas[f.campo] = []; }
    }));

    document.getElementById('modalCampos').innerHTML = c.campos.map(f => this.htmlCampo(f, registro, listas[f.campo])).join('');

    const form = document.getElementById('formModal');
    form.onsubmit = ev => { ev.preventDefault(); this.guardar(); };
    document.getElementById('modalCancelar').onclick = () => modal.close();
    document.getElementById('modalCerrar').onclick = () => modal.close();
    if (!modal.open) modal.showModal();
    const primero = modal.querySelector('input:not([type=checkbox]), select, textarea');
    if (primero) primero.focus();
  }

  htmlCampo(f, registro, lista) {
    const id = 'f_' + f.campo;
    const actual = registro ? registro[f.campo] : (f.valorInicial !== undefined ? f.valorInicial : null);
    const req = f.requerido && !(registro && f.tipo === 'password') ? 'required' : '';
    const etiqueta = `<label for="${id}">${esc(f.etiqueta)}${f.requerido ? ' <span class="obl">*</span>' : ''}</label>`;
    const ayuda = f.ayuda ? `<small class="ayuda">${esc(f.ayuda)}</small>` : '';
    let control = '';

    switch (f.tipo) {
      case 'select': {
        const idSel = actual && typeof actual === 'object' ? actual.id : actual;
        const vacio = f.requerido ? '— Seleccione —' : '— Ninguno —';
        control = `<select id="${id}" ${req}><option value="">${vacio}</option>` +
          (lista || []).map(o => `<option value="${o.id}" ${String(o.id) === String(idSel) ? 'selected' : ''}>${esc(f.texto(o))}</option>`).join('') +
          `</select>`;
        break;
      }
      case 'opciones':
        control = `<select id="${id}" ${req}><option value="">— Seleccione —</option>` +
          f.opciones.map(o => `<option value="${esc(o.valor)}" ${String(o.valor) === String(actual) ? 'selected' : ''}>${esc(o.texto)}</option>`).join('') +
          `</select>`;
        break;
      case 'checkbox':
        return `<div class="campo campo--check" data-campo="${f.campo}">
                  <label class="check"><input id="${id}" type="checkbox" ${actual ? 'checked' : ''}> ${esc(f.etiqueta)}</label>
                  <span class="campo__error"></span>
                </div>`;
      case 'textarea':
        control = `<textarea id="${id}" rows="3" ${req} maxlength="${f.max || 500}">${esc(actual ?? '')}</textarea>`;
        break;
      case 'datetime-local':
        control = `<input id="${id}" type="datetime-local" ${req} value="${esc(actual ? String(actual).slice(0, 16) : '')}">`;
        break;
      case 'date':
        control = `<input id="${id}" type="date" ${req} value="${esc(actual ? String(actual).slice(0, 10) : '')}">`;
        break;
      case 'number':
        control = `<input id="${id}" type="number" ${req} step="${f.paso || 1}" ${f.min !== undefined ? `min="${f.min}"` : ''} value="${esc(actual ?? '')}">`;
        break;
      case 'password':
        control = `<input id="${id}" type="password" ${req} autocomplete="new-password" value="">`;
        break;
      default: // text, email
        control = `<input id="${id}" type="${f.tipo || 'text'}" ${req} maxlength="${f.max || 200}" value="${esc(actual ?? '')}">`;
    }
    return `<div class="campo" data-campo="${f.campo}">${etiqueta}${control}${ayuda}<span class="campo__error"></span></div>`;
  }

  leerValor(f) {
    const el = document.getElementById('f_' + f.campo);
    switch (f.tipo) {
      case 'checkbox': return el.checked;
      case 'select': return el.value === '' ? null : { id: Number(el.value) };
      case 'number': return el.value === '' ? null : Number(el.value);
      default: {
        const v = el.value.trim();
        return v === '' ? null : v;
      }
    }
  }

  /* ---------- Guardar / eliminar ---------- */
  async guardar() {
    const c = this.cfg;
    const cuerpo = {};
    c.campos.forEach(f => { cuerpo[f.campo] = this.leerValor(f); });

    document.querySelectorAll('#modalCampos .campo__error').forEach(s => { s.textContent = ''; });
    const errorGeneral = document.getElementById('modalError');
    errorGeneral.hidden = true;

    const boton = document.getElementById('modalGuardar');
    boton.disabled = true;
    try {
      if (this.editando) {
        await api(`${c.endpoint}/${this.editando.id}`, { method: 'PUT', body: cuerpo });
        toast(`${c.singular} ${c.femenino ? 'actualizada' : 'actualizado'} correctamente`);
      } else {
        await api(c.endpoint + '/', { method: 'POST', body: cuerpo });
        toast(`${c.singular} ${c.femenino ? 'registrada' : 'registrado'} correctamente`);
      }
      document.getElementById('modal').close();
      await this.cargar();
    } catch (e) {
      const errores = e.detalle && e.detalle.erroresValidacion;
      if (errores) {
        Object.entries(errores).forEach(([campo, mensaje]) => {
          const span = document.querySelector(`#modalCampos [data-campo="${campo}"] .campo__error`);
          if (span) span.textContent = mensaje;
        });
        errorGeneral.textContent = 'Revise los campos marcados.';
      } else {
        errorGeneral.textContent = e.message;
      }
      errorGeneral.hidden = false;
    } finally {
      boton.disabled = false;
    }
  }

  async eliminar(registro) {
    const c = this.cfg;
    const nombre = c.nombreRegistro ? c.nombreRegistro(registro) : `#${registro.id}`;
    if (!confirm(`¿Eliminar ${c.femenino ? 'la' : 'el'} ${c.singular.toLowerCase()} "${nombre}"?\nEsta acción no se puede deshacer.`)) return;
    try {
      await api(`${c.endpoint}/${registro.id}`, { method: 'DELETE' });
      toast(`${c.singular} ${c.femenino ? 'eliminada' : 'eliminado'}`);
      await this.cargar();
    } catch (e) {
      toast(e.message, 'error');
    }
  }
}
