package nhl.containing.controller.simulation;

import nhl.containing.controller.Point3;

/**
 * Class for a storage item.
 *
 * @author Niels
 */
public class StorageItem
{
    private ShippingContainer m_container = null;
    private final Point3 m_position;

    /**
     * Constructor
     *
     * @param position position
     */
    public StorageItem(Point3 position)
    {
        m_position = position;
    }

    /**
     * Constructor
     *
     * @param x x-position
     * @param y y-position
     * @param z z-position
     */
    public StorageItem(int x, int y, int z)
    {
        m_position = new Point3(x, y, z);
    }

    /**
     * Sets a container
     *
     * @param container container
     * @throws Exception when place is occupied
     */
    public void setContainer(ShippingContainer container) throws Exception
    {
        if (!isEmpty())
        {
            throw new Exception("Already in use");
        }
        container.departPosition = m_position;
        m_container = container;
    }

    /**
     * Gives the container on this position
     *
     * @return container or null when no container is on this position
     */
    public ShippingContainer getContainer()
    {
        return m_container;
    }

    /**
     * Sets empty
     */
    public void setEmpty()
    {
        m_container = null;
    }

    /**
     * check if empty
     *
     * @return true when empty, otherwise false
     */
    public boolean isEmpty()
    {
        return m_container == null;
    }

    /**
     * gets storageitem on a position
     *
     * @param storage storage
     * @param p position
     * @return storageitem
     */
    public static StorageItem find(Storage storage, Point3 p)
    {
        return storage.getStoragePlaces()[p.x][p.y][p.z];
    }

    /**
     * Places a container in a storage on a position
     *
     * @param storage storage
     * @param container container
     * @param p position
     * @throws Exception when occupied
     */
    public static void place(Storage storage, ShippingContainer container, Point3 p) throws Exception
    {
        storage.getStoragePlaces()[p.x][p.y][p.z].setContainer(container);
    }

    /**
     * Removes a container in a storage on a position
     *
     * @param storage storage
     * @param p position
     */
    public static void Remove(Storage storage, Point3 p)
    {
        storage.getStoragePlaces()[p.x][p.y][p.z].setEmpty();
    }
}
