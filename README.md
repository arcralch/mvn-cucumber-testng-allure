# 🥒 Proyecto de Automatización: Cucumber + TestNG + Selenium

Este es un framework de automatización de pruebas BDD (Behavior-Driven Development) de alto nivel, diseñado para pruebas de interfaz de usuario (UI) robustas, escalables y con reportes interactivos premium.

## 🌟 Características Principales

- **[Cucumber 7](https://cucumber.io/)**: Definición de pruebas en lenguaje natural (Gherkin).
- **[TestNG](https://testng.org/)**: Motor de ejecución potente con soporte para paralelismo nativo.
- **[Selenium 4](https://www.selenium.dev/)**: Automatización de interacciones web modernas.
- **[Allure Report](https://docs.qameta.io/allure/)**: Reportes visuales con trazabilidad total de pasos.
- **[SonarCloud](https://sonarcloud.io/)**: Análisis estático de código integrado para asegurar la calidad y seguridad.
- **⚙️ Entorno Allure Automático**: Inyección de propiedades de entorno (`environment.properties`) directamente en el reporte Allure.
- **📹 Grabación de Video Nativa (MP4)**: Captura automática en formato MP4 incrustada directamente en el reporte Allure para cada escenario.
  - **Modo Visible**: Usa `Robot` de Java para capturar la pantalla física.
  - **Modo Headless**: Usa `TakesScreenshot` del WebDriver para capturar frames del navegador directamente.
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

- **Java JDK 17** o superior.
- **Maven 3.6** o superior.
- Navegadores instalados (WebDriverManager se encarga de los binarios automáticamente).

### Para Docker
- **Docker** instalado en el sistema.
- Imagen `maven:3.9-eclipse-temurin-17` con Chrome y Firefox incluidos.

## ▶️ Guía de Ejecución

### 1. Ejecutar las Pruebas (Local)
Para limpiar el proyecto y lanzar la suite definida:
```bash
mvn clean test
```

### 2. Generar y Ver el Reporte Allure
Una vez finalizada la ejecución, genera el reporte interactivo:
```bash
mvn verify allure:report
```
O abre el reporte directamente:
```bash
mvn allure:serve
```

### 3. Análisis de Código con SonarCloud
Para ejecutar el análisis de código estático y enviarlo a SonarCloud, asegúrate de tener configurada la variable de entorno `SONAR_TOKEN` y ejecuta:
```bash
mvn verify sonar:sonar
```

### 4. Ejecutar en Docker
Construir la imagen Docker:
```bash
docker build -t selenium-tests .
```

Ejecutar las pruebas:
```bash
docker run --rm selenium-tests
```

## 📂 Estructura del Framework

- **`src/test/java/com/example/config`**: Núcleo del framework (Driver, Hooks, Utilitarios).
  - **`config/utils/VideoRecorder.java`**: Utilidad para grabación de video en modo visible y headless.
  - **`config/hooks/Hooks.java`**: Hooks de Cucumber para gestionar el ciclo de vida del WebDriver.
  - **`config/driver/Driver.java`**: Gestión del WebDriver y configuración del entorno de pruebas.
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
> 
> **Modo Headless**: La grabación en modo headless captura frames directamente del navegador usando `TakesScreenshot`, lo que permite grabar pruebas en entornos sin interfaz gráfica.

> [!WARNING]
> **Limitación de Dimensiones**: Para la grabación de video, las imágenes deben tener dimensiones pares (múltiplos de 2) debido a las restricciones del códec H.264 (YUV420J). El sistema automáticamente redimensiona las imágenes si es necesario.
## 🐳 GitHub Actions (CI/CD)

El proyecto incluye un workflow de GitHub Actions (`maven-docker.yml`) que:

1. **Clona el repositorio** desde GitHub
2. **Verifica Docker** instalado en el runner
3. **Construye la imagen Docker** con Java 17, Maven, Chrome y Firefox
4. **Ejecuta las pruebas** dentro del contenedor con `HEADLESS=true`
5. **Copia los resultados** Allure del contenedor al host
6. **Genera el reporte Allure** usando `tobix/allure-cli:latest`
7. **Sube los artifacts** (`allure-results` y `allure-report`) para su descarga

### Configuración del Workflow
- Se ejecuta en cada `push` o `pull_request` a la rama `main`
- Usa `ubuntu-latest` como runner con Docker instalado
- Genera reporte Allure usando `tobix/allure-cli:latest`
- Sube artifacts `allure-results` y `allure-report` para su descarga

### Verificar el Workflow
Para verificar que el workflow funciona correctamente:
1. Haz un `push` a la rama `main`
2. Ve a la pestaña "Actions" en GitHub
3. Revisa los logs de ejecución del workflow
4. Descarga los artifacts `allure-report` para visualizar el reporte HTML

### Visualizar los Resultados
1. Descarga el artifact `allure-report`
2. Abre el archivo `index.html` en tu navegador
3. Verás el reporte interactivo con todos los escenarios, pasos y videos
