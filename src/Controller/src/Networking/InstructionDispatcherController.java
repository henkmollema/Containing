/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Networking;

import networking.protocol.Instruction;
import networking.protocol.InstructionDispatcher;

/**
 *
 * @author Jens
 */
public class InstructionDispatcherController implements InstructionDispatcher {
     
    
    @Override
    public boolean dispatchInstruction(Instruction inst)
     {
         switch(inst.getCode())
         {
             //Switch through the instruction code..
         }
         
         return false;
     }
}