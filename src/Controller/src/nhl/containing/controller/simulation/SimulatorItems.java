/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nhl.containing.controller.simulation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import nhl.containing.controller.PathFinder;
import nhl.containing.controller.Vector3f;
import nhl.containing.networking.protobuf.SimulationItemProto.*;

/**
 * Saves all the simulator items
 * @author Niels
 */
public class SimulatorItems
{
    public static final int SEA_SHIP_CRANE_COUNT = 10;
    public static final int TRAIN_CRANE_COUNT = 4;
    public static final int LORRY_CRANE_COUNT = 20;
    public static final int INLAND_SHIP_CRANE_COUNT = 8;
    public static final int STORAGE_CRANE_COUNT = 72;
    public static final int SEASHIP_COUNT = 2;
    public static final int INLANDSHIP_COUNT  = 2;
    
    public static final int STORAGE_BEGIN = INLAND_SHIP_CRANE_COUNT + LORRY_CRANE_COUNT + SEA_SHIP_CRANE_COUNT;
    public static final int SEASHIP_BEGIN = INLAND_SHIP_CRANE_COUNT + LORRY_CRANE_COUNT;
    public static final int LORRY_BEGIN = INLAND_SHIP_CRANE_COUNT;
    public static final int TRAIN_BEGIN = INLAND_SHIP_CRANE_COUNT + LORRY_CRANE_COUNT + SEA_SHIP_CRANE_COUNT + STORAGE_CRANE_COUNT;
    
    private Storage[] m_storages = new Storage[STORAGE_CRANE_COUNT];
    private Platform[] m_trainPlatforms = new Platform[TRAIN_CRANE_COUNT];
    private Shipment m_trainShipment = null;
    
    private Platform[] m_seaPlatforms = new Platform[SEA_SHIP_CRANE_COUNT];
    private Shipment[] m_seashipShipment = new Shipment[2];
    
    private Platform[] m_inlandPlatforms = new Platform[INLAND_SHIP_CRANE_COUNT];
    private Shipment[] m_inlandShipment = new Shipment[2];
    
    private LorryPlatform[] m_lorryPlatforms = new LorryPlatform[LORRY_CRANE_COUNT];
    
    private Map<Long,Parkingspot> m_parkingspotsMap = new HashMap<>();
    private List<Parkingspot> m_parkingspots = new ArrayList<>();
    
    private List<Node> m_Nodes = new ArrayList<>();
    private List<AGV> m_AGVs = new ArrayList<>();
    
    /**
     * Creates a Simulatoritems object
     * @param list list with metadata from simulator
     */
    public SimulatorItems(SimulatorItemList list){
        for(int i = list.getItemsList().size() - 1; i > 0; i--){
            SimulationItem item = list.getItemsList().get(i);
            switch(item.getType()){
                case AGV:
                    m_AGVs.add(new AGV((int)item.getId()));
                    break;
                case PLATFORM_STORAGE:
                    addStorage((int)item.getId());
                    break;
                case PLATFORM_TRAIN:
                    addTrainPlatform((int)item.getId());
                    break;
                case PLATFORM_SEASHIP:
                    addSeashipPlatform((int)item.getId());
                    break;
                case PLATFORM_INLANDSHIP:
                    addInlandPlatform((int)item.getId());
                    break;
                case PLATFORM_LORRY:
                    addLorryPlatform((int)item.getId());
                    break;
                case PARKINGSPOT_STORAGE:
                case PARKINGSPOT_TRAIN:
                case PARKINGSPOT_LORRY:
                case PARKINGSPOT_SEASHIP:
                case PARKINGSPOT_INLANDSHIP:
                    if(item.getConnectionsCount() < 2){
                        continue; //TODO error?
                    }
                    Parkingspot spot = new Parkingspot(item.getId(), new Vector3f(item.getX(), item.getY(), item.getZ()),item.getConnections(0),item.getConnections(1));
                    m_parkingspotsMap.put(item.getId(), spot);
                    if(item.getParentID() != -1){
                        switch(item.getType()){
                            case PARKINGSPOT_STORAGE:
                                m_storages[item.getParentID() - STORAGE_BEGIN].addParkingspot(spot);
                                break;
                            case PARKINGSPOT_TRAIN:
                                m_trainPlatforms[item.getParentID() - TRAIN_BEGIN].addParkingspot(spot);
                                break;
                            case PARKINGSPOT_SEASHIP:
                                m_seaPlatforms[item.getParentID() - SEASHIP_BEGIN].addParkingspot(spot);
                                break;
                            case PARKINGSPOT_INLANDSHIP:
                                m_inlandPlatforms[item.getParentID()].addParkingspot(spot);
                                break;
                            case PARKINGSPOT_LORRY:
                                m_lorryPlatforms[item.getParentID() - LORRY_BEGIN].addParkingspot(spot);
                                break;
                        }
                    }
                    m_parkingspots.add(spot);
                    break;
                case NODES:
                    m_Nodes.add(new Node((int)item.getId(), item.getX(), item.getY(), item.getConnectionsList()));
                    break;
            }
        }
        for(Storage s : m_storages){
            Collections.reverse(s.parkingspots);
        }try{
            PathFinder.initPath(m_Nodes);
        }catch(Exception e){e.printStackTrace();}      
    }
    
    /**
     * Gets platforms for a specific carrier
     * @param carrier carrier
     * @return platforms
     */
    public Platform[] getPlatformsByCarrier(Carrier carrier){
        if(carrier instanceof Train)
            return m_trainPlatforms;
        else if(carrier instanceof Truck)
            return m_lorryPlatforms;
        else if(carrier instanceof SeaShip)
            return m_seaPlatforms;
        else
            return m_inlandPlatforms;
    }
    
    /**
     * Gets an parkingspot by an ID
     * @param id id of parkingspot
     * @return parkingspot
     */
    public Parkingspot getParkingspotByID(long id){
        return m_parkingspotsMap.get(id);
    }
    
    /**
     * Get storages array
     * @return array of storages
     */
    public Storage[] getStorages(){
        return m_storages;
    }
    
    /**
     * Get trainPlatforms
     * @return Platforms
     */
    public Platform[] getTrainPlatforms(){
        return m_trainPlatforms;
    }
    
    /**
     * Get seashipPlatforms
     * @return Platforms
     */
    public Platform[] getSeaShipPlatforms(){
        return m_seaPlatforms;
    }
    
    /**
     * Get inlandPlatforms
     * @return Platforms
     */
    public Platform[] getInlandPlatforms(){
        return m_inlandPlatforms;
    }
    
    /**
     * get LorryPlatforms
     * @return Platforms
     */
    public LorryPlatform[] getLorryPlatforms(){
        return m_lorryPlatforms;
    }
    
    /**
     * get Parkingspots
     * @return Parkingspots
     */
    public List<Parkingspot> getParkingspots(){
        return m_parkingspots;
    }
    
    /**
     * Get Nodes
     * @return Nodes
     */
    public List<Node> getNodes(){
        return m_Nodes;
    }
    
    /**
     * Get node by a id
     * @param id id
     * @return node
     */
    public Node getNode(int id){
        for(Node n : m_Nodes){
            if(n.m_id == id)
                return n;
        }
        return null;
    }
    
    /**
     * Gets a platform by an AGV ID
     * @param id id of the agv
     * @return Platform
     */
    public Platform getPlatformByAGVID(int id){
        for(Parkingspot p : m_parkingspots){
            if(p.hasAGV() && p.getAGV().getID() == id)
                return  p.getPlatform();
        }
        return null;
    }
    
    /**
     * Gets current train shipment
     * @return shipment
     */
    public Shipment getTrainShipment(){
        return m_trainShipment;
    }
    
    /**
     * Sets a shipment as current train shipment
     * @param shipment shipment
     */
    public void setTrainShipment(Shipment shipment){
        m_trainShipment = shipment;
    }
    
    /**
     * Unsets a current train shipment
     */
    public void unsetTrainShipment(){
        m_trainShipment = null;
    }
    
    /**
     * Checks if there is a current train shipment
     * @return true when there is a current shipment, otherwise false
     */
    public boolean hasTrainShipment(){
        return m_trainShipment != null;
    }
    
    /**
     * Gets current seashipment
     * @param index index of the seashipping
     * @return shipment
     */
    public Shipment getSeaShipment(int index){
        if(index >= m_seashipShipment.length)
            return null;
        return m_seashipShipment[index];
    }
    
    /**
     * Sets a shipment as current seaship shipment
     * @param index of the sea shipping
     * @param shipment shipment
     */
    public void setSeaShipment(int index,Shipment shipment){
        if(index >= m_seashipShipment.length)
            return;
        m_seashipShipment[index] = shipment;
    }
    
    /**
     * Unsets current seashipment
     * @param index index of the sea shipping
     */
    public void unsetSeaShipment(int index){
        if(index >= m_seashipShipment.length)
            return;
        m_seashipShipment[index] = null;
    }
    
    /**
     * Unsets current sea shipment
     * @param shipment shipment to unset
     */
    public void unsetSeaShipment(Shipment shipment){
        for(int i = 0; i < m_seashipShipment.length; i++){
            if(hasSeaShipment(i) && m_seashipShipment[i].key.equals(shipment.key))
                m_seashipShipment = null;
        }
    }
    
    /**
     * Check if there is a current seaships shipment
     * @param index index of the sea shipping
     * @return true when there is a shipment, otherwise false
     */
    public boolean hasSeaShipment(int index){
        if(index >= m_seashipShipment.length)
            return false;
        return m_seashipShipment[index] != null;
    }
    
    /**
     * Gets current inlandshipment
     * @param index of the inland shipping
     * @return shipment
     */
    public Shipment getInlandShipment(int index){
        if(index >= m_inlandShipment.length)
            return null;
        return m_inlandShipment[index];
    }
    
    /**
     * Sets current inland shipment
     * @param index of the inland shipping
     * @param shipment shipment
     */
    public void setInlandShipment(int index,Shipment shipment){
        if(index >= m_inlandShipment.length)
            return;
        m_inlandShipment[index] = shipment;
    }
    
    /**
     * Unsets current inland shipment
     * @param index of the inland shipping
     */
    public void unsetInlandShipment(int index){
        if(index >= m_inlandShipment.length)
            return;
        m_inlandShipment[index] = null;
    }
    
    /**
     * Unsets current inland shipment
     * @param shipment shipment to unset
     */
    public void unsetInlandShipment(Shipment shipment){
        for(int i = 0; i < m_inlandShipment.length; i++){
            if(hasInlandShipment(i) && m_inlandShipment[i].key.equals(shipment.key))
                m_inlandShipment[i] = null;
        }
    }
    
    /**
     * Checks if there is a current inland shipment
     * @param index of the inland shipping
     * @return true when there is a current inland shipment, otherwise false
     */
    public boolean hasInlandShipment(int index){
        if(index >= m_inlandShipment.length)
            return false;
        return m_inlandShipment[index] != null;
    }    
    
    /**
     * Gets the platforms by the right inland shipment
     * @param shipment shipment
     * @return platforms
     */
    public Platform[] getInlandPlatformsByShipment(Shipment shipment){
        Platform[] platforms = new Platform[INLAND_SHIP_CRANE_COUNT / 2];
        int begin;
        if(hasInlandShipment(0) && m_inlandShipment[0].key.equals(shipment.key)){
            begin = 0;
        }else if(hasInlandShipment(1) && m_inlandShipment[1].key.equals(shipment.key)){
            begin = INLAND_SHIP_CRANE_COUNT / 2;
        }else{
            return null;
        }
        for(int i = 0;i < (INLAND_SHIP_CRANE_COUNT / 2);i++){
            platforms[i] = m_inlandPlatforms[i +  begin];
        }
        return platforms;
    }
    
    /**
     * Gets the platforms by the right sea shipment
     * @param shipment shipment
     * @return platforms
     */
    public Platform[] getSeaPlatformsByShipment(Shipment shipment){
        Platform[] platforms = new Platform[SEA_SHIP_CRANE_COUNT / 2];
        int begin;
        if(hasSeaShipment(0) && m_seashipShipment[0].key.equals(shipment.key)){
            begin = 0;
        }else if(hasSeaShipment(1) && m_seashipShipment[1].key.equals(shipment.key)){
            begin = SEA_SHIP_CRANE_COUNT / 2;
        }else{
            return null;
        }
        for(int i = 0;i < (SEA_SHIP_CRANE_COUNT / 2);i++){
            platforms[i] = m_seaPlatforms[i +  begin];
        }
        return platforms;
    }
    
    /**
     * Gets a free AGV
     * @return agv
     */
    public AGV getFreeAGV(){
        for(AGV agv : m_AGVs){
            if(!agv.isBusy())
                return agv;
        }
        return null;
    }
    
    public AGV getAGV(int agvID)
    {
        for(AGV agv : m_AGVs){
            if(agv.getID() == agvID)
                return agv;
        }
        return null;
    }
    
    /**
     * Adds a storagePlatform
     * @param id id of storage
     */
    private void addStorage(int id){
        m_storages[id - STORAGE_BEGIN] = new Storage(id);
    }
    
    /**
     * Adds a trainPlatform
     * @param id id of trainPlatform
     */
    private void addTrainPlatform(int id){
        Platform nPlatform = new Platform(id);
        m_trainPlatforms[id - TRAIN_BEGIN] = nPlatform;

    }
    
    /**
     * Adds seashipPlatform
     * @param id if of seashipPlatform
     */
    private void addSeashipPlatform(int id){
        m_seaPlatforms[id - SEASHIP_BEGIN] = new Platform(id);

    }
    
    /**
     * Adds inlandPlatform
     * @param id id of inlandPlatform
     */
    private void addInlandPlatform(int id){
        Platform nPlatform = new Platform(id);
        m_inlandPlatforms[id] = nPlatform;
    }
    
    /**
     * Adds lorryPlatform
     * @param id if of lorryPlatform
     */
    private void addLorryPlatform(int id){
        LorryPlatform nPlatform = new LorryPlatform(id);
        m_lorryPlatforms[id - LORRY_BEGIN] = nPlatform;

    }   
    
    /**
     * Gets a free lorryplatform
     * @return lorry platform
     */
    public LorryPlatform GetFreeLorryPlatform(){
        for(LorryPlatform p : m_lorryPlatforms){
            if(!p.hasShipment())
                return p;
        }
        return null;
    }

}
