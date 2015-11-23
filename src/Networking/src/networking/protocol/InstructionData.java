package networking.protocol;

import java.io.Serializable;
import java.util.Date;

public class InstructionData implements Serializable
{
    private Date startTime;
    private int a;
    private int b;
    private String message;

    public InstructionData parse(String instructionData)
    {
        return null;
    }

    public InstructionData createInstructionData(String data)
    {
        return null;
    }
}
