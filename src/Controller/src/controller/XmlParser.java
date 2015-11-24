package controller;

import java.util.logging.*;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

/**
 * Provides methods for parsing XML strings with container records.
 *
 * @author henkmollema
 */
public class XmlParser
{
    /**
     * Parses the specified XML string to a record set with a collection of record objects.
     *
     * @param xmlString A string containing the XML input.
     *
     * @return A record set with a collection of records.
     */
    public static RecordSet parse(String xmlString) throws Exception
    {
        try {
            Serializer serializer = new Persister();
            return serializer.read(RecordSet.class, xmlString);
        }
        catch (Exception ex) {
            throw new Exception("Error when parsing the XML file. It's probably invalid.", ex);
        }
    }
}
