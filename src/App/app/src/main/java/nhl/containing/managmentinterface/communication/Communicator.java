package nhl.containing.managmentinterface.communication;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.widget.Toast;

import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;

import nhl.containing.managmentinterface.*;
import nhl.containing.managmentinterface.activity.*;
import nhl.containing.managmentinterface.navigationdrawer.*;

import nhl.containing.networking.messaging.StreamHelper;
import nhl.containing.networking.protobuf.AppDataProto.*;
import nhl.containing.networking.protobuf.InstructionProto.*;
import nhl.containing.networking.protobuf.ClientIdProto.*;
import nhl.containing.networking.protocol.*;

/**
 * Runnable for the communication between the App and the Controller
 */
public class Communicator implements Runnable{

    private Socket socket;
    private MainActivity mainActivity;
    private ContainerActivity containerActivity = null;
    private String host;
    private int port;
    private volatile boolean isRunning = true;
    private volatile Instruction request = null;
    private static Communicator instance;

    /**
     * Constructor of the Communication class
     * @param mainActivity Mainactivity
     * @throws Exception when there isn't a host/port or mainactivity is null
     */
    public Communicator(MainActivity mainActivity) throws Exception
    {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mainActivity);
        host = prefs.getString("Connection_Host", "127.0.0.1");
        port = Integer.parseInt(prefs.getString("Connection_Port", "1337"));
        if(host == null || port == -1)
            throw new Exception(mainActivity.getString(R.string.communicator_error_host_port));
        this.mainActivity = mainActivity;
        if(this.mainActivity == null)
            throw new Exception(mainActivity.getString(R.string.communicator_error_wrong));
        instance = this;
    }

    /**
     * Check if communicator is running
     * @return true when running, otherwise false
     */
    public boolean isRunning()
    {
        return isRunning;
    }

    /**
     * Place a request
     * @param instruction instruction to send to controller
     */
    public void setRequest(Instruction instruction)
    {
        this.request = instruction;
    }

    /**
     * Returns an instance of the communicator
     * @return communicator
     */
    public static Communicator getInstance()
    {
        return instance;
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
                //BufferedInputStream input = new BufferedInputStream(socket.getInputStream());
                OutputStream output = socket.getOutputStream();
                //write client info
                ClientIdentity.Builder clientBuilder = ClientIdentity.newBuilder();
                clientBuilder.setClientType(ClientIdentity.ClientType.APP);
                StreamHelper.writeMessage(output, clientBuilder.build().toByteArray());
                //read status message
                Instruction inst =  Instruction.parseFrom(StreamHelper.readByteArray(socket.getInputStream()));
                if(inst.getInstructionType() != InstructionType.CLIENT_CONNECTION_OKAY)
                    throw new Exception(mainActivity.getString(R.string.communicator_error_not_connected));
                byte[] bytes;
                while(isRunning)
                {
                    if(request != null)
                    {
                        try
                        {
                            StreamHelper.writeMessage(output,request.toByteArray());
                            request = null;
                            socket.setSoTimeout(5000);
                            System.out.println(socket.getSoTimeout());
                            bytes = StreamHelper.readByteArray(socket.getInputStream());
                            socket.setSoTimeout(0);
                            if(bytes.length <= 1)
                                throw new Exception("No data yet");
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
                            if(e.getMessage() != null && e.getMessage().equals("No data yet"))
                                mainActivity.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(mainActivity, R.string.communicator_no_data, Toast.LENGTH_SHORT).show();
                                        if (containerActivity != null)
                                            containerActivity.goBack();
                                        else
                                            mainActivity.completeRefresh.run();
                                    }
                                });
                            else{
                                e.printStackTrace();
                                mainActivity.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(mainActivity, R.string.communicator_error_no_data, Toast.LENGTH_SHORT).show();
                                        if(containerActivity != null)
                                            containerActivity.goBack();
                                        else
                                            mainActivity.completeRefresh.run();
                                    }
                                });
                            }
                        }
                    }
                }
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
                    Toast.makeText(mainActivity, R.string.communicator_error_settings, Toast.LENGTH_SHORT).show();
                    mainActivity.completeRefresh.run();
                }
            });
        }
    }
}
