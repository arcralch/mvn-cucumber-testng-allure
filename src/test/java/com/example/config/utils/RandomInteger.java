package com.example.config.utils;

import java.util.Random;

public class RandomInteger {
    
    //Metodo para generar entre dos numero, devolucion de un numero aleatorio
    public static int getRandomNumberInRange(int min, int max){
        Random r = new Random();
        return r.ints(min, (max+1)).limit(1).findFirst().getAsInt();
    }
}
