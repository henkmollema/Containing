package networking.protocol;

import java.io.Serializable;


/*
 * Needs a protofile!!
 */
public class Instruction implements Serializable
{
    enum instructionCode{
        
    }

    private int instructionID;

    private instructionCode intructionCode;

    private InstructionData data;

    public instructionCode getCode()
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
