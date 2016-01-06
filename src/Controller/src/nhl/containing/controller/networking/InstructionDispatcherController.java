package nhl.containing.controller.networking;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import nhl.containing.controller.PathFinder;
import nhl.containing.controller.Point3;
import nhl.containing.controller.Simulator;
import nhl.containing.controller.simulation.*;
import nhl.containing.networking.protobuf.*;
import nhl.containing.networking.protocol.*;

/**
 *
 * @author Jens
 */
public class InstructionDispatcherController implements InstructionDispatcher {

    Simulator _sim;
    SimulatorItems _items;
    SimulationContext _context;
    CommunicationProtocol _com;
    private ExecutorService executorService;
    private Queue<Future> futures;
    private Queue<SavedInstruction> m_agvInstructions = new LinkedList<>();

    public InstructionDispatcherController(Simulator sim, CommunicationProtocol com) {
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
     */
    @Override
    public void forwardInstruction(final InstructionProto.Instruction inst) {

        switch (inst.getInstructionType()) {
            case InstructionType.CONSOLE_COMMAND:
                String message = inst.getMessage();
                System.out.println("GOT CONSOLECOMAND: " + message);
                //rdataBuilder.setMessage(_sim.parseCommand(message));
                break;
            case InstructionType.CLIENT_TIME_UPDATE:
                futures.add(executorService.submit(new Tickhandler(inst)));
                break;
            case InstructionType.SHIPMENT_ARRIVED:
                shipmentArrived(inst);
                break;
            case InstructionType.CRANE_TO_AGV_READY:
                craneToAGVReady(inst);
                break;
            case InstructionType.AGV_READY:
                agvReady(inst);
                break;
            case InstructionType.PLACE_CRANE_READY:
                placeCraneReady(inst);
                break;
            case InstructionType.CRANE_TO_STORAGE_READY:
                craneToStorageReady(inst);
                break;
            //More instruction types here..
        }
    }
    
    private void craneToStorageReady(InstructionProto.Instruction instruction){
        Storage storage = _items.getStorages()[instruction.getA() - SimulatorItems.STORAGE_BEGIN];
        storage.unsetBusy();
        Parkingspot spot = storage.getParkingspots().get(instruction.getB());
        AGV agv = spot.getAGV();
        spot.removeAGV();
        if(m_agvInstructions.isEmpty()){
            //TODO: send back to staging aarea
            agv.stop();
        }else{
            SavedInstruction inst = m_agvInstructions.poll();
            moveAGV(agv, inst.getPlatform(), inst.getParkingspot());
        }
    }
    

    /**
     * Handles shipment arrived
     *
     * @param instruction instruction
     */
    private void shipmentArrived(InstructionProto.Instruction instruction) {
        if (_items == null) {
            _items = _sim.getController().getItems();
        }

        Shipment shipment = _context.getShipmentByKey(instruction.getMessage());
        if (shipment == null) { //TODO: handle error
            return;
        }

        shipment.arrived = true;
        //TODO: if truck shipment, check platform id
        try{
            if(shipment.carrier instanceof Train){
                _items.setTrainShipment(shipment);
            }else if(shipment.carrier instanceof SeaShip){
                _items.setSeaShipment(shipment);
            }else if(shipment.carrier instanceof InlandShip){
                _items.setInlandShipment(shipment);
            }
        }catch(Exception e){e.printStackTrace();}
        // Get the platforms and containers.
        final Platform[] platformsByCarrier = _items.getPlatformsByCarrier(shipment.carrier);
        final List<ShippingContainer> allContainers = shipment.carrier.containers;

        // Determine how many containers per crane.
        int split = allContainers.size() / platformsByCarrier.length;
        int take = split;

        // Loop variables/
        int i = 0;
        int skip = 0;

        for (Platform platform : platformsByCarrier) {
            if (platform.isBusy()) {
                continue;
            }

            // Get a subset of the containers which get handled by this crane.
            // We create a copy of the list so the containers don't get removed from the source list.
            List<ShippingContainer> containers = new ArrayList<>(allContainers.subList(skip, take));

            // This is the last crane, add the remaning containers as well.
            if (i == platformsByCarrier.length - 1) {
                containers.addAll(allContainers.subList(take, allContainers.size()));
            }

            // Assign the containers to the platform.
            platform.containers = containers;
            placeCrane(platform);

            // Increase loop variables.
            skip += split;
            take += split;
            i++;
        }
    }

    /**
     * Places a crane for the sea/inland and train platform
     *
     * @param platform platform
     */
    private void placeCrane(Platform platform) {
        InstructionProto.Instruction.Builder builder = InstructionProto.Instruction.newBuilder();
        builder.setId(CommunicationProtocol.newUUID());
        builder.setA(platform.getID());
        ShippingContainer container = platform.containers.get(0);
        platform.containers.remove(0);
        builder.setX(container.position.x);
        builder.setY(container.position.y);
        builder.setZ(container.position.z);
        builder.setInstructionType(InstructionType.PLACE_CRANE);
        _com.sendInstruction(builder.build());
        platform.setBusy();
    }

    /**
     * Handles place crane ready instruction
     *
     * @param instruction
     */
    private void placeCraneReady(InstructionProto.Instruction instruction) {
        AGV agv = _items.getFreeAGV();
        Platform platform = null;
        Parkingspot ps = _items.getParkingspotByID(instruction.getB());
        if(instruction.getA() < SimulatorItems.LORRY_BEGIN){
            //dit is een inlandship platform
            platform = _items.getInlandPlatforms()[instruction.getA()];
        }else if(instruction.getA() < SimulatorItems.SEASHIP_BEGIN){
            //dit is een lorry platform
            //do nothing
        } else if (instruction.getA() < SimulatorItems.STORAGE_BEGIN) {
            //dit is een seaship platform
            platform = _items.getTrainPlatforms()[instruction.getA() - SimulatorItems.SEASHIP_BEGIN];
        }else if(instruction.getA() < SimulatorItems.TRAIN_BEGIN){
            //dit is een storage platform
            //do nothing
        } else {
            //dit is een train platform
            platform = _items.getTrainPlatforms()[instruction.getA() - SimulatorItems.TRAIN_BEGIN];

        }
        moveAGV(agv, platform, ps);
    }

    /**
     * Handles crane to AGV instruction type
     *
     * @param platform platform
     * @param shipment shipment
     * @param parkingspot parkingspot
     */
    private void craneToAGV(Platform platform, Shipment shipment, Parkingspot parkingspot) {
        InstructionProto.Instruction.Builder builder = InstructionProto.Instruction.newBuilder();
        builder.setId(CommunicationProtocol.newUUID());
        builder.setA(platform.getID());
        builder.setB((int) parkingspot.getId());
        builder.setInstructionType(InstructionType.CRANE_TO_AGV);
        //TODO: getposition
        Point3 point = shipment.carrier.containers.get(shipment.carrier.containers.size() - shipment.count).position;
        shipment.count--;
        builder.setX(point.x);
        builder.setY(point.y);
        builder.setZ(point.z);
        _com.sendInstruction(builder.build());
        platform.setBusy();
    }

    /**
     * Sends Move AGV command to sea/inland/train platform
     *
     * @param agv agv
     * @param to to platform
     * @param nodeid id van to node
     */
    public void moveAGV(AGV agv, Platform to, int nodeid){
        int[] route = PathFinder.getPath(agv.getNodeID(), nodeid);
        InstructionProto.Instruction.Builder builder = InstructionProto.Instruction.newBuilder();
        builder.setId(CommunicationProtocol.newUUID());
        builder.setA(agv.getID());
        for (int r : route) {
            builder.addRoute(r);
        }
        builder.setInstructionType(InstructionType.MOVE_AGV);
        _com.sendInstruction(builder.build());
        try {
            agv.setBusy();
            to.getParkingspots().get(0).setAGV(agv);
            agv.setNodeID(nodeid);
        }catch(Exception e){e.printStackTrace();} 
    }

    /**
     * Sends Move AGV command
     *
     * @param agv the AGV
     * @param to the destination platform
     * @param spot the spot where the agv needs to go
     */
    public void moveAGV(AGV agv, Platform to, Parkingspot spot) {
        if(agv != null){
            InstructionProto.Instruction.Builder builder = InstructionProto.Instruction.newBuilder();
            builder.setId(CommunicationProtocol.newUUID());
            builder.setB((int)spot.getId());
            builder.setInstructionType(InstructionType.MOVE_AGV);
            builder.setA(agv.getID());
            int[] route = PathFinder.getPath(agv.getNodeID(), spot.getArrivalNodeID());
            for(int r : route){
                builder.addRoute(r);
            }
            _com.sendInstruction(builder.build());
            try {
                agv.setBusy();
                spot.setAGV(agv);
                agv.setNodeID(spot.getDepartNodeID());
            }catch(Exception e){e.printStackTrace();} 
        }else{
            m_agvInstructions.add(new SavedInstruction(to, spot));
        }
    }

    /**
     * Handles shipment moved
     *
     * @param shipment shipment
     */
    private void shipmentMoved(Shipment shipment) {
        if (shipment == null) {
            return; //TODO: handle error
        }
        Platform p = null;
        if(shipment.carrier instanceof SeaShip){
            p = _items.getSeaShipPlatforms()[0];
        }else if(shipment.carrier instanceof InlandShip){
            p = _items.getInlandPlatforms()[0];
        }else if(shipment.carrier instanceof Train){
            p = _items.getTrainPlatforms()[0];
        }else{
            //TODO: Find truck shipment
        }
        shipment.containersMoved = true;
        InstructionProto.Instruction.Builder instruction = InstructionProto.Instruction.newBuilder();
        instruction.setId(CommunicationProtocol.newUUID());
        instruction.setA(p.getID());
        instruction.setMessage(shipment.key);
        instruction.setInstructionType(InstructionType.SHIPMENT_MOVED);
        _com.sendInstruction(instruction.build());
    }

    /**
     * Handles shipment moved
     *
     * @param key key of shipment
     */
    private void shipmentMoved(String key) {
        this.shipmentMoved(_context.getShipmentByKey(key));
    }

    //TODO: finish this
    /**
     * Handles crane to AGV ready instruction
     *
     * @param instruction instruction
     */
    private void craneToAGVReady(InstructionProto.Instruction instruction) {
        Platform platform = _items.getPlatformByAGVID(instruction.getA());
        platform.unsetBusy();
        Platform to = null;
        ShippingContainer container;
        Parkingspot p;
        Parkingspot toSpot = null;
        if (platform.getID() < SimulatorItems.LORRY_BEGIN) {
            //dit is een inlandship platform
            container = _context.getContainerById(instruction.getB());
            p = platform.getParkingspotForAGV(instruction.getA());
            //TODO: find destination
            //TODO: remove container from platform shipping list
            if (!Platform.checkIfBusy(_items.getInlandPlatforms()) && Platform.checkIfShipmentDone(_items.getInlandPlatforms())) {
                shipmentMoved(_items.getInlandShipment());
                _items.unsetInlandShipment();
            }
        } else if (platform.getID() < SimulatorItems.SEASHIP_BEGIN) {
            //dit is een lorry platform
            LorryPlatform lp = (LorryPlatform) platform;
            container = lp.getShipment().carrier.containers.get(0);
            p = lp.getParkingspotForAGV(instruction.getB());
            //TODO: find destination
            shipmentMoved(lp.getShipment());
            lp.unsetShipment();
        } else if (platform.getID() < SimulatorItems.STORAGE_BEGIN) {
            //dit is een seaship platform
            container = _context.getContainerById(instruction.getB());
            p = platform.getParkingspotForAGV(instruction.getA());
            //TODO: find destination
            //TODO: remove container from platform shipping list
            if (!Platform.checkIfBusy(_items.getSeaShipPlatforms()) && Platform.checkIfShipmentDone(_items.getSeaShipPlatforms())) {
                shipmentMoved(_items.getSeaShipment());
                _items.unsetSeaShipment();
            }
        } else if (platform.getID() < SimulatorItems.TRAIN_BEGIN) {
            //dit is een storage platform
            Storage storage = (Storage) platform;
            container = storage.getContainer(instruction.getX(), instruction.getY(), instruction.getZ());
            storage.removeContainer(instruction.getX(), instruction.getY(), instruction.getZ());
            p = storage.getParkingspotForAGV(instruction.getA());
            //TODO: find destination
        } else {
            //dit is een train platform
            container = _context.getContainerById(instruction.getB());
            p = platform.getParkingspotForAGV(instruction.getA());
            to = _context.getStoragePlatformByContainer(container);
            for(Parkingspot ps : to.getParkingspots()){
                if(!ps.hasAGV()){
                    toSpot = ps;
                    break;
                }    
            }
            if(!platform.containers.isEmpty()){
                placeCrane(platform);
            }else if (!Platform.checkIfBusy(_items.getTrainPlatforms())) {
                if(Platform.checkIfShipmentDone(_items.getTrainPlatforms())){
                    shipmentMoved(_items.getTrainShipment());
                    _items.unsetTrainShipment();
                }
            }
        }
        AGV agv = p.getAGV();
        p.removeAGV();
        try {
            agv.setContainer(container);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if(toSpot == null)
            return; //TODO: error?
        moveAGV(agv, to, toSpot);
    }

    private void agvReady(InstructionProto.Instruction instruction) {
        //TODO: send Container to place in storage/place on department shipping or place container on AGV
        Platform platform = _items.getPlatformByAGVID(instruction.getA());
        Parkingspot p = platform.getParkingspotForAGV(instruction.getA());
        Point3 position;
        if (!p.hasAGV()) //TODO: exception handling
        {
            return;
        }
        if (p.getAGV().hasContainer()) {
            if (platform.getID() < SimulatorItems.LORRY_BEGIN) {
                if (_items.hasInlandShipment() && _items.getInlandShipment().arrived) {
                    //TODO: Make instruction to get container from AGV to position on ship
                }
            } else if (platform.getID() < SimulatorItems.SEASHIP_BEGIN) {
                //dit is een lorry platform
                LorryPlatform lp = (LorryPlatform) platform;
                if (lp.hasShipment() && lp.getShipment().arrived) {
                    
                }
            } else if (platform.getID() < SimulatorItems.STORAGE_BEGIN) {
                //dit is een seaship platform
                if (_items.hasSeaShipment() && _items.getSeaShipment().arrived) {
                }
            } else if (platform.getID() < SimulatorItems.TRAIN_BEGIN) {
                //dit is een storage platform
                Storage storage = (Storage) platform;
                ShippingContainer container = p.getAGV().getContainer();
                try {
                    Point3 storagePlace = _context.determineContainerPosition(container);
                    storage.setContainer(container, storagePlace);
                    p.getAGV().unsetContainer();
                    //TODO: We probably want to send an instruction to the simulator here. 
                    InstructionProto.Instruction.Builder builder = InstructionProto.Instruction.newBuilder();
                    builder.setId(CommunicationProtocol.newUUID());
                    builder.setA(storage.getID());
                    builder.setB(storage.getParkingspotIndex(p));
                    builder.setInstructionType(InstructionType.CRANE_TO_STORAGE);
                    builder.setX(storagePlace.x);
                    builder.setY(storagePlace.y);
                    builder.setZ(storagePlace.z);
                    _com.sendInstruction(builder.build());
                } catch (Exception e) {
                    e.printStackTrace();
                }

                //TODO: send crane to storage
            } else {
                //dit is een train platform
                if (_items.hasTrainShipment() && _items.getInlandShipment().arrived) {
                    //TODO: sends crane to department instruction
                }
            }
        }
    }

    @Override
    public void forwardResponse(InstructionProto.InstructionResponse resp) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}