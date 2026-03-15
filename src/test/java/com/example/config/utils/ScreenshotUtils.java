package com.example.config.utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import io.qameta.allure.AllureLifecycle;

public class ScreenshotUtils {
    
    //Metodo para caracturas de pantalla visualizarse en reporte de salida
    public void captureScreenshot(AllureLifecycle scenario, WebDriver driver) {
        final byte[] screenShot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
        scenario.addAttachment(LocalDateTime.now().format(
                DateTimeFormatter.ofPattern("dd-MMM-yy_hh:mm:ss")), "image/png", "png", screenShot);
    }
}
