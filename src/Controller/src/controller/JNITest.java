/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import java.awt.Point;


/**
 *
 * @author Dudecake
 */
public class JNITest
{
    private int number = 88;
    
    public int getNumber()
    {
        return number;
    }
    
    public static native void initPath();
    
    public static native void helloFromC();
    
    public static native double avgFromC(int[] x);
    
    public static native int intFromC(int[] x);
    
    public static native Integer integerFromC(int x);
    
    public static native Point pointInC(int x, int y);
    
    public native void changeNumberInC();
    
    public static native void cleanup();
}
