package networking.protocol;

import java.io.Serializable;
import networking.Proto.InstructionProto;

public class Response implements Serializable
{
    private int responseID;
    private int instructionID;
    private InstructionProto.InstructionData repsonseData;

    public Response parse(String response)
    {
        return null;
    }

    public Response createResponse(String responseMessage, InstructionProto.Instruction Instruction)
    {
        return null;
    }
}
