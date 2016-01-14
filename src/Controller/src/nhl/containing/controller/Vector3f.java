package nhl.containing.controller;

/**
 * A 3-element vector that is represented by single-precision
 * floating point x, y, z coordinates.
 *
 * @author henkmollema
 */
public class Vector3f
{
    /**
     * The x coordinate.
     */
    public float x;
    
    /**
     * The y coordinate.
     */
    public float y;
    
    /**
     * The z coordinate.
     */
    public float z;

    public Vector3f()
    {
    }

    public Vector3f(float x, float y, float z)
    {
        this.x = x;
        this.y = y;
        this.z = z;
    }
}
