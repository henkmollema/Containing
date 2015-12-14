package nhl.containing.simulator.framework;

import nhl.containing.simulator.simulation.Main;

/**
 * This is a class that updates
 * You will need to register the object
 * to Main.java (Main.register(<behaviour_class>)) Or
 * in the main class itsself, or create the object and call "_init"
 * @author sietse
 */
public abstract class Behaviour {

    private boolean m_initialized;          // Check if script is not called update yet
    private boolean m_wasEnabled;           // Enabled previous frame, used for one frame check

    public boolean enabled;                 // Script is enabled

    /**
     * Constructor
     */
    public Behaviour() {
        m_wasEnabled = enabled = true;
        m_initialized = false;
    }
    /**
     * Constructor
     * @param enabled 
     */
    public Behaviour(boolean enabled) {
        m_wasEnabled = this.enabled = enabled;
        m_initialized = false;
    }

    /**
     * Registers the script to main
     */
    public final void _baseInit() {
        Main.register(this);
    }
    /**
     * 
     */
    public final void _baseAwake() {
        awake();
    }
    /**
     * Called every frame
     */
    public final void _baseUpdate() {

        // Call raw update
        rawUpdate();

        // Enable/Disable check
        if (enabled) {
            if (!m_wasEnabled) { // Script has been enabled
                onEnable();
                m_wasEnabled = true;
            }
        } else {
            if (m_wasEnabled) { // Script has been disabled
                onDisable();
                m_wasEnabled = false;
            }
        }

        if (!_canUpdate())
            return; // Stop if is disabled or timescale is zero

        if (!m_initialized) {
            // This is the first frame the script has been called
            start();
            m_initialized = true;
        }
        
        // Update script
        update();
    }
    /**
     * Called every end of frame
     */
    public final void _baseLateUpdate() {
        if (!_canUpdate())
            return;
        lateUpdate();
    }
    /**
     * Calls at a fixed time rate
     */
    public final void _baseFixedUpdate() {
        if (!_canUpdate())
            return;
        fixedUpdate();
    }
    /**
     * Called at a fixed time rate without timescaling
     */
    public final void _baseRawFixedUpdate() {
        rawFixedUpdate();
    }

    /**
     * Checks if it is (un)safe to continue
     * @return is unsafe to continue
     */
    private boolean _canUpdate() {
        return enabled && (Time.timeScale() > 0.0f);
    }

    /**
     * Calls first frame on enabling
     */
    public void onEnable() { }
    /**
     * Calls first frame on disabling
     */
    public void onDisable() { }

    /**
     * Calls on register
     */
    public void awake() { }
    /**
     * Calls first frame active
     */
    public void start() { }
    /**
     * Calls every frame when active
     */
    public void update() { }
    /**
     * Calls every end of frame when active
     */
    public void lateUpdate() { }

    /**
     * Calls every frame wheter active or not
     */
    public void rawUpdate() { }
    /**
     * Calls at a fixed time rate when active
     */
    public void fixedUpdate() { }
    /**
     * Calls at a fixed time rate without timescaling
     */
    public void rawFixedUpdate() { }
}
