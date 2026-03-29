# 🥒 Proyecto de Automatización: Cucumber + TestNG + Selenium

Este es un framework de automatización de pruebas BDD (Behavior-Driven Development) de alto nivel, diseñado para pruebas de interfaz de usuario (UI) robustas, escalables y con reportes interactivos premium.

## 🌟 Características Principales

- **[Cucumber 7](https://cucumber.io/)**: Definición de pruebas en lenguaje natural (Gherkin).
- **[TestNG](https://testng.org/)**: Motor de ejecución potente con soporte para paralelismo nativo.
- **[Selenium 4](https://www.selenium.dev/)**: Automatización de interacciones web modernas.
- **[Allure Report](https://docs.qameta.io/allure/)**: Reportes visuales con trazabilidad total de pasos.
- **📹 Grabación de Video Nativa (MP4)**: Captura de pantalla automática en formato MP4 incrustada directamente en el reporte Allure para cada escenario.
- **🚀 Ejecución Paralela**: Configuración preparada para correr pruebas simultáneas en múltiples navegadores (Chrome, Firefox, Edge).
- **🛠️ Arquitectura Limpia**: Uso de `PageFactory` y una clase base robusta (`Methods`) para minimizar la duplicación de código.
- **🎨 Consola Enriquecida**: Sistema de logs con colores (`PrintOutText`) para facilitar el debugging en tiempo real.

## ⚙️ Configuración y Parámetros

El control de las pruebas se centraliza en el archivo `testng.xml`.

### Parámetros Disponibles:
| Parámetro | Función | Valores |
| :--- | :--- | :--- |
| `video` | Activa/Desactiva la grabación de video MP4 | `true` / `false` |
| `browser` | Define el navegador de ejecución | `chrome`, `firefox`, `edge` |
| `headless` | Ejecución en segundo plano (sin ventana) | `true` / `false` |

## 🚀 Requisitos Previos

- **Java JDK 11** o superior.
- **Maven 3.6** o superior.
- Navegadores instalados (WebDriverManager se encarga de los binarios automáticamente).

## ▶️ Guía de Ejecución

### 1. Ejecutar las Pruebas
Para limpiar el proyecto y lanzar la suite definida:
```bash
mvn clean test
```

### 2. Generar y Ver el Reporte Allure
Una vez finalizada la ejecución, genera el reporte interactivo:
```bash
mvn allure:serve
```

## 📂 Estructura del Framework

- **`src/test/java/com/example/config`**: Núcleo del framework (Driver, Hooks, Utilitarios).
- **`src/test/java/com/example/web`**: Page Objects representativos del DOM.
- **`src/test/java/com/example/steps`**: Definición de los pasos (Step Definitions).
- **`src/test/resources/features`**: Archivos `.feature` con la lógica de negocio.
- **`src/test/resources/enviroment`**: Configuración por entorno (QA, Dev, etc.).

## 💡 Notas de Implementación

> [!TIP]
> **Grabación de Video**: La grabación se inicia automáticamente **después** de que el sitio web ha cargado la URL inicial, asegurando que el video se centre en las acciones de la prueba y no en los tiempos de carga de red.
> 
> **Capturas de Pantalla**: En caso de fallo, el sistema realiza una captura de pantalla automática y la adjunta al reporte antes de cerrar la sesión del navegador.

> [!IMPORTANT]
> **Soporte MP4**: Los videos generados utilizan el códec H.264 (vía JCodec), lo que permite su reproducción nativa en cualquier navegador moderno desde el reporte Allure sin necesidad de reproductores externos.