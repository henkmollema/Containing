/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Simulation;

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
    
    public static void _updateTime(float deltaTime) {
        m_deltaTime = deltaTime;
        m_time += deltaTime * m_timeScale;
    }
    
    public static void setTimeScale(float t) {
        m_timeScale = Mathf.max(t, 0.0f);
    }
    public static void setFixedTimeScale(float t) {
        m_fixedTimeScale = Mathf.max(MIN_FIXED_TIME_SCALE, t);
    }
    
    public static float timeScale() {
        return m_timeScale;
    }
    public static float fixedTimeScale() {
        return m_fixedTimeScale;
    }
    
    public static float unscaledDeltaTime() {
        return m_deltaTime;
    }
    public static float deltaTime() {
        return (m_deltaTime * m_timeScale);
    }
    
    public static float fixedDeltaTime() {
        return (m_fixedTimeScale * m_timeScale);
    }
}
