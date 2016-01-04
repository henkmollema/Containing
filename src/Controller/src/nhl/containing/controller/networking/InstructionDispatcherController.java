package nhl.containing.controller.networking;

import java.util.LinkedList;
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
            case InstructionType.SHIPMENT_ARRIVED:
                shipmentArrived(inst);
                break;
            case InstructionType.CRANE_TO_AGV_READY:
                craneToAGVReady(inst);
                break;
            case InstructionType.AGV_READY:
                agvReady(inst);
                break;
            case InstructionType.CRANE_TO_STORAGE_READY:
                
                break;
            //More instruction types here..
        }
    }
    /**
     * Handles shipment arrived
     * @param instruction instruction
     */
    private void shipmentArrived(InstructionProto.Instruction instruction){
        Shipment shipment = _context.getShipmentByKey(instruction.getMessage());
        if(shipment == null) //TODO: handle error
            return;
        shipment.arrived = true;
        for(Platform platform : _items.getPlatformsByCarrier(shipment.carrier)){
            if(platform.isBusy())
                continue;
            placeCrane(platform);
        }
    }
    
    /**
     * Places a crane for the sea/inland and train platform
     * @param platform platform
     */
    private void placeCrane(Platform platform){
        InstructionProto.Instruction.Builder builder = InstructionProto.Instruction.newBuilder();
        builder.setId(CommunicationProtocol.newUUID());
        builder.setA(platform.getID());
        //TODO : choose right container
        ShippingContainer container = platform.getShippingContainers().get(0);
        platform.getShippingContainers().remove(0);
        builder.setX(container.position.x);
        builder.setY(container.position.y);
        builder.setZ(container.position.z);
        builder.setInstructionType(InstructionType.PLACE_CRANE);
        _com.sendInstruction(builder.build());
        platform.setBusy();
    }
    
    /**
     * Handles place crane ready instruction
     * @param instruction 
     */
    private void placeCraneReady(InstructionProto.Instruction instruction){
        if(instruction.getA() < SimulatorItems.LORRY_BEGIN){
            //dit is een inlandship platform
            Platform platform = _items.getInlandPlatforms()[instruction.getA()];
            InstructionProto.Node node = instruction.getNodes(0);
            Node nodeNew = new Node(node.getId(),null,node.getConnectionsList());
            moveAGV(platform.getParkingspots().get(0).getAGV(), platform, nodeNew);
        }else if(instruction.getA() < SimulatorItems.SEASHIP_BEGIN){
            //dit is een lorry platform
            //do nothing
        }else if(instruction.getA() < SimulatorItems.STORAGE_BEGIN){
            //dit is een seaship platform
            Platform platform = _items.getTrainPlatforms()[instruction.getA() - SimulatorItems.SEASHIP_BEGIN];
            InstructionProto.Node node = instruction.getNodes(0);
            Node nodeNew = new Node(node.getId(),null,node.getConnectionsList());
            moveAGV(platform.getParkingspots().get(0).getAGV(), platform, nodeNew);
        }else if(instruction.getA() < SimulatorItems.TRAIN_BEGIN){
            //dit is een storage platform
            //do nothing
        }else{
            //dit is een train platform
            Platform platform = _items.getTrainPlatforms()[instruction.getA() - SimulatorItems.TRAIN_BEGIN];
            InstructionProto.Node node = instruction.getNodes(0);
            Node nodeNew = new Node(node.getId(),null,node.getConnectionsList());
            moveAGV(platform.getParkingspots().get(0).getAGV(), platform, nodeNew);
        }
    }
    
    /**
     * Handles crane to AGV instruction type
     * @param platform platform
     * @param shipment shipment
     * @param parkingspot parkingspot
     */
    private void craneToAGV(Platform platform,Shipment shipment,Parkingspot parkingspot){
        InstructionProto.Instruction.Builder builder = InstructionProto.Instruction.newBuilder();
        builder.setId(CommunicationProtocol.newUUID());
        builder.setA(platform.getID());
        builder.setB((int)parkingspot.getId());
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
     * @param agv agv
     * @param to to platform
     * @param node to node
     */
    public void moveAGV(AGV agv, Platform to, Node node){
        int[] route = PathFinder.getPath(agv.getNode().m_id, node.m_connections[0]);
        InstructionProto.Instruction.Builder builder = InstructionProto.Instruction.newBuilder();
        builder.setId(CommunicationProtocol.newUUID());
        builder.setA(agv.getID());
        for(int r : route){
            builder.addRoute(r);
        }
        builder.setInstructionType(InstructionType.MOVE_AGV);
        _com.sendInstruction(builder.build());
        try{
            agv.setBusy();
            to.getParkingspots().get(0).setAGV(agv);
            agv.setNode(_items.getNode(node.m_connections[0]));
        }catch(Exception e){e.printStackTrace();} 
    }
    
    /**
     * Sends Move AGV command
     * @param agv the AGV
     * @param to the destination platform
     * @param spot the spot where the agv needs to go
     */
    public void moveAGV(AGV agv,Platform to,Parkingspot spot){
        InstructionProto.Instruction.Builder builder = InstructionProto.Instruction.newBuilder();
        builder.setId(CommunicationProtocol.newUUID());
        builder.setA(agv.getID());
        //TODO:Calculate & add route
        int[] route = PathFinder.getPath(agv.getNode().m_id, spot.getNode().m_id);
        for(int r : route){
            builder.addRoute(r);
        }
        builder.setInstructionType(InstructionType.MOVE_AGV);
        _com.sendInstruction(builder.build());
        try{
            agv.setBusy();
            spot.setAGV(agv);
            agv.setNode(spot.getNode());
        }catch(Exception e){e.printStackTrace();} 
    }
    
    /**
     * Handles shipment moved
     * @param shipment shipment
     */
    private void shipmentMoved(Shipment shipment){
        if(shipment == null)
            return; //TODO: handle error
        shipment.containersMoved = true;
        InstructionProto.Instruction.Builder instruction = InstructionProto.Instruction.newBuilder();
        instruction.setId(CommunicationProtocol.newUUID());
        instruction.setMessage(shipment.key);
        instruction.setInstructionType(InstructionType.SHIPMENT_MOVED);
        _com.sendInstruction(instruction.build());
    }
    /**
     * Handles shipment moved
     * @param key key of shipment
     */
    private void shipmentMoved(String key){
        this.shipmentMoved(_context.getShipmentByKey(key));
    }
    
    //TODO: finish this
    /**
     * Handles crane to AGV ready instruction
     * @param instruction instruction
     */
    private void craneToAGVReady(InstructionProto.Instruction instruction){
        Platform platform = _items.getPlatformByAGVID(instruction.getA());
        platform.unsetBusy();
        Platform to = null;
        ShippingContainer container;
        Parkingspot p;
        Parkingspot toSpot = null;
        if(platform.getID() < SimulatorItems.LORRY_BEGIN){
            //dit is een inlandship platform
            container = _context.getContainerById(instruction.getB());
            p = platform.getParkingspotForAGV(instruction.getA());
            //TODO: find destination
            //TODO: remove container from platform shipping list
            if(!Platform.checkIfBusy(_items.getInlandPlatforms()) && Platform.checkIfShipmentDone(_items.getInlandPlatforms())){
                shipmentMoved(_items.getInlandShipment());
                _items.unsetInlandShipment();
            }
        }else if(platform.getID() < SimulatorItems.SEASHIP_BEGIN){
            //dit is een lorry platform
            LorryPlatform lp = (LorryPlatform)platform;
            container = lp.getShipment().carrier.containers.get(0);
            p = lp.getParkingspotForAGV(instruction.getB());
            //TODO: find destination
            shipmentMoved(lp.getShipment());
            lp.unsetShipment();
        }else if(platform.getID() < SimulatorItems.STORAGE_BEGIN){
            //dit is een seaship platform
            container = _context.getContainerById(instruction.getB());
            p = platform.getParkingspotForAGV(instruction.getA());
            //TODO: find destination
            //TODO: remove container from platform shipping list
            if(!Platform.checkIfBusy(_items.getSeaShipPlatforms()) && Platform.checkIfShipmentDone(_items.getSeaShipPlatforms())){
                shipmentMoved(_items.getSeaShipment());
                _items.unsetSeaShipment();
            }
        }else if(platform.getID() < SimulatorItems.TRAIN_BEGIN){
            //dit is een storage platform
            Storage storage = (Storage)platform;
            container = storage.getContainer(instruction.getX(), instruction.getY(), instruction.getZ());
            storage.removeContainer(instruction.getX(), instruction.getY(), instruction.getZ());
            p = storage.getParkingspotForAGV(instruction.getA());
            //TODO: find destination
        }else{
            //dit is een train platform
            container = _context.getContainerById(instruction.getB());
            p = platform.getParkingspotForAGV(instruction.getA());
            //TODO: find destination <-- jens, hier juiste platform ophalen
            if(!Platform.checkIfBusy(_items.getTrainPlatforms()) && Platform.checkIfShipmentDone(_items.getTrainPlatforms())){
                shipmentMoved(_items.getTrainShipment());
                _items.unsetTrainShipment();
            }
        }
        AGV agv = p.getAGV();
        p.removeAGV();
        try{
            agv.setContainer(container);
        }catch(Exception e){e.printStackTrace();}
        moveAGV(agv, to,toSpot);
    }
    
    
    private void agvReady(InstructionProto.Instruction instruction){
        //TODO: send Container to place in storage/place on department shipping or place container on AGV
        Platform platform = _items.getPlatformByAGVID(instruction.getA());
        Parkingspot p = platform.getParkingspotForAGV(instruction.getA());
        Point3 position;
        if(!p.hasAGV()) //TODO: exception handling
            return;
        if(p.getAGV().hasContainer()){
            if(platform.getID() < SimulatorItems.LORRY_BEGIN){
                if(_items.hasInlandShipment() && _items.getInlandShipment().arrived){
                    //TODO: Make instruction to get container from AGV to position on ship
                }
            }else if(platform.getID() < SimulatorItems.SEASHIP_BEGIN){
                //dit is een lorry platform
                LorryPlatform lp = (LorryPlatform)platform;
                if(lp.hasShipment() && lp.getShipment().arrived){
                    
                }
            }else if(platform.getID() < SimulatorItems.STORAGE_BEGIN){
                //dit is een seaship platform
                if(_items.hasSeaShipment() && _items.getSeaShipment().arrived){
                    
                }
            }else if(platform.getID() < SimulatorItems.TRAIN_BEGIN){
                //dit is een storage platform
                Storage storage = (Storage)platform;
                try{
                    storage.setContainer(p.getAGV().getContainer(),0,0,0);//TODO: Calculate spot for container
                    p.getAGV().unsetContainer();
                }catch(Exception e){e.printStackTrace();}
                
                //TODO: send crane to storage
            }else{
                //dit is een train platform
                if(_items.hasTrainShipment() && _items.getInlandShipment().arrived){
                    //TODO: sends crane to department instruction

                }
            }
        } 
    }

    @Override
    public void forwardResponse(InstructionProto.InstructionResponse resp)
    {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}