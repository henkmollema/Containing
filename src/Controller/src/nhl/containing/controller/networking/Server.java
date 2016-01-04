package nhl.containing.controller.networking;

import java.net.ServerSocket;
import java.net.Socket;
import nhl.containing.controller.Simulator;
import nhl.containing.networking.messaging.*;
import nhl.containing.networking.protobuf.ClientIdProto.ClientIdentity;
import nhl.containing.networking.protocol.CommunicationProtocol;

/**
 * Provides interaction with the client.
 *
 * @author Jens
 */
public class Server implements Runnable
{
    public static final int PORT = 1337;
    public Simulator simulator;
    private boolean isSimulatorConnected;
    private boolean isAppConnected;
    private boolean shouldRun;
    private ServerSocket serverSocket = null;
    private CommunicationProtocol simCom;

    public CommunicationProtocol simCom()
    {
        return simCom;
    }

    public Server(Simulator _simulator)
    {
        simulator = _simulator;
        simCom = new CommunicationProtocol(); //The communication protocol used for the simulator connection
        simCom.setDispatcher(new InstructionDispatcherController(simulator, simCom));
    }

    public Simulator getSimulator()
    {
        return simulator;
    }

    public boolean isSimulatorConnected()
    {
        return isSimulatorConnected;
    }

    public boolean isAppConnected()
    {
        return isAppConnected;
    }

    public void stop()
    {
        shouldRun = false;
    }

    public void onSimDisconnect()
    {
        this.isSimulatorConnected = false;

    }

    @Override
    public void run()
    {
        if (shouldRun)
        { //If already running, return..
            return;
        }

        shouldRun = true;

        try
        {
            serverSocket = new ServerSocket(PORT);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            System.out.println("Failed to connect to the socket: " + ex.getMessage());
            return;
        }

        while (shouldRun)
        {
            Socket tmpSocket;
            p("Waiting for connection");
            try
            {
                tmpSocket = serverSocket.accept();

                // The first message we recieve should be an clientidentity object
                byte[] data = StreamHelper.readByteArray(tmpSocket.getInputStream());
                ClientIdentity idData = ClientIdentity.parseFrom(data);

                switch (idData.getClientType())
                {
                    case SIMULATOR:
                        if (isSimulatorConnected)
                        {
                            // Accept only one sim connection
                            tmpSocket.close();
                            return;
                        }

                        SimHandler simHandler = new SimHandler(this, tmpSocket, simCom);
                        // Start anonymous thread
                        new Thread(simHandler).start();

                        isSimulatorConnected = true;
                        p("Sim connected");
                        break;

                    case APP:
                        isAppConnected = true;
                        p("App connected");

                        // Create new appHandler and run it on it's own thread
                        AppHandler appHandler = new AppHandler(this, tmpSocket);
                        new Thread(appHandler).start();
                        break;
                }
            }
            catch (Exception ex)
            {
                p("Client connection failed");
                ex.printStackTrace();
            }
            try
            {
                Thread.sleep(1000);
            }
            catch (InterruptedException ex)
            {
                ex.printStackTrace();
            }
        }
    }

    private static void p(String s)
    {
        System.out.println("Controller: " + s);
    }
}
