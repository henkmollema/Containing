/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nhl.containing.simulator.networking;

import com.jme3.math.Vector3f;
import java.io.*;
import java.net.Socket;
import nhl.containing.networking.messaging.StreamHelper;
import nhl.containing.networking.protobuf.ClientIdProto.ClientIdentity;
import nhl.containing.networking.protobuf.InstructionProto.Instruction;
import nhl.containing.networking.protobuf.SimulationItemProto.SimulationItem;
import nhl.containing.networking.protobuf.SimulationItemProto.SimulatorItemList;
import nhl.containing.networking.protocol.CommunicationProtocol;
import nhl.containing.networking.protocol.InstructionType;
import nhl.containing.simulator.framework.Point3;
import nhl.containing.simulator.framework.Time;
import nhl.containing.simulator.game.AgvPath;

/**
 * Providers interaction with the client.
 *
 * @author Jens
 */
public class SimulatorClient implements Runnable
{
    public static final String HOST = "127.0.0.1";
    public static final int PORT = 1337;
    private boolean isConnected;
    private volatile boolean shouldRun;
    private volatile boolean start = false;
    private SimulatorItemList.Builder metaList = SimulatorItemList.newBuilder();
    private Socket _socket = null;
    public static CommunicationProtocol controllerCom;

    public SimulatorClient()
    {
        controllerCom = new CommunicationProtocol();
    }

    /**
     * Gets the communication protocol of the server.
     *
     * @return The communication protocol.
     */
    public CommunicationProtocol controllerCom()
    {
        return controllerCom;
    }

    public boolean isConnected()
    {
        return isConnected;
    }

    public void stop()
    {
        shouldRun = false;
    }

    /**
     * Adds a Simulation item to the metalist
     * @param id id of the item
     * @param type type of the item
     * @param position position of the item
     * @param parentid parent of the item or -1
     */
    public void addSimulationItem(long id, SimulationItem.SimulationItemType type, Vector3f position,int parentid)
    {
        SimulationItem.Builder builder = SimulationItem.newBuilder();
        builder.setId(id);
        builder.setType(type);
        builder.setParentID(parentid);
        if (position != null)
        {
            builder.setX(position.x);
            builder.setY(position.y);
            builder.setZ(position.z);
        }
        metaList.addItems(builder.build());
    }
    
     /**
     * Adds a Simulation item to the metalist
     * @param id id of the item
     * @param type type of the item
     * @param position position of the item
     * @param parentid parent of the item or -1
     */
    public void addSimulationItem(long id, SimulationItem.SimulationItemType type, Vector3f position,int parentid,int arrival, int depart)
    {
        SimulationItem.Builder builder = SimulationItem.newBuilder();
        builder.setId(id);
        builder.setType(type);
        builder.setParentID(parentid);
        if (position != null)
        {
            builder.setX(position.x);
            builder.setY(position.y);
            builder.setZ(position.z);
        }
        builder.addConnections(arrival);
        builder.addConnections(depart);
        metaList.addItems(builder.build());
    }
    
    /**
     * Adds a node to the metalist
     * @param node node
     */
    public void addNode(AgvPath.AgvNode node){
        SimulationItem.Builder builder = SimulationItem.newBuilder();
        builder.setId(node.id());
        builder.setType(SimulationItem.SimulationItemType.NODES);
        builder.setX(node.position().x);
        builder.setY(node.position().y);
        for(int connection : node.connections()){
            builder.addConnections(connection);
        }
        metaList.addItems(builder.build());
    }

    /**
     * starts the connection when world is build
     */
    public void Start()
    {
        this.start = true;
    }

    /**
     * Opens a serversocket and waits for a client to connect. This method
     * should be called on it's own thread as it contains an indefinite loop.
     * Returns false if setup/connection failed. Returns true if connection was
     * successfull and closed peacefuly
     */
    private boolean start()
    {
        p("start()");

        if (!isConnected)
        {

            try
            {
                // Halt the thread until a connection has been accepted
                _socket = new Socket(HOST, PORT);

                p("Connected to server!");

                isConnected = true;
                return true;

            }
            catch (Exception ex)
            {
                p("Connection refused..");
                //ex.printStackTrace();
                return false;
            }
        }

        return false;
    }

    /**
     * Sends the metadata to the controller
     * @return true when succesfull, otherwise false
     */
    private boolean sendSimulatorMetadata()
    {
        p("sendSimulatorMetadata()");

        try
        {
            // Tell the Controller we are the simulator
            ClientIdentity.Builder idBuilder = ClientIdentity.newBuilder();
            idBuilder.setClientType(ClientIdentity.ClientType.SIMULATOR)
                    .setVersion(CommunicationProtocol.PROTOCOL_VERSION);

            // Write the message to the socket.
            OutputStream output = _socket.getOutputStream();
            StreamHelper.writeMessage(output, idBuilder.build().toByteArray());

            // Wait for an okay response..
            InputStream input = _socket.getInputStream();
            byte[] ba = StreamHelper.readByteArray(input);
            Instruction i = Instruction.parseFrom(ba);
            
            if (i.getInstructionType() != InstructionType.CLIENT_CONNECTION_OKAY)
            {
                throw new IOException("Server did not respond with Client OK.");
            }

            // Send metadata to controller.            
            byte[] message = metaList.build().toByteArray();
            StreamHelper.writeMessage(output, message);
            String result = StreamHelper.readString(input);

            if (result.equals(""))
            {
                System.err.println("No result of sendSimulatorMetadata().");
                return false;
            }

            if (result.equalsIgnoreCase("ok"))
            {
                p("result is " + result);
                return true;
            }

            p("result is " + result);
            return false;
        }
        catch (IOException ex)
        {
            ex.printStackTrace();
        }

        return false;
    }

    /**
     * Sends an timeupdate to the controller
     */
    public static void sendTimeUpdate()
    {
        Instruction timeUpdate = Instruction.newBuilder()
                .setId(CommunicationProtocol.newUUID())
                .setInstructionType(InstructionType.CLIENT_TIME_UPDATE)
                .setTime((long) (Time.time() * 1000)) // convert float to long ms
                .build();

        controllerCom.sendInstruction(timeUpdate);
    }

    /**
     * Sends an instruction when task is done
     * @param a id of first simulation item
     * @param b id of the second simulation item
     * @param type type of the task
     */
    public static void sendTaskDone(int a, int b, int type){
        Instruction taskDone = Instruction.newBuilder()
                .setId(CommunicationProtocol.newUUID())
                .setA(a)
                .setB(b)
                .setInstructionType(type)
                .build();
        controllerCom.sendInstruction(taskDone);
    }
    
     /**
     * Sends an instruction when task is done
     * @param a id of first simulation item
     * @param b id of the second simulation item
     * @param type type of the task
     * @param message message
     */
    public static void sendTaskDone(int a, int b, int type, String message){
                Instruction taskDone = Instruction.newBuilder()
                .setId(CommunicationProtocol.newUUID())
                .setA(a)
                .setB(a)
                .setMessage(message)
                .setInstructionType(type)
                .build();
        controllerCom.sendInstruction(taskDone);
    }
    
    /**
     * Sends an instruction when task is done
     * @param a id of the first simulation item
     * @param b id of the second simulation item
     * @param type type of the task
     * @param point a point (for placing containers)
     */
    public static void sendTaskDone(int a, int b, int type, Point3 point){
        Instruction taskDone = Instruction.newBuilder()
                .setId(CommunicationProtocol.newUUID())
                .setA(a)
                .setB(a)
                .setInstructionType(type)
                .setX(point.x)
                .setY(point.y)
                .setZ(point.z)
                .build();
        controllerCom.sendInstruction(taskDone);
    }
    
    private boolean instructionLoop()
    {
        p("Starting the instruction loop..");
        try
        {
            InputStream input = _socket.getInputStream();
            OutputStream output = _socket.getOutputStream();

            // Send empty message to start conversation..
            StreamHelper.writeMessage(output, new byte[]
            {
                0
            });

            while (shouldRun)
            {
                byte[] data = StreamHelper.readByteArray(input);
                byte[] response = controllerCom.processInput(data);

                StreamHelper.writeMessage(output, response);
            }

            p("End loop");
        }
        catch (IOException ex)
        {
            ex.printStackTrace();
            return false;
        }

        return true;
    }

    @Override
    public void run()
    {
        shouldRun = true;
        while (!start)
        {
        }
        while (shouldRun)//While shouldRun, when connection is lost, start listening for a new one
        {
            if (start())
            {
                if (sendSimulatorMetadata())
                {

                    if (instructionLoop())
                    {
                        p("Closed peacefully");
                    }
                    else
                    {
                        p("Lost connection during instructionloop");
                    }
                }
                else
                {
                    p("Error while initialising connection..");
                }
            }
            else
            {
                p("Closed forcefully");
            }

            try //Clean 
            {
                if (_socket != null)
                {
                    _socket.close();
                }
            }
            catch (Exception ex)
            {
                ex.printStackTrace();
            }

            isConnected = false;
        }
    }

    private static void p(String s)
    {
        System.out.println("[" + System.currentTimeMillis() + "] Sim: " + s);
    }
}
