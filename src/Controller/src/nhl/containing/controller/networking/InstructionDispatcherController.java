package nhl.containing.controller.networking;

import java.nio.ByteBuffer;
import java.util.UUID;
import nhl.containing.controller.Simulator;
import nhl.containing.networking.protobuf.*;
import nhl.containing.networking.protobuf.InstructionProto.*;
import nhl.containing.networking.protocol.*;

/**
 *
 * @author Jens
 */
public class InstructionDispatcherController implements InstructionDispatcher {

    Simulator _sim;
    CommunicationProtocol _com;

    public InstructionDispatcherController(Simulator sim, CommunicationProtocol com) {
        _sim = sim;
        _com = com;
    }

    /**
     * dispatchInstruction(instruction) checks the instructiontype and forwards
     * the instruction to the appropriate component in the Contoller
     *
     * @param inst The Instruction to be dispatched to the system
     * @return the byte array to return to the sender
     */
    @Override
    public void forwardInstruction(InstructionProto.Instruction inst) {

        switch (inst.getInstructionType()) {
            case InstructionType.CONSOLE_COMMAND:
                String message = inst.getMessage();
                System.out.println("GOT CONSOLECOMAND: " + message);
                //rdataBuilder.setMessage(_sim.parseCommand(message));
                break;
                
            case InstructionType.CLIENT_TIME_UPDATE:
                System.out.println("GOT TIME UPDATE: " + ByteBuffer.wrap(inst.getMessageBytes().toByteArray()).getDouble());
                //Here react on the new time, call the tick function or something like that.                        
                break;

            //More instruction types here..
        }
//
//        InstructionResponse.Builder rbuilder = InstructionResponse.newBuilder();
//        rbuilder.setInstructionId(inst.getId());
//        rbuilder.setId(UUID.randomUUID().toString());
//        InstructionResponse response = rbuilder.build();
//
//        _com.sendResponse(response);

    }

    @Override
    public void forwardResponse(InstructionProto.InstructionResponse resp) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}