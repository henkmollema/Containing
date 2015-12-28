package nhl.containing.simulator.gui;

import com.jme3.font.*;
import nhl.containing.simulator.framework.Behaviour;
import nhl.containing.simulator.simulation.Main;
import com.jme3.font.BitmapText;
import com.jme3.math.Vector2f;
import com.jme3.scene.Node;
import com.jme3.ui.Picture;
import nhl.containing.simulator.game.Container;
import org.lwjgl.opengl.Display;

/**
 *
 * @author sietse
 */
public class GUI extends Behaviour{
    
    public static final int DEFAULT_TEXT_SIZE = 170;
    
    // Singleton
    private static GUI m_instance;
    public static GUI instance() {
        return m_instance;
    }
    
    //private static List<GuiItem> m_items = new ArrayList<GuiItem>();
    //private static long m_idCounter = 0;
    
    // Items
    private Picture m_logo;
    private BitmapText m_worldInfo;
    private BitmapText m_containerInfo; // print con
    // print container details
    // 
    
    public static Node root() {
        return Main.guiRoot();
    }
    public static Vector2f screenSize() {
        return new Vector2f(screenWidth(), screenHeight());
    }
    public static float screenHeight() {
        return Display.getHeight();
    }
    public static float screenWidth() {
        return Display.getWidth();
    }
    
    @Override
    public void awake() {
        m_instance = this;
    }
    @Override
    public void start() {
                /** Write text on the screen (HUD) */
        //Main.guiRoot().detachAllChildren();
        Main.guiFont(Main.assets().loadFont("Interface/Fonts/Default.fnt"));
        Main.guiFont().getCharSet().setRenderedSize(DEFAULT_TEXT_SIZE);
        m_containerInfo = new BitmapText(Main.guiFont(), false);
        m_containerInfo.setLocalTranslation(screenWidth() - 150, (screenHeight() - 50) + m_containerInfo.getHeight(), 0);
        Main.guiRoot().attachChild(m_containerInfo);
 
    }
    public void setContainerText(String text)
    {
        m_containerInfo.setText(text);
    }
    public void setContainerInfo(Container container)
    {
        m_containerInfo.setText(container.toString());
    }
    @Override
    public void rawUpdate() {
        /*
        for(GuiItem i : m_items) {
            //Debug.log(i.layer() + "");
            i._baseUpdate();
        }
        * */
    }
    /*
    public static long register(GuiItem item) {
        if (!m_items.contains(item)) {
            boolean _isAdded = false;
            for (int i = 0; i < m_items.size(); ++i) {
                if (m_items.get(i).layer() > item.layer()) {
                    _isAdded = true;
                    m_items.add(i, item);
                    break;
                }
            }
            if (!_isAdded) {
                m_items.add(item);
            }
        } else {
            return -1l;
        }
        return m_idCounter++;
    }
    public static boolean unregister(GuiItem item) {
        return m_items.remove(item);
    }
    * */
}
