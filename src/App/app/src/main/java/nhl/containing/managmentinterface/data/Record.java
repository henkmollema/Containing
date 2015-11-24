package nhl.containing.managmentinterface.data;

import java.util.Date;

/**
 * Record class for saving of Container records
 */
public class Record
{
    public Date arrivalDate;
    public Transport arrival;
    public String arrivalCompany;
    public String owner;
    public Date departureDate;
    public Transport departure;
    public String departureCompany;
    public float weight;
    public String content;

    /**
     * Creates a Record object
     * @param arrivalDate date of arrival
     * @param arrival Transport type of arrival
     * @param arrivalCompany arrival company
     * @param owner container owner
     * @param departureDate date of departure
     * @param departure Transport type of departure
     * @param departureCompany departure company
     * @param weight weight of the container
     * @param content content of the container
     */
    public Record(Date arrivalDate,int arrival,String arrivalCompany,String owner,Date departureDate,int departure,String departureCompany, float weight,String content)
    {
        this.arrivalDate = arrivalDate;
        this.arrival = Transport.values()[arrival];
        this.arrivalCompany = arrivalCompany;
        this.owner = owner;
        this.departureDate = departureDate;
        this.departure = Transport.values()[departure];
        this.departureCompany = departureCompany;
        this.weight = weight;
        this.content = content;
    }
}
