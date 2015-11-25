/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nhl.containing.simulator.simulation;

import nhl.containing.simulator.simulation.Callback;
import nhl.containing.simulator.simulation.Time;

/**
 *
 * @author sietse
 */
public class Timer {
    private boolean m_active = false;
    private float m_startTime = 0.0f;
    public float lifeTime = 1.0f;
    
    public Timer(float lifeTime) {
        this.lifeTime = lifeTime;
    }
    
    public void start() {
        if (!m_active)
            reset();
    }
    public void reset() {
        m_active = true;
        m_startTime = Time.time();
    }
    public void stop() {
        m_active = false;
    }
    
    public boolean finished(boolean stopTimer) {
        if (finished()) {
            stop();
            return true;
        }
        return false;
    }
    public boolean finished() {
        return (m_active && ((Time.time() - m_startTime) > lifeTime));
    }
    public boolean active() {
        return m_active;
    }
}
