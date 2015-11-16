package controller;

import java.net.*;
import java.nio.file.*;
import java.util.HashSet;

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
     * @throws java.lang.Exception when serialization fails.
     */
    public void run(String xmlFileName) throws Exception
    {
        RecordSet recordSet = parseXml(xmlFileName);

        Simulator sim = new Simulator(this);
        if (sim.start())
        {
            if (sim.init(null))
            {
                if (sim.play())
                {
                    for (Record record : recordSet.records)
                    {
                        sim.processRecord(record);
                    }
                }
            }
        }
    }

    private RecordSet parseXml(String xmlFileName) throws Exception
    {
        String xmlString = readXml(xmlFileName);
        RecordSet recordSet = XmlParser.parse(xmlString);
        
        if (recordSet == null)
        {
            throw new Exception("Something went wrong when deserializing the XML file. ");
        }

        if (hasDuplicateIds(recordSet))
        {
            throw new Exception("Record set contains duplicate ID's.");
        }

        System.out.println("Parsed " + recordSet.records.size() + " records");
        return recordSet;
    }

    private boolean hasDuplicateIds(RecordSet recordSet)
    {
        HashSet<String> hashSet = new HashSet<>();
        for (Record record : recordSet.records)
        {
            if (hashSet.contains(record.id))
            {
                return true;
            }
            hashSet.add(record.id);
        }
        return false;
    }

    private String readXml(String xmlFileName)
    {
        try
        {
            URI url = Main.class.getResource("XML/" + xmlFileName).toURI();
            return new String(Files.readAllBytes(Paths.get(url)));
        }
        catch (Exception e)
        {
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
