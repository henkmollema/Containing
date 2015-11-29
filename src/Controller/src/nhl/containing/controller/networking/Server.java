package nhl.containing.controller.networking;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import nhl.containing.networking.messaging.*;
import nhl.containing.networking.protobuf.DataProto;
import nhl.containing.networking.protobuf.DataProto.ClientIdentity;
import nhl.containing.networking.protobuf.PlatformProto;
import nhl.containing.networking.protocol.*;

/**
 * Provides interaction with the client.
 *
 * @author Jens
 */
public class Server implements Runnable {

    public static final int PORT = 1337;
    private boolean isSimulatorConnected;
    private boolean isAppConnected;
    private boolean shouldRun;
    private ServerSocket serverSocket = null;
    private Socket _simulatorSocket = null;
    private Socket _appSocket = null;
    private CommunicationProtocol comProtocol;

    public Server() {
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

    public boolean isSimulatorConnected() {
        return isSimulatorConnected;
    }
    
    public boolean isAppConnected() {
        return isAppConnected;
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
    public boolean start(Socket socket) {
        p("start start()");

        if (!isSimulatorConnected) {
            try {

                p("Waiting for connection..");

                // Halt the thread until a connection has been accepted
                _simulatorSocket = serverSocket.accept();
                p("Connection Accepted!");

                isSimulatorConnected = true;
                return true;

            } catch (Exception ex) {
                System.err.println("Error in socket connection:");
                ex.printStackTrace();
                return false;
            }
        }

        return false;
    }

    public boolean initSimData() {
        p("initializing Simulator data");
        try {
            byte[] data = MessageReader.readByteArray(_simulatorSocket.getInputStream());
            PlatformProto.Platform platform = PlatformProto.Platform.parseFrom(data);

            //PrintWriter out = new PrintWriter(_socket.getOutputStream(), true);
            if (platform != null) {
                p("ok");
                MessageWriter.writeMessage(_simulatorSocket.getOutputStream(), "ok".getBytes());
                return true;
            } else {
                p("error");
                MessageWriter.writeMessage(_simulatorSocket.getOutputStream(), "error".getBytes());
                return false;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }
    
    public boolean initAppData() {
        return false;
    }

    public boolean instructionResponseLoop(Socket socket) {
        p("Starting instructionResponseLoop");
        try {
            BufferedInputStream input = new BufferedInputStream(socket.getInputStream());
            ByteArrayOutputStream dataStream = new ByteArrayOutputStream();
            OutputStream output = socket.getOutputStream();

            //Send empty message to start conversation..
            MessageWriter.writeMessage(output, new byte[]{
                0
            });

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
        if (shouldRun) {
            return;
        }
        shouldRun = true;

        try {
            serverSocket = new ServerSocket(PORT);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        Runnable simHandler = new Runnable() {
            @Override
            public void run() {
                boolean shouldDie = false;
                while (shouldRun && !shouldDie)//While shouldRun, when connection is lost, start listening for a new one
                {
                    if (initSimData()) {
                        if (instructionResponseLoop(_simulatorSocket)) {
                            p("Closed peacefully");
                        } else {
                            p("Lost connection during instructionloop");
                        }
                    } else {
                        p("Error while initialising simulator data..");
                    }
                    
                    shouldDie = true;
                }

                try //Clean 
                {
                    _simulatorSocket.close();
                } catch (Exception ex) { ex.printStackTrace(); }

                isSimulatorConnected = false;
            }
        };

        
        
        Runnable appHandler = new Runnable() {
            @Override
            public void run() {
                while (shouldRun)//While shouldRun, when connection is lost, start listening for a new one
                {
                    if (initAppData()) {
                        if (instructionResponseLoop(_appSocket)) {
                            p("Closed peacefully");
                        } else {
                            p("Lost connection during instructionloop");
                        }
                    } else {
                        p("Error while initialising simulator data..");
                    }
                }

                try //Clean 
                {
                    _appSocket.close();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }

                isAppConnected = false;
            }
        };
        
        
        
        while(shouldRun)//TODO: Also check for app connection, and start appropriate thread
        {
            Socket tmpSocket;
            p("Waiting for connection");
            try{
                tmpSocket = serverSocket.accept();
                
                BufferedInputStream input = new BufferedInputStream(tmpSocket.getInputStream());
                ByteArrayOutputStream dataStream = new ByteArrayOutputStream();
                OutputStream output = tmpSocket.getOutputStream();
                
                //The first message we recieve should be an clientidentity object
                byte[] data = MessageReader.readByteArray(input, dataStream);
                ClientIdentity idData = ClientIdentity.parseFrom(data);
                
                switch(idData.getClientType())
                {
                    case SIMULATOR:
                        _simulatorSocket = tmpSocket;
                        isSimulatorConnected = true;
                        p("Sim connected");
                        new Thread(simHandler).start();
                        
                        break;
                    case APP:
                        _appSocket = tmpSocket;
                        isAppConnected = true;
                        p("App connected");
                        new Thread(appHandler).start();
                        break;
                }
                
            
                
                
            }catch(Exception ex){p("Client connection failed"); ex.printStackTrace();}
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ex) {
                Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
            }
        }


    }

    private static void p(String s) {
        System.out.println("Controller: " + s);
    }
}
