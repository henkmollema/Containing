package controller;

import java.net.*;
import java.nio.file.*;

/**
 * Controller for the system.
 *
 * @author henkmollema
 */
public class SystemController
{
    private final Database _db;

    public SystemController()
    {
        _db = new Database();
    }

    /**
     * Starts running the simulator using the data in the specified XML file
     * name.
     *
     * @param xmlFileName The file name of the XML file. Format: [name].xml.
     */
    public void run(String xmlFileName)
    {
        Simulator sim = new Simulator(this);
        if (sim.start()) {
            if (sim.init(null)) {
                if (sim.play()) {
                    String xmlString = readXml(xmlFileName);
                    RecordSet recordSet = XmlParser.parse(xmlString);
                    for (Record record : recordSet.records) {
                        sim.processRecord(record);
                    }
                }
            }
        }
    }

    private String readXml(String xmlFileName)
    {
        String xmlString;

        try {
            URI url = Main.class.getResource("XML/" + xmlFileName).toURI();
            return new String(Files.readAllBytes(Paths.get(url)));
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Marks the specified record object as processed
     *
     * @param record The record object to mark as processed.
     */
    public void markAsProcessed(Record record)
    {
        // todo: mark as processed in db.
        _db.saveRecord(record);
    }
}
