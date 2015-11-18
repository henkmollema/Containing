package Networking;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;

/**
 *
 * @author Jens
 */
public class Client implements Runnable
{
    public static final String HOST = "127.0.0.1";
    public static final int PORT = 1337;
    public static final int START_OF_HEADING = 2;
    public static final int END_OF_TRANSMISSION = 4;
    private final CommunicationProtocolClient comProtocol;
    private boolean isConnected;
    private Socket socket;

    public Client()
    {
        comProtocol = new CommunicationProtocolClient();
    }

    /* Try's to connect   
     * 
     */
    public boolean connect()
    {
        try {
            socket = new Socket(HOST, PORT);
            isConnected = true;

            DataOutputStream out = new DataOutputStream(socket.getOutputStream());
            DataInputStream in = new DataInputStream(socket.getInputStream());
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();

            boolean shouldBreak = false;
            while (!shouldBreak) {
                int lastByte;
                boolean write = false;
                while ((lastByte = in.read()) != -1) {
                    if (!write && lastByte == START_OF_HEADING) {
                        write = true;
                        continue;
                    }
                    else if (!write && lastByte == 0) {
                        continue;
                    }

                    if (lastByte == END_OF_TRANSMISSION) {
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
                    else {
                        // Add current input to buffer
                        buffer.write(lastByte);
                    }
                }
            }
        }
        catch (Exception ex) {
            System.out.println("Can't connect to controller:");
            ex.printStackTrace();

            return false;
        }


        return true;
    }

    private boolean sendSimulatorMetadata()
    {
        return true;
    }

    private boolean read()
    {
        return false;
    }

    @Override
    public void run()
    {
        if (connect()) {
            if (sendSimulatorMetadata()) {
                if (read()) {
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
        else {
            System.out.println("Closed forcefully when connecting with the controller.");
        }
    }
}
