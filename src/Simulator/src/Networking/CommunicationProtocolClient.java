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
            // The last byte of the input is missing. Copy to a new one and add the zero.
            byte[] test = (byte[])Array.newInstance(byte.class, in.length + 1);
            System.arraycopy(in, 0, test, 0, test.length - 1);
            test[39] = 0;

            SimulationItem item = SimulationItem.parseFrom(test);

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
