/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nhl.containing.controller.networking;

import java.util.Date;
import nhl.containing.controller.Simulator;
import nhl.containing.controller.simulation.AGV;
import nhl.containing.controller.simulation.Carrier;
import nhl.containing.controller.simulation.InlandShip;
import nhl.containing.controller.simulation.LorryPlatform;
import nhl.containing.controller.simulation.Parkingspot;
import nhl.containing.controller.simulation.SavedInstruction;
import nhl.containing.controller.simulation.SeaShip;
import nhl.containing.controller.simulation.Shipment;
import nhl.containing.controller.simulation.ShippingContainer;
import nhl.containing.controller.simulation.SimulationContext;
import nhl.containing.controller.simulation.SimulatorItems;
import nhl.containing.controller.simulation.Storage;
import nhl.containing.controller.simulation.Train;
import nhl.containing.controller.simulation.Truck;
import nhl.containing.networking.protobuf.InstructionProto.Container;
import nhl.containing.networking.protobuf.InstructionProto.Instruction;
import nhl.containing.networking.protocol.CommunicationProtocol;
import nhl.containing.networking.protocol.InstructionType;

/**
 * Handles a timer tick
 *
 * @author Niels
 */
public class Tickhandler implements Runnable
{

    private final Instruction _instruction;
    private SimulatorItems _items;
    private InstructionDispatcherController _dispatcher;
    
    
    /**
     * Constructor
     *
     * @param instruction
     */
    public Tickhandler(Instruction instruction)
    {
        _instruction = instruction;
        _items = Simulator.instance().getController().getItems();
        _dispatcher = ((InstructionDispatcherController)Simulator.instance().server().simCom().dispatcher());
    }

    /**
     * Run method of the Runnable
     */
    @Override
    public void run()
    {
        // Time sent by the client.
        long time = _instruction.getTime();// * 100;
        //p("Received client time: " + time);

        // Get the first shipment from the simulation context.
        SimulationContext context = Simulator.instance().getController().getContext();
        Shipment first = context.getFirstShipment();

        // Determine the current date/time.
        Date date = new Date(first.date.getTime() + time);
        //p("Ingame time: " + date.toString());
        int id;
        // Get shipments by date.
        //Shipment[] shipments = context.getShipmentsByDate(date).toArray(new Shipment[0]);
        for (Shipment s : context.getShipmentsByDate(date))
        {
            id = -1;
            if(s.carrier instanceof Truck)
            {
                //Assign a loading platform to the truck
                LorryPlatform lp = _items.GetFreeLorryPlatform();
                if(lp != null){
                    //continue;
                    id = lp.getID();
                    lp.setShipment(s);
                }else{
                    continue;
                }
            }else if(s.carrier instanceof Train){
                if(_items.hasTrainShipment())
                    continue;
                else
                    _items.setTrainShipment(s);
            }else if(s.carrier instanceof SeaShip){
                for(int i =0 ; i < SimulatorItems.SEASHIP_COUNT; i++){
                    if(!_items.hasSeaShipment(i)){
                        _items.setSeaShipment(i,s);
                        id = i;
                        break;
                    }
                }
                if(id == -1)
                    continue;
            }else if(s.carrier instanceof InlandShip){
                for(int i =0 ; i < SimulatorItems.INLANDSHIP_COUNT; i++){
                    if(!_items.hasInlandShipment(i)){
                        _items.setInlandShipment(i,s);
                        id = i;
                        break;
                    }
                }
                if(id == -1)
                    continue;
            }
            s.processed = true;
            p("Process shipment: " + s.key +" CONTAINERCOUNT: "+s.carrier.containers.size()+" Carrier:" + s.carrier.toString() + " incomming: " + s.incoming);
            if(!s.incoming)
            {
                //TODO: move this to shipment arrived in instructiondispatcher when all shipment types are implemented
                context.setContainerShouldDepart(s.carrier.containers);
                System.out.println("Set container batch to should depart..");
            }
            createProto(s, id);
        }
        
        //Check if new departure containers can be picked up (if open parkingspots at platforms)..
        for(int i = 0; i < context.getShouldDepartContainers().size(); i++)
        {
            ShippingContainer cont = context.getShouldDepartContainers().get(i);
            Storage platform = context.getStoragePlatformByContainer(cont);
            
            boolean farside;
            farside = cont.departureShipment.carrier instanceof Truck || cont.departureShipment.carrier instanceof Train || cont.departureShipment.carrier instanceof InlandShip;
                     
            Parkingspot ps = platform.getFreeParkingspot(farside);
            
            if(ps != null)
            {
                p("setMoveAGV for departing container..");
                //Assign departing container to the parkingspot where an agv will arive when available
                SavedInstruction pickupinst = new SavedInstruction(null, platform, ps);
                
                _dispatcher.m_agvInstructions.add(pickupinst);
                context.instruction_Containertopickup.put(pickupinst, cont);
                context.setContainerDeparting(cont);
                i--;
            }
            else
            {
                p("Cant find parking spot!!");
            }
        }
        
        
        int i = -1;
        while(++i < _dispatcher.m_agvInstructions.size()) {
            SavedInstruction inst = _dispatcher.m_agvInstructions.get(i);
            if(!inst.getParkingspot().hasAGV()) //if the target parking spot is free
            {
                AGV agv = null;
                if(inst.getAGV() == null) //If no agv assigned to this instruction find a free agv
                {
                   agv = context.getSimulatorItems().getFreeAGV();
                   if(agv == null)
                       continue;
                   if(agv != null)
                   {
                       ShippingContainer containerToPickup = context.instruction_Containertopickup.get(inst);
                       context.instruction_Containertopickup.remove(inst);
                       if(containerToPickup != null)
                       {
                           context.agv_Containertopickup.put(agv, containerToPickup);
                       }
                       
                       _dispatcher.moveAGV(agv, inst.getPlatform(), inst.getParkingspot());
                       _dispatcher.m_agvInstructions.remove(i);
                       i--;
                   }
                }
                else //send agv to target parkingspot
                {
                    _dispatcher.moveAGV(inst.getAGV(), inst.getPlatform(), inst.getParkingspot());
                    _dispatcher.m_agvInstructions.remove(i);
                    i--;

                }
                
            }
        }
        /*
        for(int i = 0; i < _dispatcher.m_agvInstructions.size(); i++)
        {
            
        }*/
    }

    /**
     * Creates the profofiles for a shipment and puts them on the queue
     *
     * @param shipment shipment
     */
    private void createProto(Shipment shipment, int id)
    {
        shipment.count = shipment.carrier.containers.size();
        Instruction.Builder builder = Instruction.newBuilder();
        if(id != -1)
            builder.setA(id);
        builder.setId(CommunicationProtocol.newUUID());
        builder.setMessage(shipment.key);
        int type = 0;
        if (shipment.incoming)
        {
            if (shipment.carrier instanceof Train)
            {
                type = InstructionType.ARRIVAL_TRAIN;
            } else if (shipment.carrier instanceof SeaShip)
            {
                type = InstructionType.ARRIVAL_SEASHIP;
            } else if (shipment.carrier instanceof InlandShip)
            {
                type = InstructionType.ARRIVAL_INLANDSHIP;
            } else if (shipment.carrier instanceof Truck)
            {
                type = InstructionType.ARRIVAL_TRUCK;
            }
        } else
        {
            if (shipment.carrier instanceof Train)
            {
                type = InstructionType.DEPARTMENT_TRAIN;
            } else if (shipment.carrier instanceof SeaShip)
            {
                type = InstructionType.DEPARTMENT_SEASHIP;
            } else if (shipment.carrier instanceof InlandShip)
            {
                type = InstructionType.DEPARTMENT_INLANDSHIP;
            } else if (shipment.carrier instanceof Truck)
            {
                type = InstructionType.DEPARTMENT_TRUCK;
            }
        }
        builder.setInstructionType(type);
        builder.setArrivalCompany(shipment.carrier.company);
        Container.Builder containerBuilder = Container.newBuilder();
        for (ShippingContainer container : shipment.carrier.containers)
        {
            if(shipment.incoming){
                container.currentCategory = AppHandler.getCategory(shipment.carrier);
            }
            containerBuilder.setOwnerName(container.ownerName);
            // todo: hier ook container nummer mee sturen?
            containerBuilder.setContainerNumber(container.id);            
            containerBuilder.setLength(container.length);
            containerBuilder.setWidth(container.width);
            containerBuilder.setHeight(container.height);
            containerBuilder.setX((int) container.position.x);
            containerBuilder.setY((int) container.position.y);
            containerBuilder.setZ((int) container.position.z);
            containerBuilder.setWeightEmpty(container.weightEmpty);
            containerBuilder.setWeightLoaded(container.weightLoaded);
            containerBuilder.setContent(container.content);
            containerBuilder.setContentType(container.contentType);
            containerBuilder.setConentDanger(container.contentDanger);
            containerBuilder.setIso(container.iso);
            containerBuilder.setDepartmentDate(container.departureShipment.date.getTime());
            containerBuilder.setDepartmentTransport(getCategory(container.departureShipment.carrier));
            containerBuilder.setDepartmentCompany(container.departureShipment.carrier.company);
            containerBuilder.setArrivalDate(shipment.date.getTime());
            containerBuilder.setArrivalCompany(shipment.carrier.company);
            containerBuilder.setArrivalTransport(getCategory(shipment.carrier));
            builder.addContainers(containerBuilder.build());
        }
        Simulator.instance().server().simCom().sendInstruction(builder.build());
    }

    /**
     * Gets the category by a carrier
     *
     * @param carrier carrier
     * @return string
     */
    private String getCategory(Carrier carrier)
    {
        if (carrier instanceof InlandShip)
        {
            return "Barge";
        } else if (carrier instanceof SeaShip)
        {
            return "Seaship";
        } else if (carrier instanceof Train)
        {
            return "Train";
        } else if (carrier instanceof Truck)
        {
            return "Truck";
        } else
        {
            return "Remainder";
        }
    }

    private static void p(String s)
    {
        System.out.println("[" + System.currentTimeMillis() + "] Controller: " + s);
    }
}
