/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nhl.containing.controller.networking;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Collection;
import nhl.containing.controller.SimulatorController;
import nhl.containing.controller.simulation.*;
import nhl.containing.networking.messaging.StreamHelper;
import nhl.containing.networking.protobuf.AppDataProto.*;
import nhl.containing.networking.protobuf.InstructionProto.*;
import nhl.containing.networking.protocol.CommunicationProtocol;
import nhl.containing.networking.protocol.InstructionType;

/**
 *
 * @author Jens
 */
public class AppHandler implements Runnable{
    
    public boolean shouldRun = true;
    private Socket socket;
    private Server server;
    
    public AppHandler(Server _server, Socket _socket)
    {
        socket = _socket;
        server = _server;
    }
    
    public boolean initAppData() {
        try
        {
            Instruction okayMessage = Instruction.newBuilder()
                .setId(CommunicationProtocol.newUUID())
                .setInstructionType(InstructionType.CLIENT_CONNECTION_OKAY)
                .build();
            StreamHelper.writeMessage(socket.getOutputStream(), okayMessage.toByteArray());
            return true;
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            return false;
        }
    }

    @Override
    public void run() {
        while (shouldRun)//While shouldRun, when connection is lost, start listening for a new one
        {
            if (initAppData()) {
                if (appDataLoop()) {
                    p("Closed peacefully");
                } else {
                    p("Lost connection during instructionloop");
                    break;
                }
            } else {
                p("Error while initialising app connection..");
                break;
            }
        }

        try //Clean 
        {
            socket.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        //isAppConnected = false;
    }
    
    public boolean appDataLoop()
    {
        p("Starting appLoop");
        try {
            InputStream input = socket.getInputStream();
            OutputStream output = socket.getOutputStream();
            while (shouldRun) {
                byte[] data = StreamHelper.readByteArray(input);
                byte[] response = processInstruction(data);
                StreamHelper.writeMessage(output, response); //Send
            }
        } catch (IOException ex) {
            ex.printStackTrace();
            return false;
        }

        return true;
    }
    
    private byte [] processInstruction(byte[] inst)
    {
        SimulatorController controller = server.getSimulator().getSimulatorController();
        SimulationContext context = controller.getSimulationContext();
        Instruction instruction = null;
        datablockApp.Builder builder = datablockApp.newBuilder();
        try
        {
            instruction = Instruction.parseFrom(inst);
        }
        catch (Exception ex)
        {
            return builder.build().toByteArray();
        }
        ContainerGraphData.Builder b = ContainerGraphData.newBuilder();
        //check timing
        switch (instruction.getA())
        {
            case 0:
                //actual graph
                break;
            case 1: case 2:
                //in and out graph
                boolean incoming = instruction.getA() == 1;
                b.setCategory(ContainerCategory.TRAIN);
                b.setAantal(0);
                for(Train train : context.getTrains(incoming))
                {
                    b.setAantal(b.getAantal() + train.containers.size());
                }
                builder.addGraphs(b.build());
                b.setCategory(ContainerCategory.TRUCK);
                b.setAantal(0);
                for(Truck truck : context.getTrucks(incoming))
                {
                    b.setAantal(b.getAantal() + truck.containers.size());
                }
                builder.addGraphs(b.build());
                b.setCategory(ContainerCategory.INLINESHIP);
                b.setAantal(0);
                for(InlandShip inlineShip : context.getInlandShips(incoming))
                {
                    b.setAantal(b.getAantal() + inlineShip.containers.size());
                }
                builder.addGraphs(b.build());
                b.setCategory(ContainerCategory.SEASHIP);
                b.setAantal(0);
                for(SeaShip seaShip : context.getSeaShips(incoming))
                {
                    b.setAantal(b.getAantal() + seaShip.containers.size());
                }
               builder.addGraphs(b.build());
                break;
            case 3:
                //unkown graph
                break;
            case 4:
                //containerlist
                ContainerDataListItem.Builder itemBuilder = ContainerDataListItem.newBuilder();
                Collection<ShippingContainer> containers = context.getAllContainers();
                ContainerCategory category = ContainerCategory.SEASHIP; //change to actual
                for(ShippingContainer container : containers)
                {
                    itemBuilder.setCategory(category);
                    itemBuilder.setEigenaar(container.ownerName);
                    itemBuilder.setID(container.containerNumber);
                    builder.addItems(itemBuilder.build());
                }
                break;
            case 5:
                //containerdata
                try
                {
                    ShippingContainer container = context.getContainerById(instruction.getB());
                    ContainerInfo.Builder infoBuilder = ContainerInfo.newBuilder();
                    infoBuilder.setEigenaar(container.ownerName);
                    infoBuilder.setID(container.containerNumber);
                    infoBuilder.setGewichtLeeg(container.weigthEmpty);
                    infoBuilder.setGewichtVol(container.weightLoaded);
                    infoBuilder.setInhoud(container.content);
                    infoBuilder.setAanvoerMaatschappij(container.arrivalShipment.carrier.company);
                    infoBuilder.setBinnenkomstDatum(container.arrivalShipment.date.getTime());
                    infoBuilder.setAfvoerMaatschappij(container.departureShipment.carrier.company);
                    infoBuilder.setVertrekDatum(container.departureShipment.date.getTime());
                    infoBuilder.setInhoudType(container.contentType);
                    infoBuilder.setInhoudGevaar(container.contentDanger);
                    infoBuilder.setVervoerBinnenkomst(getCategory(container.arrivalShipment.carrier));
                    infoBuilder.setVervoerVertrek(getCategory(container.departureShipment.carrier));
                    ContainerInfo test = infoBuilder.build();
                    builder.setContainer(test);
                }
                catch(Exception e){
                    e.printStackTrace();
                }
                break;
        }
        return builder.build().toByteArray();
    }
    
    
    private ContainerCategory getCategory(Carrier carrier)
    {
        if(carrier instanceof InlandShip)
            return ContainerCategory.INLINESHIP;
        else if(carrier instanceof SeaShip)
            return ContainerCategory.SEASHIP;
        else if(carrier instanceof Train)
            return ContainerCategory.TRAIN;
        else if(carrier instanceof Truck)
            return ContainerCategory.TRUCK;
        else
            return ContainerCategory.REMAINDER;
    }
    
    private static void p(String s) {
        System.out.println("Controller: " + s);
    }
    
}
