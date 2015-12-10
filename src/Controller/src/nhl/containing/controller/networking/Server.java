package nhl.containing.controller.networking;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import nhl.containing.controller.Simulator;
import nhl.containing.networking.messaging.*;
import nhl.containing.networking.protobuf.ClientIdProto.ClientIdentity;
import nhl.containing.networking.protocol.CommunicationProtocol;

/**
 * Provides interaction with the client.
 *
 * @author Jens
 */
public class Server implements Runnable {

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
    
    public Simulator getSimulator()
    {
        return simulator;
    }

    public Server(Simulator simulator) {
        this.simulator = simulator;
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
    
    public void onSimDisconnect()
    {
        simCom = null;
        this.isSimulatorConnected = false;
    }

    @Override
    public void run() {
        if (shouldRun) { //If already running, return..
            return;
        }
            
        shouldRun = true;

        try {
            serverSocket = new ServerSocket(PORT);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        
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
                byte[] data = StreamHelper.readByteArray(tmpSocket.getInputStream());
                ClientIdentity idData = ClientIdentity.parseFrom(data);
                
                switch(idData.getClientType())
                {
                    case SIMULATOR:
                        if(simCom != null)
                        {
                            tmpSocket.close(); //Accept only one sim connection
                            return;
                        }

                        SimHandler simHandler = new SimHandler(this, tmpSocket);
                        simCom = simHandler.getComProtocol();
                        
                        new Thread(simHandler).start(); //Start anonymous thread
                        
                        isSimulatorConnected = true;
                        p("Sim connected");
                        
                        break;
                    case APP:
                        isAppConnected = true;
                        p("App connected");
                        
                        //Create new appHandler and run it on it's own thread
                        AppHandler appHandler = new AppHandler(this, tmpSocket);
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
