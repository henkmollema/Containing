package nhl.containing.managmentinterface.data;

import nhl.containing.networking.protobuf.AppDataProto;

/**
 * Container item class for te listview and containerArrayAdapter
 * Created by Niels on 18-12-2015.
 */
public class ContainerListItem implements Comparable
{
    public int ID;
    public String eigenaar;
    public AppDataProto.ContainerCategory category;

    /**
     * Constructor
     * @param ID id of container
     * @param eigenaar owner of the container
     * @param category category of the container
     */
    public ContainerListItem(int ID,String eigenaar, AppDataProto.ContainerCategory category)
    {
        this.ID = ID;
        this.eigenaar = eigenaar;
        this.category = category;
    }

    /**
     * Compares this object to the specified object to determine their relative
     * order.
     *
     * @param another the object to compare to this instance.
     * @return a negative integer if this instance is less than {@code another};
     * a positive integer if this instance is greater than
     * {@code another}; 0 if this instance has the same order as
     * {@code another}.
     * @throws ClassCastException if {@code another} cannot be converted into something
     *                            comparable to {@code this} instance.
     */
    @Override
    public int compareTo(Object another) {
        ContainerListItem item = (ContainerListItem)another;
        return Integer.compare(this.ID,item.ID);
    }
}
