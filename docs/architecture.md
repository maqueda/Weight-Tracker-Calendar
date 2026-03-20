# Arquitectura de Weight Tracker Calendar

## Vision

La aplicacion permite registrar el peso diario de una persona en una vista anual y calcular la evolucion semanal comparando el valor del domingo actual contra el del domingo anterior.

La arquitectura recomendada es un monolito modular:

- un backend Spring Boot
- una base Oracle
- un frontend Vue desacoplado

## Modulos funcionales

- `auth`: autenticacion y contexto de usuario
- `weight-entries`: registro diario de peso
- `weekly-summary`: calculo de variacion semanal
- `calendar-view`: consultas optimizadas para el calendario anual
- `audit`: trazabilidad de cambios
- `shared`: componentes tecnicos compartidos

## Capas backend

### API

Responsabilidades:

- exponer endpoints REST
- validar payloads
- devolver DTOs de respuesta
- traducir errores funcionales a respuestas HTTP

Ejemplos:

- `POST /api/v1/weight-entries`
- `PUT /api/v1/weight-entries/{entryId}`
- `GET /api/v1/calendar/{year}`
- `GET /api/v1/weekly-summaries/{year}`

### Application

Responsabilidades:

- ejecutar casos de uso
- delimitar transacciones
- coordinar repositorios y servicios de dominio

Casos de uso principales:

- `CreateWeightEntryUseCase`
- `UpdateWeightEntryUseCase`
- `GetYearCalendarUseCase`
- `GetWeeklySummariesUseCase`

### Domain

Responsabilidades:

- representar las reglas de negocio puras
- proteger invariantes
- definir contratos de persistencia

Reglas clave:

- un usuario tiene como maximo un registro por fecha
- el peso debe ser positivo
- la comparacion semanal toma domingo actual menos domingo anterior
- si falta alguno de los domingos, la semana queda como incompleta

### Infrastructure

Responsabilidades:

- persistencia JPA
- integracion con Oracle
- seguridad
- observabilidad
- implementaciones concretas de repositorios

## Modelo de datos

### Tabla `weight_entry`

- `id`
- `user_id`
- `entry_date`
- `weight_kg`
- `notes`
- `created_at`
- `updated_at`

Restricciones:

- unique `user_id + entry_date`
- check `weight_kg > 0`

### Tabla `app_user`

- `id`
- `username`
- `display_name`
- `timezone`
- `created_at`

## Flujo de datos

### Registro diario de peso

1. El usuario selecciona una fecha en el calendario anual.
2. Vue abre formulario lateral o modal.
3. El formulario envia `POST` o `PUT` al backend.
4. El controller valida el DTO.
5. El caso de uso carga o crea el registro.
6. El dominio valida reglas.
7. El repositorio persiste en Oracle.
8. El backend responde con el registro actualizado.
9. Vue refresca el dia y los agregados semanales afectados.

### Resumen semanal

1. Vue solicita el calendario de un ano.
2. Backend devuelve cada dia con su peso registrado o vacio.
3. Vue solicita o recibe en el mismo payload el resumen semanal.
4. El modulo `weekly-summary` calcula:
   - peso del domingo inicial
   - peso del domingo final
   - diferencia absoluta
   - tendencia `UP`, `DOWN` o `FLAT`
5. El frontend pinta el cambio al cerrar cada semana.

## Decisiones tecnicas

### Backend

- Java 21 por estabilidad LTS
- Spring Boot por velocidad de desarrollo
- Spring Web para REST
- Spring Validation para requests
- Spring Data JPA para persistencia
- Flyway para migraciones
- MapStruct para mapping
- Spring Security para auth

### Frontend

- Vue 3 con Composition API
- Vite para build rapido
- TypeScript para contratos mas seguros
- Pinia para estado compartido
- Vue Router para navegacion
- modulo `services` para acceso a API

### Oracle

- `NUMBER(5,2)` para peso en kg
- indice por `user_id, entry_date`
- migraciones versionadas
- consultas de calendario enfocadas en lectura

## Estructura de carpetas

```text
backend/
  src/main/java/com/example/weighttrackercalendar
    modules/
      weightentry/
      weeklysummary/
      calendar/
      auth/
    shared/
    config/
frontend/
  src/
    modules/
      weight-entry/
      calendar/
      weekly-summary/
      auth/
    shared/
    app/
```

## Endpoints iniciales

### `POST /api/v1/weight-entries`

```json
{
  "entryDate": "2026-03-20",
  "weightKg": 78.40,
  "notes": "Despues de entrenar"
}
```

### `GET /api/v1/calendar/2026`

Respuesta esperada:

- lista de dias del ano
- peso registrado por dia
- metadata para pintar semanas y meses

### `GET /api/v1/weekly-summaries/2026`

Respuesta esperada:

- numero de semana
- sundayStartDate
- sundayStartWeight
- sundayEndDate
- sundayEndWeight
- deltaKg
- trend
- complete

## Estrategia de crecimiento

Fase 1:

- monolito modular
- CRUD de registros
- calendario anual
- resumen semanal

Fase 2:

- usuarios multiples
- objetivos de peso
- exportacion CSV o PDF
- graficos mensuales

Fase 3:

- recordatorios
- estadisticas avanzadas
- integracion con dispositivos o apps externas

