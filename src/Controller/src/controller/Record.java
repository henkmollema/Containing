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
     * Information about the departure.
     */
    @Element(name = "vertrek")
    public Departure departure;  
    
    /**
     * The name of the owner.
     */
    @Path("eigenaar")
    @Element(name = "naam")
    public String ownerName;
    
    /**
     * The number which identifies the container.
     */
    @Path("eigenaar")
    @Element(name = "containernr")
    public int containerNumber;
    
     /**
     * The length element of the dimension of the container.
     */
    @Path("afmetingen")
    @Element(name = "l")
    public String length;
    
    /**
     * The width element of the dimension of the container.
     */
    @Path("afmetingen")
    @Element(name = "b")
    public String width;
    
    /**
     * The height element of the dimension of the container.
     */
    @Path("afmetingen")
    @Element(name = "h")
    public String height;
    
    /**
     * Weight of the container when it's empty.
     */
    @Path("gewicht")
    @Element(name = "leeg")
    public int weigthEmpty;
    
    /**
     * Weight of the container when it's loaded.
     */
    @Path("gewicht")
    @Element(name = "inhoud")
    public int weightLoaded;
    
    /**
     * The name of the content.
     */
    @Path("inhoud")
    @Element(name = "naam")
    public String content;
    
    /**
     * The type of the content.
     */
    @Path("inhoud")
    @Element(name = "soort")
    public String contentType;
    
    /**
     * The possible danger of the content.
     */
    @Path("inhoud")
    @Element(name = "gevaar")
    public String contentDanger;
    
    /**
     * The ISO-code of the container.
     */
    @Element(name = "ISO")
    public String iso;
}
