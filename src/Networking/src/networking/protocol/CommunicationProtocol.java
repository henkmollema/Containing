/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package networking.protocol;

import java.util.ArrayList;
import java.util.List;
import networking.Proto.InstructionProto.*;
import networking.Proto.*;

/**
 *
 * @author Jens
 */
public class CommunicationProtocol {
    private InstructionDispatcher _dispatcher;
    
    private List<Instruction> instructionQueue;
    private List<InstructionResponse> responseQueue;
    
    public CommunicationProtocol()
    {
        instructionQueue = new ArrayList<Instruction>();
        responseQueue = new ArrayList<InstructionResponse>();
    }
    
    public void sendInstruction(Instruction i)
    {
        instructionQueue.add(i);
    }
    
    public void sendResponse(InstructionResponse r)
    {
        responseQueue.add(r);
    }
    
    public byte[] processInput(byte[] in)
    {
        InstructionProto.datablock dbRecieved = null;
        if(in.length > 3)
        {
            try
            {
                dbRecieved = InstructionProto.datablock.parseFrom(in);
            }
            catch(Exception ex)
            {
                ex.printStackTrace();
            }

            if(dbRecieved != null)
            {
                //Instructions and responses are forwarded into the simulator
                for(Instruction i : dbRecieved.getInstructionsList())
                {
                    this._dispatcher.forwardInstruction(i);
                }

                for(InstructionResponse r : dbRecieved.getResponsesList())
                {
                    this._dispatcher.forwardResponse(r);
                }
            }
        }
        
        
        
       
        //The simulator will have added new instructions/responses to the queues
        //flushDataBlock will generate a datablock object using the Queues and clear the local copies.
        return flushDataBlock().toByteArray();
    }
    
    /** setDispatcher sets the dispatcher used to forward instructions into the system.
     * This is set in the Simulator class, as the instance of the dispatcher 
     * lives there.
     */
    public void setDispatcher(InstructionDispatcher dispatcher)
    {
        this._dispatcher = dispatcher;
    }
    
    public InstructionProto.datablock flushDataBlock()
    {
        datablock db = datablock.newBuilder()
                .addAllInstructions(instructionQueue)
                .addAllResponses(responseQueue)
                .build();
        
        instructionQueue.clear();
        responseQueue.clear();
        
        //System.out.print("flushed");
        
        return db;
    }
   
}
