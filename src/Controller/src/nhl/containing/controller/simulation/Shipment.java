package nhl.containing.controller.simulation;

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
}
