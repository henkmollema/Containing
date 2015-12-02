package nhl.containing.controller.simulation;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author henkmollema
 */
public abstract class Carrier
{
    public String company;
    
    public List<ShippingContainer> containers = new ArrayList();
}
