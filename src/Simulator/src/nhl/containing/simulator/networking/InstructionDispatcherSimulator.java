/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nhl.containing.simulator.networking;

import java.nio.ByteBuffer;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.Future;
import nhl.containing.simulator.simulation.Main;
import nhl.containing.networking.protobuf.InstructionProto;
import nhl.containing.networking.protocol.InstructionType;
import nhl.containing.networking.protocol.InstructionDispatcher;

/**
 *
 * @author Jens
 */
public class InstructionDispatcherSimulator implements InstructionDispatcher {

    Main _sim;
    private Queue<Future> futures;

    public InstructionDispatcherSimulator(Main sim) {
        _sim = sim;
        futures = new LinkedList<>();
    }

    /**
     * dispatchInstruction(instruction) checks the instructiontype and forwards
     * the instruction to the appropriate component in the simulator
     *
     * @param inst The Instruction to be dispatched to the system
     * @return the byte array to return to the sender
     */
    @Override
    public void forwardInstruction(InstructionProto.Instruction inst) {
        InstructionProto.InstructionResponse.Builder responseBuilder = InstructionProto.InstructionResponse.newBuilder();

        switch (inst.getInstructionType()) {
            case InstructionType.MOVE_AGV:
                System.out.println("Got MOVE AGV instruction");

                break;
                
            case InstructionType.ARRIVAL_INLANDSHIP:
            case InstructionType.ARRIVAL_SEASHIP:
            case InstructionType.ARRIVAL_TRAIN:
            case InstructionType.ARRIVAL_TRUCK:
                p("Received arrival of " + inst.getContainersCount());
                break;
                
            case InstructionType.CLIENT_TIME_UPDATE:
                System.out.println("SENT TIME UPDATE: " + ByteBuffer.wrap(inst.getMessageBytes().toByteArray()).getFloat());
                //Here react on the new time, call the tick function or something like that.
                futures.add(Main.executorService().submit(new Runnable()
                {
                    @Override
                    public void run() {
                        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                    }
                }));
                break;
            //More instruction types here..
        }

        _sim.simClient().controllerCom().sendResponse(responseBuilder.build());
    }
    
    private static void p(String s)
    {
        System.out.println("[" + System.currentTimeMillis() + "] Sim: " + s);
    }

    @Override
    public void forwardResponse(InstructionProto.InstructionResponse resp) {
        System.out.println("Recieved response: " + resp.getMessage());
    }
}