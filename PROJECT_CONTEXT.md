# Project Context

## Nombre

Weight Tracker Calendar

## Proposito

Aplicacion web para registrar el peso dia a dia en un calendario anual y visualizar la variacion semanal comparando domingo contra domingo.

## Objetivo de producto

- registrar un peso por fecha
- visualizar el ano completo en formato calendario
- detectar si el peso sube, baja o se mantiene semana a semana
- permitir corregir registros historicos

## Stack fijado

- Backend: Java 21 + Spring Boot
- Base de datos: Oracle
- Frontend: Vue 3 + Vite + TypeScript

## Arquitectura acordada

- monolito modular
- backend desacoplado del frontend
- persistencia JPA sobre Oracle
- frontend por modulos de dominio
- API REST versionada en `/api/v1`

## Modulos actuales

- `weightentry`
- `calendar`
- `weeklysummary`
- `shared`
- `config`

## Estado actual

- estructura base del backend creada
- estructura base del frontend creada
- alta y edicion de peso modeladas
- consulta de calendario anual implementada
- resumen semanal domingo a domingo implementado
- usuario demo temporal con `user_id = 1`

## Reglas funcionales

- un usuario puede tener un solo registro por fecha
- el peso debe ser mayor que cero
- la variacion semanal se calcula con domingo final menos domingo inicial
- si falta uno de los dos domingos, la semana es incompleta

## Decisiones vigentes

- trabajar siempre sobre este mismo repositorio salvo que se decida explicitamente lo contrario
- no crear proyectos nuevos por defecto en prompts futuros
- mantener la documentacion de decisiones dentro del repo
- usar este archivo como referencia persistente del proyecto

## Proximos hitos

1. levantar entorno local reproducible con Oracle
2. validar build de backend y frontend
3. anadir datos semilla para pruebas visuales
4. incorporar autenticacion real
5. mejorar UX del calendario anual

## Convenciones de trabajo

- cualquier cambio estructural importante debe reflejarse aqui o en `docs/architecture.md`
- el nombre canonico del proyecto es `Weight Tracker Calendar`
- las iteraciones futuras deben asumir continuidad sobre esta base
