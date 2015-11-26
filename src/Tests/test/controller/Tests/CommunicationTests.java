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

public class CommunicationTests {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void socketConnectionTest() {
        System.out.println("===== socketConnectionTest ======");
        final Server controller = new Server();
        final SimulatorClient simulator = new SimulatorClient();
        final Thread controllerThread = new Thread(controller);
        final Thread simulatorThread = new Thread(simulator);

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

        while (controller.isConnected() || simulator.isConnected()) {
            try {
                Thread.sleep(100); //It takes a while for the connection to be stopped
            } catch (InterruptedException ex) {
                Logger.getLogger(CommunicationTests.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    boolean hasRecievedInstruction, hasRecievedResponse;

    @Test
    public void instructionResponseTest() {

        System.out.println("===== instructionResponseTest ======");

        final Server controller = new Server();
        final SimulatorClient simulator = new SimulatorClient();
        final Thread controllerThread = new Thread(controller);
        final Thread simulatorThread = new Thread(simulator);

        hasRecievedInstruction = false;
        hasRecievedResponse = false;

        //The dispatcher is used to intercept the recieved instructions and responses.
        //In this case the controller and simulator share the same instance of the dispatcher so the same
        //forwardInstruction/Response methods are called for the recieved instructions for both
        //the controller and simulator.
        InstructionDispatcher testDispatcher = new InstructionDispatcher() {
            @Override
            public void forwardInstruction(InstructionProto.Instruction inst) {

                CommunicationTests.this.hasRecievedInstruction = true;

                InstructionResponse response = InstructionResponse.newBuilder()
                        .setId(CommunicationProtocol.newUUID())
                        .setInstructionId(inst.getId())
                        .setData(inst.getData())
                        .build();

                controller.getComProtocol().sendResponse(response);

                System.out.println("instruction recieved: "+ inst.getId());
            }

            @Override
            public void forwardResponse(InstructionProto.InstructionResponse resp) {
                CommunicationTests.this.hasRecievedResponse = true;
                System.out.println("response recieved for: "+ resp.getInstructionId());
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
            Thread.sleep(100); //It takes a while for the connection to be initialized
        } catch (InterruptedException ex) {
            Logger.getLogger(CommunicationTests.class.getName()).log(Level.SEVERE, null, ex);
        }

        assertEquals(true, hasRecievedInstruction);
        assertEquals(true, hasRecievedResponse);

        controller.stop();
        simulator.stop();

        while (controller.isConnected() || simulator.isConnected()) {
            try {
                Thread.sleep(100); //It takes a while for the connection to be stopped
            } catch (InterruptedException ex) {
                Logger.getLogger(CommunicationTests.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }
    int instructionsRecieved, repsonsesRecieved;

    @Test
    public void instructionResponseBatchTest() {


        final Server controller = new Server();
        final SimulatorClient simulator = new SimulatorClient();
        final Thread controllerThread = new Thread(controller);
        final Thread simulatorThread = new Thread(simulator);

        instructionsRecieved = 0;
        repsonsesRecieved = 0;
        int instructionsToSend = 100; //These will be sent twice, in batches of this number
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
                        .setData(inst.getData())
                        .build();

                controller.getComProtocol().sendResponse(response);
                System.out.println("instruction recieved: " + inst.getId());
            }

            @Override
            public void forwardResponse(InstructionProto.InstructionResponse resp) {
                repsonsesRecieved++;
                System.out.println("Response recieved for : " + resp.getInstructionId());
            }
        };

        //They share the same dispatcher..
        controller.getComProtocol().setDispatcher(testDispatcher);
        simulator.getComProtocol().setDispatcher(testDispatcher);

        controllerThread.start();
        simulatorThread.start();

        for (int i = 0; i < instructionsToSend; i++) {
            Instruction instruction = Instruction.newBuilder()
                    .setId(CommunicationProtocol.newUUID())
                    .setInstructionType(InstructionType.CONSOLE_COMMAND)
                    .setData(InstructionData.newBuilder().setMessage("Got response!").build())
                    .build();

            simulator.getComProtocol().sendInstruction(instruction);
        }

        for (int i = 0; i < instructionsToSend; i++) {
            Instruction instruction = Instruction.newBuilder()
                    .setId(CommunicationProtocol.newUUID())
                    .setInstructionType(InstructionType.CONSOLE_COMMAND)
                    .setData(InstructionData.newBuilder().setMessage("Got response!").build())
                    .build();

            simulator.getComProtocol().sendInstruction(instruction);
             
            instructionsToSend *= 2;
        }

        

        while (instructionsRecieved != instructionsToSend || repsonsesRecieved != instructionsToSend) {
            System.out.println("Controller Recieved " + instructionsRecieved + " instructions");
            System.out.println("Simulator Recieved " + repsonsesRecieved + " responses");
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

        controller.stop();
        simulator.stop();

        while (controller.isConnected() || simulator.isConnected()) {
            try {
                Thread.sleep(100); //It takes a while for the connection to be stopped
            } catch (InterruptedException ex) {
                Logger.getLogger(CommunicationTests.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
