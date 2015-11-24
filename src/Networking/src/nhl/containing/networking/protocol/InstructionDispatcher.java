/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nhl.containing.networking.protocol;

import nhl.containing.networking.protobuf.InstructionProto;

/**
 * This interface will be implemented by dispatchers in the Simulator and controller.
 * It is used to forward the instructions to the appropriate components in the code.
 * @author Jens
 */
public interface InstructionDispatcher {
    
    public void forwardInstruction(InstructionProto.Instruction inst);
    public void forwardResponse(InstructionProto.InstructionResponse inst);
    
}
