/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nhl.containing.simulator.simulation;

import java.lang.reflect.Method;

/**
 *
 * @author sietse
 */
public class Callback {
    
    private final Class m_class;        // Target class
    private final Object m_target;      // Object
    private final String[] m_methods;   // Selected target methods
    
    /**
     * Constructor
     * @param c
     * @param methods 
     */
    public Callback(Class c, String... methods) {
        m_class = c;
        m_target = null;
        m_methods = methods;
    }
    /**
     * Constructor
     * @param target
     * @param methods 
     */
    public Callback(Object target, String... methods) {
        m_class = target.getClass();
        m_target = target;
        m_methods = methods;
    }
    
    public String toString() {
        String s = "";
        for (String a : m_methods)
            s += a + ", ";
        return s + " ---> " + m_class.toString();
    }
    
    /**
     * Run methods
     */
    public void invoke() {
        Callback.invoke(m_target, m_class, m_methods);
    }
    
    /**
     * Run methods
     * @param c
     * @param methods 
     */
    public static void invoke(Class c, String... methods) {
        Callback.invoke(null, c, methods);
    }
    /**
     * Run methods
     * @param target
     * @param methods 
     */
    public static void invoke(Object target, String... methods) {
        Callback.invoke(target, target.getClass(), methods);
    }
    /**
     * Run methods
     * @param target
     * @param c
     * @param methods 
     */
    public static void invoke(Object target, Class c, String... methods) {
        try {
            // Get targets
            Class<?> __class = c;
            Method[] __methods = __class.getMethods();
            
            // Call all methods
            for (Method m : __methods) {
                for (String s : methods) {
                    if (m.getName() == null ? s == null : m.getName().equals(s)) {
                        m.invoke(target);
                    }
                }
            }
        } catch(Exception e) { Debug.error(e.getMessage()); }
    }
}
