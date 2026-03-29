package com.example.config.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Utilidad para la gestión de archivos .properties durante la ejecución.
 */
public class PropertiesFile {

    /**
     * Lee las propiedades de un objeto Properties y las inyecta en las propiedades del sistema Java.
     * @param properties Objeto Properties cargado.
     */
     public void readProperties(Properties properties){
        for (String key : properties.stringPropertyNames()) {
            String value = properties.getProperty(key);
            System.setProperty(key.trim(), value.trim());
        }
    }

    /**
     * Actualiza y guarda el valor de la nueva contraseña en el archivo update.properties del entorno actual.
     * @param password Nueva contraseña a persistsir.
     */
    public void writePasswordProperties(String password) throws IOException{
        String fileName = "src/test/resources/enviroment/"+System.getProperty("ENV").toLowerCase()+"/update.properties";
        Properties properties = new Properties();
        InputStream inputStream = new FileInputStream(fileName);
        properties.load(inputStream);
        inputStream.close();

        // Actualizar el valor específico
        properties.setProperty("NEW_PASSWORD", password);

        // Almacenar los cambios en el disco
        properties.store(new FileOutputStream(fileName), null);
    }

    /**
     * Crea el archivo environment.properties en la carpeta de resultados de Allure
     * para mostrar información del entorno en el reporte generado.
     */
    public static void writeEnvironmentAllure(Properties properties){
        try {
            File allureResultsDir = new File("target/allure-results");
            if (!allureResultsDir.exists()) {
                allureResultsDir.mkdirs();
            }   
            properties.store(new FileOutputStream("target/allure-results/environment.properties"), null);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
