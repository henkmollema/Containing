package nhl.containing.controller.networking;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import nhl.containing.controller.Simulator;
import nhl.containing.controller.simulation.SimulationContext;
import nhl.containing.controller.simulation.SimulatorItems;
import nhl.containing.networking.protobuf.*;
import nhl.containing.networking.protocol.*;

/**
 *
 * @author Jens
 */
public class InstructionDispatcherController implements InstructionDispatcher
{
    Simulator _sim;
    SimulatorItems _items;
    SimulationContext _context;
    CommunicationProtocol _com;
    private ExecutorService executorService;
    private Queue<Future> futures;

    public InstructionDispatcherController(Simulator sim, CommunicationProtocol com)
    {
        _sim = sim;
        _com = com;
        _items = _sim.getController().getItems();
        _context = _sim.getController().getContext();
        executorService = Executors.newSingleThreadExecutor();
        futures = new LinkedList<>();
    }

    /**
     * dispatchInstruction(instruction) checks the instructiontype and forwards
     * the instruction to the appropriate component in the Contoller
     *
     * @param inst The Instruction to be dispatched to the system
     * @return the byte array to return to the sender
     */
    @Override
    public void forwardInstruction(final InstructionProto.Instruction inst)
    {

        switch (inst.getInstructionType())
        {
            case InstructionType.CONSOLE_COMMAND:
                String message = inst.getMessage();
                System.out.println("GOT CONSOLECOMAND: " + message);
                //rdataBuilder.setMessage(_sim.parseCommand(message));
                break;

            case InstructionType.CLIENT_TIME_UPDATE:
                futures.add(executorService.submit(new Tickhandler(inst)));
                break;
            case InstructionType.CRANE_TO_AGV_READY:
                //send Move AGV instruction
                break;
            case InstructionType.AGV_READY:
                //send Container to place in storage or place on department shipping
                break;
            case InstructionType.CRANE_TO_STORAGE_READY:
                //save data to simitems
                try{
                    _items.getStorages()[inst.getA()].setContainer(_context.getContainerById(inst.getB()), inst.getX(),inst.getY(),inst.getZ());
                }catch(Exception e){e.printStackTrace();}
                break;
            //More instruction types here..
        }
    }

    @Override
    public void forwardResponse(InstructionProto.InstructionResponse resp)
    {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}