package nhl.containing.controller.simulation;

import java.util.Date;

/**
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
}
