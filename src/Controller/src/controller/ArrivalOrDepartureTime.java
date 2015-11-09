package controller;

import org.simpleframework.xml.Element;

/**
 * Represents the time of arrival or departure.
 *
 * @author henkmollema
 */
public class ArrivalOrDepartureTime
{
    /**
     * The from part of the arrival or departure time.
     */
    @Element(name = "van")
    public String from;
    
    /**
     * The until part of the arrival or departure time.
     */
    @Element(name = "tot")
    public String until;
}
