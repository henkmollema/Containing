/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Networking;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import networking.Proto.PlatformProto;
import networking.protocol.CommunicationProtocol;

/**
 *
 * @author Jens
 */
public class Server implements Runnable
{
    public static final int PORT = 1337;
    public static final int START_OF_HEADING = 2;
    public static final int END_OF_TRANSMISSION = 4;
    private boolean isConnected;
    private ServerSocket serverSocket = null;
    private Socket _socket = null;
    private CommunicationProtocol comProtocol;

    public Server()
    {
        comProtocol = new CommunicationProtocol();
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
            PlatformProto.Platform platform = null;
            BufferedInputStream in = new BufferedInputStream(_socket.getInputStream());
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();

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

                if (lastByte != END_OF_TRANSMISSION)
                {
                    // Add current input to buffer
                    buffer.write(lastByte);
                }
                else
                {
                    // We received the last byte, parse the protobuf item and
                    // break out of the loop.
                    byte[] input = buffer.toByteArray();
                    platform = PlatformProto.Platform.parseFrom(input);
                    break;
                }
            }

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
            BufferedInputStream in = new BufferedInputStream(_socket.getInputStream());
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            DataOutputStream out = new DataOutputStream(_socket.getOutputStream());;
            
            boolean shouldBreak = false;
            int lastByte;

            while (!shouldBreak)
            {
                while ((lastByte = in.read()) != -1)
                {
                    if (lastByte == END_OF_TRANSMISSION)
                    {
                        byte[] response = comProtocol.processInput(buffer.toByteArray());
                        buffer.reset();

                        //Send response
                        out.write(response);
                        out.write(END_OF_TRANSMISSION);
                        out.flush();
                    }
                    else
                    {
                        //Add current input to buffer
                        buffer.write(lastByte);
                    }
                }
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
