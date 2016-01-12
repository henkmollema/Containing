package nhl.containing.controller;

import nhl.containing.controller.networking.*;

/**
 * Provides interaction with the simulator.
 *
 * @author henkmollema
 */
public class Simulator {

    private final SimulatorController _controller;
    private Thread _thread;
    private Server _server;   

    public Simulator(SimulatorController controller) {
        _controller = controller;        
        _instance = this;
    } 
    
    private static Simulator _instance;    
    
    /**
     * Gets a instance of the {@code Simulator}.
     * 
     * @return A {@code Simulator}.
     */
    public static Simulator instance()
    {
        return _instance;
    }
    
    /**
     * Gets an instance of the associated {@code Server} class.
     * @return A {@code Server}.
     */
    public Server server()
    {
        return _server;
    }
    
    /**
     * Gets an instance of the simulator controller
     * @return simulator controller
     */
    public SimulatorController getController()
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
