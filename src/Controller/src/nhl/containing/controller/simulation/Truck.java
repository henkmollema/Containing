package nhl.containing.controller.simulation;

import nhl.containing.controller.Point3;

/**
 * A truck carrier.
 *
 * @author henkmollema
 */
public class Truck extends Carrier
{
    /**
     * Gets position of the container for the simulator
     *
     * @param container container
     * @return position of the container on the carrier
     */
    @Override
    public Point3 getPosition(ShippingContainer container)
    {
        return new Point3();
    }
}
