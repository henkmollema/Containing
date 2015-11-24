package controller.Tests;

import java.util.logging.Level;
import java.util.logging.Logger;
import nhl.containing.controller.networking.Server;
import nhl.containing.networking.protobuf.InstructionProto;
import nhl.containing.networking.protobuf.InstructionProto.Instruction;
import nhl.containing.networking.protobuf.InstructionProto.InstructionData;
import nhl.containing.networking.protobuf.InstructionProto.InstructionResponse;
import nhl.containing.networking.protocol.CommunicationProtocol;
import nhl.containing.networking.protocol.InstructionDispatcher;
import nhl.containing.networking.protocol.InstructionType;
import nhl.containing.simulator.networking.SimulatorClient;
import static org.junit.Assert.assertEquals;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class CommunicationTests
{
    
    Server          controller = new Server();
    SimulatorClient simulator  = new SimulatorClient();
    Thread controllerThread = new Thread(controller);
    Thread simulatorThread  = new Thread(simulator);
    
    @Rule
    public ExpectedException thrown = ExpectedException.none();
    
    @Test
    public void socketConnectionTest()
    {
        controllerThread.start();
        simulatorThread.start();
        
        try {
            Thread.sleep(100); //It takes a while for the connection to be initialized
        } catch (InterruptedException ex) {
            Logger.getLogger(CommunicationTests.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        
        assertEquals(true, controller.isConnected());
        assertEquals(true, simulator.isConnected());
        
        controller.stop();
        simulator.stop();
        
        try {
            Thread.sleep(100); //It takes a while for the connection to be stopped
        } catch (InterruptedException ex) {
            Logger.getLogger(CommunicationTests.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    
    boolean hasRecievedInstruction, hasRecievedResponse;
    @Test
    public void instructionResponseTest()
    {
        hasRecievedInstruction = false;
        hasRecievedResponse = false;
        
        //The dispatcher is used to intercept the recieved instructions and responses.
        //In this case the controller and simulator share the same instance of the dispatcher so the same
        //forwardInstruction/Response methods are called for the recieved instructions for both
        //the controller and simulator.
        InstructionDispatcher testDispatcher = new InstructionDispatcher()
        {

            @Override
            public void forwardInstruction(InstructionProto.Instruction inst) {
                
                CommunicationTests.this.hasRecievedInstruction = true;
                
                InstructionResponse response = InstructionResponse.newBuilder()
                    .setId(CommunicationProtocol.newUUID())
                    .setInstructionId(inst.getId())
                    .setData(inst.getData())
                    .build();
        
                controller.getComProtocol().sendResponse(response);
                
                System.out.println("instruction recieved");
            }

            @Override
            public void forwardResponse(InstructionProto.InstructionResponse inst) {
                CommunicationTests.this.hasRecievedResponse = true;
            }
            
        };
        
        //They share the same dispatcher..
        controller.getComProtocol().setDispatcher(testDispatcher);
        simulator.getComProtocol().setDispatcher(testDispatcher);
        
        controllerThread.start();
        simulatorThread.start();
        
        Instruction instruction = Instruction.newBuilder()
                .setId(CommunicationProtocol.newUUID())
                .setInstructionType(InstructionType.CONSOLE_COMMAND)
                .setData(InstructionData.newBuilder().setMessage("Got response!").build())
                .build();
        
        simulator.getComProtocol().sendInstruction(instruction);
        
        
        
        try {
            Thread.sleep(1000); //It takes a while for the connection to be initialized
        } catch (InterruptedException ex) {
            Logger.getLogger(CommunicationTests.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        assertEquals(true, hasRecievedInstruction);
        assertEquals(true, hasRecievedResponse);
        
        controller.stop();
        simulator.stop();
        
        try {
            Thread.sleep(100); //It takes a while for the connection to be stopped
        } catch (InterruptedException ex) {
            Logger.getLogger(CommunicationTests.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
