package nhl.containing.simulator.game;

import nhl.containing.simulator.simulation.Utilities;
import nhl.containing.simulator.simulation.Point3;
import nhl.containing.simulator.simulation.Transform;
import nhl.containing.simulator.world.World;
import com.jme3.math.Vector3f;
import nhl.containing.simulator.simulation.Point2;

/**
 *
 * @author sietse
 */
public class ContainerCarrier extends Item {
    
    /**
     * A container place in the carrier
     */
    public class ContainerSpot {
        public Container container = null;                  // The container itsself
        public Vector3f localPosition = Utilities.zero();   // Local container offset
        
        /**
         * Constructor
         * @param position 
         */
        public ContainerSpot(Vector3f position) {
            this.localPosition = new Vector3f(position).add(m_containerOffset);
            this.container = null;
        }
        
        /**
         * Get world position of a container spot
         * @return 
         */
        public Vector3f worldPosition() {
            Vector3f v = Utilities.zero();
            v = localToWorld(localPosition, v);
            return v;
        }
    }
    
    private Vector3f m_containerOffset = Utilities.zero();                      // Offset
    protected ContainerSpot[][][] m_containerSpots = new ContainerSpot[0][][];  // All container spots
    
    /**
     * Constructor
     */
    public ContainerCarrier() {
        super();
        initSpots(Point3.one());
        updateOuter();
    }
    /**
     * Constructor
     * @param parent 
     */
    public ContainerCarrier(Transform parent) {
        super(parent);
        initSpots(Point3.one());
        updateOuter();
    }
    /**
     * Constructor
     * @param parent
     * @param stack 
     */
    public ContainerCarrier(Transform parent, Point3 stack) {
        super(parent);
        initSpots(stack);
        updateOuter();
    }
    
    /**
     * Get offset
     * @return 
     */
    public Vector3f containerOffset() {
        return new Vector3f(m_containerOffset);
    }
    /**
     * Set for all containers a new offset
     * @param v 
     */
    public void containerOffset(Vector3f v) {
        
        // For all containers, set the new offset
        if (m_containerSpots != null)
        for (int x = 0; x < m_containerSpots.length; ++x)
        for (int y = 0; y < m_containerSpots[x].length; ++y)
        for (int z = 0; z < m_containerSpots[x][y].length; ++z) {
            m_containerSpots[x][y][z].localPosition = m_containerSpots[x][y][z].localPosition.subtract(m_containerOffset).add(v);
        }
        
        // Set offset
        m_containerOffset = new Vector3f(v);
    }
    
    /**
     * Get container
     * @return 
     */
    public Container getContainer() {
        return getContainer(Point3.zero());
    }
    /**
     * Get container
     * @param p
     * @return 
     */
    public Container getContainer(Point3 p) {
        return getContainer(p.x, p.y, p.z);
    }
    /**
     * Get container
     * @param x local x index
     * @param y local y index
     * @param z local z index
     * @return selected container, if invalid input -> null
     */
    public Container getContainer(int x, int y, int z) {
        
        // Check if invalid input
        if (x < 0 || x >= m_containerSpots.length)
            return null;
        if (y < 0 || y >= m_containerSpots[x].length)
            return null;
        if (z < 0 || z >= m_containerSpots[x][y].length)
            return null;
        if (m_containerSpots[x][y][z] == null)
            return null;
        
        // Input if valid, return the container
        return m_containerSpots[x][y][z].container;
    }
    /**
     * Initalize the spots
     * @param stack 
     */
    protected final void initSpots(Point3 stack) {
        m_containerSpots = new ContainerSpot[stack.x][][];
        for (int i = 0; i < m_containerSpots.length; ++i) {
            m_containerSpots[i] = new ContainerSpot[stack.y][];
            for (int j = 0; j < m_containerSpots[i].length; ++j) {
                m_containerSpots[i][j] = new ContainerSpot[stack.z];
                for (int k = 0; k < m_containerSpots[i][j].length; ++k) {
                    
                    // Set spot
                    m_containerSpots[i][j][k] = new ContainerSpot(new Vector3f(i, j, k).mult(World.containerSize().mult(2.0f).add(m_containerOffset)));
                    
                    // Remove this, its for testing purposes
                    setContainer(new Point3(i, j, k), new Container(new RFID()));
                }
            }
        }
    }
    
    /**
     * Get height of a horizontal position
     * @param p point x and z index
     * @return height
     */
    protected final int getStackHeight(Point2 p) {
        for (int i = 0; i < m_containerSpots[p.x].length; i++) {
            if (m_containerSpots[p.x][i][p.y].container == null)
                return i;
        }
        return m_containerSpots[p.x].length;
    }
    
    /**
     * Update occlusion culling
     */
    protected final void updateOuter() {
        for (int x = 0; x < m_containerSpots.length; ++x)
        for (int y = 0; y < m_containerSpots[x].length; ++y)
        for (int z = 0; z < m_containerSpots[x][y].length; ++z) {
            if (m_containerSpots[x][y][z].container == null)
                continue;
            m_containerSpots[x][y][z].container.setCullHint(isOuter(x, y, z) ? cullHint.Dynamic : cullHint.Always);
        }
    }
    /**
     * Check if the container need to be rendered
     * @param x
     * @param y
     * @param z
     * @return 
     */
    private boolean isOuter(int x, int y, int z) {
        
        if (x == 0 || 
          z == 0 ||
          x == m_containerSpots.length - 1 || 
          y == m_containerSpots[x].length - 1 ||  
          z == m_containerSpots[x][y].length - 1)
            return true; // The container is on the most outer place
        if (y == 0)
            return false; // It is not on outer place but on the bottom so do not render
        
        return // If any surrounding spot has not a container, it has to be rendered
            m_containerSpots[x - 1][y][z].container == null ||        
            m_containerSpots[x + 1][y][z].container == null ||
            m_containerSpots[x][y - 1][z].container == null ||
            m_containerSpots[x][y + 1][z].container == null ||
            m_containerSpots[x][y][z - 1].container == null ||
            m_containerSpots[x][y][z + 1].container == null;
    }
    /**
     * Set container for the first spot
     * @param c
     * @return 
     */
    public Container setContainer(Container c) {
        return setContainer(Point3.zero(), c);
    }
    /**
     * Set container
     * NOTE: It does not autamaticly check for occlusion culling
     * @param point
     * @param c
     * @return If the input equals null, the previous container returned, otherwise the new one
     */
    public Container setContainer(Point3 point, Container c) {
        if ( // Check if input is valid
                point.x < 0 || point.y < 0 || point.z < 0 || 
                point.x >= m_containerSpots.length || 
                point.y >= m_containerSpots[point.x].length || 
                point.z >= m_containerSpots[point.x][point.y].length)
        return null;
        
        // Save the current container before replacing
        Container temp = m_containerSpots[point.x][point.y][point.z].container;
        
        // Set the new container
        m_containerSpots[point.x][point.y][point.z].container = c;
        
        if (c == null) // Return the previous one
            return temp;
        
        // Init
        onSetContainer(c);
        c.localPosition(m_containerSpots[point.x][point.y][point.z].localPosition.clone());
        
        return c;
    }
    /**
     * Init
     * @param c 
     */
    protected void onSetContainer(Container c) { 
        this.attachChild(c);
    }
    
    /**
     * Get the first container spot
     * @return 
     */
    protected ContainerSpot getSpot() {
        return getSpot(Point3.zero());
    }
    /**
     * Get container spot
     * @param point
     * @return 
     */
    protected ContainerSpot getSpot(Point3 point) {
        return m_containerSpots[point.x][point.y][point.z];
    }
    
    /**
     * Find and Replace container
     * @param a Container that is goint to be replaced
     * @param b The new container
     * @return Replace success
     */
    protected boolean replaceContainer(Container a, Container b) {
        for (int x = 0; x < m_containerSpots.length; ++x)
        for (int y = 0; y < m_containerSpots[x].length; ++y)
        for (int z = 0; z < m_containerSpots[x][y].length; ++z) {
            if (m_containerSpots[x][y][z].container == a) {
                m_containerSpots[x][y][z].container = b;
                return true;
            }
        }
        return false;
    }
}
