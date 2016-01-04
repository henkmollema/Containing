package nhl.containing.controller;

import java.net.*;
import java.nio.file.*;
import java.util.HashSet;
import nhl.containing.controller.simulation.*;

/**
 * Controller for the simulator.
 *
 * @author henkmollema
 */
public class SimulatorController
{
    private final Database _db;
    private SimulationContext _context;
    private SimulatorItems _simitems;

    public SimulatorController()
    {
        _db = new Database();
    }

    public SimulationContext getContext()
    {
        return _context;
    }
    
    public void setItems(SimulatorItems items)
    {
        _simitems = items;
        _context.setSimulatorItems(items);
    }
    
    public SimulatorItems getItems(){
        return _simitems;
    }

    /**
     * Starts running the simulator using the data in the specified XML file
     * name.
     *
     * @param xmlFileName The file name of the XML file. Format: [name].xml.
     *
     * @throws java.lang.Exception when serialization fails.
     */
    public void run(String xmlFileName) throws Exception
    {
        long start = System.currentTimeMillis();
        RecordSet recordSet = parseXml(xmlFileName);
        _context = SimulationContext.fromRecordSet(recordSet);
        long elapsed = System.currentTimeMillis() - start;
        p("Parsed and analyzed XML data. Elapsed: " + elapsed + "ms");

        writeAnalyzeResults();

        // Warm-up the simulation context.
        
        _context.getShipments();
        _context.getFirstShipment();

        Simulator sim = new Simulator(this);
        
        sim.start();
    }

    private void writeAnalyzeResults()
    {
        p("| Type | Inkomend | Uitgaand | Totaal |");
        p("| --- | --- | --- | --- |");
        p("| Zeeschip | " + _context.getSeaShips(true).size() + " | " + _context.getSeaShips(false).size() + " | " + _context.getSeaShips(null).size() + " |");
        p("| Binnenschip | " + _context.getInlandShips(true).size() + " | " + _context.getInlandShips(false).size() + " | " + _context.getInlandShips(null).size() + " |");
        p("| Vrachtwagen | " + _context.getTrucks(true).size() + " | " + _context.getTrucks(false).size() + " | " + _context.getTrucks(null).size() + " |");
        p("| Trein | " + _context.getTrains(true).size() + " | " + _context.getTrains(false).size() + " | " + _context.getTrains(null).size() + " |");
    }

    private static void p(String s)
    {
        System.out.println(s);
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
