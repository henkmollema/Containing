package controller;

import org.simpleframework.xml.Element;

/**
 * Represents the contents of a container.
 *
 * @author henkmollema
 */
public class Content
{
    /**
     * The name of the contents.
     */
    @Element(name = "naam")
    public String name;
    
    /**
     * The type of the contents.
     */
    @Element(name = "soort")
    public String type;
    
    /**
     * The possible danger of the contents.
     */
    @Element(name = "gevaar")
    public String danger;
}
