package nhl.containing.simulator.game;

import nhl.containing.networking.protobuf.SimulationItemProto.SimulationItem.*;

/**
 *
 * @author sietse
 */
public class AGV extends MovingItem {
    private boolean m_waiting = false;
    
    public AGV(){
        this.register(SimulationItemType.AGV);
    }
}
