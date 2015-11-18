/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Simulation;

import Game.Camera;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.math.Vector2f;
import java.util.ArrayList;
import java.util.List;

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
    private Transform m_target;
    
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
        
        if (Main.instance().camera().viewType() == Camera.ViewType.RTS) {
            //
            //
        } else {
            //
            //
        }
        
        Main.inputManager().addMapping("timescale-lower", new KeyTrigger(KeyInput.KEY_R));
        Main.inputManager().addMapping("timescale-higher", new KeyTrigger(KeyInput.KEY_T));
        Main.inputManager().addMapping("pause", new KeyTrigger(KeyInput.KEY_Y));
        Main.inputManager().addMapping("view-switch", new KeyTrigger(KeyInput.KEY_C));
        Main.inputManager().addMapping("high-speed", new KeyTrigger(KeyInput.KEY_LSHIFT));
        Main.inputManager().addMapping("low-speed", new KeyTrigger(KeyInput.KEY_LCONTROL));
        
        Main.inputManager().addListener(Main.instance().actionListener, "pause", "timescale-lower", "timescale-higher", "view-switch", "high-speed", "low-speed");
        
    }
    
    
    
    
    public enum CameraViewType {
        Fly,
        RTS
    }
}
