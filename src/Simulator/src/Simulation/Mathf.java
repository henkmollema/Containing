/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Simulation;

import com.jme3.math.Vector2f;

/**
 *
 * @author sietse
 */
public final class Mathf {
    
    public static final float PI = 3.141593f;
    public static final float Deg2Rad = 0.01745329f;        // Degrees-to-radians conversion constant 
    public static final float Rad2Deg = 57.29578f;          // Radians-to-degrees conversion constant 
    
    /**
     * Returns the sine of angle f in radians.
     * @param f
     * @return 
     */
    public static float sin(float f) {
        return (float) Math.sin((double) f);
    }
    /**
     * Returns the cosine of angle f in radians.
     * @param f
     * @return 
     */
    public static float cos(float f) {
        return (float) Math.cos((double) f);
    }
    /**
     * Returns the tangent of angle f in radians.
     * @param f
     * @return 
     */
    public static float tan(float f) {
        return (float) Math.tan((double) f);
    }
    /**
     * Returns the arc-sine of f - the angle in radians whose sine is f.
     * @param f
     * @return 
     */
    public static float asin(float f) {
        return (float) Math.asin((double) f);
    }
    /**
     * Returns the arc-cosine of f - the angle in radians whose cosine is f.
     * @param f
     * @return 
     */
    public static float acos(float f) {
        return (float) Math.acos((double) f);
    }
    /**
     * Returns the arc-tangent of f - the angle in radians whose tangent is f.
     * @param f
     * @return 
     */
    public static float atan(float f) {
        return (float) Math.atan((double) f);
    }
    /**
     * Returns the angle in radians whose Tan is y/x.
     * @param y
     * @param x
     * @return 
     */
    public static float atan2(float y, float x) {
        return (float) Math.atan2((double) y, (double) x);
    }
    /**
     * Returns square root of f.
     * @param f
     * @return 
     */
    public static float sqrt(float f) {
        return (float) Math.sqrt((double) f);
    }
    /**
     * Returns the absolute value of f
     * @param f value
     * @return absolute f
     */
    public static float abs(float f) {
        return Math.abs(f);
    }
    /**
     * Returns the absolute value of value
     * @param value
     * @return absolute value
     */
    public static int abs(int value) {
        return Math.abs(value);
    }
    /**
     * Returns f raised to power p
     * @param f
     * @param p
     * @return 
     */
    public static float pow(float f, float p) {
        return (float) Math.pow((double) f, (double) p);
    }
    /**
     * Returns e raised to the specified power.
     * @param power
     * @return 
     */
    public static float exp(float power) {
        return (float) Math.exp((double) power);
    }
    /**
     * Returns the base 10 logarithm of a specified number.
     * @param f
     * @return 
     */
    public static float log10(float f) {
        return (float) Math.log10((double) f);
    }
    /**
     * Returns the smallest integer greater to or equal to f.
     * @param f
     * @return 
     */
    public static float ceil(float f) {
        return (float) Math.ceil((double) f);
    }
    /**
     * Returns the largest integer smaller to or equal to f.
     * @param f
     * @return 
     */
    public static float floor(float f) {
        return (float) Math.floor((double) f);
    }
    /**
     * Returns f rounded to the nearest integer.
     * @param f
     * @return 
     */
    public static float round(float f) {
        return (float) Math.round((double) f);
    }
    /**
     * Returns the smallest integer greater to or equal to f.
     * @param f
     * @return 
     */
    public static int ceilToInt(float f) {
        return (int) Math.ceil((double) f);
    }
    /**
     * Returns the largest integer smaller to or equal to f.
     * @param f
     * @return 
     */
    public static int floor2Int(float f) {
        return (int) Math.floor((double) f);
    }
    /**
     * Returns f rounded to the nearest integer.
     * @param f
     * @return 
     */
    public static int round2Int(float f) {
        return (int) Math.round((double) f);
    }
    /**
     * Returns the sign of f.
     * @param f
     * @return 
     */
    public static float sign(float f) {
        return ((f < 0f) ? -1f : 1f);
    }
    /**
     * Loops the value t, so that it is never larger than length and never smaller than 0.
     * @param t
     * @param length
     * @return 
     */
    public static float repeat(float t, float length) {
        return (t - (floor(t / length) * length));
    }
    
    /**
     * Returns the smallest of two values.
     * @param a
     * @param b
     * @return 
     */
    public static float min(float a, float b) {
        return a < b ? a : b;
    }
    /**
     * Returns the smallest of two or more values.
     * @param values
     * @return 
     */
    public static float min(float... values) {
        int length = values.length;
        if (length == 0)
            return 0f;
        
        float a = values[0];
        for (int i = 1; i < length; i++)
            if (values[i] < a)
                a = values[i];
        
        return a;
    }
    /**
     * Returns the smallest of two values.
     * @param a
     * @param b
     * @return 
     */
    public static int min(int a, int b) {
        return a < b ? a : b;
    }
    /**
     * Returns the smallest of two or more values.
     * @param values
     * @return 
     */
    public static int min(int... values) {
        int length = values.length;
        if (length == 0)
            return 0;
        
        int a = values[0];
        for (int i = 1; i < length; i++)
            if (values[i] < a)
                a = values[i];
        
        return a;
    }
    
    /**
     * Returns largest of two values.
     * @param a
     * @param b
     * @return 
     */
    public static float max(float a, float b) {
        return a > b ? a : b;
    }
    /**
     * Returns largest of two or more values.
     * @param values
     * @return 
     */
    public static float max(float... values) {
        int length = values.length;
        if (length == 0)
            return 0f;
        
        float a = values[0];
        for (int i = 1; i < length; i++)
            if (values[i] > a)
                a = values[i];
        
        return a;
    }
    /**
     * Returns largest of two values.
     * @param a
     * @param b
     * @return 
     */
    public static int max(int a, int b) {
        return a > b ? a : b;
    }
    /**
     * Returns largest of two or more values.
     * @param values
     * @return 
     */
    public static int max(int... values) {
        int length = values.length;
        if (length == 0)
            return 0;
        
        int a = values[0];
        for (int i = 1; i < length; i++)
            if (values[i] > a)
                a = values[i];
        
        return a;
    }
    
    /**
     * Safely clamps a value between a minimum float and maximum float value.
     * @param a
     * @param b
     * @param c
     * @return 
     */
    public static float sclamp(float a, float b, float c) {
        return clamp(a, b < c ? b : c, b < c ? c : b);
    }
    /**
     * Clamps a value between a minimum float and maximum float value.
     * @param a
     * @param min
     * @param max
     * @return 
     */
    public static float clamp(float a, float min, float max) {
        return (a < min ? min : (a > max ? max : a));
    }
    /**
     * Clamps a value between a minimum int and maximum int value.
     * @param a
     * @param min
     * @param max
     * @return 
     */
    public static int clamp(int a, int min, int max) {
        return (a < min ? min : (a > max ? max : a));
    }
    /**
     * Clamps a value between 0 and 1.
     * @param a
     * @return 
     */
    public static float clamp(float a) {
        return clamp(a, 0.0f, 1.0f);
    }
    
    /**
     * Linearly interpolates between from and to by t
     * @param from
     * @param to
     * @param t
     * @return 
     */
    public static float lerp(float from, float to, float t) {
        return (from + ((to - from) * clamp(t)));
    }
    /**
     * Same as Lerp but makes sure the values interpolate correctly when they wrap around 360 degrees.
     * @param from
     * @param to
     * @param t
     * @return 
     */
    public static float lerpAngle(float from, float to, float t) {
        to = repeat(to - from, 360.0f);
        return (from + ((to > 180.0f ? to - 360.0f : to) * clamp(t)));
    }
    
    /**
     * "Distance" between two values
     * @param a
     * @param b
     * @return 
     */
    public static float delta(float a, float b) {
        return abs(a - b);
    }
    /**
     * Calculates the shortest difference between two given angles given in degrees.
     * @param a
     * @param b
     * @return 
     */
    public static float deltaAngle(float a, float b) {
        b = repeat(b - a, 360.0f);
        return b > 180.0f ? b - 360.0f : b;
    }
    
    /**
     * Checks if two values difference is less than 0.001
     * @param a
     * @param b
     * @return 
     */
    public static boolean inRange(float a, float b) {
        return inRange(a, b, 0.001f);
    }
    /**
     * Checks if two values difference is less than t
     * @param a
     * @param b
     * @param t
     * @return 
     */
    public static boolean inRange(float a, float b, float t) {
        return delta(a, b) < t;
    }
    
    /**
     * http://answers.unity3d.com/questions/24756/formula-behind-smoothdamp.html
     * @param current
     * @param target
     * @param velocity
     * @param smoothTime
     * @param maxSpeed
     * @param deltaTime
     * @return 
     */
    public static float smoothdamp(float current, float target, Float velocity, float smoothTime, float maxSpeed, float deltaTime) {
        smoothTime = max(0.0001f, smoothTime);
        float num = 2f / smoothTime;
        float num2 = num * deltaTime;
        float num3 = 1f / (1f + num2 + 0.48f * num2 * num2 + 0.235f * num2 * num2 * num2);
        float num4 = current - target;
        float num5 = target;
        float num6 = maxSpeed * smoothTime;
        num4 = clamp (num4, -num6, num6);
        target = current - num4;
        float num7 = (velocity + num * num4) * deltaTime;
        velocity = (velocity - num * num7) * num3;
        float num8 = target + (num4 + num7) * num3;
        if (num5 - current > 0f == num8 > num5)
        {
            num8 = num5;
            velocity = (num8 - num5) / deltaTime;
        }
        return num8;
    }
    public static Vector2f smoothdamp(Vector2f current, Vector2f target, Vector2f velocity, float smoothTime, float maxSpeed, float deltaTime) {
        Vector2f __new = new Vector2f();
        Float num0 = velocity.x;
        Float num1 = velocity.y;
        
        __new.x = smoothdamp(current.x, target.x, num0, smoothTime, maxSpeed, deltaTime);
        __new.y = smoothdamp(current.y, target.y, num1, smoothTime, maxSpeed, deltaTime);
        
        velocity = new Vector2f(num0, num1);
        
        return __new;
    }
}
