# Weight Tracker Calendar

Aplicacion web para registrar el peso dia a dia en un calendario anual y comparar la variacion semanal de domingo a domingo.

## Stack

- Backend: Java 21, Spring Boot, Oracle
- Frontend: Vue 3, Vite, TypeScript
- Base de datos: Oracle

## Estructura

- `docs/architecture.md`: arquitectura funcional y tecnica
- `PROJECT_CONTEXT.md`: contexto persistente del proyecto
- `backend/`: API Spring Boot modular
- `frontend/`: aplicacion Vue

## Objetivo funcional

- Registrar un peso por fecha
- Visualizar el ano completo en modo calendario
- Mostrar la variacion semanal tomando como referencia domingo a domingo
- Permitir correccion de registros previos

## Puesta en marcha

1. Levantar Oracle y crear el esquema `weight_calendar`
2. Arrancar el backend para ejecutar Flyway
3. Arrancar el frontend en `http://localhost:5173`

## Flujo implementado

- Alta de peso diario
- Edicion de peso ya registrado
- Consulta del calendario anual completo
- Calculo de variacion semanal de domingo a domingo
- Panel lateral para registrar o corregir el peso del dia seleccionado

## Continuidad

Este repositorio es la base permanente del proyecto `Weight Tracker Calendar`.
Las decisiones funcionales, tecnicas y de roadmap se mantienen en `PROJECT_CONTEXT.md`.
