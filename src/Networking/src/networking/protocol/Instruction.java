package networking.protocol;

import java.io.Serializable;


/*
 * Needs a protofile!!
 */
public class Instruction implements Serializable
{
    private int instructionID;
    private InstructionCode intructionCode;
    private InstructionData data;

    public InstructionCode getCode()
    {
        return this.intructionCode;
    }

    public int getID()
    {
        return this.instructionID;
    }

    public Instruction parse(String instruction)
    {
        return null;
    }
}
