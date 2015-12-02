package nhl.containing.controller.simulation;

import nhl.containing.controller.Vector3f;

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
    
    public Vector3f position;
    
    /**
     * Weight of the container when it's empty.
     */
    public int weigthEmpty;
    
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
}
