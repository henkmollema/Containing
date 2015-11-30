package nhl.containing.managmentinterface.communication;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.net.Socket;

import nhl.containing.networking.messaging.MessageReader;
import nhl.containing.networking.messaging.MessageWriter;
import nhl.containing.networking.protobuf.DataProto;

/**
 * Created by Niels on 30-11-2015.
 */
public class Communicator_new implements Runnable{

    private Socket socket;
    private String host  = "127.0.0.1";
    private int port = 1337;
    private volatile boolean isRunning = true;

    public Communicator_new()
    {

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
            MessageReader.readByteArray(input,dataStream);
            //check message
            while(isRunning)
            {
                
            }
        }
        catch (Exception e)
        {
            //failed
        }
    }
}
