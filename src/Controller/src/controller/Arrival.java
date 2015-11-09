package controller;

import org.simpleframework.xml.Element;

/**
 * Defines the arrival information about a container.
 *
 * @author henkmollema
 */
public class Arrival extends ArrivalOrDeparture
{    
    /**
     * The 3-element vector representation of the position of the container at arrival.
     */
    @Element(name = "positie")
    public Vector3f position;
}
