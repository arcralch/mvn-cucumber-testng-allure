package com.example.config.utils;

import org.jcodec.api.awt.AWTSequenceEncoder;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Utilidad hilos-segura para la grabación de la actividad del navegador.
 * Versión REVERTIDA: Utiliza java.awt.Robot para captar la pantalla física (Modo Visible únicamente).
 */
public class VideoRecorder {
    
    // Almacenamiento por hilo para soportar ejecuciones paralelas (aunque Robot capte toda la pantalla)
    private static final ThreadLocal<ScheduledExecutorService> executorTL = new ThreadLocal<>();
    private static final ThreadLocal<AWTSequenceEncoder> encoderTL = new ThreadLocal<>();
    private static final ThreadLocal<File> videoFileTL = new ThreadLocal<>();
    private static final ThreadLocal<AtomicInteger> frameCountTL = new ThreadLocal<>();

    /**
     * Inicia la grabación capturando la pantalla física mediante Robot.
     * @param scenarioName Nombre único del escenario.
     */
    public static void startRecording(String scenarioName) throws Exception {
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
        
        new PrintOutText().getOutPrintColors("[VideoRecorder] Iniciando grabación (ROBOT): " + videoFile.getName(), "GREEN");

        // Mejora: 10 FPS (100ms por frame) para mayor fluidez
        AWTSequenceEncoder encoder = AWTSequenceEncoder.createSequenceEncoder(videoFile, 10);
        encoderTL.set(encoder);
        AtomicInteger counter = frameCountTL.get();

        Robot robot = new Robot();
        Rectangle screenRect = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());

        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
        executorTL.set(executor);

        // Tarea de captura periódica
        executor.scheduleAtFixedRate(() -> {
            captureFrame(robot, screenRect, encoder, counter);
        }, 0, 100, TimeUnit.MILLISECONDS);
    }

    /**
     * Captura el área de la pantalla mediante Robot y la codifica.
     */
    private static void captureFrame(Robot robot, Rectangle rect, AWTSequenceEncoder encoder, AtomicInteger counter) {
        try {
            BufferedImage screenCapture = robot.createScreenCapture(rect);
            if (screenCapture != null) {
                // Asegurar formato RGB para compatibilidad JCodec H.264
                BufferedImage image = new BufferedImage(screenCapture.getWidth(), screenCapture.getHeight(), BufferedImage.TYPE_INT_RGB);
                image.getGraphics().drawImage(screenCapture, 0, 0, null);
                
                synchronized (encoder) {
                    encoder.encodeImage(image);
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
                new PrintOutText().getOutPrintColors("[VideoRecorder] Grabación FINALIZADA (ROBOT): " + result.getName() 
                                   + " | Frames: " + totalFrames 
                                   + " | Tamaño: " + result.length() + " bytes", "BLUE");
            }
            videoFileTL.remove();
            frameCountTL.remove();
            return result;
        }
        return null;
    }
}
