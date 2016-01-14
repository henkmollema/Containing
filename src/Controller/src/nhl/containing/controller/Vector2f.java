package nhl.containing.controller;

/**
 * A 2-element vector that is represented by single-precision
 * floating point x and y coordinates.
 *
 * @author Niels
 */
public class Vector2f
{
    /**
     * The x coordinate.
     */
    public float x;
    
    /**
     * The y coordinate.
     */
    public float y;

    public Vector2f(float x, float y)
    {
        this.x = x;
        this.y = y;
    }
}
