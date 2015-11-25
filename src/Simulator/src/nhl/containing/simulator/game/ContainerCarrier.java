/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nhl.containing.simulator.game;

import nhl.containing.simulator.simulation.Utilities;
import nhl.containing.simulator.simulation.Point3;
import nhl.containing.simulator.simulation.Transform;
import nhl.containing.simulator.world.World;
import nhl.containing.simulator.world.ContainerPool;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;

/**
 *
 * @author sietse
 */
public class ContainerCarrier extends Item {
    public class ContainerSpot {
        public Container container = null;
        public Transform transform = null;
        
        public ContainerSpot(ContainerCarrier carrier) {
            transform = ContainerPool.get();
        }
    }
    
    private Vector3f m_containerOffset = Utilities.zero();
    private ContainerSpot[][][] m_containerSpots = new ContainerSpot[0][][];
    
    public ContainerCarrier() {
        super();
    }
    public ContainerCarrier(Transform parent) {
        super(parent);
    }
    public ContainerCarrier(Transform parent, Point3 stack) {
        super(parent);
        initSpots(stack);
        updateOuter();
    }
    
    public Container getContainer(Point3 p) {
        return getContainer(p.x, p.y, p.z);
    }
    public Container getContainer(int x, int y, int z) {
        if (x < 0 || x >= m_containerSpots.length)
            return null;
        if (y < 0 || y >= m_containerSpots[x].length)
            return null;
        if (z < 0 || z >= m_containerSpots[x][y].length)
            return null;
        if (m_containerSpots[x][y][z] == null)
            return null;
        
        return m_containerSpots[x][y][z].container;
    }
    
    
    protected final void initSpots(Point3 stack) {
        m_containerSpots = new ContainerSpot[stack.x][][];
        for (int i = 0; i < m_containerSpots.length; ++i) {
            m_containerSpots[i] = new ContainerSpot[stack.y][];
            for (int j = 0; j < m_containerSpots[i].length; ++j) {
                m_containerSpots[i][j] = new ContainerSpot[stack.z];
                for (int k = 0; k < m_containerSpots[i][j].length; ++k) {
                    m_containerSpots[i][j][k] = new ContainerSpot(this);
                    m_containerSpots[i][j][k].transform.localPosition(new Vector3f(i, j, k).mult(World.containerSize().add(m_containerOffset)));
                    
                    m_containerSpots[i][j][k].container = new Container();
                }
            }
        }
    }
    protected final void updateOuter() {
        for (int x = 0; x < m_containerSpots.length; ++x) {
            for (int y = 0; y < m_containerSpots[x].length; ++y) {
                for (int z = 0; z < m_containerSpots[x][y].length; ++z) {
                    if (isOuter(x, y, z)) {
                        if (m_containerSpots[x][y][z].transform == null)
                            m_containerSpots[x][y][z].transform = ContainerPool.get();
                    } else {
                        if (m_containerSpots[x][y][z].transform != null) {
                            ContainerPool.dispose(m_containerSpots[x][y][z].transform);
                            m_containerSpots[x][y][z].transform = null;
                        }
                    }
                }
            }
        }
    }
    private final boolean isOuter(int x, int y, int z) {
        if (m_containerSpots[x][y][z].container == null)
            return false;
        if (x == 0 || x == m_containerSpots.length - 1 || y == m_containerSpots[x].length - 1 || z == 0 || z == m_containerSpots[x][y].length - 1)
            return true;
        if (y == 0)
            return false;
        
        return 
            m_containerSpots[x - 1][y][z].container == null ||        
            m_containerSpots[x + 1][y][z].container == null ||
            m_containerSpots[x][y - 1][z].container == null ||
            m_containerSpots[x][y + 1][z].container == null ||
            m_containerSpots[x][y][z - 1].container == null ||
            m_containerSpots[x][y][z + 1].container == null;
    }
}
