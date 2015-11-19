/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Simulation;

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
import Utilities.Utilities;

/**
 *
 * @author sietse
 */
public class Input extends Behaviour {
    
    // Smoothing
    public final float      MOUSE_SENSITIVITY_X         = 5.0f;
    public final float      MOUSE_SENSITIVITY_Y         = 5.0f;
    public final int        MOUSE_SMOOTH_CHECKS         = 10;
    
    // Acceleration
    public final boolean    MOUSE_ACCELERATION_ACTIVE   = true;
    public final float      MOUSE_ACCELERATION          = 1.0f;
    public final float      MOUSE_LINEAR                = 4.0f;
    
    // Clamp
    public final boolean    MOUSE_CLAMPING_ACTIVE       = true;
    public final float      MOUSE_CLAMPING_X            = 4.0f;
    public final float      MOUSE_CLAMPING_Y            = 4.0f;
    
    
    public final Vector2f   MOUSE_CLAMPING              = new Vector2f(Mathf.abs(MOUSE_CLAMPING_X), Mathf.abs(MOUSE_CLAMPING_Y));
    public final Vector2f   MOUSE_SENSITIVITY           = new Vector2f(MOUSE_SENSITIVITY_X, MOUSE_SENSITIVITY_Y);
    public final int        MOUSE_SMOOTH_BUFFER         = 6;
    public final int        MOUSE_MAX_SMOOTH_BUFFER     = Mathf.max(1, MOUSE_SMOOTH_CHECKS);
    public final float      MOUSE_SMOOTH_WEIGHT         = Mathf.clamp(0.5f);
    
    // 
    private Vector2f        m_rawMouseMove = Vector2f.ZERO;
    private Vector2f        m_mouseMove = Vector2f.ZERO;
    private List<Vector2f>  m_mouseSmoothBuffer;
    
    // Double click
    public final static float DOUBLE_CLICK_TIME = 0.5f;
    private float m_doubleClickTimer = 0.0f;
    private Transform m_doubleClickTarget;
    
    // 
    //private Transform m_target;
    private final InternalListener m_listener = new InternalListener();
    private static List<String> m_mappings = new ArrayList<String>();
    
    private Button[] m_buttons;
    
    /**
     *
     * @param button
     * @return
     */
    public final Button getButton(String button) {
        for (Button b : m_buttons)
            if (b.isButton(button))
                return b;
        return null;
    }
    
    @Override
    public void awake() {
        m_mouseSmoothBuffer = new ArrayList<Vector2f>(MOUSE_MAX_SMOOTH_BUFFER);
        initInput();
    }
    @Override
    public void update() {
        m_rawMouseMove = getRawMouseInput();
        
        // 
        m_mouseSmoothBuffer.add(new Vector2f(m_rawMouseMove).divide(Time.unscaledDeltaTime()));
        while(m_mouseSmoothBuffer.size() > MOUSE_MAX_SMOOTH_BUFFER)
            m_mouseSmoothBuffer.remove(0);
        
        m_mouseMove = getSmoothMouseInput(MOUSE_SMOOTH_BUFFER);
        m_mouseMove = getMouseAcceleration(m_mouseMove);
        m_mouseMove = getClampedInput(m_mouseMove);
    }
    
    // 
    public Vector2f rawMouseMove() {
        return m_rawMouseMove.clone();
    }
    public Vector2f mouseMove() {
        return m_mouseMove.clone();
    }
    
    // 
    private Vector2f getRawMouseInput() {
        return Vector2f.ZERO;
    }
    private Vector2f getSmoothMouseInput(int checks) {
        checks = Mathf.clamp(checks, 0, MOUSE_MAX_SMOOTH_BUFFER);
        
        Vector2f total = Vector2f.ZERO;
        float weight = 1.0f;
        float devider = 0.0f;
        for (int i = m_mouseSmoothBuffer.size() - 1; i >= Mathf.max(0, m_mouseSmoothBuffer.size() - checks); i--) {
            Vector2f __tempv = new Vector2f(m_mouseSmoothBuffer.get(i));
            __tempv = __tempv.mult(weight);
            
            total = total.add(__tempv);
            devider += weight;
            weight *= MOUSE_SMOOTH_WEIGHT;
        }
        total = total.divide(devider);
        return total.mult(Time.unscaledDeltaTime());
    }
    private Vector2f getMouseAcceleration(Vector2f inp) {
        if (!MOUSE_ACCELERATION_ACTIVE)
            return inp;
        return new Vector2f(
                (1.0f / (MOUSE_ACCELERATION + MOUSE_LINEAR)) * inp.x * Mathf.abs(inp.x) * MOUSE_SENSITIVITY_X * MOUSE_LINEAR,
                (1.0f / (MOUSE_ACCELERATION + MOUSE_LINEAR)) * inp.y * Mathf.abs(inp.y) * MOUSE_SENSITIVITY_Y * MOUSE_LINEAR);
    }
    private Vector2f getClampedInput(Vector2f inp) {
        if (!MOUSE_CLAMPING_ACTIVE)
            return inp;
        return new Vector2f(
                Mathf.clamp(inp.x, -Mathf.abs(MOUSE_CLAMPING_X), Mathf.abs(MOUSE_CLAMPING_X)),
                Mathf.clamp(inp.y, -Mathf.abs(MOUSE_CLAMPING_Y), Mathf.abs(MOUSE_CLAMPING_Y)));
    }
    
    //
    private void initInput() {
        
        // Clear default
        Main.inputManager().clearMappings();
        Main.inputManager().clearRawInputListeners();
        //Main.inputManager().deleteMapping(SimpleApplication.INPUT_MAPPING_MEMORY);
        
        m_buttons = new Button[] {
            new Button("W",     new KeyTrigger(KeyInput.KEY_W)),                //  0
            new Button("A",     new KeyTrigger(KeyInput.KEY_A), true),          //  1
            new Button("S",     new KeyTrigger(KeyInput.KEY_S), true),          //  2
            new Button("D",     new KeyTrigger(KeyInput.KEY_D)),                //  3
            new Button("Q",     new KeyTrigger(KeyInput.KEY_Q), true),          //  4
            new Button("E",     new KeyTrigger(KeyInput.KEY_E)),                //  5
            new Button("Shift", new KeyTrigger(KeyInput.KEY_LSHIFT)),           //  6
            new Button("Ctrl",  new KeyTrigger(KeyInput.KEY_LCONTROL)),         //  7
            new Button("R",     new KeyTrigger(KeyInput.KEY_R), true),          //  8
            new Button("T",     new KeyTrigger(KeyInput.KEY_T)),                //  9
            new Button("Y",     new KeyTrigger(KeyInput.KEY_Y)),                // 10
            new Button("F",     new KeyTrigger(KeyInput.KEY_F)),                // 11
            new Button("G",     new KeyTrigger(KeyInput.KEY_G)),                // 12
            new Button("Exit",  new KeyTrigger(KeyInput.KEY_ESCAPE))            // 13
        };
        
        m_buttons[10].setOnDownCallback(new Callback(Main.instance(), "togglePause"));
        m_buttons[11].setOnDownCallback(new Callback(Main.instance(), "resetTimescale"));
        m_buttons[12].setOnDownCallback(new Callback(Main.instance().camera(), "toggleCameraMode"));
        m_buttons[13].setOnDownCallback(new Callback(Main.instance(), "exit"));
        
        Main.inputManager().addMapping("-Wheel", new MouseAxisTrigger(MouseInput.AXIS_WHEEL, false));
        Main.inputManager().addMapping("+Wheel", new MouseAxisTrigger(MouseInput.AXIS_WHEEL, true));

        Main.inputManager().addMapping("-MouseX", new MouseAxisTrigger(MouseInput.AXIS_X, false));
        Main.inputManager().addMapping("+MouseX", new MouseAxisTrigger(MouseInput.AXIS_X, true));
        Main.inputManager().addMapping("-MouseY", new MouseAxisTrigger(MouseInput.AXIS_Y, false));
        Main.inputManager().addMapping("+MouseY", new MouseAxisTrigger(MouseInput.AXIS_Y, true));

        Main.inputManager().addMapping("Button1", new MouseButtonTrigger(MouseInput.BUTTON_LEFT));
        Main.inputManager().addMapping("Button2", new MouseButtonTrigger(MouseInput.BUTTON_MIDDLE));
        Main.inputManager().addMapping("Button3", new MouseButtonTrigger(MouseInput.BUTTON_RIGHT));
        
        Main.inputManager().addListener(m_listener, m_mappings.toArray(new String[m_mappings.size()]));
    }
    
    private class InternalListener implements ActionListener, AnalogListener {
        @Override
        public void onAction(String name, boolean isPressed, float tpf) {
            for (Button b : m_buttons)
                b.safeSet(name, isPressed);
        }

        @Override
        public void onAnalog(String name, float value, float tpf) {
            /*
            if (!isEnabled()) {
                return;
            }

            if (!name.contains("WHEEL") && !name.contains("MOUSE")) {
                return;
            }

            char sign = name.charAt(0);
            if (sign == '-') {
                value = -value;
            } else if (sign != '+') {
                return;
            }

            if (name.contains("Wheel")) {
                if (!wheelEnabled) {
                    return;
                }
                float speed = maxSpeedPerSecondOfAccell[DISTANCE] * maxAccellPeriod[DISTANCE] * WHEEL_SPEED;
                offsetMoves[DISTANCE] += value * speed;
            } else if (name.contains("MOUSE")) {
                if (mouseRotation) {
                    int direction;
                    if (name.endsWith("X")) {
                        direction = ROTATE;
                        if ( up == UpVector.Z_UP ) {
                            value = -value;
                        }
                    } else {
                        direction = TILT;
                    }
                    offsetMoves[direction] += value;
                } else if (mouseDrag) {
                    int direction;
                    if (name.endsWith("X")) {
                        direction = SIDE;
                        if ( up == UpVector.Z_UP ) {
                            value = -value;
                        }
                    } else {
                        direction = FWD;
                        value = -value;
                    }
                    offsetMoves[direction] += value * maxSpeedPerSecondOfAccell[direction] * maxAccellPeriod[direction];
                }
            }
            */
        }
    }
    public class Button {
        public String name;
        public KeyTrigger trigger;
        public boolean isNegative;
        
        private boolean m_isDown = false;
        private Callback m_onDownCallback;
        private Callback m_onUpCallback;
        
        public Button(String name, KeyTrigger trigger) {
            this.name = name;
            this.trigger = trigger;
            this.isNegative = false;
            init();
        }
        public Button(String name, KeyTrigger trigger, boolean isNegative) {
            this.name = name;
            this.trigger = trigger;
            this.isNegative = isNegative;
            init();
        }
        
        public final void safeSet(String name, boolean isDown) {
            if(isButton(name)) {
                set(isDown);
            }
                
        }
        public final void set(boolean isDown) {
            if (isDown && !m_isDown)
                onDown();
            else if (!isDown && m_isDown)
                onUp();
            onSet(isDown);
            m_isDown = isDown;
        }
        protected void onSet(boolean isDown) { }
        protected void onDown() { 
            if (m_onDownCallback != null)
                m_onDownCallback.invoke();
        }
        protected void onUp() { 
            if (m_onUpCallback != null)
                m_onUpCallback.invoke();
        }
        
        public final void setOnDownCallback(Callback c) {
            m_onDownCallback = c;
        }
        public final void setOnUpCallback(Callback c) {
            m_onUpCallback = c;
        }
        
        public final void init() {
            if (!Utilities.nullOrEmpty(name) && trigger != null) {
                Main.inputManager().addMapping(name, trigger);
                m_mappings.add(name);
            }
                
        }
        public final float isButtonf(String name) {
            return isButton(name) ? (isNegative ? -1.0f : 1.0f) : 0.0f;
        }
        public final boolean isButton(String s) {
            return !Utilities.nullOrEmpty(name) && s.equals(name);
        }
        
        public final boolean isDown() {
            return m_isDown;
        }
    }
    public class Toggle extends Button {
        private boolean m_isActive;
        
        public Toggle(String name, KeyTrigger trigger) {
            super(name, trigger, false);
            m_isActive = false;
        }
        public Toggle(String name, KeyTrigger trigger, boolean startValue) {
            super(name, trigger, false);
            m_isActive = startValue;
        }
        
        @Override
        protected void onDown() {
            super.onDown();
            m_isActive = !m_isActive;
        }
        
        public boolean isActive() {
            return m_isActive;
        }
    }
}
