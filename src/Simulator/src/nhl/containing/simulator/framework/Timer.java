/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nhl.containing.simulator.framework;

import nhl.containing.simulator.framework.Callback;
import nhl.containing.simulator.framework.Time;

/**
 *
 * @author sietse
 */
public class Timer {
    private boolean m_active = false;
    private float m_startTime = 0.0f;
    public float lifeTime = 1.0f;
    
    /**
     * Constructor
     * @param lifeTime 
     */
    public Timer(float lifeTime) {
        this.lifeTime = lifeTime;
    }
    
    /**
     * Start timer if not started
     */
    public void start() {
        if (!m_active)
            reset();
    }
    /**
     * Reset and Start timer
     */
    public void reset() {
        m_active = true;
        m_startTime = Time.time();
    }
    /**
     * Stop timer
     */
    public void stop() {
        m_active = false;
    }
    
    /**
     * Is finished
     * @param stopTimer
     * @return 
     */
    public boolean finished(boolean stopTimer) {
        if (finished()) {
            if (stopTimer)
                stop();
            return true;
        }
        return false;
    }
    /**
     * Is finished
     * @return 
     */
    public boolean finished() {
        return (m_active && ((Time.time() - m_startTime) > lifeTime));
    }
    /**
     * Is active
     * @return 
     */
    public boolean active() {
        return m_active;
    }
}
