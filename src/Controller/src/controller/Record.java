package controller;

import org.simpleframework.xml.*;

/**
 * Represents a record with information about a container shipment.
 *
 * @author henkmollema
 */
@Root(strict = false)
public class Record
{
    /**
     * The ID of the record.
     */
    @Attribute(name = "id")
    public String id;
    
    /**
     * Information about the arrival.
     */
    @Element(name = "aankomst")
    public Arrival arrival;
    
    /**
     * Information about the owner.
     */
    @Element(name = "eigenaar")
    public OwnerInfo owner;
    
    /**
     * Information about the departure.
     */
    @Element(name = "vertrek")
    public Departure departure;
    
    /**
     * The dimensions of the container.
     */
    @Element(name = "afmetingen")
    public Dimensions dimensions;
    
    /**
     * The weight of the container.
     */
    @Element(name = "gewicht")
    public Weight weight;
    
    /**
     * The content of the container.
     */
    @Element(name = "inhoud")
    public Content contents;
    
    /**
     * The ISO-code of the container.
     */
    @Element(name = "ISO")
    public String iso;
}
