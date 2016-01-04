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
        
        final float left = 1650.0f;
        final float up = 625.0f;
        final float ext = 100.0f;
        final float iof = 100.0f;
        final float road = 20.0f;
        final float zero = 0.0f;
        
        
       /*
         * 
         *           Lorry               Inland
         *       00____>______01       02___>___03
         *        |            |        |        |
         * 08____09___<_______10_11_12_13___<___14_________15
         * |      ˄            ˅  ˅  ˄  ˄        ˅          |
         * |  16_17_____>_____18_19_20_21___>___22 __23     |
         * |  |                   |  |                |     |
         * |  |                   |  |                24_>_25__04
         * |  |                   |  |                |     |   |
         * |  |                   |  |                |     |   |   Sea
         * |  |    Storage        ˅  ˄   Storage      |     |   |
         * |  |                   |  |                |     |   |
         * |  |                   |  |                26_<_27__05
         * |  |                   |  |                |     |
         * |  28_29______<_______30_31___<______32___33     |
         * |      ˅               ˅  ˄           ˄          |
         * 34____35______>_______36_37_____>____38_________39
         *        |                              |
         *       06_____________________________07
         *                 Train
         */
        nodes = new AgvNode[] {
            
            // Lorry
            new AgvNode(new Vector2f(left - ext, up + ext)), // 0
            new AgvNode(new Vector2f(ext, up + ext)), // 1
            
            // Inland
            new AgvNode(new Vector2f(-ext, up + ext)), // 2
            new AgvNode(new Vector2f(-left + ext, up + ext)), // 3
            
            // Sea
            new AgvNode(new Vector2f(-left - ext, up - ext)), // 4
            new AgvNode(new Vector2f(-left - ext, -up + ext)), // 5
            
            // Train
            new AgvNode(new Vector2f(left - ext, -up - ext)), // 6
            new AgvNode(new Vector2f(-left + ext, -up - ext)), // 7
            
            // 
            new AgvNode(new Vector2f(left + road, up + road)), // 8
            new AgvNode(new Vector2f(left - iof, up + road)), // 9
            new AgvNode(new Vector2f(iof, up + road)), // 10
            new AgvNode(new Vector2f(road / 2.0f, up + road)), // 3
            new AgvNode(new Vector2f(-road / 2.0f, up + road)), // 3
            new AgvNode(new Vector2f(-iof, up + road)), // 10
            new AgvNode(new Vector2f(-left + iof, up + road)), // 8
            new AgvNode(new Vector2f(-left - road, up + road)), // 8
            
            // 
            new AgvNode(new Vector2f(left, up)), // 8
            new AgvNode(new Vector2f(left - iof, up)), // 9 + (road / 2.0f)
            new AgvNode(new Vector2f(iof, up)), // 10
            new AgvNode(new Vector2f(road / 2.0f, up)), // 3
            new AgvNode(new Vector2f(-road / 2.0f, up)), // 3
            new AgvNode(new Vector2f(-iof, up)), // 10
            new AgvNode(new Vector2f(-left + iof, up)), // 8
            new AgvNode(new Vector2f(-left, up)), // 8
            
            //
            new AgvNode(new Vector2f(-left, up - iof)), // 8
            new AgvNode(new Vector2f(-left - road, up - iof)), // 8
            
            //
            new AgvNode(new Vector2f(-left, -up + iof)), // 8
            new AgvNode(new Vector2f(-left - road, -up + iof)), // 8
            
            // 
            new AgvNode(new Vector2f(left, -up)), // 8
            new AgvNode(new Vector2f(left - iof, -up)), // 9
            new AgvNode(new Vector2f(road / 2.0f, -up)), // 3
            new AgvNode(new Vector2f(-road / 2.0f, -up)), // 3
            new AgvNode(new Vector2f(-left + iof, -up)), // 8
            new AgvNode(new Vector2f(-left, -up)), // 8
            
            // 
            new AgvNode(new Vector2f(left, -up - road)), // 8
            new AgvNode(new Vector2f(left - iof, -up - road)), // 9
            new AgvNode(new Vector2f(road / 2.0f, -up - road)), // 3
            new AgvNode(new Vector2f(-road / 2.0f, -up - road)), // 3
            new AgvNode(new Vector2f(-left + iof, -up - road)), // 8
            new AgvNode(new Vector2f(-left - road, -up - road)), // 8
        };
        
        // 
        nodes[ 0].setConnections(1);
        nodes[ 1].setConnections(10);
        
        // 
        nodes[ 2].setConnections(3);
        nodes[ 3].setConnections(14);
        
        // 
        nodes[ 4].setConnections(5);
        nodes[ 5].setConnections(27);
        
        // 
        nodes[ 6].setConnections(7);
        nodes[ 7].setConnections(38);
        
        // 
        nodes[ 8].setConnections(34);
        nodes[ 9].setConnections(8, 0);
        nodes[10].setConnections(9, 18);
        nodes[11].setConnections(10, 19);
        nodes[12].setConnections(11);
        nodes[13].setConnections(12, 2);
        nodes[14].setConnections(13, 22);
        nodes[15].setConnections(14);
        
        // 
        nodes[16].setConnections(17);
        nodes[17].setConnections(18, 9);
        nodes[18].setConnections(19);
        nodes[19].setConnections(20, 30);
        nodes[20].setConnections(21, 12);
        nodes[21].setConnections(22, 13);
        nodes[22].setConnections(23);
        nodes[23].setConnections(24);
        
        // 
        nodes[24].setConnections(25, 26);
        nodes[25].setConnections(15, 4);
        nodes[26].setConnections(33);
        nodes[27].setConnections(26, 25);
        
        // 
        nodes[28].setConnections(16);
        nodes[29].setConnections(28, 35);
        nodes[30].setConnections(29, 36);
        nodes[31].setConnections(30, 20);
        nodes[32].setConnections(31);
        nodes[33].setConnections(32);
        
        // 
        nodes[34].setConnections(35);
        nodes[35].setConnections(36, 6);
        nodes[36].setConnections(37);
        nodes[37].setConnections(38, 31);
        nodes[38].setConnections(39, 32);
        nodes[39].setConnections(27);
        
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
