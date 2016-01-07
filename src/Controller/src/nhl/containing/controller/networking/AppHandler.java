package nhl.containing.controller.networking;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import nhl.containing.controller.SimulatorController;
import nhl.containing.controller.simulation.*;
import nhl.containing.networking.messaging.StreamHelper;
import nhl.containing.networking.protobuf.AppDataProto.*;
import nhl.containing.networking.protobuf.InstructionProto.*;
import nhl.containing.networking.protocol.CommunicationProtocol;
import nhl.containing.networking.protocol.InstructionType;

/**
 * Handles the App requests
 *
 * @author Jens
 */
public class AppHandler implements Runnable
{

    public boolean shouldRun = true;
    private Socket socket;
    private Server server;
    private ContainerCategory[] categories = new ContainerCategory[]
    {
        ContainerCategory.TRAIN, ContainerCategory.TRUCK, ContainerCategory.INLANDSHIP, ContainerCategory.SEASHIP, ContainerCategory.STORAGE, ContainerCategory.AGV, ContainerCategory.REMAINDER
    };

    /**
     * The constructor
     *
     * @param _server The server
     * @param _socket the socket where the app listens on
     */
    public AppHandler(Server _server, Socket _socket)
    {
        socket = _socket;
        server = _server;
    }

    /**
     * Send Okay message
     *
     * @return true when succeeded, else false
     */
    public boolean initAppData()
    {
        try
        {
            Instruction okayMessage = Instruction.newBuilder()
                    .setId(CommunicationProtocol.newUUID())
                    .setInstructionType(InstructionType.CLIENT_CONNECTION_OKAY)
                    .build();
            StreamHelper.writeMessage(socket.getOutputStream(), okayMessage.toByteArray());
            return true;
        } catch (Exception ex)
        {
            ex.printStackTrace();
            return false;
        }
    }

    /**
     * The run function
     */
    @Override
    public void run()
    {
        while (shouldRun)//While shouldRun, when connection is lost, start listening for a new one
        {
            if (initAppData())
            {
                if (appDataLoop())
                {
                    p("Closed peacefully");
                } else
                {
                    p("Lost connection during instructionloop");
                    break;
                }
            } else
            {
                p("Error while initialising app connection..");
                break;
            }
        }

        try //Clean 
        {
            socket.close();
        } catch (Exception ex)
        {
            ex.printStackTrace();
        }

        //isAppConnected = false;
    }

    /**
     * Gets the requests and sends the requested data
     *
     * @return false when Error, otherwise true
     */
    public boolean appDataLoop()
    {
        p("Starting appLoop");
        try
        {
            InputStream input = socket.getInputStream();
            OutputStream output = socket.getOutputStream();
            while (shouldRun)
            {
                byte[] data = StreamHelper.readByteArray(input);
                byte[] response = processInstruction(data);
                StreamHelper.writeMessage(output, response); //Send
            }
        } catch (IOException ex)
        {
            ex.printStackTrace();
            return false;
        }

        return true;
    }

    /**
     * parses the instruction class from a byte array and serializes the right
     * information to an byte array
     *
     * @param inst instruction byte array
     * @return information byte array
     */
    private byte[] processInstruction(byte[] inst)
    {
        SimulatorController controller = server.getSimulator().getController();
        SimulationContext context = controller.getContext();
        Instruction instruction;
        datablockApp.Builder builder = datablockApp.newBuilder();
        try
        {
            instruction = Instruction.parseFrom(inst);
        } catch (Exception ex)
        {
            return builder.build().toByteArray();
        }
        ContainerGraphData.Builder b = ContainerGraphData.newBuilder();
        //check timing
        int[] numbers;
        switch (instruction.getA())
        {
            case 0:
                numbers = new int[7];
                for(Shipment shipment : context.getShipments()){
                    if(shipment.processed){
                        for(ShippingContainer container : shipment.carrier.containers){
                            if(!container.departureShipment.containersMoved){
                                switch(container.currentCategory){
                                    case TRAIN:
                                        numbers[0]++;
                                        break;
                                    case TRUCK:
                                        numbers[1]++;
                                        break;
                                    case SEASHIP:
                                        numbers[2]++;
                                        break;
                                    case INLANDSHIP:
                                        numbers[3]++;
                                        break;
                                    case STORAGE:
                                        numbers[4]++;
                                        break;
                                    case AGV:
                                        numbers[5]++;
                                        break;
                                    case REMAINDER:
                                        numbers[6]++;
                                        break;
                                }
                            }
                        }
                    }
                }
                for (int i = 0; i < 7; i++)
                {
                    b.setCategory(categories[i]);
                    b.setAantal(numbers[i]);
                    builder.addGraphs(b.build());
                }
                break;
            case 1:
            case 2:
                //in and out graph
                numbers = new int[5];
                boolean incoming = instruction.getA() == 1;
                for (Shipment shipment : context.getShipments())
                {
                    if (shipment.processed && shipment.incoming == incoming)
                    {
                        int type;
                        if (shipment.carrier instanceof Train)
                        {
                            type = 0;
                        } else if (shipment.carrier instanceof Truck)
                        {
                            type = 1;
                        } else if (shipment.carrier instanceof InlandShip)
                        {
                            type = 2;
                        } else if (shipment.carrier instanceof SeaShip)
                        {
                            type = 3;
                        } else
                        {
                            type = 4;
                        }
                        numbers[type] += shipment.carrier.containers.size();
                    }
                }
                for (int i = 0; i < 4; i++)
                {
                    b.setCategory(categories[i]);
                    b.setAantal(numbers[i]);
                    builder.addGraphs(b.build());
                }
                break;
            case 3:
                //unkown graph [WIP]
                break;
            case 4:
                //containerlist
                ContainerDataListItem.Builder itemBuilder = ContainerDataListItem.newBuilder();
                for(ShippingContainer container : context.getAllContainers())
                {
                    if(container.arrivalShipment.processed && !container.departureShipment.processed)
                    {
                        itemBuilder.setID(container.containerNumber);
                        builder.addItems(itemBuilder.build());
                    }
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
                    infoBuilder.setGewichtLeeg(container.weightEmpty);
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
                } catch (Exception e)
                {
                    e.printStackTrace();
                }
                break;
        }
        return builder.build().toByteArray();
    }

    /**
     * Gets the right containercategory by a specific type of carrier
     *
     * @param carrier The carrier
     * @return the container category
     */
    public static ContainerCategory getCategory(Carrier carrier)
    {
        if (carrier instanceof InlandShip)
        {
            return ContainerCategory.INLANDSHIP;
        } else if (carrier instanceof SeaShip)
        {
            return ContainerCategory.SEASHIP;
        } else if (carrier instanceof Train)
        {
            return ContainerCategory.TRAIN;
        } else if (carrier instanceof Truck)
        {
            return ContainerCategory.TRUCK;
        } else
        {
            return ContainerCategory.REMAINDER;
        }
    }

    /**
     * Prints a string
     *
     * @param s the string to print
     */
    private static void p(String s)
    {
        System.out.println("Controller: " + s);
    }

}
