package com.example.config.utils;

import java.io.PrintStream;

public class PrintOutText {
    
    public static final String ANSI_BLUE = "\u001B[34m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_WHITE = "\u001B[37m";

    //Metodo impresion a los colores detallados para diferentes tipos de mensajes salidas
    public void getOutPrintColors(String str, String colors){
        switch(colors.toUpperCase()){
            case "BLUE": //Info text print color
                PrintOutText.getPrintText(ANSI_BLUE+str);
                break;
            case "RED": //Error text printing color
                PrintOutText.getPrintText(ANSI_RED+str);
                break;
            case "GREEN": //Affirmative Text Print Color
                PrintOutText.getPrintText(ANSI_GREEN+str);
                break;
            case "YELLOW": //Caution Text Print Color
                PrintOutText.getPrintText(ANSI_YELLOW+str);
                break;
            default:
                PrintOutText.getPrintText(ANSI_WHITE+str);
                break;
        }
        PrintOutText.getPrintText(ANSI_WHITE);
    }

    public static void getPrintText(String str){
        PrintStream out = System.out;
        System.setOut(out);
        System.out.println(str);
    }

}
