package nhl.containing.controller.networking;

import java.util.*;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import nhl.containing.controller.Simulator;
import nhl.containing.controller.simulation.*;
import nhl.containing.networking.protobuf.*;
import nhl.containing.networking.protobuf.InstructionProto.*;
import nhl.containing.networking.protocol.*;

/**
 *
 * @author Jens
 */
public class InstructionDispatcherController implements InstructionDispatcher
{
    Simulator _sim;
    CommunicationProtocol _com;
    private ExecutorService executorService;
    private Queue<Future> futures;

    public InstructionDispatcherController(Simulator sim, CommunicationProtocol com)
    {
        _sim = sim;
        _com = com;
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
                futures.add(executorService.submit(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        // Time sent by the client.
                        long time = inst.getTime();
                        System.out.println("Received client time: " + time);

                        // Get the first shipment from the simulation context.
                        SimulationContext context = Simulator.instance().getController().getContext();
                        Shipment first = context.getFirstShipment();

                        // Determine the current date/time.
                        Date date = new Date(first.date.getTime() + time);
                        
                        // Get shipments by date.
                        //Shipment[] shipments = context.getShipmentsByDate(date).toArray(new Shipment[0]);
                        for (Shipment s : context.getShipmentsByDate(date))
                        {
                            s.processed = true;
                            System.out.println("Process " + s.key);
                            Simulator.instance().server().simCom().sendInstruction(inst);
                            // todo: create proto for shipment.
                        }
                    }
                }));
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