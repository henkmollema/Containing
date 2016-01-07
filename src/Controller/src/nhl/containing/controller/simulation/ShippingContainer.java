package nhl.containing.controller.simulation;

import nhl.containing.controller.Point3;
import nhl.containing.networking.protobuf.AppDataProto;

/**
 *
 * @author henkmollema
 */
public class ShippingContainer
{
    /**
     * The name of the owner.
     */
    
    public String ownerName;
    
    /**
     * The number which identifies the container.
     */
    public int containerNumber;
    
    /**
     * The length element of the dimension of the container.
     */
    public String length;
    
    /**
     * The width element of the dimension of the container.
     */
    public String width;
    
    /**
     * The height element of the dimension of the container.
     */
    public String height;
    
    public Point3 position;
    
    /**
     * Weight of the container when it's empty.
     */
    public int weightEmpty;
    
    /**
     * Weight of the container when it's loaded.
     */
    public int weightLoaded;
    
    /**
     * The name of the content.
     */
    public String content;
    
    /**
     * The type of the content.
     */
    public String contentType;
    
    /**
     * The possible danger of the content.
     */
    public String contentDanger;
    
    /**
     * The ISO-code of the container.
     */
    public String iso;
    
    /**
     * The arrival shipment of the container.
     */
    public Shipment arrivalShipment;
    
    /**
     * The departure shipment of the container.
     */
    public Shipment departureShipment;
    
    /**
     * The current categorie the container is on
     */
    public volatile AppDataProto.ContainerCategory currentCategory = AppDataProto.ContainerCategory.REMAINDER;
}
