# Project Context

## Nombre

Weight Tracker Calendar

## Proposito

Aplicacion web para registrar el peso dia a dia en un calendario anual y visualizar la evolucion semanal, mensual y personal de cada usuario.

## Objetivo de producto

- registrar un peso por fecha
- visualizar el ano completo en formato calendario
- comparar el cambio semanal de domingo a domingo
- detectar tendencias de subida, bajada o estabilidad
- permitir corregir registros historicos
- ofrecer objetivos, metricas y resumenes utiles sin perder simplicidad

## Stack fijado

- Backend: Java 21 + Spring Boot
- Base de datos: Oracle XE
- Frontend: Vue 3 + Vite + TypeScript
- Estado frontend: Pinia
- Migraciones: Flyway
- Seguridad: JWT con autenticacion local

## Arquitectura acordada

- monolito modular
- backend desacoplado del frontend
- persistencia JPA sobre Oracle
- frontend por modulos de dominio
- API REST versionada en `/api/v1`
- configuracion separada por entorno en backend y frontend

## Modulos actuales backend

- `auth`
- `calendar`
- `monthlysummary`
- `weeklysummary`
- `weightentry`
- `weightgoal`
- `shared`
- `config`

## Modulos actuales frontend

- `auth`
- `calendar`
- `monthly-summary`
- `weekly-summary`
- `weight-entry`
- `weight-goal`

## Estado funcional actual

- autenticacion local con usuarios reales
- login, registro, cierre de sesion y persistencia de sesion
- edicion de perfil del usuario desde modal en la vista principal
- cambio de contrasena desde el panel de perfil
- alta y edicion de peso diario
- calendario anual completo por usuario
- resumen semanal de domingo a domingo
- explicacion de semanas incompletas
- objetivo de peso con progreso
- tendencia mensual por promedios
- metricas rapidas en dashboard
- exportacion CSV
- racha actual y mejor racha
- filtro por mes y salto rapido a hoy

## Estado tecnico actual

- Oracle XE configurado con servicio `XEPDB1`
- backend preparado con perfiles `local` y `prod`
- frontend preparado con `VITE_API_BASE_URL`
- scripts de arranque y parada para desarrollo en Windows
- bateria de tests unitarios en backend y frontend
- build de backend y frontend validada localmente

## Reglas funcionales vigentes

- un usuario puede tener un solo registro por fecha
- el peso debe ser mayor que cero
- la variacion semanal se calcula con domingo final menos domingo inicial
- si falta uno de los dos domingos, la semana es incompleta
- cada usuario solo puede ver y modificar sus propios datos
- el objetivo de peso pertenece a un usuario
- el calendario, metricas y exportaciones se calculan sobre el usuario autenticado

## Decisiones vigentes

- trabajar siempre sobre este mismo repositorio salvo que se decida explicitamente lo contrario
- no crear proyectos nuevos por defecto en prompts futuros
- mantener la documentacion de decisiones dentro del repo
- usar este archivo como referencia persistente del proyecto
- priorizar una UX clara y ligera antes que complejidad analitica excesiva
- evitar microservicios salvo necesidad real

## Prioridades pendientes recomendadas

1. grafico anual de evolucion
2. datos semilla para demos y pruebas visuales
3. mejora visual del modal de perfil y estados vacios
4. exportaciones adicionales como PDF
5. despliegue productivo completo

## Convenciones de trabajo

- cualquier cambio estructural importante debe reflejarse aqui o en `docs/architecture.md`
- el nombre canonico del proyecto es `Weight Tracker Calendar`
- las iteraciones futuras deben asumir continuidad sobre esta base
- no exponer credenciales reales o temporales en la interfaz
- no modificar migraciones historicas ya aplicadas salvo decision explicita y controlada
