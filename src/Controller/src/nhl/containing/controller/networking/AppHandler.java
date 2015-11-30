/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nhl.containing.controller.networking;

import java.net.Socket;
import nhl.containing.networking.protocol.CommunicationProtocol;

/**
 *
 * @author Jens
 */
public class AppHandler implements Runnable{
    
    public boolean shouldRun = true;
   
    private Socket socket;
    private Server server;
    
    private CommunicationProtocol comProtocol;
    
    public AppHandler(Server _server, Socket _socket)
    {
        socket = _socket;
        server = _server;
        
        comProtocol = new CommunicationProtocol();
    }
    
    public boolean initAppData() {
        return true;
    }

    @Override
    public void run() {
        while (shouldRun)//While shouldRun, when connection is lost, start listening for a new one
        {
            if (initAppData()) {
                
            } else {
                p("Error while initialising simulator data..");
            }
        }

        try //Clean 
        {
            socket.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        //isAppConnected = false;
    }
    
    private static void p(String s) {
        System.out.println("Controller: " + s);
    }
    
}
