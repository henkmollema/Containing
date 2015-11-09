/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Game;

import Simulation.Behaviour;
import Simulation.Debug;
import Simulation.Main;
import com.jme3.light.DirectionalLight;
import com.jme3.math.ColorRGBA;
import com.jme3.post.FilterPostProcessor;
import com.jme3.post.filters.BloomFilter;
import com.jme3.post.filters.FogFilter;
import com.jme3.post.ssao.SSAOFilter;
import com.jme3.shadow.DirectionalLightShadowFilter;
import com.jme3.shadow.DirectionalLightShadowRenderer;


/**
 *
 * @author sietse
 */
public class Camera extends Behaviour {
    
    // 
    private final float CAMERA_SPEED = 15.0f;
    
    // Shadows
    private final int SHADOW_MAP_RESOLUTION = 2048;
    private final float SHADOW_INTENSITY = 0.4f;
    
    // SSAO
    
    // FOG
    private final float FOG_DENSITY = 10.0f;
    private final ColorRGBA FOG_COLOR = new ColorRGBA(0.6f, 0.7f, 0.9f, 1.0f);
    
    // Bloom
    private final float m_bloomBlurScale= 0.5f;
    private final float m_bloomExposurePower = 1.0f;
    private final float m_bloomExposureCutoff = 0.2f;
    private final float m_bloomIntensity = 0.3f;
    
    private FilterPostProcessor m_postProcessor;
    public FilterPostProcessor postProcessor() {
        if (m_postProcessor == null)
            m_postProcessor = new FilterPostProcessor(Main.assets());
        return m_postProcessor;
    }
    
    @Override
    public void awake() {
        createSSAO();
        createFog();
        createBloom();
    }
    @Override
    public void start() {
        Main.view().addProcessor(postProcessor());
        
        Main.instance().flyCamera().setMoveSpeed(CAMERA_SPEED);
    }
    @Override
    public void update() {
        
    }
    @Override
    public void fixedUpdate() {
        
    }
    
    
    public void createShadowsFiler(DirectionalLight sun) {
        
        DirectionalLightShadowRenderer shadowRenderer = new DirectionalLightShadowRenderer(Main.assets(), SHADOW_MAP_RESOLUTION, 3);
        shadowRenderer.setLight(sun);
        Main.view().addProcessor(shadowRenderer);
        shadowRenderer.setShadowIntensity(SHADOW_INTENSITY);
        
        DirectionalLightShadowFilter shadowFilter = new DirectionalLightShadowFilter(Main.assets(), SHADOW_MAP_RESOLUTION, 3);
        shadowFilter.setLight(sun);
        shadowFilter.setEnabled(true);
        
        postProcessor().addFilter(shadowFilter);
    }
    
    public void createSSAO() {
        SSAOFilter ssaoFilter = new SSAOFilter(6.94f, 10.92f, 0.33f, 0.61f);
        postProcessor().addFilter(ssaoFilter);
    }
    
    public void createFog() {
        FogFilter fogFilter = new FogFilter();
        fogFilter.setEnabled(true);
        fogFilter.setFogColor(FOG_COLOR);
        fogFilter.setFogDensity(FOG_DENSITY);
        postProcessor().addFilter(fogFilter);
    }
    
    public void createBloom() {
        BloomFilter bloomFilter = new BloomFilter();
        bloomFilter.setDownSamplingFactor(2);
        bloomFilter.setBlurScale(m_bloomBlurScale);
        bloomFilter.setExposurePower(m_bloomExposurePower);
        bloomFilter.setExposureCutOff(m_bloomExposureCutoff);
        bloomFilter.setBloomIntensity(m_bloomIntensity);
        
        postProcessor().addFilter(bloomFilter);
    }
}
