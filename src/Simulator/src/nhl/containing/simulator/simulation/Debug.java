/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nhl.containing.simulator.simulation;

/**
 *
 * @author sietse
 */
public final class Debug {
    
    private final static String LOG_START_MESSAGE = "LOG: ";
    private final static String WARNING_START_MESSAGE = "WARNING: ";
    private final static String ERROR_START_MESSAGE = "ERROR: ";
    
    /**
     * System.out.prtln shortcut
     * @param msg 
     */
    public static void print(String msg) {
        System.out.println(msg);
    }
    /**
     * Prints log message
     * @param msg message
     */
    public static void log(String msg) {
        print(LOG_START_MESSAGE + msg);
    }
    /**
     * Prints warning message
     * @param msg messgae
     */
    public static void warning(String msg) {
        print(WARNING_START_MESSAGE + msg);
    }
    /**
     * Prints error message
     * @param msg message
     */
    public static void error(String msg) {
        print(ERROR_START_MESSAGE + msg);
    }
}
