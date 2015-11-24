package networking.Messaging;

import java.io.*;

/**
 * Helper methods for writing data on a socket output stream.
 *
 * @author henkmollema
 */
public class MessageWriter
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
        stream.write(MessageReader.START_OF_HEADING);
        stream.write(data);
        stream.write(MessageReader.END_OF_TRANSMISSION);
    }
}
