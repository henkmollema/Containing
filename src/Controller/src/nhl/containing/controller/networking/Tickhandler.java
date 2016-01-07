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
        int platformid = -1;
        // Get shipments by date.
        //Shipment[] shipments = context.getShipmentsByDate(date).toArray(new Shipment[0]);
        for (Shipment s : context.getShipmentsByDate(date))
        {
            if(s.carrier instanceof Truck)
            {
                //Assign a loading platform to the truck
                LorryPlatform lp = LorryPlatform.GetPlatformIDForFreeLorryPlatform(_items.getLorryPlatforms());
                if(lp != null){
                    platformid = lp.getID();
                    lp.setShipment(s);
                }else{
                    //TODO: problems or queue?
                }
            }
 
            s.processed = true;
            p("Process shipment: " + s.key +" CONTAINERCOUNT: "+s.carrier.containers.size()+" Carrier:" + s.carrier.toString() + " incomming: " + s.incoming);
            createProto(s, platformid);
        }
        
        //Check if new departure containers can be picked up (if open parkingspots at platforms)..
        for(ShippingContainer cont : context.getShouldDepartContainers())
        {
            Storage platform = context.getStoragePlatformByContainer(cont);
            Parkingspot ps = platform.getFreeParkingspot();
            if(ps != null)
            {
                p("setMoveAGV for departing container..");
                //Assign departing container to the parkingspot where an agv will arive when available
                context.parkingspot_Containertopickup.put(ps, cont);
                _dispatcher.moveAGV(null, context.getStoragePlatformByContainer(cont), ps);
                context.setContainerDeparting(cont);
            }
            else
            {
                p("Cant find parking spot!!");
            }
        }
        AGV freeagv = context.getSimulatorItems().getFreeAGV();
        while(freeagv != null)
        {
            SavedInstruction inst = _dispatcher.m_agvInstructions.poll();
            _dispatcher.moveAGV(freeagv, inst.getPlatform(), inst.getParkingspot());
            freeagv = context.getSimulatorItems().getFreeAGV();
        }
            //            
    }

    /**
     * Creates the profofiles for a shipment and puts them on the queue
     *
     * @param shipment shipment
     */
    private void createProto(Shipment shipment, int platformid)
    {
        shipment.count = shipment.carrier.containers.size();
        Instruction.Builder builder = Instruction.newBuilder();
        if(platformid != -1)
            builder.setA(platformid);
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
            containerBuilder.setContainerNumber(container.containerNumber);
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
