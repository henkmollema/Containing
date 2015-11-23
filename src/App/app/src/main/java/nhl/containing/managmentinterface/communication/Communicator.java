package nhl.containing.managmentinterface.communication;

import com.google.protobuf.ByteString;
import com.jjoe64.graphview.series.DataPoint;


import nhl.containing.managmentinterface.data.ContainerProtos.*;

/**
 * Created by Niels on 16-11-2015.
 */
public class Communicator
{
    public static String test()
    {
        ContainerGraphList list = ContainerGraphList.newBuilder()
                .addContainers(ContainerGraphData.newBuilder().setAantal(10).setCategory(ContainerCategory.TRAIN).build())
                .addContainers(ContainerGraphData.newBuilder().setAantal(5).setCategory(ContainerCategory.TRUCK).build())
                .addContainers(ContainerGraphData.newBuilder().setAantal(20).setCategory(ContainerCategory.SEASHIP).build())
                .addContainers(ContainerGraphData.newBuilder().setAantal(3).setCategory(ContainerCategory.INLINESHIP).build())
                .addContainers(ContainerGraphData.newBuilder().setAantal(9).setCategory(ContainerCategory.STORAGE).build())
                .addContainers(ContainerGraphData.newBuilder().setAantal(12).setCategory(ContainerCategory.AGV).build())
                .addContainers(ContainerGraphData.newBuilder().setAantal(50).setCategory(ContainerCategory.REMAINDER).build())
                .build();

        return list.toByteString().toStringUtf8();
    }

    public static String test1()
    {
        ContainerGraphList list = ContainerGraphList.newBuilder()
                .addContainers(ContainerGraphData.newBuilder().setAantal(15).setCategory(ContainerCategory.TRAIN).build())
                .addContainers(ContainerGraphData.newBuilder().setAantal(20).setCategory(ContainerCategory.TRUCK).build())
                .addContainers(ContainerGraphData.newBuilder().setAantal(80).setCategory(ContainerCategory.SEASHIP).build())
                .addContainers(ContainerGraphData.newBuilder().setAantal(33).setCategory(ContainerCategory.INLINESHIP).build())
                .addContainers(ContainerGraphData.newBuilder().setAantal(91).setCategory(ContainerCategory.STORAGE).build())
                .addContainers(ContainerGraphData.newBuilder().setAantal(69).setCategory(ContainerCategory.AGV).build())
                .addContainers(ContainerGraphData.newBuilder().setAantal(15).setCategory(ContainerCategory.REMAINDER).build())
                .build();

        return list.toByteString().toStringUtf8();
    }


    public static DataPoint[] getData(int index)
    {
        ByteString received = null;
        switch (index)
        {
            case 0:
                received = ByteString.copyFromUtf8(test()); //make communication method
                break;
            case 1:
                received = ByteString.copyFromUtf8(test1()); //make communication method
                break;
        }
        ContainerGraphList list;
        try
        {
             list = ContainerGraphList.parseFrom(received);
        }
        catch (Exception e)
        {
            return new DataPoint[0];
        }
        DataPoint[] returnlist = new DataPoint[list.getContainersList().size()];
        for(int i = 0; i < list.getContainersList().size();i++)
        {
            ContainerGraphData data = list.getContainersList().get(i);
            DataPoint point = new DataPoint(data.getCategory().getNumber(),data.getAantal());
            returnlist[i] = point;
        }
        return returnlist;
    }
}
