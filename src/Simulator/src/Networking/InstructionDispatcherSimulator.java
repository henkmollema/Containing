/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Networking;

import Simulation.Main;
import networking.Proto.InstructionProto;
import networking.protocol.InstructionType;
import networking.protocol.InstructionDispatcher;

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
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}