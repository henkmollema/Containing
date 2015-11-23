package networking.Messaging;

import java.io.*;
import java.lang.String;

/**
 * Helper methods for reading data from a socket input stream.
 *
 * @author henkmollema
 */
public class MessageReader
{
    public static final int START_OF_HEADING = 2;
    public static final int END_OF_TRANSMISSION = 4;

    /**
     * Reads a byte-aray from the specified buffered input stream using the
     * specified byte array output stream.
     *
     * @param input The buffered input stream.
     * @param output The byte-array output stream.
     *
     * @return The byte-array.
     *
     * @throws IOException when reading fails.
     */
    public static byte[] readByteArray(BufferedInputStream input, ByteArrayOutputStream output) throws IOException
    {
        int current;
        boolean write = false;
        while ((current = input.read()) != -1)
        {
            if (!write && current == START_OF_HEADING)
            {
                write = true;
                continue;
            }
            else if (!write && current == 0)
            {
                continue;
            }

            if (current != END_OF_TRANSMISSION)
            {
                // Add current input to buffer
                output.write(current);
            }
            else
            {
                // We received the last byte, parse the protobuf item and
                // break out of the loop.
                byte[] bytes =  output.toByteArray();
                output.reset();
                return bytes;
            }
        }

        return null;
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
        BufferedInputStream input = new BufferedInputStream(stream);
        ByteArrayOutputStream output = new ByteArrayOutputStream();

        return readByteArray(input, output);
    }

    public static String readString(InputStream stream)
    {

        return "";
    }
}
