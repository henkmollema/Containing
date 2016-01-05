package nhl.containing.networking.protocol;

/**
 * Contains instruction types.
 *
 * @author henkmollema
 */
public class InstructionType
{
    public static final int CONSOLE_COMMAND = 1;
    public static final int MOVE_AGV = 2;
    public static final int APP_REQUEST_DATA = 3;
    public static final int CLIENT_CONNECTION_OKAY = 4;
    public static final int CLIENT_TIME_UPDATE = 5;
    public static final int ARRIVAL_TRAIN = 6;
    public static final int ARRIVAL_SEASHIP = 7;
    public static final int ARRIVAL_INLANDSHIP = 8;
    public static final int ARRIVAL_TRUCK = 9;
    public static final int DEPARTMENT_TRAIN = 10;
    public static final int DEPARTMENT_SEASHIP = 11;
    public static final int DEPARTMENT_INLANDSHIP = 12;
    public static final int DEPARTMENT_TRUCK = 13;
    public static final int CRANE_TO_STORAGE_READY = 14;
    public static final int CRANE_TO_STORAGE = 15;
    public static final int CRANE_TO_AGV_READY = 16;
    public static final int CRANE_TO_AGV = 17;
    public static final int CRANE_TO_DEPARTMENT_READY = 18;
    public static final int CRANE_TO_DEPARTMENT = 19;
    public static final int AGV_READY = 20;
    public static final int SHIPMENT_ARRIVED = 21;
    public static final int SHIPMENT_MOVED = 22;
    public static final int PLACE_CRANE = 23;
    public static final int PLACE_CRANE_READY = 24;
}
