package nhl.containing.controller.simulation;

import java.util.Comparator;
import java.util.Date;

/**
 * Information about a shipment.
 * 
 * @author henkmollema
 */
public class Shipment
{
    public Shipment(String key, boolean incoming)
    {
        this.key = key;
        this.incoming = incoming;
    }
    
    public String key;
    
    public boolean incoming;
    
    public Carrier carrier;
    
    /**
     * The date when the shipment arrives.
     */
    public Date date;
    
    /**
     * The date when the shipment should be processed.
     */
    public Date dateProcessed;
    
    /**
     * Indicates whether the shipment is processed by the controller.
     */
    public boolean processed;

    public static class ShipmentDateComparator implements Comparator<Shipment>
    {
        @Override
        public int compare(Shipment o1, Shipment o2)
        {
            return o1.date.before(o2.date) ? -1 : o1.date.equals(o2.date) ? 0 : 1;
        }
    }
}
