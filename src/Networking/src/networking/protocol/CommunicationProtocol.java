/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package networking.protocol;

/**
 *
 * @author Jens
 */
public class CommunicationProtocol {
    private InstructionDispatcher dispatcher;
    
    public void setDispatcher(InstructionDispatcher _dispatcher)
    {
        this.dispatcher = _dispatcher;
    }
    
    public byte[] processInput(byte[] in)
    {
        System.out.println("Server Recieved complete message");
        byte[] response = {0};
        return response;
    }
}
