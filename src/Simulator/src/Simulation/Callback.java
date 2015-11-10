/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Simulation;

import java.lang.reflect.Method;

/**
 *
 * @author sietse
 */
public class Callback {
    
    private Class m_class;
    private Object m_target;
    private String[] m_methods;
    
    public Callback(Class c, String... methods) {
        m_class = c;
        m_target = null;
        m_methods = methods;
    }
    public Callback(Object target, String... methods) {
        m_class = target.getClass();
        m_target = target;
        m_methods = methods;
    }
    
    public void invoke() {
        Callback.invoke(m_target, m_class, m_methods);
    }
    
    public static void invoke(Class c, String... methods) {
        Callback.invoke(null, c, methods);
    }
    public static void invoke(Object target, String... methods) {
        try {
            Callback.invoke(target, target.getClass(), methods);
        } catch (Exception e) { }
    }
    public static void invoke(Object target, Class c, String... methods) {
        try {
            Class<?> __class = c;
            Method[] __methods = __class.getMethods();
            
            for (Method m : __methods) {
                for (String s : methods) {
                    if (m.getName() == s) {
                        m.invoke(target);
                    }
                }
            }
        } catch(Exception e){}
    }
}
