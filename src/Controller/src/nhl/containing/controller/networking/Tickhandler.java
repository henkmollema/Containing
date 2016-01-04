/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nhl.containing.controller.networking;

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import nhl.containing.controller.Simulator;
import nhl.containing.controller.Vector3f;
import nhl.containing.controller.simulation.AGV;
import nhl.containing.controller.simulation.Carrier;
import nhl.containing.controller.simulation.InlandShip;
import nhl.containing.controller.simulation.Parkingspot;
import nhl.containing.controller.simulation.Platform;
import nhl.containing.controller.simulation.SeaShip;
import nhl.containing.controller.simulation.Shipment;
import nhl.containing.controller.simulation.ShippingContainer;
import nhl.containing.controller.simulation.SimulationContext;
import nhl.containing.controller.simulation.SimulatorItems;
import nhl.containing.controller.simulation.StorageItem;
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

    private Instruction _instruction;
    private SimulatorItems _simulatorItems;
    private InstructionDispatcherController _dispatcherController;
    
    private static final int minInterval = 5 * 60 * 1000; // Five minutes in miliseconds
    private HashMap<ShippingContainer, Integer> container_PlatformID;
    private HashMap<ShippingContainer, Vector3f> container_PlatformPosition;
    
    public int getPlatformIDByContainer(ShippingContainer container)
    {
        return container_PlatformID.get(container);
    }
    
    public Vector3f getPlatformPositionByContainer(ShippingContainer container)
    {
        return container_PlatformPosition.get(container);
    }
    
    /**
     * determineContainerPlatforms
     * Determines the storageplatforms where the given containers will be placed by filling the container_StorageID hashmap
     *
     * @param containers
     */
    public void determineContainerPlatforms(List<ShippingContainer> containers)
    {   
        for(int i = 0; i < containers.size(); i++)
        {
            ShippingContainer container = containers.get(i);
            
            for(int j = SimulatorItems.STORAGE_BEGIN; j < SimulatorItems.STORAGE_BEGIN + SimulatorItems.STORAGE_CRANE_COUNT; j++)
            {
               if(canBePlacedInPlatform(container, j))
                    container_PlatformID.put(container, j);
            }
            
        }
    }
    
    /**
     * canBePlacedInPlatform
     * Checks if a container can be placed in a given platformID
     * 
     * @param c
     * @param platformID
     */
    public boolean canBePlacedInPlatform(ShippingContainer c, int platformID)
    {
        Iterator it = container_PlatformID.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            int currentPlatform = (int) pair.getValue();
            if(currentPlatform == platformID)
            {
                ShippingContainer currentContainer = (ShippingContainer)pair.getKey();
                
                //If departure times differ less than minInterval they can't be in the same platform
                if(Math.abs(currentContainer.departureShipment.date.getTime() - c.departureShipment.date.getTime()) < minInterval)
                {
                    return false;
                }
            }
            it.remove(); // avoids a ConcurrentModificationException
        }
        return true;
    }
    
    public void determineContainerPosition(ShippingContainer c)
    {
        int PlatformID = this.getPlatformIDByContainer(c);
        StorageItem[][][] storagePlaces = _simulatorItems.getStorages()[PlatformID].getStoragePlaces();
        for(int z = 0; z < storagePlaces[0].length; z++)
        {
            for(int x = 0; x < storagePlaces.length; x++)
            {
                for(int y = 0; y < storagePlaces[0][0].length; y++)
                {
                    if(y > 0 && storagePlaces[x][y - 1][z].isEmpty()) break; //No container beneath, so can not be placed here.
                    
                    //If there's no container on this spot 
                    if(storagePlaces[x][y][z].isEmpty())
                    {
                        if(y > 0){
                            //check the container beneath it.
                            if(storagePlaces[x][y - 1][z].getContainer().departureShipment.date.getTime() < c.departureShipment.date.getTime())
                            {
                                break; //Container can not be placed here, because the container beneath departs earlier
                            }
                        }
                        
                        container_PlatformPosition.put(c, new Vector3f(x,y,z));
                    }
                }
            }
        }
    }

    /**
     * Constructor
     *
     * @param instruction
     */
    public Tickhandler(Instruction instruction)
    {
        _instruction = instruction;
        _simulatorItems = Simulator.instance().getController().getItems();
        if(Simulator.instance().server().simCom().dispatcher() instanceof InstructionDispatcherController)
            _dispatcherController = (InstructionDispatcherController)Simulator.instance().server().simCom().dispatcher();
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
        p("Ingame time: " + date.toString());

        // Get shipments by date.
        //Shipment[] shipments = context.getShipmentsByDate(date).toArray(new Shipment[0]);
        for (Shipment s : context.getShipmentsByDate(date))
        {
            determineContainerPlatforms(s.carrier.containers);
            
            s.processed = true;
            p("Process shipment: " + s.key);
            createProto(s);
            
            
        }
    }

    /**
     * Creates the profofiles for a shipment and puts them on the queue
     *
     * @param shipment shipment
     */
    private void createProto(Shipment shipment)
    {
        shipment.count = shipment.carrier.containers.size();
        //TODO: give key of shipment
        Instruction.Builder builder = Instruction.newBuilder();
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
                type = InstructionType.ARRIVAL_TRAIN;
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
