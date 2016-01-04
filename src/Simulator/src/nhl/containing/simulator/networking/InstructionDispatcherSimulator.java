/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nhl.containing.simulator.networking;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.Future;
import nhl.containing.simulator.simulation.Main;
import nhl.containing.networking.protobuf.InstructionProto;
import nhl.containing.networking.protocol.InstructionType;
import nhl.containing.networking.protocol.InstructionDispatcher;
import nhl.containing.simulator.framework.Behaviour;
import nhl.containing.simulator.framework.Point3;
import nhl.containing.simulator.game.*;
import nhl.containing.simulator.gui.GUI;
import nhl.containing.simulator.world.World;

/**
 *
 * @author Jens
 */
public class InstructionDispatcherSimulator extends Behaviour implements InstructionDispatcher
{
    private Main _sim;
    private GUI m_gui;
    private World m_world;
    
    private final int SAFE_FRAMES = 2;
    private int m_safeFrames = 0;
    
    
    public Main Main() {
        return _sim == null ? (_sim = Main.instance()) : _sim;
    }
    public GUI GUI() {
        return m_gui == null ? (m_gui = GUI.instance()) : m_gui;
    }
    public World World() {
        return m_world == null ? (m_world = Main().getWorld()) : m_world;
    }
    
    
    private Queue<Future> futures;
    private List<InstructionProto.Instruction> m_queue = new ArrayList<>();
    
    
    public InstructionDispatcherSimulator(Main sim)
    {
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
        m_queue.add(inst);
    }
    
    @Override
    public void rawUpdate() {
        if (m_safeFrames < SAFE_FRAMES) {
            m_safeFrames++;
            if(m_safeFrames == SAFE_FRAMES) {
                //handleTrain(true, null, 15);
            }
                
            return;
        }
        
        while (m_queue.size() > 0) {
            handleInstruction(m_queue.get(0));
            m_queue.remove(0);
        }
    }
    
    private void handleInstruction(InstructionProto.Instruction inst) {
        InstructionProto.InstructionResponse.Builder responseBuilder = InstructionProto.InstructionResponse.newBuilder();

        switch (inst.getInstructionType())
        {
            case InstructionType.MOVE_AGV:
                handleMoveAGV(inst);
                break;
            case InstructionType.ARRIVAL_INLANDSHIP:
                handleInland(true, inst);
                break;
            case InstructionType.ARRIVAL_SEASHIP:
                handleSea(true, inst);
                break;
            case InstructionType.ARRIVAL_TRAIN:
                handleTrain(true, inst, 0);
                break;
            case InstructionType.ARRIVAL_TRUCK:
                handleLorry(true, inst);
                break;
            case InstructionType.DEPARTMENT_INLANDSHIP:
                handleInland(false, inst);
                break;
            case InstructionType.DEPARTMENT_SEASHIP:
                handleSea(false, inst);
                break;
            case InstructionType.DEPARTMENT_TRAIN:
                handleTrain(false, inst, 0);
                break;
            case InstructionType.DEPARTMENT_TRUCK:
                handleLorry(false, inst);
                break;
            case InstructionType.PLACE_CRANE:
                handlePlaceCrane(inst);
                break;
        }

        //_sim.simClient().controllerCom().sendResponse(responseBuilder.build());
    }
    
    /**
     * Handles move agv instruction
     * @param instruction instruction
     */
    private void handleMoveAGV(InstructionProto.Instruction instruction){
        int[] route = new int[instruction.getRouteCount()];
        for(int i = 0; i < instruction.getRouteCount(); i++){
            route[i] = instruction.getRoute(i);
        }
        AGV agv = Main.getAgv(instruction.getA());
        agv.path().setPath(AgvPath.getPath(route));
        //TODO: send agv ready on path finished
    }
    
    
    /**
     * Handles place crane instruction
     * @param instruction instruction
     */
    private void handlePlaceCrane(InstructionProto.Instruction instruction){
        Point3 point = new Point3(instruction.getX(), instruction.getY(), instruction.getZ());
        if(instruction.getA() < World.LORRY_BEGIN){
            //dit is een inlandship platform
            PlatformInland inlandPlatform = World().getInlandPlatforms().get(instruction.getA());
            inlandPlatform.take(point, 0);
        }else if(instruction.getA() < World.SEASHIP_BEGIN){
            //dit is een lorry platform
            PlatformLorry lorryPlatform = World().getLorryPlatforms().get(instruction.getA() - World.LORRY_BEGIN).a;
            lorryPlatform.take(point, 0);
        }else if(instruction.getA() < World.STORAGE_BEGIN){
            //dit is een seaship platform
            PlatformSea seaPlatform = World().getSeaPlatforms().get(instruction.getA() - World.SEASHIP_BEGIN);
            seaPlatform.take(point, 0);
        }else if(instruction.getA() < World.TRAIN_BEGIN){
            //dit is een storage platform
            //TODO: stuur naar platform een crane move direction (Don't send place crane ready)
        }else{
            //dit is een train platform
            PlatformTrain trainPlatform = World().getTrainPlatforms().get(instruction.getA() - World.TRAIN_BEGIN).a;
            trainPlatform.take(point, 0);
        }
    }
    
    //TODO: remove int, this is for testing
    private void handleTrain(boolean arriving,final InstructionProto.Instruction inst, int testsize) {
        if (arriving) {
            
            int size = inst == null ? testsize : inst.getContainersCount(); 
           
            p("Train arrived with " + size + " containers.");
            GUI().setContainerText("Aankomst:\nTrein\n" + size + " container(s)");
            //TODO: remove testing compatibility
            if(inst == null)
                World().getTrain().init(size);
            else
                World().getTrain().init(inst.getContainersList());
            World().getTrain().state(Vehicle.VehicleState.ToLoad, new Vehicle.VehicleStateApplied() {
                @Override public void done(Vehicle v) {
                    p("Train " + v.id() + " arrived at loading platform.");
                            // todo: load the containers from the train
                    SimulatorClient.sendTaskDone(0, 0, InstructionType.SHIPMENT_ARRIVED, inst.getMessage());
                    //v.state(Vehicle.VehicleState.ToOut);
                }
            });
        } else {
            p("Train departed with " + inst.getContainersCount() + " containers.");
            GUI().setContainerText("Vertrek:\nTrein\n" + inst.getContainersCount() + " container(s)");

            World().getTrain().state(Vehicle.VehicleState.ToLoad, new Vehicle.VehicleStateApplied()
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
        }
        
        
    }
    private void handleInland(boolean arriving, InstructionProto.Instruction inst) {
        
        if (arriving) {
            p("Inland ship arrived with " + inst.getContainersCount() + " containers.");
            GUI().setContainerText("Aankomst:\nBinnenvaartschip\n" + inst.getContainersCount() + " container(s)");

            // Let the ship arrive at the platform.
            World().getInlandShip().state(Vehicle.VehicleState.ToLoad, new Vehicle.VehicleStateApplied()
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
        } else {
            p("Inland ship departed with " + inst.getContainersCount() + " containers.");
            GUI().setContainerText("Vertrek:\nBinnenvaartschip\n" + inst.getContainersCount() + " container(s)");

            // Let the ship arrive at the platform.
            World().getInlandShip().state(Vehicle.VehicleState.ToLoad, new Vehicle.VehicleStateApplied()
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
        }
        
        
        
        
    }
    private void handleSea(boolean arriving, InstructionProto.Instruction inst) {
        
        if (arriving) {
            p("Sea ship arrived with " + inst.getContainersCount() + " containers.");
            GUI().setContainerText("Aankomst:\nZeeschip\n" + inst.getContainersCount() + " container(s)");

            // Let the ship arrive at the platform.
            World().getSeaShip().state(Vehicle.VehicleState.ToLoad, new Vehicle.VehicleStateApplied()
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
        } else {
            p("Sea ship departed with " + inst.getContainersCount() + " containers.");
            GUI().setContainerText("Vertrek:\nZeeschip\n" + inst.getContainersCount() + " container(s)");

            // Let the ship arrive at the platform.
            World().getSeaShip().state(Vehicle.VehicleState.ToLoad, new Vehicle.VehicleStateApplied()
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
        }
    }
    private void handleLorry(boolean arriving, InstructionProto.Instruction inst) {
        
        if (arriving) {
            p("Truck arrived with " + inst.getContainersCount() + " containers.");
            GUI().setContainerText("Aankomst:\nVrachtwagen\n" + inst.getContainersCount() + " container(s)");
         
        } else {
            p("Truck departed with " + inst.getContainersCount() + " containers.");
            GUI().setContainerText("Vertrek:\n vrachtwagen\n" + inst.getContainersCount() + " container(s).");
        }
        
               
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