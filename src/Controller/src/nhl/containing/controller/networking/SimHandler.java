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
import java.nio.ByteBuffer;
import java.util.Timer;
import java.util.TimerTask;
import nhl.containing.controller.Time;
import nhl.containing.networking.messaging.StreamHelper;
import nhl.containing.networking.protobuf.InstructionProto.Instruction;
import nhl.containing.networking.protobuf.SimulationItemProto.SimulationItem;
import nhl.containing.networking.protocol.CommunicationProtocol;
import nhl.containing.networking.protocol.InstructionDispatcher;
import nhl.containing.networking.protocol.InstructionType;

/**
 *
 * @author Jens
 */
public class SimHandler implements Runnable {
    
    public boolean shouldRun = true;
   
    private Socket _socket;
    private Server _server;
    
    private Timer _timer;
    
    private InstructionDispatcher _instructionDispatcher;
    
    private CommunicationProtocol _comProtocol;
    
    public SimHandler(Server server, Socket socket)
    {
        _socket = socket;
        _server = server;
        
        _comProtocol = new CommunicationProtocol();
        _instructionDispatcher = new InstructionDispatcherController(server.simulator, _comProtocol);
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
        
        _server.onSimDisconnect();

        //server.isSimulatorConnected = false;
    }
    
    public boolean initSimData(Socket _socket) {
        p("initializing Simulator data");
        try {
            Instruction okayMessage = Instruction.newBuilder()
                    .setId(CommunicationProtocol.newUUID())
                    .setInstructionType(InstructionType.CLIENT_CONNECTION_OKAY)
                    .build();
            
            StreamHelper.writeMessage(_socket.getOutputStream(), okayMessage.toByteArray());
            
            byte[] data = StreamHelper.readByteArray(_socket.getInputStream());
            //SimItemProto platform = PlatformProto.Platform.parseFrom(data);
            SimulationItem platform = SimulationItem.parseFrom(data);

            //PrintWriter out = new PrintWriter(_socket.getOutputStream(), true);
            if (platform != null) {
                p("ok");
                StreamHelper.writeMessage(_socket.getOutputStream(), "ok".getBytes());
                return true;
            } else {
                p("error");
                StreamHelper.writeMessage(_socket.getOutputStream(), "error".getBytes());
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
            OutputStream output = socket.getOutputStream();

            //Send empty message to start conversation..
            StreamHelper.writeMessage(output, new byte[] { 0 });

            
            while (shouldRun) {
                // Re-use streams for more efficiency.
                byte[] data = StreamHelper.readByteArray(socket.getInputStream());
                byte[] response = _comProtocol.processInput(data);

                StreamHelper.writeMessage(output, response); 
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
    
    private class TimeUpdater extends TimerTask
    {
        @Override
        public void run()
        {
            Time._updateTime(5.0 / 1000.0);
        }
    }
}
