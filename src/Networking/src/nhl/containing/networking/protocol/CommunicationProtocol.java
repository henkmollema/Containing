package nhl.containing.networking.protocol;

import java.util.ArrayList;
import java.util.LinkedList;
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
    
    public static final int PROTOCOL_VERSION = 1;
    public static int DATABLOCK_SIZE = 500; //max instructions and responses per datablock
    
    public long bytesSent = 0;

    private InstructionDispatcher _dispatcher;
    private final LinkedList<Instruction> instructionQueue;
    private final LinkedList<InstructionResponse> responseQueue;

    volatile boolean safeMode = false; //If safemode is enabled, instructions and resoponses will check for acknowelegedment from the reciever
    
    final List<String> pendingAcknowelegeInst;
    final List<String> pendingAcknowelegeResp;
    
    final List<String> recievedInstructionUUIDs;
    final List<String> recievedResponseUUIDs;

    public CommunicationProtocol() {
        instructionQueue = new LinkedList<>();
        responseQueue = new LinkedList<>();
        
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
        datablockSimulator dbRecieved = null;
        if (in != null && in.length > 2) //If we're not getting an empty message.
        {
            try {
                //Try to parse the incomming data into a datablock
                dbRecieved = datablockSimulator.parseFrom(in);
            } catch (Exception ex) {
                System.out.println("Failed to read datablock");
            }

            if (dbRecieved != null && this._dispatcher != null) {
                //Instructions and responses are forwarded into the simulator, 

                for (Instruction i : dbRecieved.getInstructionsList()) {
                    if(safeMode) recievedInstructionUUIDs.add(i.getId());
                    try{
                        this._dispatcher.forwardInstruction(i);
                    }
                    catch(Exception e)
                    {
                       System.out.println("Exception in handling instruction");
                       e.printStackTrace();
                    }
                    
                }

                for (InstructionResponse r : dbRecieved.getResponsesList()) {
                    if(safeMode) recievedInstructionUUIDs.add(r.getId());
                    try{
                        this._dispatcher.forwardResponse(r);
                    }
                    catch(Exception e)
                    {
                        System.out.println("Exception in handling response");
                       e.printStackTrace();
                    }
                    
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
        return flushDataBlock();
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
    public byte[] flushDataBlock() {
        datablockSimulator.Builder dbBuilder = datablockSimulator.newBuilder();



        if (instructionQueue != null) {
            synchronized (instructionQueue) {
                for(int i = 0; i < Math.min(instructionQueue.size(), DATABLOCK_SIZE); i++)
                {
                    Instruction inst = instructionQueue.removeFirst();
                    dbBuilder.addInstructions(inst);
                     if(safeMode) pendingAcknowelegeInst.add(inst.getId());
                }
                
                
               
            }
        }

        if (responseQueue != null) {
            synchronized (responseQueue) {
                for(int i = 0; i < Math.min(responseQueue.size(), DATABLOCK_SIZE); i++)
                {
                    InstructionResponse resp = responseQueue.removeFirst();
                    dbBuilder.addResponses(resp);
                     
                     if(safeMode) pendingAcknowelegeResp.add(resp.getId());
                }
            }
        }
        
        //TODO: MAKE THREAD SAFE
        if(safeMode)
        {
            dbBuilder.addAllRecievedInstructionUUIDs(recievedInstructionUUIDs);
            dbBuilder.addAllRecievedInstructionUUIDs(recievedResponseUUIDs);
        }
        
        byte[] ba = dbBuilder.build().toByteArray();
        bytesSent +=  ba.length;
        return ba;
    }
}
