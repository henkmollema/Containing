/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nhl.containing.controller.simulation;

import nhl.containing.controller.Vector3f;

/**
 *
 * @author Niels
 */
public final class Parkingspot
{
    private long m_id;
    private Vector3f m_position;
    
    public Parkingspot(long id,Vector3f position){
        m_id = id;
        m_position = position;
    }
    
    public long getId(){
        return m_id;
    }
    
    public Vector3f getPosition(){
        return m_position;
    }
    
}
