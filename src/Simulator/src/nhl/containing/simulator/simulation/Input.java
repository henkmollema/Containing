package nhl.containing.simulator.simulation;

import nhl.containing.simulator.game.CameraMode;
import com.jme3.input.KeyInput;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.AnalogListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.MouseAxisTrigger;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.math.Vector2f;
import java.util.ArrayList;
import java.util.List;
import com.jme3.collision.CollisionResults;
import com.jme3.math.Ray;
import com.jme3.math.Vector3f;

/**
 * Handles input
 * @author sietse
 */
public class Input extends Behaviour {
    
    // Smoothing
    public final float      MOUSE_SENSITIVITY_X         =  6.0f;
    public final float      MOUSE_SENSITIVITY_Y         = -6.0f;
    
    // Acceleration
    public final boolean    MOUSE_ACCELERATION_ACTIVE   = true;
    public final float      MOUSE_ACCELERATION          = 0.5f;
    public final float      MOUSE_LINEAR                = 4.0f;
    
    // Clamp
    public final boolean    MOUSE_CLAMPING_ACTIVE       = true;
    public final float      MOUSE_CLAMPING_X            = 4.0f;
    public final float      MOUSE_CLAMPING_Y            = 4.0f;
    
    
    public final Vector2f   MOUSE_CLAMPING              = new Vector2f(Mathf.abs(MOUSE_CLAMPING_X), Mathf.abs(MOUSE_CLAMPING_Y));
    public final int        MOUSE_SMOOTH_BUFFER         = 6;
    public final float      MOUSE_SMOOTH_WEIGHT         = Mathf.clamp(0.4f);
    
    // 
    private Vector2f        m_rawMouseMove = Vector2f.ZERO;
    private Vector2f        m_mouseMove = Vector2f.ZERO;
    private List<Vector2f>  m_mouseSmoothBuffer;
    
    // 
    private final InternalListener m_listener = new InternalListener();
    private static List<String> m_mappings = new ArrayList<String>();
    
    private Button[] m_buttons;
    
    /**
     * Get button
     * @param button
     * @return
     */
    public final Button getButton(String button) {
        for (Button b : m_buttons)
            if (b.isButton(button))
                return b;
        return null;
    }
    
    /**
     * Called at create
     */
    @Override
    public void awake() {
        m_mouseSmoothBuffer = new ArrayList<>(MOUSE_SMOOTH_BUFFER);
    }
    /**
     * Called at first frame
     */
    @Override
    public void start() {
        initInput();
    }
    /**
     * Called every frame
     */
    @Override
    public void rawUpdate() {
        
        // Get raw input !speed! (input/deltatime)
        m_mouseSmoothBuffer.add(rawMouseMove().divide(Time.unscaledDeltaTime()));
        while(m_mouseSmoothBuffer.size() > MOUSE_SMOOTH_BUFFER)
            m_mouseSmoothBuffer.remove(0);
        
        // Set movement
        m_mouseMove = getSmoothMouseInput(MOUSE_SMOOTH_BUFFER);     // Smoothing mouse
        m_mouseMove = getMouseAcceleration(m_mouseMove);            // Accelerating mouse
        m_mouseMove = m_mouseMove.mult(Time.unscaledDeltaTime());   // Input speed to distance
    }
    
    /**
     * Raw mouse input
     * @return 
     */
    public Vector2f rawMouseMove() {
        Vector2f _movement = m_rawMouseMove.clone();
        m_rawMouseMove = new Vector2f(0.0f, 0.0f);
        return _movement;
    }
    /**
     * Get mouse movement
     * @return 
     */
    public Vector2f mouseMove() {
        return m_mouseMove.clone();
    }
    /**
     * Get input axis
     * @return 
     */
    public Vector2f rawInputAxes() {
        Vector2f __axis = new Vector2f(0.0f, 0.0f);
        
        // Horizontal axis
        if (getButton("D").isDown())
            __axis.x = 1.0f;
        else if (getButton("A").isDown())
            __axis.x = -1.0f;
        
        // Vertical Axes
        if (getButton("W").isDown())
            __axis.y = 1.0f;
        else if (getButton("S").isDown())
            __axis.y = -1.0f;
        
        return __axis;
    }
    
    /**
     * Smooth out the mouse input
     * @param checks
     * @return 
     */
    private Vector2f getSmoothMouseInput(int checks) {
        
        // Vars
        Vector2f total = Vector2f.ZERO;
        float weight = 1.0f;
        float devider = 0.0f;
        
        // For all buffers
        for (int i = m_mouseSmoothBuffer.size() - 1; i >= Mathf.max(0, m_mouseSmoothBuffer.size() - checks); i--) {
            
            // get current
            Vector2f __tempv = new Vector2f(m_mouseSmoothBuffer.get(i));
            __tempv = __tempv.mult(weight);
            
            // Set vars
            total = total.add(__tempv);
            devider += weight;
            weight *= Mathf.clamp(MOUSE_SMOOTH_WEIGHT);
        }
        
        // Set total
        total = total.divide(devider);
        return total; //total.divide(Time.unscaledDeltaTime());
    }
    /**
     * Accelerate mouse input
     * (1 / (a + b)) * (v * |v|) * b * c
     * @param inp
     * @return 
     */
    private Vector2f getMouseAcceleration(Vector2f inp) {
        
        Vector2f __v = new Vector2f(inp);
        
        if (MOUSE_ACCELERATION_ACTIVE) {
            __v = __v.multLocal(new Vector2f(Mathf.abs(__v.x) + MOUSE_LINEAR, Mathf.abs(__v.y) + MOUSE_LINEAR));
            __v = __v.multLocal(1.0f / (MOUSE_ACCELERATION + MOUSE_LINEAR));
        }
        
        __v = getClampedInput(__v); // Clamp mouse input
        __v = __v.multLocal(new Vector2f(MOUSE_SENSITIVITY_X, MOUSE_SENSITIVITY_Y));
        return __v;
    }
    /**
     * Clamp input
     * @param inp
     * @return 
     */
    private Vector2f getClampedInput(Vector2f inp) {
        if (!MOUSE_CLAMPING_ACTIVE)
            return inp;
        
        return new Vector2f(
                Mathf.clamp(inp.x, -Mathf.abs(MOUSE_CLAMPING_X), Mathf.abs(MOUSE_CLAMPING_X)),
                Mathf.clamp(inp.y, -Mathf.abs(MOUSE_CLAMPING_Y), Mathf.abs(MOUSE_CLAMPING_Y)));
    }
    
    /**
     * Initialize all buttons
     */
    private void initInput() {
        
        // Clear default
        Main.inputManager().clearMappings();
        Main.inputManager().clearRawInputListeners();
        
        m_buttons = new Button[] {
            new Button("W",     new KeyTrigger(KeyInput.KEY_W)),                    //  0
            new Button("A",     new KeyTrigger(KeyInput.KEY_A), true),              //  1
            new Button("S",     new KeyTrigger(KeyInput.KEY_S), true),              //  2
            new Button("D",     new KeyTrigger(KeyInput.KEY_D)),                    //  3
            new Button("Q",     new KeyTrigger(KeyInput.KEY_Q), true),              //  4
            new Button("E",     new KeyTrigger(KeyInput.KEY_E)),                    //  5
            new Button("Shift", new KeyTrigger(KeyInput.KEY_LSHIFT)),               //  6
            new Button("Ctrl",  new KeyTrigger(KeyInput.KEY_LCONTROL)),             //  7
            new Button("R",     new KeyTrigger(KeyInput.KEY_R), true),              //  8
            new Button("T",     new KeyTrigger(KeyInput.KEY_T)),                    //  9
            new Button("Y",     new KeyTrigger(KeyInput.KEY_Y)),                    // 10
            new Button("F",     new KeyTrigger(KeyInput.KEY_F)),                    // 11
            new Button("G",     new KeyTrigger(KeyInput.KEY_G)),                    // 12
            new Button("Exit",  new KeyTrigger(KeyInput.KEY_ESCAPE)),               // 13
                
            new Button("Button1", new MouseButtonTrigger(MouseInput.BUTTON_LEFT)),  // 14
            new Button("Button2", new MouseButtonTrigger(MouseInput.BUTTON_MIDDLE)),// 15
            new Button("Button3", new MouseButtonTrigger(MouseInput.BUTTON_RIGHT))  // 16
        };
        
        m_buttons[10].setOnDownCallback(new Callback(Main.instance(), "togglePause"     ));
        m_buttons[11].setOnDownCallback(new Callback(Main.instance(), "resetTimescale"  ));
        m_buttons[12].setOnDownCallback(new Callback(Main.camera()  , "toggleCameraMode"));
        m_buttons[13].setOnDownCallback(new Callback(Main.instance(), "exit"            ));
        m_buttons[14].setOnDownCallback(new Callback(this           , "pickObject"      ));
        
        Main.inputManager().addMapping("-Wheel", new MouseAxisTrigger(MouseInput.AXIS_WHEEL, false));
        Main.inputManager().addMapping("+Wheel", new MouseAxisTrigger(MouseInput.AXIS_WHEEL, true));

        Main.inputManager().addMapping("-MouseX", new MouseAxisTrigger(MouseInput.AXIS_X, false));
        Main.inputManager().addMapping("+MouseX", new MouseAxisTrigger(MouseInput.AXIS_X, true));
        Main.inputManager().addMapping("-MouseY", new MouseAxisTrigger(MouseInput.AXIS_Y, false));
        Main.inputManager().addMapping("+MouseY", new MouseAxisTrigger(MouseInput.AXIS_Y, true));
        
        Utilities.addAll(m_mappings, new String[]{"-Wheel", "+Wheel", "-MouseX", "+MouseX", "-MouseY", "+MouseY"});
        
        Main.inputManager().addListener(m_listener, m_mappings.toArray(new String[m_mappings.size()]));
    }
    /**
     * Raycasting to follow selected transform
     */
    public void pickObject() {
        
        // Raycast hits
        CollisionResults hit = new CollisionResults();
        
        // Calculate raycast direction
        Vector2f _mousePosition = Main.inputManager().getCursorPosition();
        Vector3f from = Main.cam().getWorldCoordinates(new Vector2f(_mousePosition), 0f).clone();
        Vector3f direction = Main.cam().getWorldCoordinates(new Vector2f(_mousePosition), 1.0f).subtractLocal(from).normalizeLocal();
        
        // Create raycast
        Ray ray = new Ray(from, direction);
        Main.root().collideWith(ray, hit);
        
        // vars
        int lowestIndex = -1;
        Long transformID = null;
        float lowestDistance = Float.MAX_VALUE;
        
        // For all hits
        for (int i = 0; i < hit.size(); ++i) {
            
            // Get id to check if can be followed
            Long o = hit.getCollision(i).getGeometry().getUserData(Main.TRANSFORM_ID_KEY);
            
            if (o == null)
                continue;
            
            // Check if this one is the closest transform
            float __dist = hit.getCollision(i).getDistance();
            if (__dist < lowestDistance) {
                
                // Set
                lowestDistance = __dist;
                lowestIndex = i;
                transformID =   (o+0)   ;
            }
        }
        
        if (lowestIndex < 0) {
            // no transform selected
            Main.camera().setTarget(null);
            return;
        }
         // Set
        Main.camera().setTarget(Main.getTransform(transformID));
    }
    
    /**
     * Input listner
     */
    private class InternalListener implements ActionListener, AnalogListener {
        
        /**
         * Buttons
         * @param name
         * @param isPressed
         * @param tpf 
         */
        @Override
        public void onAction(String name, boolean isPressed, float tpf) {
            for (Button b : m_buttons)
                b.safeSet(name, isPressed);
        }

        /**
         * Axis
         * @param name
         * @param value
         * @param tpf 
         */
        @Override
        public void onAnalog(String name, float value, float tpf) {
            
            if (!name.contains("Wheel") && !name.contains("Mouse")) {
                return;
            }

            char sign = name.charAt(0);
            if (sign == '-') {
                value = -value;
            } else if (sign != '+') {
                return;
            }

            if (name.contains("Wheel")) {
                if (Main.camera().cameraMode() != CameraMode.RTS) {
                    return;
                }
                Main.camera().zoom(value);
            } else if (name.contains("Mouse")) {
                
                if (name.charAt(name.length() - 1) == 'X')
                    m_rawMouseMove.x -= value;
                else
                    m_rawMouseMove.y -= value;
                
            }
        }
    }
    public class Button {
        public String name;                 // Button name
        public KeyTrigger trigger;              // Key
        public MouseButtonTrigger mouseTrigger;     // Mouse key
        
        public boolean isNegative;              // Invert
        
        private boolean m_isDown = false;       // is pressed down
        private Callback m_onDownCallback;      // callback on down
        private Callback m_onUpCallback;        // callback on up
        
        /**
         * Constructor
         * @param name
         * @param trigger 
         */
        public Button(String name, KeyTrigger trigger) {
            this.name = name;
            this.trigger = trigger;
            this.mouseTrigger = null;
            this.isNegative = false;
            init();
        }
        /**
         * Constructor
         * @param name
         * @param trigger
         * @param isNegative 
         */
        public Button(String name, KeyTrigger trigger, boolean isNegative) {
            this.name = name;
            this.trigger = trigger;
            this.mouseTrigger = null;
            this.isNegative = isNegative;
            init();
        }
        /**
         * Constructor
         * @param name
         * @param trigger 
         */
        public Button(String name, MouseButtonTrigger trigger) {
            this.name = name;
            this.trigger = null;
            this.mouseTrigger = trigger;
            this.isNegative = false;
            init();
        }
        /**
         * Constructor
         * @param name
         * @param trigger
         * @param isNegative 
         */
        public Button(String name, MouseButtonTrigger trigger, boolean isNegative) {
            this.name = name;
            this.trigger = null;
            this.mouseTrigger = trigger;
            this.isNegative = isNegative;
            init();
        }
        
        /**
         * Safely set button
         * @param name
         * @param isDown 
         */
        public final void safeSet(String name, boolean isDown) {
            if(isButton(name)) {
                set(isDown);
            }  
        }
        /**
         * Unsafe set
         * Does no safety check before setting
         * @param isDown 
         */
        public final void set(boolean isDown) {
            if (isDown && !m_isDown)
                onDown();
            else if (!isDown && m_isDown)
                onUp();
            onSet(isDown);
            m_isDown = isDown;
        }
        /**
         * 
         * @param isDown 
         */
        protected void onSet(boolean isDown) { }
        /**
         * First frame on down
         */
        protected void onDown() { 
            if (m_onDownCallback != null)
                m_onDownCallback.invoke();
        }
        /**
         * First frame on up
         */
        protected void onUp() { 
            if (m_onUpCallback != null)
                m_onUpCallback.invoke();
        }
        
        /**
         * Set callback
         * @param c 
         */
        public final void setOnDownCallback(Callback c) {
            m_onDownCallback = c;
        }
        /**
         * Set callback
         * @param c 
         */
        public final void setOnUpCallback(Callback c) {
            m_onUpCallback = c;
        }
        
        /**
         * Initialize
         */
        public final void init() {
            if (!Utilities.nullOrEmpty(name)) {
                
                // Check wich button to check (mouse or keyboard)
                if (trigger != null) {
                    Main.inputManager().addMapping(name, trigger);
                } else if (mouseTrigger != null) {
                    Main.inputManager().addMapping(name, mouseTrigger);
                } else {
                    return;
                }
                
                // set
                m_mappings.add(name);
            }
        }
        /**
         * Float button value
         * @param name
         * @return 
         */
        public final float isButtonf(String name) {
            return isButton(name) ? (isNegative ? -1.0f : 1.0f) : 0.0f;
        }
        /**
         * Int button value
         * @param s
         * @return 
         */
        public final boolean isButton(String s) {
            return !Utilities.nullOrEmpty(name) && s.equals(name);
        }
        /**
         * Is the button down
         * @return 
         */
        public final boolean isDown() {
            return m_isDown;
        }
    }
}
