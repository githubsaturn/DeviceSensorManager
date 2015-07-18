package com.itskasra.devicesensormanager;

/**
 * Created by kbigdeli on 2/10/2015.
 */
public class AppConfig {
    public static boolean IS_DEBUG_ON = true;
    public static String getClassName(Object o){
        String fullClassName = o.getClass().getName();
        int lastDot = fullClassName.lastIndexOf(".");
        String localClassName = fullClassName.substring(lastDot);
        return localClassName;
    }
}
