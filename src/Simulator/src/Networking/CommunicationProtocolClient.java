package Networking;

import com.google.protobuf.*;

/**
 *
 * @author Jens
 */
public class CommunicationProtocolClient
{
    public byte[] processInput(byte[] in)
    {
        try {
            System.out.println("Client recieved complete message. Length: " + in.length);

            byte[] response = { 0 };
            return response;
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }

        byte[] response = { 0 };
        return response;
    }
}
