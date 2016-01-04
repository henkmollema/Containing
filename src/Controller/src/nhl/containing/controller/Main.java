package nhl.containing.controller;

import java.awt.Dimension;
import java.awt.Point;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import nhl.containing.controller.simulation.Node;

@SuppressWarnings("all")
public class Main
{
    
    static
    {
        try
        {
            File file = new File(Main.class.getResource("/lib/").getPath(), "Controller.dll");
            System.load(file.getAbsolutePath());
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            System.exit(1);
        }
    }/**/

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
                try { PathFinder.cleanup(); }
                catch (Exception ex) {}
                System.out.println("exit");
            }
        });

        //run();
        Node[] nodes =
        {
            new Node(5, 1.0f, 1.0f, Arrays.asList(new Integer[]{1,2})),
            new Node(6, 2.0f, 2.0f, Arrays.asList(new Integer[]{0,3})),
            new Node(7, 3.0f, 3.0f, Arrays.asList(new Integer[]{0,3})),
            new Node(8, 4.0f, 4.0f, Arrays.asList(new Integer[]{2,1}))
        };
        PathFinder.initPath(nodes);
        PathFinder.getPath(0, 3, 5.0f);
        /*
        int size = 100;
        long start = System.currentTimeMillis();
        PathFinder.initPath(new Dimension(size, size));
        long end = System.currentTimeMillis();
        long elapsed = end - start;
        System.out.println("grid gegenereerd in: " + String.valueOf(elapsed) + " ms");
        start = System.currentTimeMillis();
        int[] path = PathFinder.getPath(0, (int)Math.pow(size, 2) - 1, 5.0f);
        end = System.currentTimeMillis();
        for (int i : path)
        {
            System.out.println("node " + i);
        }
        System.out.println("grid gegenereerd in: " + String.valueOf(elapsed) + " ms");
        System.out.println("pad gegenereerd in: " + String.valueOf(end - start) + " ms");
        System.out.println("Pad is " + String.valueOf(path.length) + " lang");
        /**/
    }

    /**
     * Run Forest! Run!
     */
    private static void run()
    {
        SimulatorController controller = new SimulatorController();
        try
        {
            //controller.run("xml1.xml");
            controller.run("xml2.xml");
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
}
