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
import nhl.containing.simulator.simulation.Callback;
import nhl.containing.simulator.simulation.Debug;

/**
 *
 * @author sietse
 */
public class ContainerCarrier extends Item {
    public class ContainerSpot {
        public Container container = null;
        public Vector3f localPosition = Utilities.zero();
        
        public ContainerSpot(Vector3f position) {
            this.localPosition = new Vector3f(position).add(m_containerOffset);
            this.container = null;
        }
    }
    
    private Vector3f m_containerOffset = Utilities.zero();
    private ContainerSpot[][][] m_containerSpots = new ContainerSpot[0][][];
    
    public ContainerCarrier() {
        super();
        initSpots(Point3.one());
        updateOuter();
    }
    public ContainerCarrier(Transform parent) {
        super(parent);
        initSpots(Point3.one());
        updateOuter();
    }
    public ContainerCarrier(Transform parent, Point3 stack) {
        super(parent);
        initSpots(stack);
        updateOuter();
    }
    
    public Vector3f containerOffset() {
        return new Vector3f(m_containerOffset);
    }
    public void containerOffset(Vector3f v) {
        if (m_containerSpots != null)
        for (int x = 0; x < m_containerSpots.length; ++x)
        for (int y = 0; y < m_containerSpots[x].length; ++y)
        for (int z = 0; z < m_containerSpots[x][y].length; ++z) {
            m_containerSpots[x][y][z].localPosition = m_containerSpots[x][y][z].localPosition.subtract(m_containerOffset).add(v);
        }
        m_containerOffset = new Vector3f(v);
    }
    
    public Container getContainer() {
        return getContainer(Point3.zero());
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
                    m_containerSpots[i][j][k] = new ContainerSpot(new Vector3f(i, j, k).mult(World.containerSize().add(m_containerOffset)));
                    
                    // Remove this
                    setContainer(new Point3(i, j, k), new Container(new RFID()));
                }
            }
        }
    }
    
    protected final void updateOuter() {
        for (int x = 0; x < m_containerSpots.length; ++x)
        for (int y = 0; y < m_containerSpots[x].length; ++y)
        for (int z = 0; z < m_containerSpots[x][y].length; ++z) {
            if (m_containerSpots[x][y][z].container == null)
                continue;
            m_containerSpots[x][y][z].container.setCullHint(isOuter(x, y, z) ? cullHint.Dynamic : cullHint.Always);
        }
    }
    private boolean isOuter(int x, int y, int z) {
        if (x == 0 || 
          z == 0 ||
          x == m_containerSpots.length - 1 || 
          y == m_containerSpots[x].length - 1 ||  
          z == m_containerSpots[x][y].length - 1)
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
    
    public Container setContainer(Container c) {
        return setContainer(Point3.zero(), c);
    }
    public Container setContainer(Point3 point, Container c) {
        if (
                point.x < 0 || point.y < 0 || point.z < 0 || 
                point.x >= m_containerSpots.length || 
                point.y >= m_containerSpots[point.x].length || 
                point.z >= m_containerSpots[point.x][point.y].length)
        return null;
        
        Container temp = m_containerSpots[point.x][point.y][point.z].container;
        m_containerSpots[point.x][point.y][point.z].container = c;
        if (c == null)
            return temp;
        onSetContainer(c);
        c.localPosition(m_containerSpots[point.x][point.y][point.z].localPosition.clone());
        return c;
    }
    protected void onSetContainer(Container c) { 
        this.attachChild(c);
    }
    
    protected ContainerSpot getSpot() {
        return getSpot(Point3.zero());
    }
    protected ContainerSpot getSpot(Point3 point) {
        return m_containerSpots[point.x][point.y][point.z];
    }
    
    protected void replaceContainer(Container a, Container b) {
        for (int x = 0; x < m_containerSpots.length; ++x)
        for (int y = 0; y < m_containerSpots[x].length; ++y)
        for (int z = 0; z < m_containerSpots[x][y].length; ++z) {
            if (m_containerSpots[x][y][z].container == a) {
                m_containerSpots[x][y][z].container = b;
                return;
            }
        }
    }
}
