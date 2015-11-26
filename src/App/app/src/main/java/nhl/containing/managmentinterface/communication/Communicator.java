package nhl.containing.managmentinterface.communication;

import com.google.protobuf.ByteString;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import nhl.containing.managmentinterface.data.ContainerProtos.*;

/**
 * Created by Niels on 16-11-2015.
 */
public class Communicator
{
    /**
     * [FOR TESTING PURPOSE ONLY]
     * Gives serialized string
     * @return string
     */
    private static String test()
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

    /**
     * [FOR TESTING PURPOSE ONLY]
     * Gives serialized string
     * @return string
     */
    private static String test1()
    {
        ContainerGraphList list = ContainerGraphList.newBuilder()
                .addContainers(ContainerGraphData.newBuilder().setAantal(15).setCategory(ContainerCategory.TRAIN).build())
                .addContainers(ContainerGraphData.newBuilder().setAantal(20).setCategory(ContainerCategory.TRUCK).build())
                .addContainers(ContainerGraphData.newBuilder().setAantal(80).setCategory(ContainerCategory.SEASHIP).build())
                .addContainers(ContainerGraphData.newBuilder().setAantal(33).setCategory(ContainerCategory.INLINESHIP).build())
                .build();

        return list.toByteString().toStringUtf8();
    }

    /**
     * [FOR TESTING PURPOSE ONLY]
     * Gives serialized string
     * @return string
     */
    private static String testContainerList()
    {
        ContainerDataList list = ContainerDataList.newBuilder()
                .addItems(ContainerDataListItem.newBuilder().setEigenaar("Niels").setID(1).setCategory(ContainerCategory.REMAINDER).build())
                .addItems(ContainerDataListItem.newBuilder().setEigenaar("Sietse").setID(2).setCategory(ContainerCategory.INLINESHIP).build())
                .addItems(ContainerDataListItem.newBuilder().setEigenaar("Henk").setID(3).setCategory(ContainerCategory.SEASHIP).build())
                .addItems(ContainerDataListItem.newBuilder().setEigenaar("Coen").setID(4).setCategory(ContainerCategory.AGV).build())
                .build();

        return list.toByteString().toStringUtf8();
    }

    /**
     * [FOR TESTING PURPOSE ONLY]
     * Gives serialized string
     * @return string
     */
    private static ByteString testContainer()
    {
        ContainerInfo info = ContainerInfo.newBuilder()
                .setID(1)
                .setAanvoerMaatschappij("Test Maatschappij")
                .setAfvoerMaatschappij("Test 1 Maatschappij")
                .setBinnenkomstDatum(new Date().getTime())
                .setEigenaar("Niels")
                .setGewichtLeeg(3)
                .setGewichtVol(78)
                .setInhoud("Death Star")
                .setVertrekDatum(new Date().getTime())
                .setVervoerBinnenkomst(ContainerCategory.SEASHIP)
                .setVervoerVertrek(ContainerCategory.TRAIN)
                .build();
        return info.toByteString();
    }


    public static ContainerInfo getContainerInfo(int id) throws Exception
    {
        //add communication with controller
        ContainerInfo info = ContainerInfo.parseFrom(testContainer());
        return info;
    }



    public static List<ContainerDataListItem> getContainerList()
    {
        ContainerDataList list;
        try
        {
            list = ContainerDataList.parseFrom(ByteString.copyFromUtf8(testContainerList()));
        }
        catch (Exception e){return new ArrayList<>();}
        return list.getItemsList();
    }

    public static List<Integer> getData(int index) throws Exception
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
        List<Integer> returnlist = new ArrayList<>();
        ContainerGraphList list = ContainerGraphList.parseFrom(received);
        for(int i = 0; i < list.getContainersList().size();i++)
        {
            ContainerGraphData data = list.getContainersList().get(i);
            returnlist.add(data.getCategory().getNumber(),data.getAantal());
        }
        return returnlist;
    }
}
