/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nhl.containing.simulator.simulation;

/**
 *
 * @author sietse
 */
public final class Time {
    private static float m_timeScale = 1.0f;
    private static float m_fixedTimeScale = 0.02f; // 50 fps
    private static float m_deltaTime = 0.0f;
    private static float m_time = 0.0f;
    
    private static final float MIN_FIXED_TIME_SCALE = 0.005f;
    
    /**
     * Update time, only call this from main
     * @param deltaTime 
     */
    public static void _updateTime(float deltaTime) {
        m_deltaTime = deltaTime;
        m_time += deltaTime * m_timeScale;
    }
    
    /**
     * Set simulation timescale
     * @param t 
     */
    public static void setTimeScale(float t) {
        m_timeScale = Mathf.max(t, 0.0f);
    }
    /**
     * Set fixedtimescale
     * @param t 
     */
    public static void setFixedTimeScale(float t) {
        m_fixedTimeScale = Mathf.max(MIN_FIXED_TIME_SCALE, t);
    }
    
    /**
     * Get timescale
     * @return 
     */
    public static float timeScale() {
        return m_timeScale;
    }
    /***
     * Get fixedtmescale
     * @return 
     */
    public static float fixedTimeScale() {
        return m_fixedTimeScale;
    }
    
    /**
     * Get deltatime unscaled
     * @return 
     */
    public static float unscaledDeltaTime() {
        return m_deltaTime;
    }
    /**
     * Get deltatime scaled
     * @return 
     */
    public static float deltaTime() {
        return (m_deltaTime * m_timeScale);
    }
    
    /**
     * Get fixedDeltatime scaled
     * @return 
     */
    public static float fixedDeltaTime() {
        return (m_fixedTimeScale * m_timeScale);
    }
    
    public static float time() {
        return m_time;
    }
}
