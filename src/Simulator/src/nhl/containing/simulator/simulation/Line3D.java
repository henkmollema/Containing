/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nhl.containing.simulator.simulation;

import nhl.containing.simulator.simulation.Main;
import nhl.containing.simulator.simulation.Mathf;
import nhl.containing.simulator.simulation.Transform;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.Node;
import com.jme3.scene.VertexBuffer;
import com.jme3.util.BufferUtils;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author Jens
 */
public final class Line3D extends Transform {
    
    Mesh m_Mesh;
    Geometry geo;
    Material mat;
    
    List<Line3DNode> lineNodes;

    Vector3f[] vertexArray;
    Vector3f[] normalArray;
    ColorRGBA[] colorArray;
    int[] indexArray;
    Vector2f[] uvArray;
    
    /*
     * Line3D is used to draw lines in 3D space 
     * It takes a list of Line3DNodes(Position, width, color)
     * Note that at this time the Positions are relative to the Line3D object
     */
    public Line3D(List<Line3DNode> points, Material material)
    {
        super();
        m_Mesh = new Mesh();
        mat  = material;
        
        geo  = new Geometry();
        geo.setMaterial(mat);
        geo.setMesh(m_Mesh);
        this.attachChild(geo);
        
        lineNodes = points;
        
        initArrays(); //Initialize the arrays
        
        UpdateMesh();//Generate the mesh according to the given cam
        Main.root().attachChild(this);
    }
    public Line3D(Material material, Line3DNode... nodes)
    {
        super();
        m_Mesh = new Mesh();
        mat  = material;
        
        geo  = new Geometry();
        geo.setMaterial(mat);
        geo.setMesh(m_Mesh);
        this.attachChild(geo);
        
        lineNodes = new ArrayList<Line3DNode>();
        lineNodes.addAll(Arrays.asList(nodes));
        
        initArrays(); //Initialize the arrays
        
        UpdateMesh();//Generate the mesh according to the given cam
    }
   public Line3D(Transform parent, Material material, Line3DNode... nodes)
    {
        super(parent);
        m_Mesh = new Mesh();
        mat  = material;
        
        geo  = new Geometry();
        geo.setMaterial(mat);
        geo.setMesh(m_Mesh);
        this.attachChild(geo);
        
        lineNodes = new ArrayList<Line3DNode>();
        lineNodes.addAll(Arrays.asList(nodes));
        
        initArrays(); //Initialize the arrays
        
        UpdateMesh();//Generate the mesh according to the given cam
    }
    
    /*
     * Initializes the vertex, normal, color, index, and textcoord arrays.
     * The index and UV values are also generated here.
     * Call this when altering the lineNodes list.
     */
    public void initArrays()
    {
        vertexArray = new Vector3f[lineNodes.size()*2];
        normalArray = new Vector3f[lineNodes.size()*2];
        colorArray = new ColorRGBA[lineNodes.size()*2];
        uvArray = new Vector2f[lineNodes.size()*2];
        for (int i = 0; i < lineNodes.size(); i++)
        {
            uvArray[i*2] = uvArray[i*2+1] = new Vector2f((float)i/(lineNodes.size()-1),0);
            uvArray[i*2+1].y = 1.0f;
        }
        
        setIndicies(); //Generates the indicies for the triangle strip
    }
    
    /*
     * Fill the indexArray as a triangle strip
     */
    private void setIndicies()
    {
        int triCount = (lineNodes.size() -1) * 2;
        indexArray = new int[triCount*3];
        
        for(int i = 0; i < triCount; i++)
        {
            if(i%2 == 1)
            {
                indexArray[i*3] = i;
                indexArray[(i*3)+1] = i + 2;
                indexArray[(i*3)+2] = i + 1;
            }else
            {
                indexArray[i*3] = i + 2;
                indexArray[(i*3)+1] = i;
                indexArray[(i*3)+2] = i + 1;
            }
        }
        
    }

    /*
     * Generates the mesh from the perspective of the initialized camera.
     * This should be called every time the camera's perspective on the line changes.
     * Generates the vertex, normal and color values and sets the buffers in the mesh object.
     */
    public void UpdateMesh()
    {
        UpdateMesh(Main.instance().getCamera());
    }

    /*
     * Generates the mesh from the perspective of the given camera.
     * This should be called every time the camera's perspective on the line changes.
     * Generates the vertex, normal and color values and sets the buffers in the mesh object.
     */
    public void UpdateMesh(Camera aCamera)
    {
       Vector3f localViewPos = Vector3f.ZERO;
       super.worldToLocal(aCamera.getLocation(), localViewPos);
       
       Vector3f[] vertices   = this.vertexArray;
       Vector3f[] normals    = this.normalArray;
       ColorRGBA[]   colors  = this.colorArray;
       Vector3f oldTangent  = new Vector3f();
       Vector3f oldDir      = new Vector3f();

       for (int i = 0; i < lineNodes.size()-1; i++)
       {
           Vector3f faceNormal  = (localViewPos.subtract(lineNodes.get(i).position)).normalize();
           Vector3f dir         = (lineNodes.get(i+1).position.subtract(lineNodes.get(i).position));
           Vector3f tangent     = dir.cross(faceNormal).normalize();
           Vector3f offset      = (oldTangent.add(tangent)).normalize().mult(lineNodes.get(i).width/2.0f);
           vertices[i*2]        = lineNodes.get(i).position.subtract(offset);
           vertices[i*2+1]      = lineNodes.get(i).position.add(offset);            
           normals[i*2]         = normals[i*2+1] = faceNormal;
           colors[i*2]          = colors[i*2+1] = lineNodes.get(i).color;
           if (i == lineNodes.size() - 2) // last two points
           {
               vertices[i*2+2] = lineNodes.get(i+1).position.subtract(tangent.mult(lineNodes.get(i+1).width/2.0f));
               vertices[i*2+3] = lineNodes.get(i+1).position.add(tangent.mult(lineNodes.get(i+1).width/2.0f));
               normals[i*2+2]  = normals[i*2+3] = faceNormal;
               colors[i*2+2]   = colors[i*2+3] = lineNodes.get(i+1).color;
           }
           oldDir = dir;
           oldTangent = tangent;
       }

       m_Mesh.setBuffer(VertexBuffer.Type.Position, 3, BufferUtils.createFloatBuffer(vertexArray));
       m_Mesh.setBuffer(VertexBuffer.Type.Normal, 3, BufferUtils.createFloatBuffer(normalArray));
       m_Mesh.setBuffer(VertexBuffer.Type.TexCoord, 2, BufferUtils.createFloatBuffer(uvArray));
       m_Mesh.setBuffer(VertexBuffer.Type.Index,    3, BufferUtils.createIntBuffer(indexArray));

       m_Mesh.updateBound();
    }
 
    /*
     * Set the position of the line node at a given index
     */
    public void SetPosition(int aIndex, Vector3f aPosition)
    {
        if (aIndex < 0 || aIndex >= lineNodes.size()) return;
            
        lineNodes.get(aIndex).position = aPosition;
    }
    
    /*
     * Set the width of the line node at a given index
     */
    public void SetWidth(int aIndex, float aWidth)
    {
        if (aIndex < 0 || aIndex >= lineNodes.size()) return;
            
        lineNodes.get(aIndex).width = aWidth;
    }
    
    /*
     * Set the width at the start and end of the line
     * Interpolates the values in between
     */
    public void SetWidth(float aStartWidth, float aEndWidth)
    {
        for (int i = 0; i < lineNodes.size(); i++)
        {
            lineNodes.get(i).width = Mathf.lerp(aStartWidth,aEndWidth,(float)i/(lineNodes.size()-1));
        }
    }
    
    /*
     * Set the color of the line node at a given index
     */
    public void SetColor(int aIndex, ColorRGBA aColor)
    {
        if (aIndex < 0 || aIndex >= lineNodes.size()) return;
            
        lineNodes.get(aIndex).color = aColor;
    }
    
    

}


