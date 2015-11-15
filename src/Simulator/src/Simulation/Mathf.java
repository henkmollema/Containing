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
    public static final float Deg2Rad = 0.01745329f;
    public static final float Rad2Deg = 57.29578f;
    
    public static float sin(float f) {
        return (float) Math.sin((double) f);
    }
    public static float cos(float f) {
        return (float) Math.cos((double) f);
    }
    public static float tan(float f) {
        return (float) Math.tan((double) f);
    }
    public static float asin(float f) {
        return (float) Math.asin((double) f);
    }
    public static float acos(float f) {
        return (float) Math.acos((double) f);
    }
    public static float atan(float f) {
        return (float) Math.atan((double) f);
    }
    public static float atan2(float y, float x) {
        return (float) Math.atan2((double) y, (double) x);
    }
    public static float sqrt(float f) {
        return (float) Math.sqrt((double) f);
    }
    public static float abs(float f) {
        return Math.abs(f);
    }
    public static int abs(int value) {
        return Math.abs(value);
    }
    public static float pow(float f, float p) {
        return (float) Math.pow((double) f, (double) p);
    }
    public static float exp(float power) {
        return (float) Math.exp((double) power);
    }
    public static float log10(float f) {
        return (float) Math.log10((double) f);
    }
    public static float ceil(float f) {
        return (float) Math.ceil((double) f);
    }
    public static float floor(float f) {
        return (float) Math.floor((double) f);
    }
    public static float round(float f) {
        return (float) Math.round((double) f);
    }
    public static int ceilToInt(float f) {
        return (int) Math.ceil((double) f);
    }
    public static int floor2Int(float f) {
        return (int) Math.floor((double) f);
    }
    public static int round2Int(float f) {
        return (int) Math.round((double) f);
    }
    public static float sign(float f) {
        return ((f < 0f) ? -1f : 1f);
    }
    public static float repeat(float t, float length) {
        return (t - (floor(t / length) * length));
    }
    
    public static float min(float a, float b) {
        return a < b ? a : b;
    }
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
    public static int min(int a, int b) {
        return a < b ? a : b;
    }
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
    
    public static float max(float a, float b) {
        return a > b ? a : b;
    }
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
    public static int max(int a, int b) {
        return a > b ? a : b;
    }
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
    
    public static float sclamp(float a, float b, float c) {
        return clamp(a, b < c ? b : c, b < c ? c : b);
    }
    public static float clamp(float a, float min, float max) {
        return (a < min ? min : (a > max ? max : a));
    }
    public static int clamp(int a, int min, int max) {
        return (a < min ? min : (a > max ? max : a));
    }
    public static float clamp(float a) {
        return clamp(a, 0.0f, 1.0f);
    }
    
    public static float lerp(float from, float to, float t) {
        return (from + ((to - from) * clamp(t)));
    }
    public static float lerpAngle(float from, float to, float t) {
        to = repeat(to - from, 360.0f);
        return (from + ((to > 180.0f ? to - 360.0f : to) * clamp(t)));
    }
    
    public static float delta(float a, float b) {
        return abs(a - b);
    }
    public static float deltaAngle(float a, float b) {
        b = repeat(b - a, 360.0f);
        return b > 180.0f ? b - 360.0f : b;
    }
    
    public static boolean inRange(float a, float b) {
        return inRange(a, b, 0.001f);
    }
    public static boolean inRange(float a, float b, float t) {
        return delta(a, b) < t;
    }
}
