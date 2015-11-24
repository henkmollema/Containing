package nhl.containing.controller.networking;

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
    
    public InstructionDispatcherController(Simulator sim)
    {
        _sim = sim;
    }
    
    
    /**
     * dispatchInstruction(instruction) checks the instructiontype 
     * and forwards the instruction to the appropriate component in the simulator
     * 
     * @param inst The Instruction to be dispatched to the system
     * @return the byte array to return to the sender
     */
    @Override
    public void forwardInstruction(InstructionProto.Instruction inst)
     {
         
         InstructionData.Builder rdataBuilder = InstructionData.newBuilder();
         
         switch(inst.getInstructionType())
         {
             case InstructionType.CONSOLE_COMMAND:
                 String message = inst.getData().getMessage();
                    System.out.println("GOT CONSOLECOMAND: " + message);
                    rdataBuilder.setMessage(_sim.parseCommand(message));
                 break;
                 
                 //More instruction types here..
         }
         
         InstructionResponse.Builder rbuilder = InstructionResponse.newBuilder();
         rbuilder.setData(rdataBuilder.build());
         rbuilder.setInstructionId(inst.getId());
         rbuilder.setId(UUID.randomUUID().toString());
         InstructionResponse response = rbuilder.build();
         
         _sim.communication().sendResponse(response);
         
     }

    @Override
    public void forwardResponse(InstructionProto.InstructionResponse resp) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}