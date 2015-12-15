package nhl.containing.controller;

import nhl.containing.controller.networking.*;
import nhl.containing.networking.protocol.*;

/**
 * Provides interaction with the simulator.
 *
 * @author henkmollema
 */
public class Simulator {

    private final SimulatorController _controller;
    private Thread _thread;
    private Server _server;
    //private InstructionDispatcher _instructionDispatcher;

    /*public CommunicationProtocol communication() {
        return _server.getComProtocol();
    }*/

    public Simulator(SimulatorController controller) {
        _controller = controller;
        

    }
    
    /**
     * Gets an instance of the simulator controller
     * @return simulator controller
     */
    public SimulatorController getSimulatorController()
    {
        return _controller;
    }

    /**
     * Starts the simulator.
     *
     * @return true if the simulator initialized successfully; otherwise, false.
     */
    public boolean start() {
        _server = new Server(this); //Needs a reference to this for instruction dispatching

        _thread = new Thread(_server);
        _thread.setName("Networking Controller");
        //_thread.setDaemon(true);
        _thread.start();

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
    public boolean init(Object config) {
        // todo
        return true;
    }

    /**
     * Starts playing the simulator.
     *
     * @return true if playing started successfully; otherwhise, false.
     */
    public boolean play() {
        // todo
        return true;
    }

    /**
     * Processes the specified record in the simulator.
     *
     * @param record The record object to process.
     */
    public void processRecord(Record record) {
        // todo: send to simulator.

        _controller.markAsProcessed(record);
    }

    /**
     * Processes a (console)command given to the controller by the simulator.
     *
     * @param command The command string to process.
     */
    public String parseCommand(String command) {
        String result;
        //.. do something with command string

        result = "Parsed command '" + command + "'";
        return result;
    }
}
