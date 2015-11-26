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
    
    boolean safeMode = true; //If safemode is enabled, instructions and resoponses will check for acknowelegedment from the reciever
    
    final List<String> pendingAcknowelegeInst;
    final List<String> pendingAcknowelegeResp;
    
    final List<String> recievedInstructionUUIDs;
    final List<String> recievedResponseUUIDs;

    public CommunicationProtocol() {
        instructionQueue = new ArrayList<>();
        responseQueue = new ArrayList<>();
        
        pendingAcknowelegeInst = new ArrayList<>();
        pendingAcknowelegeResp = new ArrayList<>();
                
        recievedInstructionUUIDs = new ArrayList<>();
        recievedResponseUUIDs = new ArrayList<>();
                
    }
    
    public int getNumPendingInst()
    {
        return pendingAcknowelegeInst.size();
    }
    
    public int getNumPendingResp()
    {
        return pendingAcknowelegeResp.size();
    }

    public static String newUUID() {
        return UUID.randomUUID().toString();
    }

    public InstructionDispatcher dispatcher() {
        return _dispatcher;
    }

    
    public void sendInstruction(Instruction i) {
        if (i != null && instructionQueue != null) {
            synchronized (instructionQueue) {
                instructionQueue.add(i);
            }
        }

    }

    public void sendResponse(InstructionResponse r) {
        if (r != null && responseQueue != null) {
            synchronized (responseQueue) {
                responseQueue.add(r);
            }
        }

    }

    /** processInput(in) processes the message recieved over the network
     * It tries to parse the in byte array back into a datablock and will loop through the instructions and repsonses in it.
     * @param in the byte array of the message recieved i.e. the byte array between START_OF_HEADING and END_OF_TRANSMISSION
     * @return 
     */
    public byte[] processInput(byte[] in) {
        InstructionProto.datablock dbRecieved = null;
        if (in != null && in.length > 3) //If we're not getting an empty message.
        {
            try {
                //Try to parse the incomming data into a datablock
                dbRecieved = InstructionProto.datablock.parseFrom(in);
            } catch (Exception ex) {
                ex.printStackTrace();
            }

            if (dbRecieved != null) {
                //Instructions and responses are forwarded into the simulator, 

                for (Instruction i : dbRecieved.getInstructionsList()) {
                    if(safeMode) recievedInstructionUUIDs.add(i.getId());
                    this._dispatcher.forwardInstruction(i);
                    
                }

                for (InstructionResponse r : dbRecieved.getResponsesList()) {
                    if(safeMode) recievedInstructionUUIDs.add(r.getId());
                    this._dispatcher.forwardResponse(r);
                    
                }
                if(safeMode)
                {
                    for(String uuid : dbRecieved.getRecievedInstructionUUIDsList())
                    {
                        pendingAcknowelegeInst.remove(uuid);
                    }

                    for(String uuid : dbRecieved.getRecievedResponseUUIDsList())
                    {
                        pendingAcknowelegeResp.remove(uuid);
                    }
                }
                
            }
        }
        

        //The simulator will have added new instructions/responses to the queues
        return flushDataBlock().toByteArray();
    }

    /**
     * setDispatcher sets the dispatcher used to forward instructions into the
     * system. This is set in the Simulator class, as the instance of the
     * dispatcher lives there.
     */
    public void setDispatcher(InstructionDispatcher dispatcher) {
        this._dispatcher = dispatcher;
    }

    /** flushDataBlock() will generate a datablock object using the Instruction and Response Queues and clear the local copies.
     * A datablock contains a list of Instructions and Queues, these are sent over the network and processed on the other side (:O)
     * This is used as the return value of processInput(), after incomming messages have been processed.
     * @return datablock object to be sent over network
     */
    public InstructionProto.datablock flushDataBlock() {
        datablock.Builder dbBuilder = datablock.newBuilder();



        if (instructionQueue != null) {
            synchronized (instructionQueue) {
                for(Instruction inst : instructionQueue)
                {
                    dbBuilder.addInstructions(inst);
                     if(safeMode) pendingAcknowelegeInst.add(inst.getId());
                }
                
                
                instructionQueue.clear();
            }
        }


        if (responseQueue != null) {
            synchronized (responseQueue) {
                for(InstructionResponse resp : responseQueue)
                {
                    dbBuilder.addResponses(resp);
                    if(safeMode) pendingAcknowelegeResp.add(resp.getId());
                }
                responseQueue.clear();
            }
        }
        
        //TODO: MAKE THREAD SAFE
        if(safeMode)
        {
            dbBuilder.addAllRecievedInstructionUUIDs(recievedInstructionUUIDs);
            dbBuilder.addAllRecievedInstructionUUIDs(recievedResponseUUIDs);
        }
        

        return dbBuilder.build();
    }
}