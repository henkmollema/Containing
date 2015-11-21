/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Simulation;

import Game.Camera;
import Game.CameraMode;
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
    public final float      MOUSE_SENSITIVITY_Y         = -5.0f;
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
    private Vector2f        m_tempRawMouseMove = Vector2f.ZERO;
    private Vector2f        m_previousMousePosition = Vector2f.ZERO;
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
        
    }
    
    @Override
    public void start() {
        
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
        m_mouseMove = m_mouseMove.mult(Time.unscaledDeltaTime() * 0.4f);
        
        if (getButton("Button1").isDown()) {
            Debug.log("adfasdfasdfasfasfasfasfasfadfasfasfasfasf");
        }
    }
    
    // 
    public Vector2f rawMouseMove() {
        return m_rawMouseMove.clone();
    }
    public Vector2f mouseMove() {
        return m_mouseMove.clone();
    }
    public Vector2f rawInputAxis() {
        Vector2f __axis = new Vector2f(0.0f, 0.0f);
        
        if (getButton("D").isDown())
            __axis.x = 1.0f;
        else if (getButton("A").isDown())
            __axis.x = -1.0f;
        
        if (getButton("W").isDown())
            __axis.y = 1.0f;
        else if (getButton("S").isDown())
            __axis.y = -1.0f;
        
        return __axis;
    }
    // 
    private Vector2f getRawMouseInput() {
        Vector2f pos = new Vector2f(Main.instance().cursorPosition()).subtract(m_previousMousePosition);
        m_previousMousePosition = new Vector2f(Main.instance().cursorPosition());
        return pos;
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
        Main.instance().flyCamera().setEnabled(false);
        
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
        
        Utilities.addAll(m_mappings, new String[]{"-Wheel", "+Wheel", "-MouseX", "+MouseX", "-MouseY", "+MouseY"});
        
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
                if (Main.instance().camera().cameraMode() != CameraMode.RTS) {
                    return;
                }
                Main.instance().camera().zoom(value);
            } else if (name.contains("Mouse")) {
                //m_tempRawMouseMove
                
                if (getButton("Button1").isDown()) {
                    Debug.log("ASDFasdfafafsasdfasdfafasfasfasdf");
                }
                
                /*
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
                    
                    offsetMoves[direction] += value * maxSpeedPerSecondOfAccell[direction] * maxAccellPeriod[direction];
                }*/
            }
        }
    }
    public class Button {
        public String name;
        public KeyTrigger trigger;
        public MouseButtonTrigger mouseTrigger;
        
        public boolean isNegative;
        
        private boolean m_isDown = false;
        private Callback m_onDownCallback;
        private Callback m_onUpCallback;
        
        public Button(String name, KeyTrigger trigger) {
            this.name = name;
            this.trigger = trigger;
            this.mouseTrigger = null;
            this.isNegative = false;
            init();
        }
        public Button(String name, KeyTrigger trigger, boolean isNegative) {
            this.name = name;
            this.trigger = trigger;
            this.mouseTrigger = null;
            this.isNegative = isNegative;
            init();
        }
        public Button(String name, MouseButtonTrigger trigger) {
            this.name = name;
            this.trigger = null;
            this.mouseTrigger = trigger;
            this.isNegative = false;
            init();
        }
        public Button(String name, MouseButtonTrigger trigger, boolean isNegative) {
            this.name = name;
            this.trigger = null;
            this.mouseTrigger = trigger;
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
            if (!Utilities.nullOrEmpty(name)) {
                
                if (trigger != null) {
                    Main.inputManager().addMapping(name, trigger);
                } else if (mouseTrigger != null) {
                    Main.inputManager().addMapping(name, mouseTrigger);
                } else {
                    return;
                }
                
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
}
