/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Networking;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import networking.Messaging.*;
import networking.Messaging.MessageReader;
import networking.Proto.PlatformProto;
import networking.protocol.CommunicationProtocol;

/**
 * Providers interaction with the client.
 *
 * @author Jens
 */
public class Server implements Runnable
{
    public static final int PORT = 1337;
    private boolean isConnected;
    private ServerSocket serverSocket = null;
    private Socket _socket = null;
    private CommunicationProtocol comProtocol;

    public Server()
    {
        comProtocol = new CommunicationProtocol();
    }

    /**
     * Gets the communication protocol of the server.
     *
     * @return The communication protocol.
     */
    public CommunicationProtocol getComProtocol()
    {
        return comProtocol;
    }

    /**
     * Opens a serversocket and waits for a client to connect.
     * This method should be called on it's own thread as it contains an
     * indefinite loop.
     * Returns false if setup/connection failed.
     * Returns true if connection was successfull and closed peacefuly
     */
    public boolean start()
    {
        p("start start()");

        if (!isConnected)
        {
            try
            {
                serverSocket = new ServerSocket(PORT);
                p("Waiting for connection..");

                // Halt the thread until a connection has been accepted
                _socket = serverSocket.accept();
                p("Connection Accepted!");

                isConnected = true;
                return true;

            }
            catch (Exception ex)
            {
                System.err.println("Error in socket connection:");
                ex.printStackTrace();
                return false;
            }
        }

        return false;
    }

    public boolean init()
    {
        p("init()");
        try
        {
            byte[] data = MessageReader.readByteArray(_socket.getInputStream());
            PlatformProto.Platform platform = PlatformProto.Platform.parseFrom(data);

            PrintWriter out = new PrintWriter(_socket.getOutputStream(), true);
            if (platform != null)
            {
                p("ok");
                out.println("ok");
                return true;
            }
            else
            {
                p("error");
                out.println("error");
                return false;
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            return false;
        }
    }

    public boolean read()
    {
        p("read()");
        try
        {
            BufferedInputStream input = new BufferedInputStream(_socket.getInputStream());
            ByteArrayOutputStream dataStream = new ByteArrayOutputStream();
            OutputStream output = _socket.getOutputStream();

            boolean shouldBreak = false;
            while (!shouldBreak)
            {
                // Re-use streams for more efficiency.
                byte[] data = MessageReader.readByteArray(input, dataStream);
                byte[] response = comProtocol.processInput(data);

                MessageWriter.writeMessage(output, response);
            }
        }
        catch (IOException ex)
        {
            ex.printStackTrace();
        }

        return true;
    }

    @Override
    public void run()
    {
        if (start())
        {
            if (init())
            {
                if (read())
                {
                    p("Closed peacefully");
                }
            }
        }
        else
        {
            p("Closed forcefully");
        }
    }

    private static void p(String s)
    {
        System.out.println("Server: " + s);
    }
}
