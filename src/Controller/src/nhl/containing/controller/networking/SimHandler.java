/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nhl.containing.controller.networking;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import nhl.containing.networking.messaging.StreamHelper;
import nhl.containing.networking.protobuf.InstructionProto.Instruction;
import nhl.containing.networking.protobuf.SimulationItemProto.SimulatorItemList;
import nhl.containing.networking.protocol.CommunicationProtocol;
import nhl.containing.networking.protocol.InstructionDispatcher;
import nhl.containing.networking.protocol.InstructionType;

/**
 *
 * @author Jens
 */
public class SimHandler implements Runnable
{
    public boolean shouldRun = true;
    private Socket _socket;
    private Server _server;
    private InstructionDispatcher _instructionDispatcher;
    private CommunicationProtocol _comProtocol;

    public SimHandler(Server server, Socket socket, CommunicationProtocol comProtocol)
    {
        _socket = socket;
        _server = server;

        _comProtocol = comProtocol;
        _instructionDispatcher = comProtocol.dispatcher();
    }

    /**
     * Gets the communication protocol of the server.
     *
     * @return The communication protocol.
     */
    public CommunicationProtocol getComProtocol()
    {
        return _comProtocol;
    }

    @Override
    public void run()
    {
        boolean shouldDie = false;
        
        if (initSimData(_socket))
        {
            if (instructionResponseLoop(_socket)) //<-- This method contains an indefinite while loop
            {
                p("Closed peacefully");
            }
            else
            {
                p("Lost connection during instructionloop");
            }
        }
        else
        {
            p("Error while initialising simulator data..");
        }


        try //Clean 
        {
            _socket.close();
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }

        _server.onSimDisconnect();
    }

    private boolean initSimData(Socket _socket)
    {
        p("initializing Simulator data");
        try
        {
            Instruction okayMessage = Instruction.newBuilder()
                    .setId(CommunicationProtocol.newUUID())
                    .setInstructionType(InstructionType.CLIENT_CONNECTION_OKAY)
                    .build();

            StreamHelper.writeMessage(_socket.getOutputStream(), okayMessage.toByteArray());

            byte[] data = StreamHelper.readByteArray(_socket.getInputStream());
            SimulatorItemList platform = null;
            try{
                 platform = SimulatorItemList.parseFrom(data);
                 p("Received " + platform.getItemsCount() + " metadata items");
            }catch(Exception e){
                e.printStackTrace();
            }
            
            if (platform != null) {
                p("ok");
                StreamHelper.writeString(_socket.getOutputStream(), "ok");
                return true;
            }
            else
            {
                p("error");
                StreamHelper.writeString(_socket.getOutputStream(), "error");
                return false;
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            return false;
        }
    }

    public boolean instructionResponseLoop(Socket socket)
    {
        p("Starting instructionResponseLoop");
        try
        {
            OutputStream output = socket.getOutputStream();

            //Send empty message to start conversation..
            StreamHelper.writeMessage(output, new byte[] { 0 });

            while (shouldRun)
            {
                // Re-use streams for more efficiency.
                byte[] data = StreamHelper.readByteArray(socket.getInputStream());
                byte[] response = _comProtocol.processInput(data);

                StreamHelper.writeMessage(output, response);
            }
        }
        catch (IOException ex)
        {
            ex.printStackTrace();
            return false;
        }

        return true;
    }

    private static void p(String s)
    {
        System.out.println("Controller "+System.currentTimeMillis() +" :" + s);
    }
}
