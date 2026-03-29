package com.example.config.utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import io.qameta.allure.AllureLifecycle;

/**
 * Utilidad para la captura de pantalla del navegador e integración con Allure.
 */
public class ScreenshotUtils {
    
    /**
     * Captura el estado actual de la pantalla del navegador y lo adjunta al reporte Allure.
     * @param scenario El ciclo de vida de Allure activo para añadir el adjunto.
     * @param driver Instancia de WebDriver de la cual obtener la captura.
     */
    public void captureScreenshot(AllureLifecycle scenario, WebDriver driver) {
        final byte[] screenShot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
        // Generar nombre basado en la fecha y hora actual para evitar solapamientos
        scenario.addAttachment(LocalDateTime.now().format(
                DateTimeFormatter.ofPattern("dd-MMM-yy_hh:mm:ss")), "image/png", "png", screenShot);
    }
}
