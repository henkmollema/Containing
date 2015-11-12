package controller;

import java.awt.Point;
import java.io.*;
import java.net.*;
import java.nio.channels.FileChannel;
import java.nio.file.*;
import java.util.logging.Level;
import java.util.logging.Logger;

@SuppressWarnings("all")
public class Main
{
    static
    {
        try
        {
            String tempDir = System.getProperty("java.io.tmpdir");
            File file = new File(Main.class.getResource("/lib/").toURI() + "JNITest.dll");
            File resFile = new File(tempDir + "JNITest.dll");
            if (!resFile.exists()) resFile.createNewFile();
            Main.copyFileToTemp(file.getPath().substring(5), resFile.getAbsolutePath());
            System.load(resFile.getAbsolutePath());
            //System.loadLibrary("JNITest");
        }
        catch (Exception ex)
        {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
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
    
    private static void copyFileToTemp(String source, String dest) throws IOException {
	FileChannel inputChannel = null;
	FileChannel outputChannel = null;
	try
        {
            inputChannel = new FileInputStream(source).getChannel();
            outputChannel = new FileOutputStream(dest).getChannel();
            outputChannel.transferFrom(inputChannel, 0, inputChannel.size());
	}
        catch (Exception ex)
        {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
        finally
        {
            inputChannel.close();
            outputChannel.close();
	}
}
}
