/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nhl.containing.controller.networking;

import nhl.containing.controller.Simulator;
import nhl.containing.controller.simulation.Carrier;
import nhl.containing.controller.simulation.InlandShip;
import nhl.containing.controller.simulation.SeaShip;
import nhl.containing.controller.simulation.Shipment;
import nhl.containing.controller.simulation.ShippingContainer;
import nhl.containing.controller.simulation.Train;
import nhl.containing.controller.simulation.Truck;
import nhl.containing.networking.protobuf.InstructionProto.Container;
import nhl.containing.networking.protobuf.InstructionProto.Instruction;
import nhl.containing.networking.protocol.CommunicationProtocol;

/**
 *
 * @author Niels
 */
public class Tickhandler implements Runnable
{
    private Instruction _insInstruction;
    public Tickhandler(Instruction instruction)
    {
        _insInstruction = instruction;
    }

    @Override
    public void run()
    {
        
    }
    
    
    private void createProto(Shipment shipment){
        Instruction.Builder builder = Instruction.newBuilder();
        builder.setId(CommunicationProtocol.newUUID());
        int type = 0;
        if(shipment.incoming){
            if(shipment.carrier instanceof Train)
                type = 6;
            else if(shipment.carrier instanceof SeaShip)
                type = 7;
            else if(shipment.carrier instanceof InlandShip)
                type = 8;
            else if(shipment.carrier instanceof Truck)
                type = 9;
        }else{
            if(shipment.carrier instanceof Train)
                type = 10;
            else if(shipment.carrier instanceof SeaShip)
                type = 11;
            else if(shipment.carrier instanceof InlandShip)
                type = 12;
            else if(shipment.carrier instanceof Truck)
                type = 13;
        }
        builder.setInstructionType(type);
        builder.setArrivalCompany(shipment.carrier.company);
        Container.Builder containerBuilder = Container.newBuilder();
        for(ShippingContainer container : shipment.carrier.containers){
            containerBuilder.setOwnerName(container.ownerName);
            containerBuilder.setContainerNumber(container.containerNumber);
            containerBuilder.setLength(container.length);
            containerBuilder.setWidth(container.width);
            containerBuilder.setHeight(container.height);
            containerBuilder.setX((int)container.position.x);
            containerBuilder.setY((int)container.position.y);
            containerBuilder.setZ((int)container.position.z);
            containerBuilder.setWeightEmpty(container.weightEmpty);
            containerBuilder.setWeightLoaded(container.weightLoaded);
            containerBuilder.setContent(container.content);
            containerBuilder.setContentType(container.contentType);
            containerBuilder.setConentDanger(container.contentDanger);
            containerBuilder.setIso(container.iso);
            containerBuilder.setDepartmentData(container.departureShipment.date.getTime());
            containerBuilder.setDepartmentTransport(getCategory(container.departureShipment.carrier));
            containerBuilder.setDepartmentCompany(container.departureShipment.carrier.company);
            builder.addContainers(containerBuilder.build());
        }
        Simulator.instance().server().simCom().sendInstruction(builder.build());
    }
    
    private String getCategory(Carrier carrier)
    {
        if(carrier instanceof InlandShip)
            return "Barge";
        else if(carrier instanceof SeaShip)
            return "Seaship";
        else if(carrier instanceof Train)
            return "Train";
        else if(carrier instanceof Truck)
            return "Truck";
        else
            return "Remainder";
    }
}
