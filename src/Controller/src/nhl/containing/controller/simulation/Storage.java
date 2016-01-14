package nhl.containing.controller.simulation;

import nhl.containing.controller.Point3;

/**
 * Class for storage platform.
 *
 * @author Niels
 */
public class Storage extends Platform
{
    private StorageItem[][][] m_storageplaces = new StorageItem[6][6][45];

    /**
     * Constructor
     *
     * @param id id of storage
     */
    public Storage(int id)
    {
        super(id);
        for (int x = 0; x < m_storageplaces.length; x++)
        {
            for (int y = 0; y < m_storageplaces[0].length; y++)
            {
                for (int z = 0; z < m_storageplaces[0][0].length; z++)
                {
                    m_storageplaces[x][y][z] = new StorageItem(x, y, z);
                }
            }
        }
    }

    /**
     * Sets a container on a position
     *
     * @param container container
     * @param position position
     * @throws Exception when position is occupied
     */
    public void setContainer(ShippingContainer container, Point3 position) throws Exception
    {
        if (!checkPosition(position))
        {
            throw new Exception("Not valid Position");
        }
        StorageItem.place(this, container, position);
    }

    /**
     * Sets a container on a position
     *
     * @param container container
     * @param x x-position
     * @param y y-position
     * @param z z-position
     * @throws Exception when position is occupied
     */
    public void setContainer(ShippingContainer container, int x, int y, int z) throws Exception
    {
        this.setContainer(container, new Point3(x, y, z));
    }

    /**
     * Removes a container on a position
     *
     * @param position position
     */
    public void removeContainer(Point3 position)
    {
        StorageItem.Remove(this, position);
    }

    /**
     * Removes a container on a position
     *
     * @param x x-position
     * @param y y-position
     * @param z z-position
     */
    public void removeContainer(int x, int y, int z)
    {
        this.removeContainer(new Point3(x, y, z));
    }

    /**
     * Get container for a position
     *
     * @param position position of the container
     * @return container or null when no container is found
     */
    public ShippingContainer getContainer(Point3 position)
    {
        if (position.x > 7 || position.y > 7 || position.z > 46)
        {
            return null;
        }
        return StorageItem.find(this, position).getContainer();
    }

    /**
     * Get container for a position
     *
     * @param x x-position
     * @param y y-position
     * @param z z-position
     * @return container or null when no container is found
     */
    public ShippingContainer getContainer(int x, int y, int z)
    {
        return this.getContainer(new Point3(x, y, z));
    }

    /**
     * Gets the storagePlaces
     *
     * @return array with storageplaces
     */
    public StorageItem[][][] getStoragePlaces()
    {
        return m_storageplaces;
    }

    /**
     * Checks if position exists and if position is free
     *
     * @param position position
     * @return true when exists and free, otherwise false
     */
    private boolean checkPosition(Point3 position)
    {

        return position.x < 7 && position.y < 7 && position.z < 46 && StorageItem.find(this, position).isEmpty(); //zet 
    }
}