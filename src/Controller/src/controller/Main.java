/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

/**
 *
 * @author Dudecake
 */
@SuppressWarnings("unused")
public class Main
{
    static
    {
        System.loadLibrary("JNITest");
    }
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args)
    {
        Main p = new Main();
        JNITest.helloFromC();
    }
}
