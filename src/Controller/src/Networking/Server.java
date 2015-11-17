/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Networking;

import controller.Proto.SimulationItemProto.SimulationItem;
import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 *
 * @author Jens
 */
public class Server implements Runnable
{
    public static final int PORT = 1337;
    public static final int END_OF_TRANSMISSION = 4;
    private boolean isConnected;
    private ServerSocket serverSocket = null;
    private Socket clientSocket = null;
    private CommunicationProtocolServer comProtocol;

    public Server()
    {
        comProtocol = new CommunicationProtocolServer();
    }
    private DataOutputStream _out;
    private BufferedInputStream _in;
    private ByteArrayOutputStream _buffer;

    /* Opens a serversocket and waits for a client to connect.
     * This method should be called on it's own thread as it contains an indefinite loop.
     * Returns false if setup/connection failed.
     * Returns true if connection was successfull and closed peacefuly
     */
    public boolean Start()
    {
        if (!isConnected) {
            try {
                serverSocket = new ServerSocket(PORT);
                System.out.println("Waiting for connection..");

                clientSocket = serverSocket.accept(); //This halts the thread until a connection has been accepted
                System.out.println("Connection Accepted!");

                _out = new DataOutputStream(clientSocket.getOutputStream());
                _in = new BufferedInputStream(clientSocket.getInputStream());
                _buffer = new ByteArrayOutputStream();

                isConnected = true;
                return true;

            }
            catch (Exception ex) {
                System.err.println("Error in socket connection:");
                ex.printStackTrace();
                return false;
            }
        }

        return false;
    }

    public boolean init()
    {
        try {
            SimulationItem.Builder builder = SimulationItem.newBuilder();
            SimulationItem item = builder
                    .setId(java.util.UUID.randomUUID().toString())
                    .setType(SimulationItem.SimulationItemType.PLATFORM)
                    .build();

            byte[] bytes = item.toByteArray();
            System.out.println("Sending " + bytes.length + " bytes...");
            _out.write(bytes);
            _out.write(END_OF_TRANSMISSION);
            _out.flush();

            return true;
        }
        catch (IOException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    public boolean read()
    {

        try {
            boolean shouldBreak = false;
            int lastByte;

            while (!shouldBreak) {
                while ((lastByte = _in.read()) > 0) {

                    if (lastByte == END_OF_TRANSMISSION) {
                        byte[] response = comProtocol.processInput(_buffer.toByteArray());
                        _buffer.reset();

                        //Send response
                        _out.write(response);
                        _out.write(END_OF_TRANSMISSION);
                        _out.flush();
                    }
                    else {
                        //Add current input to buffer
                        _buffer.write(lastByte);
                    }
                }
            }
        }
        catch (IOException ex) {
            ex.printStackTrace();
        }

        return true;
    }

    @Override
    public void run()
    {
        if (Start()) {
            if (init()) {
                if (read()) {

                    System.out.println("Closed peacefully");
                }
            }
        }
        else {
            System.out.println("Closed forcefully");
        }
    }
}
