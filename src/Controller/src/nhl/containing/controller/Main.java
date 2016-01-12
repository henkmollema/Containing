package nhl.containing.controller;

import java.io.File;

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

        run();
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
            //controller.run("xml2.xml");
            //controller.run("xml3.xml");
            controller.run("xml4.xml");
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
