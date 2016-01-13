package nhl.containing.controller;

/**
 *  Time class
 * @author sietse
 */
public final class Time {
    private static double m_timeScale = 1.0;
    private static double m_fixedTimeScale = 0.02; // 50 fps
    private static double m_deltaTime = 0.0;
    private static double m_time = 0.0;
    
    private static final double MIN_FIXED_TIME_SCALE = 0.005;
    
    /**
     * Update time, only call this from main
     * @param deltaTime 
     */
    public static void _updateTime(double deltaTime) {
        m_deltaTime = deltaTime;
        m_time += deltaTime * m_timeScale;
    }
    
    /**
     * Set simulation timescale
     * @param t 
     */
    public static void setTimeScale(double t) {
        m_timeScale = Math.max(t, 0.0);
    }
    /**
     * Set fixedtimescale
     * @param t 
     */
    public static void setFixedTimeScale(double t) {
        m_fixedTimeScale = Math.max(MIN_FIXED_TIME_SCALE, t);
    }
    
    /**
     * Get timescale
     * @return 
     */
    public static double timeScale() {
        return m_timeScale;
    }
    /***
     * Get fixedtmescale
     * @return 
     */
    public static double fixedTimeScale() {
        return m_fixedTimeScale;
    }
    
    /**
     * Get deltatime unscaled
     * @return 
     */
    public static double unscaledDeltaTime() {
        return m_deltaTime;
    }
    /**
     * Get deltatime scaled
     * @return 
     */
    public static double deltaTime() {
        return (m_deltaTime * m_timeScale);
    }
    
    /**
     * Get fixedDeltatime scaled
     * @return 
     */
    public static double fixedDeltaTime() {
        return (m_fixedTimeScale * m_timeScale);
    }
    
    /**
     * Returns the time
     * @return 
     */
    public static double time() {
        return m_time;
    }
}
