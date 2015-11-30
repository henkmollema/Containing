package nhl.containing.managmentinterface.communication;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import nhl.containing.managmentinterface.MainActivity;
import nhl.containing.managmentinterface.navigationdrawer.ContainersFragment;
import nhl.containing.managmentinterface.navigationdrawer.GraphFragment;
import nhl.containing.networking.messaging.MessageReader;
import nhl.containing.networking.messaging.MessageWriter;
import nhl.containing.networking.protobuf.DataProto;
import nhl.containing.networking.protobuf.appDataProto;
import nhl.containing.networking.protocol.InstructionType;

/**
 * Created by Niels on 30-11-2015.
 */
public class Communicator_new implements Runnable{

    private Socket socket;
    private MainActivity mainActivity;
    private String host  = "127.0.0.1";
    private int port = 1337;
    private volatile boolean isRunning = true;
    private volatile DataProto.Instruction request = null;
    public Communicator_new(MainActivity mainActivity)
    {
        this.mainActivity = mainActivity;
    }

    @Override
    public void run()
    {
        try
        {
            socket = new Socket(host,port);
            BufferedInputStream input = new BufferedInputStream(socket.getInputStream());
            ByteArrayOutputStream dataStream = new ByteArrayOutputStream();
            OutputStream output = socket.getOutputStream();
            //write client info
            DataProto.ClientIdentity.Builder clientBuilder = DataProto.ClientIdentity.newBuilder();
            clientBuilder.setClientType(DataProto.ClientIdentity.ClientType.APP);
            MessageWriter.writeMessage(output, clientBuilder.build().toByteArray());
            //read status message
            DataProto.Instruction inst =  DataProto.Instruction.parseFrom(MessageReader.readByteArray(input,dataStream));
            if(inst.getInstructionType() != InstructionType.CLIENT_CONNECTION_OKAY)
                throw new Exception("Not connected");
            while(isRunning)
            {
                if(request != null)
                {
                    MessageWriter.writeMessage(output,request.toByteArray());
                    request = null;
                    try
                    {
                        appDataProto.datablockApp inputBlock = appDataProto.datablockApp.parseFrom(MessageReader.readByteArray(input,dataStream));
                        if(mainActivity.fragment instanceof GraphFragment)
                            ((GraphFragment) mainActivity.fragment).UpdateGraph(inputBlock);
                        else if(mainActivity.fragment instanceof ContainersFragment)
                            ((ContainersFragment) mainActivity.fragment).UpdateGraph(inputBlock);
                    }
                    catch (Exception e)
                    {

                    }
                }
            }
        }
        catch (Exception e)
        {
            //failed
        }
    }
}
