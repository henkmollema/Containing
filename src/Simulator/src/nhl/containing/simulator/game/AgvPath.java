/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nhl.containing.simulator.game;

import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Line;
import nhl.containing.simulator.simulation.Main;
import nhl.containing.simulator.world.MaterialCreator;
import nhl.containing.simulator.world.World;

/**
 *
 * @author sietse
 */
public class AgvPath /*extends Behaviour*/ {
    
    private boolean m_initialized = false;
    private boolean m_nodesSend = false;
    private static int currentID = 0;
    
    public class AgvNode {
        private final int m_id;
        private final Vector2f m_position;
        private int[] m_connections;
        
        public AgvNode(Vector3f position) {
            m_id = currentID++;
            m_position = new Vector2f(position.x, position.z);
        }
        public AgvNode(Vector2f position) {
            m_id = currentID++;
            m_position = new Vector2f(position);
        }
        
        public void setConnections(int... nodes) {
            m_connections = nodes;
        }
        public int id(){
            return m_id;
        }
        
        public Vector3f position() {
            return new Vector3f(m_position.x, World.WORLD_HEIGHT, m_position.y);
        }
        public int[] connections() {
            return m_connections;
        }
    }
    
    //public List<Tuple<Integer, Vector3f[]>> m_results = new ArrayList<>();
    
    public AgvNode[] nodes;
    
    public void init() {
        if (m_initialized)
            return;
        
        /*   lorry       inland
         * a_________b c_________d
         * 0_________235_________6
         * |          |          7 e
         * |          |          | | sea
         * | storage  |  storage | |
         * |          |          8 f
         * 1__________4__________9
         *  g___________________h
         *         train
         * 
         * + -> node
         * 
         */
        
        final float left = 1650.0f;
        final float up = 625.0f;
        final float extents = 100.0f;
        final float zero = 0.0f;
        
        
        nodes = new AgvNode[] {
            
            // Path
            new AgvNode(new Vector2f(left, up)), // 0
            new AgvNode(new Vector2f(left, -up)), // 1
            new AgvNode(new Vector2f(extents, up)), // 2
            new AgvNode(new Vector2f(zero, up)), // 3
            new AgvNode(new Vector2f(zero, -up)), // 4
            new AgvNode(new Vector2f(-extents, up)), // 5
            new AgvNode(new Vector2f(-left, up)), // 6
            new AgvNode(new Vector2f(-left, up - extents)), // 7
            new AgvNode(new Vector2f(-left, -up + extents)), // 8
            new AgvNode(new Vector2f(-left, -up)), // 9
            
            // Lorry
            new AgvNode(new Vector2f(left, up + extents)), // 10 (a)
            new AgvNode(new Vector2f(extents, up + extents)), // 11 (b)
            
            // Inland
            new AgvNode(new Vector2f(-extents, up + extents)), // 12 (c)
            new AgvNode(new Vector2f(-left,up + extents)), // 13 (d)
            
            // Sea
            new AgvNode(new Vector2f(-left - extents, up - extents)), // 14 (e)
            new AgvNode(new Vector2f(-left - extents, -up + extents)), // 15 (f)
            
            // Train
            new AgvNode(new Vector2f(left, -up - extents)), // 16 (g)
            new AgvNode(new Vector2f(-left, -up - extents)), // 17 (h)
        };
        
        nodes[0].setConnections(1, 2, 10);
        nodes[1].setConnections(0, 4);
        nodes[2].setConnections(0, 3);
        nodes[3].setConnections(2, 4, 5);
        nodes[4].setConnections(1, 3, 9);
        nodes[5].setConnections(3,6,12);
        nodes[6].setConnections(5,7);
        nodes[7].setConnections(6,8,14);
        nodes[8].setConnections(7,9);
        nodes[9].setConnections(4,8,17);
        
        nodes[10].setConnections(11);
        nodes[11].setConnections(2);
        
        nodes[12].setConnections(13);
        nodes[13].setConnections(6);
        
        nodes[14].setConnections(15);
        nodes[15].setConnections(8);
        
        nodes[16].setConnections(1);
        nodes[17].setConnections(16);
        
        sendNodes();
        
        m_initialized = true;
        
        debugPath();
    }
    private void debugPath() {
        for (int i = 0; i < nodes.length; i++) {
            for (int j : nodes[i].connections()) {
                Vector3f offset = new Vector3f(0.0f, 20.0f, 0.0f);
                
                Line a = new Line(nodes[i].position().add(offset), nodes[j].position().add(offset));
                Geometry g = new Geometry("Colored Box", a); 
                g.setMaterial(MaterialCreator.unshadedMagenta());
                
                Main.root().attachChild(g);
            }
        }
    }
    
    private void sendNodes() {
        for(AgvNode node : nodes){
            Main.getSimClient().addNode(node);
        }
    }
    
    private Vector3f[] getPath(int[] ids, Vector3f from, Vector3f to) {
        Vector3f[] p = new Vector3f[ids.length + 2];
        p[0] = new Vector3f(from);
        p[p.length - 1] = new Vector3f(to);
        
        // or i = ids.length - 1; i >= 0; i--
        for (int i = 0; i < ids.length; i++) {
            p[i + 1] = nodes[i].position();
        }
        
        return p;
    }
    
    
    /*
    public void getPath(AGV target, AgvNode to) {
        // Send this somewhere
        
        // To send, the two custom nodes
        
    }
    
    public void nodesHasBeenSend() {
        m_initialized = true;
    }
    
    @Override
    public void rawUpdate() {
        if (!m_initialized) {
            // 
            if (!m_nodesSend) {
                // Send nodes
                m_nodesSend = true;
            }
            return;
        }
        
        while(m_results.size() > 0) {
            //
            AGV selected = new AGV(); // GetAGV by id
            selected.path().setPath(m_results.get(0).b);
            m_results.remove(0);
        }
    }
    * */
}
