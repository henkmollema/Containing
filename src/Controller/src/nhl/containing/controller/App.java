/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nhl.containing.controller;

import java.util.Date;
import nhl.containing.networking.protobuf.AppDataProto.*;
import nhl.containing.networking.protobuf.InstructionProto.*;

/**
 *
 * @author Niels
 */
public class App
{
    public static byte[] TestData(byte[] data)
    {     
        Instruction instruction = null;
        datablockApp.Builder builder = datablockApp.newBuilder();
        try
        {
            instruction = Instruction.parseFrom(data);
        }
        catch (Exception ex)
        {
            return builder.build().toByteArray();
        }
        ContainerGraphData.Builder b;
        switch(instruction.getA())
        {
            case 0:
                b = ContainerGraphData.newBuilder();
                b.setCategory(ContainerCategory.TRAIN);
                b.setAantal(100);
                builder.addGraphs(0,b.build());
                b.setCategory(ContainerCategory.TRUCK);
                b.setAantal(50);
                builder.addGraphs(b.build());
                b.setCategory(ContainerCategory.INLINESHIP);
                b.setAantal(500);
                builder.addGraphs(b.build());
                b.setCategory(ContainerCategory.SEASHIP);
                b.setAantal(900);
                builder.addGraphs(b.build());
                b.setCategory(ContainerCategory.REMAINDER);
                b.setAantal(0);
                builder.addGraphs(b.build());
                b.setCategory(ContainerCategory.STORAGE);
                b.setAantal(55);
                builder.addGraphs(b.build());
                break;
            case 1:
                b = ContainerGraphData.newBuilder();
                b.setCategory(ContainerCategory.TRAIN);
                b.setAantal(100);
                builder.addGraphs(b.build());
                b.setCategory(ContainerCategory.TRUCK);
                b.setAantal(50);
                builder.addGraphs(b.build());
                b.setCategory(ContainerCategory.INLINESHIP);
                b.setAantal(500);
                builder.addGraphs(b.build());
                b.setCategory(ContainerCategory.SEASHIP);
                b.setAantal(900);
                builder.addGraphs(b.build());
                break;
            case 2:
                b = ContainerGraphData.newBuilder();
                b.setCategory(ContainerCategory.TRAIN);
                b.setAantal(900);
                builder.addGraphs(b.build());
                b.setCategory(ContainerCategory.TRUCK);
                b.setAantal(500);
                builder.addGraphs(b.build());
                b.setCategory(ContainerCategory.INLINESHIP);
                b.setAantal(100);
                builder.addGraphs(b.build());
                b.setCategory(ContainerCategory.SEASHIP);
                b.setAantal(100);
                builder.addGraphs(b.build());
                break;
            case 3:
                b = ContainerGraphData.newBuilder();
                b.setCategory(ContainerCategory.TRAIN);
                b.setAantal(0);
                builder.addGraphs(b.build());
                b.setCategory(ContainerCategory.TRUCK);
                b.setAantal(0);
                builder.addGraphs(b.build());
                b.setCategory(ContainerCategory.INLINESHIP);
                b.setAantal(0);
                builder.addGraphs(b.build());
                b.setCategory(ContainerCategory.SEASHIP);
                b.setAantal(0);
                builder.addGraphs(b.build());
                break;
            case 4:
                ContainerDataListItem.Builder itemBuilder = ContainerDataListItem.newBuilder();
                itemBuilder.setCategory(ContainerCategory.TRAIN);
                itemBuilder.setEigenaar("Niels");
                itemBuilder.setID(1);
                builder.addItems(itemBuilder.build());
                itemBuilder.setCategory(ContainerCategory.AGV);
                itemBuilder.setEigenaar("Jens");
                itemBuilder.setID(2);
                builder.addItems(itemBuilder.build());
                itemBuilder.setCategory(ContainerCategory.SEASHIP);
                itemBuilder.setEigenaar("Henk");
                itemBuilder.setID(3);
                builder.addItems(itemBuilder.build());
                break;
            case 5:
                builder.setContainer(getContainer(instruction.getB()));
                break;
        }
        datablockApp test = builder.build();
        return test.toByteArray();
    }
    
    
    private static ContainerInfo getContainer(int id)
    {
        ContainerInfo.Builder builder = ContainerInfo.newBuilder();
        builder.setID(1)
                .setAanvoerMaatschappij("Test Maatschappij")
                .setAfvoerMaatschappij("Test 1 Maatschappij")
                .setBinnenkomstDatum(new Date().getTime())
                .setEigenaar("Niels")
                .setGewichtLeeg(3)
                .setGewichtVol(78)
                .setInhoud("Death Star")
                .setVertrekDatum(new Date().getTime())
                .setVervoerBinnenkomst(ContainerCategory.SEASHIP)
                .setVervoerVertrek(ContainerCategory.TRAIN);
        return builder.build();
    }
}
