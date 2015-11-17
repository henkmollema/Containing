package Networking;

import Networking.SimulationItemProto.SimulationItem;
import com.google.protobuf.*;
import java.lang.reflect.*;

/**
 *
 * @author Jens
 */
public class CommunicationProtocolClient
{
    public byte[] processInput(byte[] in)
    {
        try {
            SimulationItem item = SimulationItem.parseFrom(in);

            System.out.println("Client recieved complete message");
            System.out.println("Simulation item ID: " + item.getId());
            System.out.println("Simulation item type: " + item.getType().toString());

            byte[] response = { 0 };
            return response;
        }
        catch (InvalidProtocolBufferException ex) {
            ex.printStackTrace();
        }

        byte[] response = { 0 };
        return response;
    }
}
