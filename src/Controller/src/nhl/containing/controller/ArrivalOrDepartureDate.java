package nhl.containing.controller;

import java.text.*;
import java.util.Date;
import java.util.logging.*;
import org.simpleframework.xml.*;

/**
 * Represents the date of arrival.
 *
 * @author henkmollema
 */
public class ArrivalOrDepartureDate
{
    /**
     * The day element of the arrival or departure date.
     */
    @Element(name = "d")
    public String day;
    
    /**
     * The month element of the arrival or departure date.
     */
    @Element(name = "m")
    public String month;
    
    /**
     * The year element of the arrival or departure date.
     */
    @Element(name = "j")
    public String year;

    /**
     * Returns the Date object for the arrival or departure date.
     * @return A Date object for the arrival or departure date.
     */
    public Date getDate()
    {
        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            return format.parse("20" + year + "-" + month + "-" + day);
        }
        catch (ParseException ex) {
            Logger.getLogger(ArrivalOrDepartureDate.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }
}
