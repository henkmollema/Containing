package controller;

import org.simpleframework.xml.Element;

/**
 * Serves as base class for either an arrival or a departure.
 * 
 * @author henkmollema
 */
public abstract class ArrivalOrDeparture
{
    @Element(name = "datum")
    public ArrivalOrDepartureDate date;
    
    @Element(name = "tijd")
    public ArrivalOrDepartureTime time;
    
    @Element(name = "soort_vervoer")
    public String transportType;
    
    @Element(name = "bedrijf")
    public String company;
}
