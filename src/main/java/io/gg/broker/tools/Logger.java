package io.gg.broker.tools;

/**
 * Created by gerald on 08/03/2018.
 */
public class Logger {
    public static void log(String message){
        System.out.println("######");
        System.out.println(message);
        System.out.println("######");
    }
    public static void log(String tag,String level, String message){
        System.out.println("######");
        System.out.println(message);
        System.out.println("######");
    }
}
