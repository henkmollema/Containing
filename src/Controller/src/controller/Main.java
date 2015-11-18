package controller;

import java.awt.Point;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.logging.*;

@SuppressWarnings("all")
public class Main
{
    static {
        try {
            File file = new File(Main.class.getResource("/lib/").toURI().toString(), "JNITest.dll");
            File resFile = new File(System.getProperty("java.io.tmpdir"), "JNITest.dll");
            if (!resFile.exists()) {
                resFile.createNewFile();
            }
            Main.copyFileToTemp(file.getPath().substring(5), resFile.getAbsolutePath());
            System.load(resFile.getAbsolutePath());
            //System.loadLibrary("JNITest");
        }
        catch (Exception ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args)
    {
        Runtime.getRuntime().addShutdownHook(new Thread()
        {
            @Override
            public void run()
            {
                JNITest.cleanup();
                System.out.println("exit");
            }
        });
        //run();
        
        JNITest.initPath();
        int[] path = JNITest.getPath(0, 4, 5.0f);
        for (int i : path)
        {
            System.out.println("node " + i);
        }
        /*
        JNITest.helloFromC();
        int[] iA = { 5, 6, 8 };
        System.out.println("average: " + JNITest.avgFromC(iA));
        System.out.println("average int: " + JNITest.intFromC(iA));
        Integer i = JNITest.integerFromC(5);
        System.out.println(i);
        Point point = JNITest.pointInC(5, 5);
        System.out.println("x: " + point.x + " y: " + point.y);
        JNITest test = new JNITest();
        test.changeNumberInC();
        System.out.println(test.getNumber());
        */
    }
    
    /**
     * Run Forest! Run!
     */
    private static void run()
    {
        SystemController controller = new SystemController();
        controller.run("XML/xml5.xml");
    }
    
    private static void copyFileToTemp(String source, String dest) throws IOException
    {
        FileChannel inputChannel = null;
        FileChannel outputChannel = null;
        try {
            inputChannel = new FileInputStream(source).getChannel();
            outputChannel = new FileOutputStream(dest).getChannel();
            outputChannel.transferFrom(inputChannel, 0, inputChannel.size());
        }
        catch (Exception ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
        finally {
            inputChannel.close();
            outputChannel.close();
        }
    }
}
