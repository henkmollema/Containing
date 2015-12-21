/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nhl.containing.simulator.networking;

import java.nio.ByteBuffer;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.Future;
import nhl.containing.simulator.simulation.Main;
import nhl.containing.networking.protobuf.InstructionProto;
import nhl.containing.networking.protocol.InstructionType;
import nhl.containing.networking.protocol.InstructionDispatcher;
import nhl.containing.simulator.game.*;
import nhl.containing.simulator.world.World;

/**
 *
 * @author Jens
 */
public class InstructionDispatcherSimulator implements InstructionDispatcher
{
    Main _sim;
    private Queue<Future> futures;

    public InstructionDispatcherSimulator(Main sim)
    {
        _sim = sim;
        futures = new LinkedList<>();
    }

    /**
     * dispatchInstruction(instruction) checks the instructiontype and forwards
     * the instruction to the appropriate component in the simulator
     *
     * @param inst The Instruction to be dispatched to the system
     * @return the byte array to return to the sender
     */
    @Override
    public void forwardInstruction(InstructionProto.Instruction inst)
    {
        World world = _sim.getWorld();
        InstructionProto.InstructionResponse.Builder responseBuilder = InstructionProto.InstructionResponse.newBuilder();

        switch (inst.getInstructionType())
        {
            case InstructionType.MOVE_AGV:
                p("Got MOVE AGV instruction");
                break;

            case InstructionType.ARRIVAL_INLANDSHIP:
                p("Inland ship arrived with " + inst.getContainersCount() + " containers.");
                break;

            case InstructionType.ARRIVAL_SEASHIP:
                p("Sea ship arrived with " + inst.getContainersCount() + " containers.");
                break;

            case InstructionType.ARRIVAL_TRAIN:
                p("Train arrived with " + inst.getContainersCount() + " containers.");
                world.getTrain().state(Vehicle.VehicleState.ToLoad, new Vehicle.VehicleStateApplied()
                {
                    @Override
                    public void done(Vehicle v)
                    {
                        // Move train back when arrived.
                        p("Train " + v.id() + " arrived at loading platform.");
                        v.state(Vehicle.VehicleState.ToOut);
                    }
                });

                break;

            case InstructionType.ARRIVAL_TRUCK:
                p("Trucks arrived with " + inst.getContainersCount() + " containers.");
                break;

            case InstructionType.DEPARTMENT_INLANDSHIP:
                p("Inland ship departed with " + inst.getContainersCount() + " containers.");
                break;

            case InstructionType.DEPARTMENT_SEASHIP:
                p("Sea ship departed with " + inst.getContainersCount() + " containers.");
                break;

            case InstructionType.DEPARTMENT_TRAIN:
                p("Train departed with " + inst.getContainersCount() + " containers.");
                break;

            case InstructionType.DEPARTMENT_TRUCK:
                p("Trucks departed with " + inst.getContainersCount() + " containers.");
                break;
        }

        //_sim.simClient().controllerCom().sendResponse(responseBuilder.build());
    }

    private static void p(String s)
    {
        System.out.println("[" + System.currentTimeMillis() + "] Sim: " + s);
    }

    @Override
    public void forwardResponse(InstructionProto.InstructionResponse resp)
    {
        System.out.println("Recieved response: " + resp.getMessage());
    }
}