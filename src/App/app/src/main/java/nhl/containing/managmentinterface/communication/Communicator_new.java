package nhl.containing.managmentinterface.communication;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;

import nhl.containing.managmentinterface.*;
import nhl.containing.managmentinterface.navigationdrawer.*;
import nhl.containing.networking.messaging.*;
import nhl.containing.networking.protobuf.AppDataProto.*;
import nhl.containing.networking.protobuf.InstructionProto.*;
import nhl.containing.networking.protobuf.ClientIdProto.*;
import nhl.containing.networking.protocol.CommunicationProtocol;
import nhl.containing.networking.protocol.InstructionType;

/**
 * Runnable for the communication between the App and the Controller
 */
public class Communicator_new implements Runnable{

    private Socket socket;
    private MainActivity mainActivity;
    private ContainerActivity containerActivity = null;
    private String host  = "127.0.0.1";
    private int port = 1337;
    private volatile boolean isRunning = true;
    private volatile Instruction request = null;

    /**
     * Constructor of the Communication class
     * @param mainActivity Mainactivity
     * @throws Exception when there isn't a host/port or mainactivity is null
     */
    public Communicator_new(MainActivity mainActivity) throws Exception
    {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mainActivity);
        host = prefs.getString("Connection_Host", null);
        port = Integer.parseInt(prefs.getString("Connection_Port", "-1"));
        if(host == null || port == -1)
            throw new Exception("Host or Port are not defined, Please go to settings to add a Host and Port");
        this.mainActivity = mainActivity;
        if(this.mainActivity == null)
            throw new Exception("Something went wrong");
    }

    /**
     * Check if communicator is running
     * @return
     */
    public boolean isRunning()
    {
        return isRunning;
    }

    /**
     * Place a request
     * @param instruction
     */
    public void setRequest(Instruction instruction)
    {
        this.request = instruction;
    }

    /**
     * Place a container acivity and do a request
     * @param containerActivity container activity
     */
    public void setContainerActivity(ContainerActivity containerActivity)
    {
        this.containerActivity = containerActivity;
        Instruction.Builder instructionBuilder = Instruction.newBuilder();
        instructionBuilder.setInstructionType(InstructionType.APP_REQUEST_DATA);
        instructionBuilder.setId(CommunicationProtocol.newUUID());
        instructionBuilder.setA(5);
        instructionBuilder.setB(containerActivity.ID);
        request = instructionBuilder.build();
    }

    /**
     * Stops the runnable
     */
    public void stop()
    {
        this.isRunning = false;
    }

    /**
     * Removes the container activity
     */
    public void detachContainerActivity()
    {
        this.containerActivity = null;
    }

    /**
     * Communication to Controller
     */
    @Override
    public void run()
    {
        try
        {
            while(isRunning)
            {
                socket = new Socket();
                socket.connect(new InetSocketAddress(host,port),3000);
                BufferedInputStream input = new BufferedInputStream(socket.getInputStream());
                ByteArrayOutputStream dataStream = new ByteArrayOutputStream();
                OutputStream output = socket.getOutputStream();
                //write client info
                ClientIdentity.Builder clientBuilder = ClientIdentity.newBuilder();
                clientBuilder.setClientType(ClientIdentity.ClientType.APP);
                MessageWriter.writeMessage(output, clientBuilder.build().toByteArray());
                //read status message
                Instruction inst =  Instruction.parseFrom(MessageReader.readByteArray(input,dataStream));
                if(inst.getInstructionType() != InstructionType.CLIENT_CONNECTION_OKAY)
                    throw new Exception("Not connected");
                byte[] bytes;
                while(isRunning)
                {
                    if(request != null)
                    {
                        try
                        {
                            MessageWriter.writeMessage(output,request.toByteArray());
                            request = null;
                            bytes = MessageReader.readByteArray(input,dataStream);
                            datablockApp inputBlock = datablockApp.parseFrom(bytes);
                            if(containerActivity != null)
                                containerActivity.setData(inputBlock);
                            else if(mainActivity.fragment instanceof GraphFragment)
                                ((GraphFragment) mainActivity.fragment).UpdateGraph(inputBlock);
                            else if(mainActivity.fragment instanceof ContainersFragment)
                                ((ContainersFragment) mainActivity.fragment).UpdateGraph(inputBlock);
                        }
                        catch (SocketException s)
                        {
                            break;
                        }
                        catch (Exception e)
                        {
                            e.printStackTrace();
                            mainActivity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(mainActivity, "Couldn't get data", Toast.LENGTH_SHORT).show();
                                    if(containerActivity != null)
                                        containerActivity.goBack();
                                    else
                                        mainActivity.completeRefresh.run();
                                }
                            });
                        }
                    }
                }
                input.close();
                output.close();
                socket.close();
            }
        }
        catch (Exception e)
        {
            isRunning = false;
            mainActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(mainActivity, "Couldn't connect to Host. Please check your settings", Toast.LENGTH_SHORT).show();
                    mainActivity.completeRefresh.run();
                }
            });
        }
    }
}
