# Veterinaria Genérica

Proyecto "Java Script Genérico" con tema **veterinaria**, 

## Estructura

```
VeterinariaGenerica/
├── backend/     Spring Boot (Maven). API REST en /api/...
│   └── src/main/java/uis/edu/entornos/veterinaria/
│       ├── modelo/        13 entidades JPA (Cliente, Mascota, Cita, ...)
│       ├── repositorio/   Spring Data JPA
│       ├── servicio/      Reglas de negocio (CrudServicioBase + un servicio por entidad)
│       ├── controlador/   API REST (CrudControlador genérico + extras por módulo)
│       ├── excepcion/     BadRequest, ResourceNotFound, Unauthorized, GlobalExceptionHandler
│       └── config/        BCrypt y CORS
├── frontend/    HTML + CSS + JavaScript (motor CRUD genérico configurado en js/modulos.js)
├── database/    VeterinariaGenerica.sql (MySQL), DiagramaER_Veterinaria.png/.dot
└── docs/        Backlog de Jira (CSV) y test_api.js
```

## Requisitos

- JDK 17 o superior
- Conexión a internet la primera vez (Maven descarga las dependencias)
- (Opcional) MySQL 8, Node 18+ para `docs/test_api.js`

## Ejecutar (H2 en memoria, sin configurar nada)

```bash
cd backend
./mvnw spring-boot:run          # Windows: mvnw.cmd spring-boot:run
```

| Qué | URL |
| --- | --- |
| **Aplicación (frontend)** | http://localhost:8095/ |
| Swagger UI (documentación de la API) | http://localhost:8095/swagger-ui.html |
| Consola H2 | http://localhost:8095/h2-console (JDBC URL: `jdbc:h2:mem:veterinariagenerica`, usuario `sa`, sin contraseña) |

El frontend se copia automáticamente a `static/` al compilar, por lo que lo sirve el propio Spring Boot.
También puede abrirse por separado (por ejemplo con Live Server de VS Code): el backend ya permite CORS y el frontend apunta a `http://localhost:8095`.

### Usuarios de prueba

| Usuario | Contraseña | Rol | Módulos que ve |
| --- | --- | --- | --- |
| `admin` | `admin123` | ADMINISTRADOR | Todos, incluidos usuarios y catálogos |
| `lprada` / `cvega` | `vet12345` | VETERINARIO | Gestión y clínica |
| `mrey` | `recep123` | RECEPCIONISTA | Clientes, mascotas, citas y servicios (solo lectura) |

> La restricción por rol es solo de interfaz. Igual que en el proyecto base, la API REST no exige token.

## Usar MySQL en lugar de H2

1. Ejecutar `database/VeterinariaGenerica.sql` en MySQL (crea la base, las tablas y los datos iniciales).
2. Ajustar usuario y contraseña en `backend/src/main/resources/application-mysql.properties`.
3. Ejecutar: `./mvnw spring-boot:run -Dspring-boot.run.profiles=mysql`

## API REST

Todos los módulos comparten el mismo patrón que `ClienteControlador` del proyecto base:

| Método | Ruta | Descripción |
| --- | --- | --- |
| GET | `/api/{recurso}/list` | Lista todos |
| GET | `/api/{recurso}/list/{id}` | Busca por id (404 si no existe) |
| POST | `/api/{recurso}/` | Crea (201; 400 si hay datos inválidos o duplicados) |
| PUT | `/api/{recurso}/{id}` | Actualiza |
| DELETE | `/api/{recurso}/{id}` | Elimina (400 si está relacionado con otros datos) |

Recursos: `clientes`, `mascotas`, `citas`, `historias`, `vacunaciones`, `servicios`, `usuarios`, `razas`, `especies`, `vacunas`, `tipodocumentos`, `roles`, `estadocitas`.

Endpoints adicionales: `POST /api/auth/login`, `GET /api/usuarios/veterinarios`, `GET /api/mascotas/cliente/{id}`, `GET /api/razas/especie/{id}`,
`GET /api/citas/agenda?veterinario={id}&fecha=AAAA-MM-DD`, `GET /api/historias/mascota/{id}`, `GET /api/vacunaciones/mascota/{id}`.

### Reglas de negocio

- Documento y correo únicos por cliente y por usuario.
- Un veterinario no puede tener dos citas a la misma fecha y hora; la cita exige un usuario con rol VETERINARIO, mascota y servicio activos.
- La historia clínica puede ligarse a una cita, que debe ser de la misma mascota; sin fecha se usa la actual.
- La vacuna debe corresponder a la especie de la mascota; la próxima dosis no puede ser anterior a la aplicación.
- Las contraseñas se guardan con BCrypt y nunca se devuelven en las respuestas.

### Pruebas de la API

Con el backend en ejecución: `node docs/test_api.js`

## Git y Jira

```bash
git init
git add .
git commit -m "VET-2: proyecto base Veterinaria Genérica"
git branch -M main
git remote add origin https://github.com/TU_USUARIO/veterinaria-generica.git
git push -u origin main
git checkout -b develop && git push -u origin develop
```

- Ramas: `main` (estable), `develop` (integración) y `feature/VET-n-descripcion` (una por tarea de Jira).
- Commits: `VET-n: verbo en infinitivo + qué se hizo`.
- Backlog de Jira: importar `docs/Jira_Backlog_Veterinaria_ES.csv` (encabezados en español) o `docs/Jira_Backlog_Veterinaria.csv`.
- Diseño de la base de datos: `database/DiagramaER_Veterinaria.png` y `database/VeterinariaGenerica.sql`.
