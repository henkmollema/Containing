package controller;

import org.simpleframework.xml.Element;

/**
 * Represents the dimensions of a container.
 *
 * @author henkmollema
 */
public class Dimensions
{
    /**
     * The length element of the dimension of the container.
     */
    @Element(name = "l")
    public String length;
    
    /**
     * The width element of the dimension of the container.
     */
    @Element(name = "b")
    public String width;
    
    /**
     * The height element of the dimension of the container.
     */
    @Element(name = "h")
    public String height;
}
