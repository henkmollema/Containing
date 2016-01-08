/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nhl.containing.simulator.networking;

import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
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
import nhl.containing.simulator.framework.Tuple;
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
    
    private final int SAFE_FRAMES = 10;
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
            //if(m_safeFrames == SAFE_FRAMES) {
                //handleTrain(true, null, 15);
            //}
            return;
        }
        
        while (m_queue.size() > 0) {
            try
            {
                handleInstruction(m_queue.get(0));
            }
            catch(Exception e)
            {
                p("Error handling Instruction");
                e.printStackTrace();
            }
            
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
            case InstructionType.CRANE_TO_STORAGE:
                handleCraneToStorage(inst);
                break;
            case InstructionType.SHIPMENT_MOVED:
                handleShipmentMoved(inst);
                break;
        }

        //_sim.simClient().controllerCom().sendResponse(responseBuilder.build());
    }
    
    /**
     * Handles the shipment moved instruction
     * @param instruction  instruction
     */
    private void handleShipmentMoved(InstructionProto.Instruction instruction){
        GUI().setContainerText("");
        if(instruction.getA() < World.LORRY_BEGIN){
            //dit is een inlandship platform
            World().getInlandShip().state(Vehicle.VehicleState.ToOut);
        }else if(instruction.getA() < World.SEASHIP_BEGIN){
            //dit is een lorry platform
            Tuple<PlatformLorry,Vehicle> lp = World().getLorryPlatforms().get(instruction.getA() - World.LORRY_BEGIN);
        }else if(instruction.getA() < World.STORAGE_BEGIN){
            //dit is een seaship platform
            World().getSeaShip().state(Vehicle.VehicleState.ToOut);
        }else if(instruction.getA() < World.TRAIN_BEGIN){
            //dit is een storage platform
            //nothing to do
        }else{
            World().getTrain().state(Vehicle.VehicleState.ToOut);
        }
    }
    
    /**
     * Handles crane to storage instruction
     * @param instruction instruction
     */
    private void handleCraneToStorage(InstructionProto.Instruction instruction){
        p("take crane instruction");
        PlatformStorage storage = World().getStoragePlatforms().get(instruction.getA() - World.STORAGE_BEGIN);
        World().sendStoragePlace(storage, instruction.getB(), new Point3(instruction.getX(), instruction.getY(), instruction.getZ()));
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
        
        
        
        
        //TEMPFIX for agv
        AGV agv = Main.getAgv(instruction.getA() + 1);
        ParkingSpot p = Main.getParkingSpot(instruction.getB() + 1);
        Vector3f[] path = AgvPath.getPath(route,p );
        agv.path().setPath(path);
        agv.setParkingspotID(instruction.getB());
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
            PlatformStorage storagePlatform =  World().getStoragePlatforms().get(instruction.getA() - World.STORAGE_BEGIN);
            storagePlatform.take(point, instruction.getB());
            //TODO: stuur naar platform een crane move direction (Don't send place crane ready)
        }else{
            //dit is een train platform
            Tuple<PlatformTrain,Vector2f> trainPlatform = World().getTrainPlatforms().get(instruction.getA() - World.TRAIN_BEGIN);
            World().sendTrainTake(trainPlatform, point.x); //TODO whole pos?
            int test = (int)trainPlatform.a.getParkingSpot().id();
            SimulatorClient.sendTaskDone(trainPlatform.a.getPlatformID(),test, InstructionType.PLACE_CRANE_READY);
            
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
            if(inst != null){
                World().getTrain().init(inst.getContainersList());
                World().getTrain().state(Vehicle.VehicleState.ToLoad, new Vehicle.VehicleStateApplied() {
                    @Override public void done(Vehicle v) {
                        p("Train " + v.id() + " arrived at loading platform.");
                        SimulatorClient.sendTaskDone(0, 0, InstructionType.SHIPMENT_ARRIVED, inst.getMessage());             
                    }
                });
            } 
        } else {
            p("Train departed with " + inst.getContainersCount() + " containers.");
            GUI().setContainerText("Vertrek:\nTrein\n" + inst.getContainersCount() + " container(s)");

            World().getTrain().state(Vehicle.VehicleState.ToLoad, new Vehicle.VehicleStateApplied()
            {
                @Override
                public void done(Vehicle v)
                {
                    p("Train " + v.id() + " arrived at loading platform.");
                    SimulatorClient.sendTaskDone(0, 0, InstructionType.DEPARTMENT_ARRIVED, inst.getMessage());
                }
            });
        }
    }
    
    private void handleInland(boolean arriving,final InstructionProto.Instruction inst) {
        
        if (arriving) {
            p("Inland ship arrived with " + inst.getContainersCount() + " containers.");
            GUI().setContainerText("Aankomst:\nBinnenvaartschip\n" + inst.getContainersCount() + " container(s)");
            for(InstructionProto.Container container : inst.getContainersList()){
                Container c = new Container(new RFID(container));
                c.show();
                World().getInlandShip().setContainer(new Point3(inst.getX(),inst.getY(),inst.getZ()), c);
            }
            // Let the ship arrive at the platform.
            World().getInlandShip().state(Vehicle.VehicleState.ToLoad, new Vehicle.VehicleStateApplied()
            {
                @Override
                public void done(Vehicle v)
                {
                    p("Inland ship " + v.id() + " arrived at loading platform.");
                    SimulatorClient.sendTaskDone(0, 0, InstructionType.SHIPMENT_ARRIVED, inst.getMessage());
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
                    SimulatorClient.sendTaskDone(0, 0, InstructionType.DEPARTMENT_ARRIVED, inst.getMessage());
                }
            });
        }        
    }
    
    private void handleSea(boolean arriving,final InstructionProto.Instruction inst) {
        
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
                    SimulatorClient.sendTaskDone(0, 0, InstructionType.SHIPMENT_ARRIVED, inst.getMessage());
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
                    SimulatorClient.sendTaskDone(0, 0, InstructionType.DEPARTMENT_ARRIVED, inst.getMessage());
                }
            });
        }
    }
    
    private void handleLorry(boolean arriving,final InstructionProto.Instruction inst) {
        final Tuple<PlatformLorry,Vehicle> lorryTuple = World().getLorryPlatforms().get(inst.getA() - World.LORRY_BEGIN);
        if (arriving) {
            GUI().setContainerText("Aankomst:\nVrachtwagen\n" + inst.getContainersCount() + " container(s)");
            Container container = new Container(new RFID(inst.getContainers(0)));
            container.show();
            lorryTuple.b.setContainer(container);
            lorryTuple.b.state(Vehicle.VehicleState.ToLoad, new Vehicle.VehicleStateApplied() {

                @Override
                public void done(Vehicle v) {
                    p("Truck arrived with " + inst.getContainersCount() + " containers.");
                    SimulatorClient.sendTaskDone(lorryTuple.a.getPlatformID(), 0, InstructionType.SHIPMENT_ARRIVED, inst.getMessage());
                }
            });
         
        } else {
            GUI().setContainerText("Vertrek:\n vrachtwagen\n" + inst.getContainersCount() + " container(s).");
            lorryTuple.b.setContainer(null);
            lorryTuple.b.state(Vehicle.VehicleState.ToLoad, new Vehicle.VehicleStateApplied() {

                @Override
                public void done(Vehicle v) {
                    p("Truck for department arrived for " + inst.getContainersCount() + " containers.");
                    SimulatorClient.sendTaskDone(lorryTuple.a.getPlatformID(), 0, InstructionType.DEPARTMENT_ARRIVED, inst.getMessage());
                }
            });
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