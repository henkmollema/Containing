/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Game;

import Simulation.*;
import World.World;
import World.WorldCreator;
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
        public Geometry geometry = null;
        
        public ContainerSpot(ContainerCarrier carrier) {
            transform = new Transform(carrier);
        }
    }
    
    private Vector3f m_containerOffset = Utilities.zero();
    private ContainerSpot[][][] m_containerSpots = new ContainerSpot[0][][];
    
    public ContainerCarrier() {
        super();
    }
    public ContainerCarrier(Transform parent, Point3 stack) {
        super(parent);
        initSpots(stack);
        updateOuter();
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
                    m_containerSpots[i][j][k].geometry = WorldCreator.createBox(m_containerSpots[i][j][k].transform);
                }
            }
        }
    }
    protected final void updateOuter() {
        for (int x = 0; x < m_containerSpots.length; ++x) {
            for (int y = 0; y < m_containerSpots[x].length; ++y) {
                for (int z = 0; z < m_containerSpots[x][y].length; ++z) {
                    //if (m_containerSpots[x][y][z].container == null)
                    //    continue;
                    
                    if (
                            x == 0 || x == m_containerSpots.length - 1 ||
                            y == m_containerSpots[x].length - 1 ||
                            z == 0 || z == m_containerSpots[x][y].length - 1) {
                        m_containerSpots[x][y][z].geometry.setCullHint(CullHint.Dynamic);
                    } else if (y == 0) {
                        m_containerSpots[x][y][z].geometry.setCullHint(CullHint.Always);
                    } else if (
                            m_containerSpots[x - 1][y][z].container != null && 
                            m_containerSpots[x + 1][y][z].container != null &&
                            m_containerSpots[x][y - 1][z].container != null && 
                            m_containerSpots[x][y + 1][z].container != null && 
                            m_containerSpots[x][y][z - 1].container != null && 
                            m_containerSpots[x][y][z + 1].container != null) {
                        m_containerSpots[x][y][z].geometry.setCullHint(CullHint.Always);
                    } else {
                        m_containerSpots[x][y][z].geometry.setCullHint(CullHint.Dynamic);
                    }
                }
            }
        }
    }
}
