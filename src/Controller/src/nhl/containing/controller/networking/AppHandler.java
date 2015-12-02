/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nhl.containing.controller.networking;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import nhl.containing.networking.messaging.MessageReader;
import nhl.containing.networking.messaging.MessageWriter;
import nhl.containing.networking.protobuf.InstructionProto.*;
import nhl.containing.networking.protocol.CommunicationProtocol;
import nhl.containing.networking.protocol.InstructionType;

/**
 *
 * @author Jens
 */
public class AppHandler implements Runnable{
    
    public boolean shouldRun = true;
    private Socket socket;
    private Server server;
    
    public AppHandler(Server _server, Socket _socket)
    {
        socket = _socket;
        server = _server;
    }
    
    public boolean initAppData() {
        try
        {
            Instruction okayMessage = Instruction.newBuilder()
                .setId(CommunicationProtocol.newUUID())
                .setInstructionType(InstructionType.CLIENT_CONNECTION_OKAY)
                .build();
            MessageWriter.writeMessage(socket.getOutputStream(), okayMessage.toByteArray());
            return true;
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            return false;
        }
    }

    @Override
    public void run() {
        while (shouldRun)//While shouldRun, when connection is lost, start listening for a new one
        {
            if (initAppData()) {
                if (appDataLoop()) {
                    p("Closed peacefully");
                } else {
                    p("Lost connection during instructionloop");
                }
            } else {
                p("Error while initialising app connection..");
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
    
    public boolean appDataLoop()
    {
        p("Starting appLoop");
        try {
            BufferedInputStream input = new BufferedInputStream(socket.getInputStream());
            ByteArrayOutputStream dataStream = new ByteArrayOutputStream();
            OutputStream output = socket.getOutputStream();
            while (shouldRun) {
                // Re-use streams for more efficiency.
                byte[] data = MessageReader.readByteArray(input, dataStream); //Read
                byte[] response = nhl.containing.controller.App.TestData(data);
                MessageWriter.writeMessage(output, response); //Send
            }
        } catch (IOException ex) {
            ex.printStackTrace();
            return false;
        }

        return true;
    }
    
    private static void p(String s) {
        System.out.println("Controller: " + s);
    }
    
}
