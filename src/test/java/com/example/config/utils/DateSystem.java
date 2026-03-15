package com.example.config.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class DateSystem {
    
    //Metodo que retorna la fecha en un formato especifico
    public static String getCurrDateAsString(String format) {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat formatDate = new SimpleDateFormat(format);
        return formatDate.format(cal.getTime());
    }

    //Metodo que permite devolver el numero de dias calendario con un formato especifico
    public static String getEndDateAsString(int numDay,String format) {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, numDay);
        SimpleDateFormat formatDate = new SimpleDateFormat(format);
        return formatDate.format(cal.getTime());
    }
}
