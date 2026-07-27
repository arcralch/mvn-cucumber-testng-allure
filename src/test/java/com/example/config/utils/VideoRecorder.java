package com.example.config.utils;

import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import javax.imageio.ImageIO;

import org.jcodec.api.awt.AWTSequenceEncoder;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

/**
 * Utilidad hilos-segura para la grabación de la actividad del navegador.
 * Soporta dos modos:
 * - Modo visible: Usa java.awt.Robot para capturar la pantalla física
 * - Modo headless: Usa TakesScreenshot del WebDriver para capturar frames del navegador
 */
public class VideoRecorder {
    
    // Almacenamiento por hilo para soportar ejecuciones paralelas
    private static final ThreadLocal<ScheduledExecutorService> executorTL = new ThreadLocal<>();
    private static final ThreadLocal<AWTSequenceEncoder> encoderTL = new ThreadLocal<>();
    private static final ThreadLocal<File> videoFileTL = new ThreadLocal<>();
    private static final ThreadLocal<AtomicInteger> frameCountTL = new ThreadLocal<>();
    private static final ThreadLocal<WebDriver> webDriverTL = new ThreadLocal<>();
    private static final ThreadLocal<Boolean> isHeadlessTL = new ThreadLocal<>();

    /**
     * Inicia la grabación capturando la pantalla física mediante Robot (modo visible).
     * @param scenarioName Nombre único del escenario.
     */
    public static void startRecording(String scenarioName) throws Exception {
        startRecording(scenarioName, null);
    }

    /**
     * Inicia la grabación capturando desde el navegador o pantalla.
     * @param scenarioName Nombre único del escenario.
     * @param driver WebDriver activo (opcional, para modo headless).
     */
    public static void startRecording(String scenarioName, WebDriver driver) throws Exception {
        String projectPath = System.getProperty("user.dir");
        File folder = new File(projectPath, "target/recordings/");
        
        synchronized (VideoRecorder.class) {
            if (!folder.exists()) {
                folder.mkdirs();
            }
        }

        // Sanitización y nombre único
        String cleanName = scenarioName.replaceAll("[^a-zA-Z0-9\\.\\-]", "_");
        String uniqueName = cleanName + "_" + System.identityHashCode(Thread.currentThread()) + "_" + System.currentTimeMillis();
        File videoFile = new File(folder, uniqueName + ".mp4");
        videoFileTL.set(videoFile);
        frameCountTL.set(new AtomicInteger(0));
        
        boolean isHeadless = driver != null && Boolean.parseBoolean(System.getProperty("HEADLESS", "false"));
        isHeadlessTL.set(isHeadless);
        webDriverTL.set(driver);

        new PrintOutText().getOutPrintColors("[VideoRecorder] Iniciando grabación (" + (isHeadless ? "HEADLESS" : "ROBOT") + "): " + videoFile.getName(), "GREEN");

        AWTSequenceEncoder encoder = AWTSequenceEncoder.createSequenceEncoder(videoFile, 10);
        encoderTL.set(encoder);

        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
        executorTL.set(executor);
        AtomicInteger counter = frameCountTL.get();

        if (isHeadless && driver instanceof TakesScreenshot) {
            new PrintOutText().getOutPrintColors("[VideoRecorder] Iniciando captura headless (TakesScreenshot)", "GREEN");
            // Modo headless: captura frames del navegador usando TakesScreenshot
            executor.scheduleAtFixedRate(() -> {
                captureFrameFromBrowser(driver, encoder, counter);
            }, 0, 100, TimeUnit.MILLISECONDS);
        } else {
            new PrintOutText().getOutPrintColors("[VideoRecorder] Iniciando captura visible (Robot)", "GREEN");
            // Modo visible: captura pantalla física
            Robot robot = new Robot();
            Rectangle screenRect = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
            executor.scheduleAtFixedRate(() -> {
                captureFrameFromScreen(robot, screenRect, encoder, counter);
            }, 0, 100, TimeUnit.MILLISECONDS);
        }
    }

    /**
     * Captura frame desde el navegador usando TakesScreenshot (modo headless o visible).
     */
    private static void captureFrameFromBrowser(WebDriver driver, AWTSequenceEncoder encoder, AtomicInteger counter) {
        try {
            if (driver instanceof TakesScreenshot) {
                byte[] screenshotBytes = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
                if (screenshotBytes != null && screenshotBytes.length > 0) {
                    BufferedImage image = ImageIO.read(new ByteArrayInputStream(screenshotBytes));
                    if (image != null) {
                        // JCodec requiere dimensiones pares para YUV420J (H.264)
                        BufferedImage compliantImage = ensureEvenDimensions(image);
                        synchronized (encoder) {
                            encoder.encodeImage(compliantImage);
                            counter.incrementAndGet();
                        }
                    }
                }
            }
        } catch (Exception e) {
            // Ignorar errores durante la captura
        }
    }

    /**
     * Redimensiona la imagen para que sus dimensiones sean múltiplos de 2 (requerido por JCodec H.264).
     */
    private static BufferedImage ensureEvenDimensions(BufferedImage source) {
        int width = source.getWidth();
        int height = source.getHeight();
        
        // Si las dimensiones ya son pares, no hacer nada
        if (width % 2 == 0 && height % 2 == 0) {
            return source;
        }
        
        // Crear nueva imagen con dimensiones pares (redondeando hacia abajo)
        int newWidth = width % 2 == 0 ? width : width - 1;
        int newHeight = height % 2 == 0 ? height : height - 1;
        
        BufferedImage target = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_RGB);
        target.getGraphics().drawImage(source, 0, 0, newWidth, newHeight, null);
        
        return target;
    }

    /**
     * Captura frame desde pantalla física (modo visible).
     */
    private static void captureFrameFromScreen(Robot robot, Rectangle rect, AWTSequenceEncoder encoder, AtomicInteger counter) {
        try {
            BufferedImage screenCapture = robot.createScreenCapture(rect);
            if (screenCapture != null) {
                BufferedImage image = new BufferedImage(screenCapture.getWidth(), screenCapture.getHeight(), BufferedImage.TYPE_INT_RGB);
                image.getGraphics().drawImage(screenCapture, 0, 0, null);
                
                // JCodec requiere dimensiones pares para YUV420J (H.264)
                BufferedImage compliantImage = ensureEvenDimensions(image);
                
                synchronized (encoder) {
                    encoder.encodeImage(compliantImage);
                    counter.incrementAndGet();
                }
            }
        } catch (Exception e) {
            // Ignorar errores durante el cierre
        }
    }

    /**
     * Finaliza la grabación y libera los recursos del hilo actual.
     * @return Archivo .mp4 generado.
     */
    public static File stopRecording() throws Exception {
        ScheduledExecutorService executor = executorTL.get();
        if (executor != null) {
            executor.shutdown();
            executor.awaitTermination(3, TimeUnit.SECONDS);
            executorTL.remove();
        }

        AWTSequenceEncoder encoder = encoderTL.get();
        AtomicInteger frames = frameCountTL.get();
        if (encoder != null) {
            synchronized (encoder) {
                encoder.finish();
            }
            encoderTL.remove();
            
            File result = videoFileTL.get();
            if (result != null) {
                int totalFrames = (frames != null) ? frames.get() : 0;
                boolean isHeadless = isHeadlessTL.get() != null && isHeadlessTL.get();
                new PrintOutText().getOutPrintColors("[VideoRecorder] Grabación FINALIZADA (" + (isHeadless ? "HEADLESS" : "ROBOT") + "): " + result.getName() 
                                   + " | Frames: " + totalFrames 
                                   + " | Tamaño: " + result.length() + " bytes", "BLUE");
            }
            videoFileTL.remove();
            frameCountTL.remove();
            isHeadlessTL.remove();
            webDriverTL.remove();
            return result;
        }
        return null;
    }
}
