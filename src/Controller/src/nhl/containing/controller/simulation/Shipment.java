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
    
    public Date date;
}
