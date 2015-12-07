/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nhl.containing.networking.messaging;

import java.io.*;

/**
 *
 * @author henkmollema
 */
public class StreamHelper
{
    /**
     * Writes the specified data to the specified output stream.
     *
     * @param stream The output stream.
     * @param data The data to write.
     *
     * @throws IOException when the writing failed.
     */
    public static void writeMessage(OutputStream stream, byte[] data) throws IOException
    {
        DataOutputStream output = new DataOutputStream(stream);
        output.writeInt(data.length);
        output.write(data);
    }
    
    /**
     * Reads a byte-array from the specified input stream.
     *
     * @param stream The input stream.
     *
     * @return A byte-array.
     *
     * @throws IOException when reading fails.
     */
    public static byte[] readByteArray(InputStream stream) throws IOException
    {
        DataInputStream input = new DataInputStream(stream);
        int length = input.readInt();

        if (length > 0)
        {
            byte[] message = new byte[length];
            input.readFully(message, 0, length);
            
            return message;
        }
        
        return new byte[] { 0 } ;
    }
}
