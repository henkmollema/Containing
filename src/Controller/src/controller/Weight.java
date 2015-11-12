package controller;

import org.simpleframework.xml.Element;

/**
 * Represents the weight of a container.
 *
 * @author henkmollema
 */
public class Weight
{
    /**
     * Weight of the container when it's empty.
     */
    @Element(name = "leeg")
    public int empty;
    
    /**
     * Weight of the container when it's loaded.
     */
    @Element(name = "inhoud")
    public int loaded;
}
