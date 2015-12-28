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
import nhl.containing.simulator.gui.GUI;
import nhl.containing.simulator.world.World;
import nhl.containing.simulator.world.WorldCreator;

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
        GUI gui = GUI.instance();
        World world = _sim.getWorld();
        InstructionProto.InstructionResponse.Builder responseBuilder = InstructionProto.InstructionResponse.newBuilder();

        switch (inst.getInstructionType())
        {
            case InstructionType.MOVE_AGV:
                p("Got MOVE AGV instruction");
                break;

            case InstructionType.ARRIVAL_INLANDSHIP:
                p("Inland ship arrived with " + inst.getContainersCount() + " containers.");
                gui.setContainerText("Aankomst:\nBinnenvaartschip\n" + inst.getContainersCount() + " container(s)");

                // Let the ship arrive at the platform.
                world.getInlandShip().state(Vehicle.VehicleState.ToLoad, new Vehicle.VehicleStateApplied()
                {
                    @Override
                    public void done(Vehicle v)
                    {
                        p("Inland ship " + v.id() + " arrived at loading platform.");

                        //
                        // todo: get the containers from the ship
                        //

                        // Depart the ship.
                        v.state(Vehicle.VehicleState.ToOut);
                    }
                });
                break;

            case InstructionType.ARRIVAL_SEASHIP:
                p("Sea ship arrived with " + inst.getContainersCount() + " containers.");
                gui.setContainerText("Aankomst:\nZeeschip\n" + inst.getContainersCount() + " container(s)");

                // Let the ship arrive at the platform.
                world.getSeaShip().state(Vehicle.VehicleState.ToLoad, new Vehicle.VehicleStateApplied()
                {
                    @Override
                    public void done(Vehicle v)
                    {
                        p("Sea ship " + v.id() + " arrived at loading platform.");

                        //
                        // todo: get the containers from the ship
                        //

                        // Depart the ship.
                        v.state(Vehicle.VehicleState.ToOut);
                    }
                });
                break;

            case InstructionType.ARRIVAL_TRAIN:
                p("Train arrived with " + inst.getContainersCount() + " containers.");
                gui.setContainerText("Aankomst:\nTrein\n" + inst.getContainersCount() + " container(s)");

                // Re-init the train with amount of containers.
                p("Init train with " + inst.getContainersCount() + " containers");

                // todo: exception at this line:
                //world.getTrain().init(inst.getContainersCount());

                world.getTrain().state(Vehicle.VehicleState.ToLoad, new Vehicle.VehicleStateApplied()
                {
                    @Override
                    public void done(Vehicle v)
                    {
                        p("Train " + v.id() + " arrived at loading platform.");

                        //
                        // todo: load the containers from the train
                        //

                        // Train departs.
                        v.state(Vehicle.VehicleState.ToOut);
                    }
                });

                break;

            case InstructionType.ARRIVAL_TRUCK:
                p("Truck arrived with " + inst.getContainersCount() + " containers.");
                gui.setContainerText("Aankomst:\nVrachtwagen\n" + inst.getContainersCount() + " container(s)");
                break;

            case InstructionType.DEPARTMENT_INLANDSHIP:
                p("Inland ship departed with " + inst.getContainersCount() + " containers.");
                gui.setContainerText("Vertrek:\nBinnenvaartschip\n" + inst.getContainersCount() + " container(s)");

                // Let the ship arrive at the platform.
                world.getInlandShip().state(Vehicle.VehicleState.ToLoad, new Vehicle.VehicleStateApplied()
                {
                    @Override
                    public void done(Vehicle v)
                    {
                        p("Inland ship " + v.id() + " arrived at loading platform.");

                        //
                        // todo: get the containers from the ship
                        //

                        // Depart the ship.
                        v.state(Vehicle.VehicleState.ToOut);
                    }
                });
                break;

            case InstructionType.DEPARTMENT_SEASHIP:
                p("Sea ship departed with " + inst.getContainersCount() + " containers.");
                gui.setContainerText("Vertrek:\nZeeschip\n" + inst.getContainersCount() + " container(s)");

                // Let the ship arrive at the platform.
                world.getSeaShip().state(Vehicle.VehicleState.ToLoad, new Vehicle.VehicleStateApplied()
                {
                    @Override
                    public void done(Vehicle v)
                    {
                        p("Sea ship " + v.id() + " arrived at loading platform.");

                        //
                        // todo: load the ship with outgoing containers
                        //

                        // Depart the ship.
                        v.state(Vehicle.VehicleState.ToOut);
                    }
                });
                break;

            case InstructionType.DEPARTMENT_TRAIN:
                p("Train departed with " + inst.getContainersCount() + " containers.");
                gui.setContainerText("Vertrek:\nTrein\n" + inst.getContainersCount() + " container(s)");

                world.getTrain().state(Vehicle.VehicleState.ToLoad, new Vehicle.VehicleStateApplied()
                {
                    @Override
                    public void done(Vehicle v)
                    {
                        p("Train " + v.id() + " arrived at loading platform.");

                        //
                        // todo: load the containers onto the train
                        //

                        // Depart the train.
                        v.state(Vehicle.VehicleState.ToOut);
                    }
                });

                break;

            case InstructionType.DEPARTMENT_TRUCK:
                p("Truck departed with " + inst.getContainersCount() + " containers.");
                gui.setContainerText("Vertrek:\n vrachtwagen\n" + inst.getContainersCount() + " container(s).");
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