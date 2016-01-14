package nhl.containing.controller.simulation;

import java.util.ArrayList;
import java.util.List;
import nhl.containing.controller.Point3;

/**
 * Base class for a carrier.
 *
 * @author henkmollema
 */
public abstract class Carrier
{
    public String company;
    public List<ShippingContainer> containers = new ArrayList();

    /**
     * Get shipping container by position
     *
     * @param x x-position
     * @param y y-position
     * @param z z-position
     * @return shipping container
     */
    public ShippingContainer getByPosition(int x, int y, int z)
    {
        return getByPosition(new Point3(x, y, z));
    }

    /**
     * Get shipping container by position
     *
     * @param position position
     * @return shipping container
     */
    public ShippingContainer getByPosition(Point3 position)
    {
        for (ShippingContainer container : containers)
        {
            if (container.position.equals(position))
            {
                return container;
            }
        }
        return null;
    }

    public abstract Point3 getPosition(ShippingContainer container);
}
