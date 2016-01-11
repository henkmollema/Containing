/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nhl.containing.simulator.framework;

/**
 * Tuple
 * @author sietse
 */
public class Tuple<T1, T2> {
    public T1 a;    // First
    public T2 b;    // Second
    
    /**
     * Default Constructor, set to null
     */
    public Tuple() {
        a = null;
        b = null;
    }
    /**
     * Constructor
     * @param a
     * @param b 
     */
    public Tuple(T1 a, T2 b) {
        this.a = a;
        this.b = b;
    }
}
