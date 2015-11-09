package controller;

import org.simpleframework.xml.Element;

/**
 * Information about the owner of a container.
 *
 * @author henkmollema
 */
public class OwnerInfo
{
    /**
     * The name of the owner.
     */
    @Element(name = "naam")
    public String name;
    
    /**
     * The number which identifies the container.
     */
    @Element(name = "containernr")
    public int containerNumber;
}
