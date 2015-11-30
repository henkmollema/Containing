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
import nhl.containing.networking.protobuf.PlatformProto;
import nhl.containing.networking.protocol.CommunicationProtocol;
import nhl.containing.networking.protocol.InstructionDispatcher;

/**
 *
 * @author Jens
 */
public class SimHandler implements Runnable {
    
    public boolean shouldRun = true;
   
    private Socket _socket;
    private Server _server;
    
    private InstructionDispatcher _instructionDispatcher;
    
    private CommunicationProtocol _comProtocol;
    
    public SimHandler(Server server, Socket socket)
    {
        _socket = socket;
        _server = server;
        
        _comProtocol = new CommunicationProtocol();
        _instructionDispatcher = new InstructionDispatcherController(server.simulator);
        _comProtocol.setDispatcher(_instructionDispatcher);
    }
    
    /**
     * Gets the communication protocol of the server.
     *
     * @return The communication protocol.
     */
    public CommunicationProtocol getComProtocol() {
        return _comProtocol;
    }
    
    @Override
    public void run() {
        boolean shouldDie = false;
        while (shouldRun && !shouldDie)//While shouldRun, when connection is lost, start listening for a new one
        {
            if (initSimData(_socket)) {
                if (instructionResponseLoop(_socket)) {
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
            _socket.close();
        } catch (Exception ex) { ex.printStackTrace(); }

        //server.isSimulatorConnected = false;
    }
    
    public boolean initSimData(Socket _socket) {
        p("initializing Simulator data");
        try {
            byte[] data = MessageReader.readByteArray(_socket.getInputStream());
            PlatformProto.Platform platform = PlatformProto.Platform.parseFrom(data);

            //PrintWriter out = new PrintWriter(_socket.getOutputStream(), true);
            if (platform != null) {
                p("ok");
                MessageWriter.writeMessage(_socket.getOutputStream(), "ok".getBytes());
                return true;
            } else {
                p("error");
                MessageWriter.writeMessage(_socket.getOutputStream(), "error".getBytes());
                return false;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
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
                byte[] response = _comProtocol.processInput(data);

                MessageWriter.writeMessage(output, response);
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
