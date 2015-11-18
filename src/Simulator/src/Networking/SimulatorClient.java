package Networking;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.util.UUID;
import networking.Proto.PlatformProto.*;

/**
 * The client which connects to the controller through a socket.
 *
 * @author Jens
 */
public class SimulatorClient implements Runnable
{
    public static final String HOST = "127.0.0.1";
    public static final int PORT = 1337;
    public static final int START_OF_HEADING = 2;
    public static final int END_OF_TRANSMISSION = 4;
    private final CommunicationProtocolClient comProtocol;
    private boolean isConnected;
    private Socket socket;

    public SimulatorClient()
    {
        comProtocol = new CommunicationProtocolClient();
    }

    /**
     * Tries to connect with the controller.
     *
     * @return true if the connection succeded; otherwise, false.
     */
    public boolean connect()
    {
        try
        {
            socket = new Socket(HOST, PORT);
            isConnected = true;
            return true;
        }
        catch (Exception ex)
        {
            System.out.println("Can't connect to controller:");
            ex.printStackTrace();

            return false;
        }
    }

    /**
     * Sends metadata of the simulator to the controller.
     *
     * @return true if sending the metadata succeeded; otherwise, false.
     */
    private boolean sendSimulatorMetadata()
    {
        Platform.Builder platformBuilder = Platform.newBuilder();
        platformBuilder
                .setId(newUUID())
                .setType(Platform.PlatformType.SeaShip)
                .addCranes(Platform.Crane.newBuilder()
                .setId(newUUID())
                .setType(Platform.Crane.CraneType.Rails))
                .addCranes(Platform.Crane.newBuilder()
                .setId(newUUID())
                .setType(Platform.Crane.CraneType.Rails));

        Platform platform = platformBuilder.build();

        return true;
    }

    /**
     * Starts the loop of reading instructions and writing the results over the
     * socket.
     *
     * @return The result of the loop.
     */
    private boolean startLoop()
    {
        try
        {
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());
            DataInputStream in = new DataInputStream(socket.getInputStream());
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();

            boolean shouldBreak = false;
            while (!shouldBreak)
            {
                int lastByte;
                boolean write = false;
                while ((lastByte = in.read()) != -1)
                {
                    if (!write && lastByte == START_OF_HEADING)
                    {
                        write = true;
                        continue;
                    }
                    else if (!write && lastByte == 0)
                    {
                        continue;
                    }

                    if (lastByte == END_OF_TRANSMISSION)
                    {
                        byte[] input = buffer.toByteArray();
                        System.out.println("Received " + input.length + " bytes ");
                        byte[] response = comProtocol.processInput(input);
                        buffer.reset();

                        // Send response
                        out.write(response);
                        out.write(END_OF_TRANSMISSION);
                        out.flush();

                        // Stop writing
                        write = false;
                    }
                    else
                    {
                        // Add current input to buffer
                        buffer.write(lastByte);
                    }
                }
            }
        }
        catch (Exception ex)
        {
            System.out.println("Can't connect to controller:");
            ex.printStackTrace();

            return false;
        }
        return true;
    }

    private static String newUUID()
    {
        return UUID.randomUUID().toString();
    }

    @Override
    public void run()
    {
        if (connect())
        {
            if (sendSimulatorMetadata())
            {
                if (startLoop())
                {
                    System.out.println("Closed peacefully");
                }
                else
                {
                    System.out.println("Reading stopped");
                }
            }
            else
            {
                System.out.println("Failed ssending simulator metadata.");
            }
        }
        else
        {
            System.out.println("Closed forcefully when connecting with the controller.");
        }
    }
}
