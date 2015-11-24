/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nhl.containing.simulator.networking;

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
    
    public InstructionDispatcherSimulator(Main sim)
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
         InstructionProto.InstructionResponse.Builder responseBuilder = InstructionProto.InstructionResponse.newBuilder();
         
         switch(inst.getInstructionType())
         {
             case InstructionType.MOVE_AGV:
                    System.out.println("Got MOVE AGV instruction");
                    
                 break;
                 
                 //More instruction types here..
         } 
         
         _sim.simClient().getComProtocol().sendResponse(responseBuilder.build());
     }

    @Override
    public void forwardResponse(InstructionProto.InstructionResponse resp) {
        System.out.println("Recieved response: "+resp.getData().getMessage());
    }
}