package com.example.config.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Properties;

public class PropertiesFile {

    //Metodo de lectura de los archivos properties
     public void readProperties(Properties properties){
        Enumeration<Object> indexs;
        indexs = properties.keys();
        while(indexs.hasMoreElements()){
            Object index = indexs.nextElement();
            System.setProperty(index.toString(), properties.get(index).toString());
        }
    }

    //Metodo de escritura en los archivos properties
    public void writePasswordProperties(String password) throws IOException{
        String fileName = "src/test/resources/enviroment/"+System.getProperty("ENV").toLowerCase()+"/update.properties";
        Properties properties = new Properties();
        InputStream inputStream = new FileInputStream(fileName);
        properties.load(inputStream);
        inputStream.close();

        //Actualizar los valores necesarios
        properties.setProperty("NEW_PASSWORD", password);

        //Escribir los valores actualizados en el archivo de propiedades
        properties.store(new FileOutputStream(fileName), null);
    }

    //Metodo de creacion environment Allure
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
