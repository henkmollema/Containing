package nhl.containing.controller.simulation;

/**
 * Used for persisting an AGV move instruction whenever all AGVs are busy.
 *
 * @author Niels
 */
public class SavedInstruction
{
    private Platform m_to;
    private Parkingspot m_spot;
    private AGV m_agv;

    /**
     * Constuctor
     *
     * @param to to platform
     * @param spot to parkingspot
     */
    public SavedInstruction(AGV agv, Platform to, Parkingspot spot)
    {
        m_to = to;
        m_spot = spot;
        m_agv = agv;
    }

    /**
     * Gets the platform
     *
     * @return platform
     */
    public Platform getPlatform()
    {
        return m_to;
    }

    /**
     * Gets the parkingspot
     *
     * @return parkingspot
     */
    public Parkingspot getParkingspot()
    {
        return m_spot;
    }

    /**
     * Gets the AGV
     *
     * @return AGV
     */
    public AGV getAGV()
    {
        return m_agv;
    }
}
