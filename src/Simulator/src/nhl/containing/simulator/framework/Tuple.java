/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nhl.containing.simulator.framework;

/**
 *
 * @author sietse
 */
public class Tuple<T1, T2> {
    public T1 a;
    public T2 b;
    
    public Tuple() {
        a = null;
        b = null;
    }
    public Tuple(T1 a, T2 b) {
        this.a = a;
        this.b = b;
    }
}
