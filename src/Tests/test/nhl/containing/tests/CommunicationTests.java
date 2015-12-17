package nhl.containing.tests;

import java.util.logging.Level;
import java.util.logging.Logger;
import nhl.containing.controller.networking.Server;
import nhl.containing.networking.protobuf.InstructionProto;
import nhl.containing.networking.protobuf.InstructionProto.Instruction;
import nhl.containing.networking.protobuf.InstructionProto.InstructionResponse;
import nhl.containing.networking.protobuf.SimulationItemProto;
import nhl.containing.networking.protocol.CommunicationProtocol;
import nhl.containing.networking.protocol.InstructionDispatcher;
import nhl.containing.networking.protocol.InstructionType;
import nhl.containing.simulator.networking.SimulatorClient;
import static org.junit.Assert.assertEquals;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class CommunicationTests {

    @Rule
    public ExpectedException thrown = ExpectedException.none();
 
    volatile int instructionsRecieved, repsonsesRecieved;

    @Test
    public void instructionResponseBatchTest() {


        final Server controller = new Server(null);
        final SimulatorClient simulator = new SimulatorClient();
        final Thread controllerThread = new Thread(controller);
        final Thread simulatorThread = new Thread(simulator);

        instructionsRecieved = 0;
        repsonsesRecieved = 0;
        int instructionsToSend = 10; //These will be sent twice, in batches of this number
        //When instructionsToSend is around 10 000 the TCP write and read buffer will fill up and hang the program TODO: Find a fix, or make sure the batches are less than 10 000 in size

        System.out.println("===== instructionResponseBatchTest =====");
        System.out.println("===== Batch sending " + (instructionsToSend * 2) + " instructions in 2 batches of " + instructionsToSend + " ============");

        //The dispatcher is used to intercept the recieved instructions and responses.
        //In this case the controller and simulator share the same instance of the dispatcher so the same
        //forwardInstruction/Response methods are called for the recieved instructions for both
        //the controller and simulator.
        InstructionDispatcher testDispatcher = new InstructionDispatcher() {
            @Override
            public void forwardInstruction(InstructionProto.Instruction inst) {

                instructionsRecieved++;


                InstructionResponse response = InstructionResponse.newBuilder()
                        .setId(CommunicationProtocol.newUUID())
                        .setInstructionId(inst.getId())
                        .setMessage("Recieved it!")
                        .build();

                simulator.controllerCom().sendResponse(response);
                System.out.println("instruction recieved: " + inst.getId());
            }

            @Override
            public void forwardResponse(InstructionProto.InstructionResponse resp) {
                repsonsesRecieved++;
                System.out.println("Response recieved for : " + resp.getInstructionId());
            }
        };

        //They share the same dispatcher..
        controller.simCom().setDispatcher(testDispatcher);
        simulator.controllerCom().setDispatcher(testDispatcher);

        controllerThread.start();
        simulatorThread.start();
        
        simulator.Start();
        
        

        for (int i = 0; i < instructionsToSend; i++) {
            Instruction instruction = Instruction.newBuilder()
                    .setId(CommunicationProtocol.newUUID())
                    .setInstructionType(InstructionType.MOVE_AGV)
                    .setMessage("Got response!")
                    .build();

            //controller.simCom().sendInstruction(instruction);
            simulator.controllerCom().sendInstruction(instruction);
        }
        System.out.println("Test: Added "+instructionsToSend+" to the instruction queue");
         try {
                Thread.sleep(100); //Wait so the queued instructions can be sent.
            } catch (InterruptedException ex) {
                Logger.getLogger(CommunicationTests.class.getName()).log(Level.SEVERE, null, ex);
            }
         
        System.out.println("Test: Added "+instructionsToSend+" to the instruction queue");
        for (int i = 0; i < instructionsToSend; i++) {
            Instruction instruction = Instruction.newBuilder()
                    .setId(CommunicationProtocol.newUUID())
                    .setInstructionType(InstructionType.MOVE_AGV)
                    .setMessage("Got response!")
                    .build();

            //controller.simCom().sendInstruction(instruction);
             simulator.controllerCom().sendInstruction(instruction);
            
        }
        instructionsToSend *= 2;
        
       

        while (instructionsRecieved != instructionsToSend || repsonsesRecieved != instructionsToSend) {
            try {
                Thread.sleep(10); //It takes a while for the connection to be stopped
            } catch (InterruptedException ex) {
                Logger.getLogger(CommunicationTests.class.getName()).log(Level.SEVERE, null, ex);
            }
        }



        assertEquals(instructionsToSend, instructionsRecieved);
        assertEquals(instructionsToSend, repsonsesRecieved);

        System.out.println("Controller Recieved " + instructionsRecieved + " instructions");
        System.out.println("Simulator Recieved " + repsonsesRecieved + " responses");
        System.out.println("Simulator has " + simulator.controllerCom().getNumPendingInst() + " pending instructions");
        System.out.println("Controller has " + simulator.controllerCom().getNumPendingResp() + " pending responses");
        System.out.println("Simulator has sent " + (simulator.controllerCom().bytesSent / 1024) + " KB");
        System.out.println("Controller has sent " + (controller.simCom().bytesSent / 1024) + " KB");

        controller.stop();
        simulator.stop();

        
    }
}
