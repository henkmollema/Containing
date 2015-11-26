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
    void getBox() {
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
    void disposeNull() {
        
        ContainerPool.dispose(null);
    }
    void disposeCreated() {
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
         * comments:
            * when putting null or any other transform that is not the pool trasform
            * FALSE will return
         */
        Transform t = ContainerPool.get();
        ContainerPool.dispose(t);
    }
    void disposeOther() {
        Transform t = new Transform();
        ContainerPool.dispose(t);
    }
    void createStorage() {
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
