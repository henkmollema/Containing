package controller;

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
    }
}
