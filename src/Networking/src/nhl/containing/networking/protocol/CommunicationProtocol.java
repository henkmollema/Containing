package nhl.containing.networking.protocol;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import nhl.containing.networking.protobuf.*;
import nhl.containing.networking.protobuf.InstructionProto.*;

/**
 * The communication protocol.
 * 
 * @author Jens
 */
public class CommunicationProtocol {
    private InstructionDispatcher _dispatcher;
    
    private List<Instruction> instructionQueue;
    private List<InstructionResponse> responseQueue;
    
    public CommunicationProtocol()
    {
        instructionQueue = new ArrayList<>();
        responseQueue = new ArrayList<>();
    }
    
    public static String newUUID()
    {
        return UUID.randomUUID().toString();
    }
    
    public InstructionDispatcher dispatcher()
    {
        return _dispatcher;
    }
    
    //TODO: Make thread safe
    public void sendInstruction(Instruction i)
    {
        if(i != null && instructionQueue != null)
            instructionQueue.add(i);
    }
    
    //TODO: Make thread safe
    public void sendResponse(InstructionResponse r)
    {
        if(r != null && responseQueue != null)
            responseQueue.add(r);
    }
    
    public byte[] processInput(byte[] in)
    {
        InstructionProto.datablock dbRecieved = null;
        if(in != null && in.length > 3) //If we're not getting an empty message.
        {
            try
            {
                //Try to parse the incomming data into a datablock
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
    
    
    //TODO: Make thread safe
    public InstructionProto.datablock flushDataBlock()
    {
        datablock.Builder dbBuilder = datablock.newBuilder();
        
        if(instructionQueue != null)
        {
            dbBuilder.addAllInstructions(instructionQueue);
            instructionQueue.clear();
        }
        
        if(responseQueue != null)
        {
            dbBuilder.addAllResponses(responseQueue);
            responseQueue.clear();
        }
        
        return dbBuilder.build();
    }
   
}
