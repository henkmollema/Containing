/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nhl.containing.simulator.world;

import nhl.containing.simulator.game.StoragePlatform;
import nhl.containing.simulator.simulation.Transform;
import nhl.containing.simulator.simulation.Utilities;

/**
 *
 * @author sietse
 */
public class SimulatorTests {
    public void getBox() {
        /*Unit
         * mo 24-11-2015
         * expected:
            * Transform with box
         * 
         * actual result:
            * Transform with box
         * 
         * pass/fail
            * pass
         */
        Transform t = ContainerPool.get();
    }
    public void disposeNull() {
        /*Unit
         * mo 24-11-2015
         * expected:
            * Nothing is happening, Return false
         *
         * actual result:
            * Nothing happened, Return false
         * 
         * pass/fail:
            * pass
         */
        boolean expected = false;
        boolean real = ContainerPool.dispose(null);
        
        boolean pass = expected == real;
    }
    public void disposeCreated() {
        /*Unit
         * mo 24-11-2015
         * expected:
            * input trasform disapears and stored
            * return value TRUE
         * 
         * actual result:
            * input transform disapears and stored
            * return value TRUE
         * 
         * pass/fail:
            * pass
         * 
         */
        
        boolean expected = true;
        
        Transform t = ContainerPool.get();
        boolean real = ContainerPool.dispose(t);
        
        boolean pass = expected == real;
    }
    public void disposeOther() {
        /*Unit
         * mo 24-11-2015
         * expected:
            * Nothing happens
            * return value FALSE
         * 
         * actual result:
            * Nothing happened
            * return value FALSE
         * 
         * pass/fail:
            * pass
         * 
         */
        boolean expected = false;
        
        Transform t = new Transform();
        boolean real = ContainerPool.dispose(t);
        
        boolean pass = expected == real;
        
    }
    public void createStorage() {
        /* Intergration,
         * mo 24-11-2015
         * expected:
            * a 6 x 6 x 22 containers where only the outer visible containers are visible where the offset is equal to the input
         * 
         * actual result:
            * a 6 x 6 x 22 containers where only the outer visible containers are visible where the offset is equal to the input
         * 
         * pass/fail:
            * pass
         * 
         */
        new StoragePlatform(Utilities.zero());
    }
}
