package controller;

/**
 * Provides interaction with the simulator.
 *
 * @author henkmollema
 */
public class Simulator
{
    private final SystemController _controller;

    public Simulator(SystemController controller)
    {
        _controller = controller;
    }

    /**
     * Starts the simulator.
     *
     * @return true if the simulator initialized successfully; otherwise, false.
     */
    public boolean start()
    {
        // todo
        return true;
    }

    /**
     * Initializes the simulator, optionally with the specified config.
     *
     * @param config The configuration for the simulator.
     *
     * @return true if the simulator initialized successfully; otherwise, false.
     */
    public boolean init(Object config)
    {
        // todo
        return true;
    }

    /**
     * Starts playing the simulator.
     *
     * @return true if playing started successfully; otherwhise, false.
     */
    public boolean play()
    {
        // todo
        return true;
    }

    /**
     * Processes the specified record in the simulator.
     *
     * @param record The record object to process.
     */
    public void processRecord(Record record)
    {
        // todo: send to simulator.

        _controller.markAsProcessed(record);
    }
}
