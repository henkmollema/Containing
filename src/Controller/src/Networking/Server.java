/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Networking;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

/**
 *
 * @author Jens
 */
public class Server implements Runnable{
    
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
    
    
    /* Opens a serversocket and waits for a client to connect.
     * This method should be called on it's own thread as it contains an indefinite loop.
     * Returns false if setup/connection failed.
     * Returns true if connection was successfull and closed peacefuly
     */
    public boolean Start()
    {
        if(!isConnected)
        {
           try{
                
               
                serverSocket = new ServerSocket(PORT);
                System.out.println("Waiting for connection..");

                clientSocket = serverSocket.accept(); //This halts the thread until a connection has been accepted
                System.out.println("Connection Accepted!");

                
                DataOutputStream out = new DataOutputStream(clientSocket.getOutputStream());
                BufferedInputStream in = new BufferedInputStream(clientSocket.getInputStream());
                ByteArrayOutputStream buffer = new ByteArrayOutputStream();

                isConnected = true;

                boolean shouldBreak = false;
                int lastByte;
                
                out.write(42);
                out.write(END_OF_TRANSMISSION);
                out.flush();
                
                while(!shouldBreak)
                {
                    while((lastByte = in.read()) > 0)
                    {
                        
                        if(lastByte == END_OF_TRANSMISSION)
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
            catch(Exception ex)
            {
                System.out.println("Error in socket connection:");
                System.out.println(ex.getMessage());
                return false;
            }
        }
        
        return true;
    }

    @Override
    public void run() {
        if(Start())
        {
            System.out.println("Closed peacefully");
        }
        else
        {
            System.out.println("Closed forcefully");
        }
    }
    
    
    
}
