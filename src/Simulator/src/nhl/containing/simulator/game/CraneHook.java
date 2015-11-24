/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nhl.containing.simulator.game;

import nhl.containing.simulator.simulation.Callback;
import nhl.containing.simulator.simulation.LoopMode;
import nhl.containing.simulator.simulation.Path;
import nhl.containing.simulator.simulation.Transform;
import com.jme3.math.Vector3f;

/**
 *
 * @author sietse
 */
public class CraneHook extends MovingItem{
    
    private Vector3f m_upPosition;
    
    public CraneHook(Transform parent, float loadedSpeed, float unloadedSpeed, float secureTime, Vector3f up) {
        super(
            parent, 
            loadedSpeed, 
            unloadedSpeed, 
            null
        );
        m_upPosition= new Vector3f(up);
        path(
            new Path(
                null, null, true, true, 
                unloadedSpeed, 
                secureTime, 
                LoopMode.PingPong, 
                null, 
                null, 
                new Vector3f(up), 
                new Vector3f(up).add(new Vector3f (0.0f, -1.0f, 0.0f)
            )
        ));
        path().setCallback(new Callback(this, "onNode"));
    }
    public void onNode() {
        
    }
    public final void _update() {
        path().update();
        localPosition(path().getPosition());
        update();
    }
    protected void update() { }
    private void setSpeed(boolean loaded) {
        path().setSpeed(loaded ? m_loadedSpeed : m_empySpeed);
    }
    private void move2next() {
        path().next();
    }
    public void moveUp(boolean loaded) {
        setSpeed(loaded);
        if (path().getTargetIndex() == 1)
            move2next();
    }
    public void moveDown(boolean loaded, float worldHeight) {
        setSpeed(loaded);
        Vector3f down = position();
        down = down.subtract(localPosition());
        down = new Vector3f(m_upPosition.x, down.y + worldHeight, m_upPosition.z);
        
        path().setPath(m_upPosition, down);
        if (path().getTargetIndex() == 0)
            move2next();
    }
    public boolean isUp() {
        return path().atFirst();
    }
    public boolean isDown() {
        return path().atLast();
    }
    public boolean finishedWaiting() {
        return path().finishedWaiting();
    }
    
    public void setAngles(Vector3f v) {
        this.eulerAngles(v);
    }
}
