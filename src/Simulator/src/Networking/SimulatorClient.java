package Networking;

import java.io.*;
import java.net.Socket;
import java.util.UUID;
import networking.Proto.PlatformProto.*;
import networking.protocol.CommunicationProtocol;

/**
 * The client which connects to the controller through a socket.
 *
 * @author Jens
 */
public class SimulatorClient implements Runnable
{
    // Statics
    public static final String HOST = "127.0.0.1";
    public static final int PORT = 1337;
    public static final int START_OF_HEADING = 2;
    public static final int END_OF_TRANSMISSION = 4;
    
    // Communication
    private final CommunicationProtocol comProtocol;
    
    // Socket fields    
    private Socket socket;
    private InputStream _inputStream;
    private OutputStream _outputStream;
    private boolean isConnected;
    
    /**
     * Gets the communication protocol of the server.
     *
     * @return The communication protocol.
     */
    public CommunicationProtocol getComProtocol()
    {
        return comProtocol;
    }

    public SimulatorClient()
    {
        comProtocol = new CommunicationProtocol();
    }

    /**
     * Connects to the controller.
     *
     * @return true if the connection succeded; otherwise, false.
     */
    public boolean connect()
    {
        p("run()");
        try
        {
            socket = new Socket(HOST, PORT);
            _inputStream = socket.getInputStream();
            _outputStream = socket.getOutputStream();

            isConnected = true;
            p("connected with the controller");
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
        p("sendSimulatorMetadata()");

        DataOutputStream out;
        BufferedReader reader = null;
        try
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

            out = new DataOutputStream(_outputStream);

            byte[] message = platform.toByteArray();
            System.out.println("Sending " + message.length + " bytes...");

            // Write the object to the socket.
            out.write(START_OF_HEADING);
            out.write(message);
            out.write(END_OF_TRANSMISSION);
            out.flush();

            p("Message sent to controller, start reading input..");

            reader = new BufferedReader(new InputStreamReader(_inputStream));
            String result = reader.readLine();

            if (result == null || result.equals(""))
            {
                System.err.println("No result of sendSimulatorMetadata().");
                return false;
            }

            if (result.equalsIgnoreCase("ok"))
            {
                p("result is " + result);
                return true;
            }

            p("result is " + result);
            return false;
        }
        catch (IOException ex)
        {
            ex.printStackTrace();
        }

        return false;
    }

    /**
     * Starts the loop of reading instructions and writing the results over the
     * socket.
     *
     * @return The result of the loop.
     */
    private boolean startLoop()
    {
        p("startLoop()");
        
        try
        {
            DataOutputStream out = new DataOutputStream(_outputStream);
            DataInputStream in = new DataInputStream(_inputStream);
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
                        
                        if(input.length > 0) p("Received " + input.length + " bytes ");
                        
                        byte[] response = comProtocol.processInput(input);
                        buffer.reset();

                        // Send response
                        out.write(START_OF_HEADING);
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
            System.err.println("Can't connect to controller");
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
        p("run()");
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

    private static void p(String s)
    {
        System.out.println("SimulatorClient: " + s);
    }
}
