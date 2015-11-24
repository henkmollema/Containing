package nhl.containing.controller;

import java.util.List;
import org.simpleframework.xml.*;

/**
 * A collection of records.
 *
 * @author henkmollema
 */
@Root
public class RecordSet
{
    /**
     * Gets the collection of records for the current record set.
     */
    @ElementList(name = "records", inline = true)
    public List<Record> records;
}
