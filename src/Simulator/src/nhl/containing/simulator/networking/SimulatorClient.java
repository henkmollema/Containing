/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nhl.containing.simulator.networking;

import nhl.containing.networking.messaging.MessageWriter;
import java.io.*;
import java.net.Socket;
import nhl.containing.networking.messaging.MessageReader;
import nhl.containing.networking.protobuf.DataProto;
import nhl.containing.networking.protobuf.PlatformProto;
import nhl.containing.networking.protocol.CommunicationProtocol;

/**
 * Providers interaction with the client.
 *
 * @author Jens
 */
public class SimulatorClient implements Runnable {

    public static final String HOST = "127.0.0.1";
    public static final int PORT = 1337;
    private boolean isConnected;
    private boolean shouldRun;
    private Socket _socket = null;
    private CommunicationProtocol comProtocol;

    public SimulatorClient() {
        comProtocol = new CommunicationProtocol();
    }

    /**
     * Gets the communication protocol of the server.
     *
     * @return The communication protocol.
     */
    public CommunicationProtocol getComProtocol() {
        return comProtocol;
    }

    public boolean isConnected() {
        return isConnected;
    }

    public void stop() {
        shouldRun = false;
    }

    /**
     * Opens a serversocket and waits for a client to connect. This method
     * should be called on it's own thread as it contains an indefinite loop.
     * Returns false if setup/connection failed. Returns true if connection was
     * successfull and closed peacefuly
     */
    public boolean start() {
        p("start()");

        if (!isConnected) {

            try {
                // Halt the thread until a connection has been accepted
                _socket = new Socket(HOST, PORT);

                p("Connected to server!");

                isConnected = true;
                return true;

            } catch (Exception ex) {
                p("Connection refused..");
                //ex.printStackTrace();
                return false;
            }
        }

        return false;
    }

    private boolean sendSimulatorMetadata() {
        p("sendSimulatorMetadata()");

        try {

            BufferedInputStream input = new BufferedInputStream(_socket.getInputStream());
            ByteArrayOutputStream dataStream = new ByteArrayOutputStream();
            OutputStream output = _socket.getOutputStream();
            
            //Tell the Controller we are the simulator
            DataProto.ClientIdentity.Builder idBuilder = DataProto.ClientIdentity.newBuilder();
            idBuilder.setClientType(DataProto.ClientIdentity.ClientType.SIMULATOR)
                     .setVersion(CommunicationProtocol.PROTOCOL_VERSION);
            
            MessageWriter.writeMessage(output, idBuilder.build().toByteArray());
            //Wait for an okay response..
            //MessageReader.readByteArray(input, dataStream);
            
            


            PlatformProto.Platform.Builder platformBuilder = PlatformProto.Platform.newBuilder();
            platformBuilder
                    .setId(CommunicationProtocol.newUUID())
                    .setType(PlatformProto.Platform.PlatformType.SeaShip)
                    .addCranes(PlatformProto.Platform.Crane.newBuilder()
                    .setId(CommunicationProtocol.newUUID())
                    .setType(PlatformProto.Platform.Crane.CraneType.Rails))
                    .addCranes(PlatformProto.Platform.Crane.newBuilder()
                    .setId(CommunicationProtocol.newUUID())
                    .setType(PlatformProto.Platform.Crane.CraneType.Rails));

            PlatformProto.Platform platform = platformBuilder.build();



            byte[] message = platform.toByteArray();
            System.out.println("Sending " + message.length + " bytes...");

            MessageWriter.writeMessage(output, message);

            p("Message sent to controller, start reading input..");

            String result = new String(MessageReader.readByteArray(input, dataStream), "UTF-8");

            if (result == null || result.equals("")) {
                System.err.println("No result of sendSimulatorMetadata().");
                return false;
            }

            if (result.equalsIgnoreCase("ok")) {
                p("result is " + result);
                return true;
            }

            p("result is " + result);
            return false;
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        return false;
    }

    public boolean instructionLoop() {
        p("read()");
        try {
            BufferedInputStream input = new BufferedInputStream(_socket.getInputStream());
            ByteArrayOutputStream dataStream = new ByteArrayOutputStream();
            OutputStream output = _socket.getOutputStream();

            //Send empty message to start conversation..
            MessageWriter.writeMessage(output, new byte[]{0});

            while (shouldRun) {
                // Re-use streams for more efficiency.
                byte[] data = MessageReader.readByteArray(input, dataStream);
                byte[] response = comProtocol.processInput(data);

                MessageWriter.writeMessage(output, response);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
            return false;
        }

        return true;
    }

    @Override
    public void run() {
        shouldRun = true;

        while (shouldRun)//While shouldRun, when connection is lost, start listening for a new one
        {
            if (start()) {
                if (sendSimulatorMetadata()) {
                    if (instructionLoop()) {
                        p("Closed peacefully");
                    } else {
                        p("Lost connection during instructionloop");
                    }
                } else {
                    p("Error while initialising connection..");
                }
            } else {
                p("Closed forcefully");
            }

            try //Clean 
            {
                if (_socket != null) {
                    _socket.close();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }

            isConnected = false;
        }
    }

    private static void p(String s) {
        System.out.println("Simulator: " + s);
    }
}
