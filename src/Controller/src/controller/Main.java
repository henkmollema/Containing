package controller;

import java.awt.Point;
import java.io.*;
import java.net.*;
import java.nio.file.*;

public class Main
{
    static
    {
        System.loadLibrary("JNITest");
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args)
    {
        String xmlString;

        try {
            URI url = Main.class.getResource("XML/xml2.xml").toURI();
            xmlString = new String(Files.readAllBytes(Paths.get(url)));
        }
        catch (URISyntaxException | IOException e) {
            e.printStackTrace();
            return;
        }

        RecordSet recordSet = XmlParser.parse(xmlString);
        for (Record r : recordSet.records) {
            // process each record..
        }
        
        JNITest.helloFromC();
        int[] iA = { 5, 6, 8};
        System.out.println("average: " + JNITest.avgFromC(iA));
        System.out.println("average int: " + JNITest.intFromC(iA));
        Integer i = JNITest.integerFromC(5);
        System.out.println(i);
        Point point = JNITest.pointInC(5, 5);
        System.out.println("x: " + point.x + " y: " + point.y);
        JNITest test = new JNITest();
        test.changeNumberInC();
        System.out.println(test.getNumber());
        
    }
}
