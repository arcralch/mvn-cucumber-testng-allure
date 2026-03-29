package com.example.config.hooks;

import com.example.config.utils.VideoRecorder;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import io.qameta.allure.Allure;
import com.example.config.driver.Driver;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Clase que gestiona los Hooks de Cucumber (@Before y @After).
 * Controla el ciclo de vida del WebDriver y la captura de resultados para Allure.
 */
public class Hooks {

    private final Driver driver;

    /**
     * Constructor con inyección de dependencias (PicoContainer).
     */
    public Hooks(Driver driver) {
        this.driver = driver;
    }

    /**
     * Hook que se ejecuta antes de cada escenario.
     * Inicializa el navegador y configura el contexto de Allure.
     */
    @Before(order = 1)
    public void setUp(Scenario scenario) throws IOException {
        // 1. Inicializa el driver
        driver.getDriver();
        driver.setScenario(scenario);
        
        // 2. Configura parámetros en Allure
        String browser = Driver.getBrowserCurrentThread();
        if (browser != null) {
            Allure.parameter("Browser", browser);
            Allure.label("browser", browser);
        }

        // 3. Iniciar grabación de video SOLO en modo headed (NO headless)
        boolean videoEnabled = Boolean.parseBoolean(System.getProperty("VIDEO", "false"));
        boolean isHeadless = Boolean.parseBoolean(System.getProperty("HEADLESS", "false"));
        
        if (videoEnabled && !isHeadless) {
            try {
                // Pequeño retraso para que el navegador se maximize/estabilice antes de grabar
                Thread.sleep(1000);
                VideoRecorder.startRecording(scenario.getName());
            } catch (Exception e) {
                System.err.println("No se pudo iniciar la grabación de video: " + e.getMessage());
            }
        }
    }

    /**
     * Hook que se ejecuta al finalizar cada escenario.
     * Gestiona la captura de pantalla en caso de fallo, detiene videos y cierra el navegador.
     */
    @After(order = 1)
    public void tearDown(Scenario scenario) {
        // 1. Captura de pantalla en caso de error (sitio web aún interactivo)
        driver.tearDown(scenario);

        // 2. Gestión del video (si está habilitado)
        boolean videoEnabled = Boolean.parseBoolean(System.getProperty("VIDEO", "false"));
        if (videoEnabled) {
            try {
                // Detiene la grabación y obtiene el archivo resultante
                File videoFile = VideoRecorder.stopRecording();
                if (videoFile != null) {
                    attachVideo(videoFile);
                }
            } catch (Exception e) {
                System.err.println("No se pudo detener la grabación de video: " + e.getMessage());
            }
        }
        
        // 3. Destrucción de la sesión del WebDriver (liberación de memoria)
        driver.quitDriver();
    }

    /**
     * Lee un archivo de video del disco y lo inyecta como adjunto en el reporte Allure.
     */
    private void attachVideo(File videoFile) {
        try (InputStream is = new FileInputStream(videoFile)) {
            Allure.addAttachment("Video de ejecución", "video/mp4", is, ".mp4");
        } catch (IOException e) {
            System.err.println("No se pudo adjuntar el video al reporte Allure: " + e.getMessage());
        }
    }
}
