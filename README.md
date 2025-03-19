# Estrategia de Pruebas para la API de Rick and Morty

Este proyecto contiene pruebas automatizadas para la API de Rick and Morty (https://rickandmortyapi.com/) utilizando REST Assured y TestNG.

## Estrategia de Pruebas

La suite de pruebas cubre los siguientes aspectos de la API:

### 1. Endpoints Probados
- Personajes: `/api/character`
- Ubicaciones: `/api/location`
- Episodios: `/api/episode`

### 2. Categorías de Pruebas
Para cada endpoint, validamos integralmente:

#### Códigos de Estado
- 200 OK para solicitudes exitosas
- 404 Not Found para recursos inexistentes
- 500 Server Error para simulación de errores del servidor

#### Payload de Respuesta
- Validación de estructura JSON
- Validación de esquema
- Validación de tipos de datos
- Validación de lógica de negocio (ej: verificación de relaciones entre entidades)

#### Cabeceras de Respuesta
- Verificación del Content-Type
- Validación de Cache-Control
- Medición del tiempo de respuesta

### 3. Estructura de Pruebas
- Clase base con funcionalidad común
- Clases de prueba específicas para cada endpoint
- Clases modelo para representación de datos
- Clases utilitarias para funciones reutilizables

## Ejecución de las Pruebas

1. Clonar el repositorio
2. Ejecutar `mvn clean test` para ejecutar todas las pruebas
3. Ver el informe de TestNG en `target/surefire-reports`

## Enfoque de Pruebas
- **Pruebas positivas**: Verificación del comportamiento esperado con entradas válidas
- **Pruebas negativas**: Verificación del manejo de errores con entradas inválidas
- **Pruebas de límites**: Pruebas de valores límite (ej: primera/última página de resultados)
- **Pruebas de integración**: Verificación de relaciones entre diferentes endpoints

## Detalles de Implementación

Cada prueba es integral y evalúa múltiples aspectos del API en un solo método de prueba:
- Validación del código de estado
- Verificación detallada de la estructura y contenido del payload
- Validación de las cabeceras de respuesta
- Medición del tiempo de respuesta

Este enfoque permite un ciclo de pruebas más eficiente al reducir el número de solicitudes HTTP mientras mantiene una cobertura completa del API.

## Notas Técnicas

- Utilización de matchers de Hamcrest para aserciones expresivas
- Extracción y validación del payload mediante JsonPath
- Verificación de relaciones entre entidades (personajes, ubicaciones, episodios)
- Pruebas de rendimiento para evaluar tiempos de respuesta y comportamiento bajo carga

Este framework de pruebas está diseñado para ser mantenible y extensible, permitiendo agregar fácilmente nuevas pruebas a medida que el API evoluciona. 
