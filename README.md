# Weight Tracker Calendar

Aplicación web para registrar el peso día a día en un calendario anual y revisar la variación semanal comparando domingo contra domingo.

## Qué hace el proyecto

- Registra un peso por fecha.
- Visualiza el año completo en formato calendario.
- Calcula la variación semanal de domingo a domingo.
- Permite corregir registros previos.
- Gestiona un peso objetivo y muestra tendencia mensual.

## Stack

- Backend: Java 21, Spring Boot, Oracle XE
- Frontend: Vue 3, Vite, TypeScript
- Base de datos: Oracle

## Estructura

- `backend/`: API Spring Boot modular
- `frontend/`: aplicación Vue
- `scripts/`: arranque y parada para desarrollo en Windows
- [`docs/architecture.md`](docs/architecture.md): arquitectura funcional y técnica
- [`docs/oracle-xe-setup.sql`](docs/oracle-xe-setup.sql): script de creación del esquema en Oracle XE
- [`PROJECT_CONTEXT.md`](PROJECT_CONTEXT.md): contexto persistente del proyecto

## Estado actual

- Backend funcionando sobre Oracle XE
- Migraciones Flyway funcionando
- Frontend conectado al backend
- Vista anual del calendario operativa
- Registro y edición de peso diario operativos
- Resumen semanal de domingo a domingo operativo
- Objetivo de peso y tendencia mensual operativos

## Puesta en marcha local

### 1. Preparar Oracle XE

El proyecto está configurado para Oracle XE con estos valores por defecto:

- host: `localhost`
- puerto: `1521`
- servicio: `XEPDB1`
- usuario: `weight_calendar`
- contraseña: `weight_calendar`

Puedes crear el usuario ejecutando [`docs/oracle-xe-setup.sql`](docs/oracle-xe-setup.sql) desde una sesión con privilegios de administración, por ejemplo `SYSTEM`.

Ejemplo:

```sql
sqlplus system/TU_PASSWORD@localhost:1521/XEPDB1
@docs/oracle-xe-setup.sql
```

### 2. Arrancar el backend

La forma recomendada en Windows es usar el script del proyecto:

```powershell
.\scripts\start-backend.ps1
```

Si necesitas detener un backend viejo que siga escuchando en `8080`:

```powershell
.\scripts\stop-backend.ps1
```

También puedes arrancarlo manualmente desde `backend/`:

```powershell
mvn spring-boot:run
```

La API queda disponible en:

```text
http://localhost:8080
```

### 3. Arrancar el frontend

La forma recomendada en Windows es:

```powershell
.\scripts\start-frontend.ps1
```

También puedes arrancarlo manualmente desde `frontend/`:

```powershell
npm install
npm run dev
```

La aplicación queda disponible en:

```text
http://localhost:5173
```

## Configuración por entornos

El proyecto ya está preparado para separar configuración de `local` y `producción`.

### Backend

Archivos principales:

- [`backend/src/main/resources/application.yml`](backend/src/main/resources/application.yml)
- [`backend/src/main/resources/application-local.yml`](backend/src/main/resources/application-local.yml)
- [`backend/src/main/resources/application-prod.yml`](backend/src/main/resources/application-prod.yml)

Qué hace cada uno:

- `application.yml`: configuración común
- `application-local.yml`: valores por defecto para desarrollo local
- `application-prod.yml`: valores pensados para producción

El backend usa estas variables:

- `DB_URL`
- `DB_USERNAME`
- `DB_PASSWORD`
- `APP_CORS_ALLOWED_ORIGINS`
- `SERVER_PORT`

#### Perfil local

El perfil `local` es el perfil por defecto. No necesitas pasar nada extra si trabajas en tu máquina.

#### Perfil de producción

Para usar producción, arranca Spring Boot indicando el perfil y las variables necesarias.

Ejemplo:

```powershell
$env:SPRING_PROFILES_ACTIVE="prod"
$env:DB_URL="jdbc:oracle:thin:@mi-host:1521/XEPDB1"
$env:DB_USERNAME="weight_calendar"
$env:DB_PASSWORD="mi_password_segura"
$env:APP_CORS_ALLOWED_ORIGINS="https://weight-tracker-calendar.example"
mvn spring-boot:run
```

### Frontend

El frontend usa `VITE_API_BASE_URL`.

Archivo de ejemplo:

- [`frontend/.env.example`](frontend/.env.example)

Para local, puedes crear un archivo `.env.local` dentro de `frontend/` con algo así:

```env
VITE_API_BASE_URL=http://localhost:8080/api/v1
```

Para producción, puedes usar `.env.production`:

```env
VITE_API_BASE_URL=https://api.weight-tracker-calendar.example/api/v1
```

Después:

```powershell
npm run dev
```

o para build de producción:

```powershell
npm run build
```

## Flujo funcional implementado

- Alta de peso diario
- Edición de peso ya registrado
- Consulta del calendario anual completo
- Cálculo de variación semanal de domingo a domingo
- Objetivo de peso
- Tendencia mensual por medias
- Panel lateral para registrar o corregir el peso del día seleccionado

## Tests

### Backend

Desde `backend/`:

```powershell
mvn test
```

La batería actual cubre:

- calendario anual completo
- resumen semanal
- resumen mensual
- objetivo de peso y cálculo de progreso

### Frontend

Desde `frontend/`:

```powershell
npm test
```

Los tests de frontend están centrados en lógica sencilla y fácil de leer, por ejemplo:

- cómo se decide si un mes va en subida, bajada o estable
- cómo se construye el texto explicativo de la gráfica
- cómo se calcula la altura mínima visible de las barras

## Notas de arquitectura

- El backend sigue una estructura modular por dominio.
- El frontend consume la API mediante una URL configurable por entorno.
- Flyway versiona el esquema Oracle.
- El usuario actual está simulado con un usuario demo temporal (`id = 1`).

## Continuidad del proyecto

Este repositorio es la base permanente del proyecto `Weight Tracker Calendar`.
Las decisiones funcionales, técnicas y de roadmap se mantienen en [`PROJECT_CONTEXT.md`](PROJECT_CONTEXT.md).
