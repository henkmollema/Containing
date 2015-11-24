package controller;

import java.awt.Dimension;
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

    static
    {
        try
        {
//            File file = new File(Main.class.getResource("/lib/").toURI().toString(), "JNITest.dll");
//            File resFile = new File(System.getProperty("java.io.tmpdir"), "JNITest.dll");
//            if (!resFile.exists())
//            {
//                resFile.createNewFile();
//            }
//            Main.copyFileToTemp(file.getPath().substring(5), resFile.getAbsolutePath());
//            System.load(resFile.getAbsolutePath());
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
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

        run();

        JNITest.initPath(new Dimension(25, 25));
        int[] path = JNITest.getPath(0, 130, 5.0f);
        for (int i : path)
        {
            System.out.println("node " + i);
        }
    }

    /**
     * Run Forest! Run!
     */
    private static void run()
    {
        SimulatorController controller = new SimulatorController();
        try
        {
            controller.run("xml1.xml");
            //controller.run("xml2.xml");
            //controller.run("xml3.xml");
            //controller.run("xml4.xml");
            //controller.run("xml5.xml");
            //controller.run("xml6.xml");
            //controller.run("xml7.xml");
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }
    
    private static void copyFileToTemp(String source, String dest) throws IOException
    {
        FileChannel inputChannel = null;
        FileChannel outputChannel = null;
        try
        {
            inputChannel = new FileInputStream(source).getChannel();
            outputChannel = new FileOutputStream(dest).getChannel();
            outputChannel.transferFrom(inputChannel, 0, inputChannel.size());
        }
        catch (Exception ex)
        {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
        finally
        {
            inputChannel.close();
            outputChannel.close();
        }
    }
}
