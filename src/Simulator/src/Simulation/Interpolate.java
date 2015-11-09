/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Simulation;

import com.jme3.math.Vector3f;

/**
 *
 * @author sietse
 */
public final class Interpolate {
    
    public static float ease(EaseType type, float start, float target, float elapsedTime, float duration) {
        switch (type) {
            case Instant:           return target;
            case Linear:            return linear(start, target, elapsedTime, duration);
            case EaseInQuad:        return easeInQuad(start, target, elapsedTime, duration);
            case EaseOutQuad:       return easeOutQuad(start, target, elapsedTime, duration);
            case EaseInOutQuad:     return easeInOutQuad(start, target, elapsedTime, duration);
            case EaseInCubic:       return easeInCubic(start, target, elapsedTime, duration);
            case EaseOutCubic:      return easeOutCubic(start, target, elapsedTime, duration);
            case EaseInOutCubic:    return easeInOutCubic(start, target, elapsedTime, duration);
            case EaseInQuart:       return easeInQuart(start, target, elapsedTime, duration);
            case EaseOutQuart:      return easeOutQuart(start, target, elapsedTime, duration);
            case EaseInOutQuart:    return easeInOutQuart(start, target, elapsedTime, duration);
            case EaseInQuint:       return easeInQuint(start, target, elapsedTime, duration);
            case EaseOutQuint:      return easeOutQuint(start, target, elapsedTime, duration);
            case EaseInOutQuint:    return easeInOutQuint(start, target, elapsedTime, duration);
            case EaseInSine:        return easeInSine(start, target, elapsedTime, duration);
            case EaseOutSine:       return easeOutSine(start, target, elapsedTime, duration);
            case EaseInOutSine:     return easeInOutSine(start, target, elapsedTime, duration);
            case EaseInExpo:        return easeInExpo(start, target, elapsedTime, duration);
            case EaseOutExpo:       return easeOutExpo(start, target, elapsedTime, duration);
            case EaseInOutExpo:     return easeInOutExpo(start, target, elapsedTime, duration);
            case EaseInCirc:        return easeInCirc(start, target, elapsedTime, duration);
            case EaseOutCirc:       return easeOutCirc(start, target, elapsedTime, duration);
            case EaseInOutCirc:     return easeInOutCirc(start, target, elapsedTime, duration);
        }
        return 0.0f;
    }
    
    public static Vector3f ease(EaseType type, Vector3f start, Vector3f target, float elapsedTime, float duration) {
        return new Vector3f(
            Interpolate.ease(type, start.x, target.x, elapsedTime, duration),
            Interpolate.ease(type, start.y, target.y, elapsedTime, duration),
            Interpolate.ease(type, start.z, target.z, elapsedTime, duration));
    }
    
    public static float clampTime(float elapsedTime, float duration, float maxValue) {
        return (elapsedTime > duration) ? maxValue : elapsedTime / (duration / maxValue);
    }
    
    public static float linear(float start, float target, float elapsedTime, float duration) {
        // clamp elapsedTime to be <= duration
        if (elapsedTime > duration) { elapsedTime = duration; }
        return (target - start) * (elapsedTime / duration) + start;
    }

    public static float easeInQuad(float start, float target, float elapsedTime, float duration) {
        // clamp elapsedTime so that it cannot be greater than duration
        elapsedTime = clampTime(elapsedTime, duration, 1.0f);
        return (target - start) * elapsedTime * elapsedTime + start;
    }

    public static float easeOutQuad(float start, float target, float elapsedTime, float duration) {
        // clamp elapsedTime so that it cannot be greater than duration
        elapsedTime = clampTime(elapsedTime, duration, 1.0f);
        return -(target - start) * elapsedTime * (elapsedTime - 2) + start;
    }

    public static float easeInOutQuad(float start, float target, float elapsedTime, float duration) {
        // clamp elapsedTime so that it cannot be greater than duration
        elapsedTime = clampTime(elapsedTime, duration, 2.0f);
        if (elapsedTime < 1) return (target - start) / 2 * elapsedTime * elapsedTime + start;
        elapsedTime--;
        return -(target - start) / 2 * (elapsedTime * (elapsedTime - 2) - 1) + start;
    }

    public static float easeInCubic(float start, float target, float elapsedTime, float duration) {
        // clamp elapsedTime so that it cannot be greater than duration
        elapsedTime = clampTime(elapsedTime, duration, 1.0f);
        return (target - start) * elapsedTime * elapsedTime * elapsedTime + start;
    }

    public static float easeOutCubic(float start, float target, float elapsedTime, float duration) {
        // clamp elapsedTime so that it cannot be greater than duration
        elapsedTime = clampTime(elapsedTime, duration, 1.0f);
        elapsedTime--;
        return (target - start) * (elapsedTime * elapsedTime * elapsedTime + 1) + start;
    }

    public static float easeInOutCubic(float start, float target, float elapsedTime, float duration) {
        // clamp elapsedTime so that it cannot be greater than duration
        elapsedTime = clampTime(elapsedTime, duration, 2.0f);
        if (elapsedTime < 1) return (target - start) / 2 * elapsedTime * elapsedTime * elapsedTime + start;
        elapsedTime -= 2.0f;
        return (target - start) / 2 * (elapsedTime * elapsedTime * elapsedTime + 2.0f) + start;
    }

    public static float easeInQuart(float start, float target, float elapsedTime, float duration) {
        // clamp elapsedTime so that it cannot be greater than duration
        elapsedTime = clampTime(elapsedTime, duration, 1.0f);
        return (target - start) * elapsedTime * elapsedTime * elapsedTime * elapsedTime + start;
    }

    public static float easeOutQuart(float start, float target, float elapsedTime, float duration) {
        // clamp elapsedTime so that it cannot be greater than duration
        elapsedTime = clampTime(elapsedTime, duration, 1.0f);
        elapsedTime--;
        return -(target - start) * (elapsedTime * elapsedTime * elapsedTime * elapsedTime - 1.0f) + start;
    }

    public static float easeInOutQuart(float start, float target, float elapsedTime, float duration) {
        // clamp elapsedTime so that it cannot be greater than duration
        elapsedTime = clampTime(elapsedTime, duration, 2.0f);
        if (elapsedTime < 1.0f) return (target - start) / 2 * elapsedTime * elapsedTime * elapsedTime * elapsedTime + start;
        elapsedTime -= 2.0f;
        return -(target - start) / 2.0f * (elapsedTime * elapsedTime * elapsedTime * elapsedTime - 2.0f) + start;
    }

    public static float easeInQuint(float start, float target, float elapsedTime, float duration) {
        // clamp elapsedTime so that it cannot be greater than duration
        elapsedTime = clampTime(elapsedTime, duration, 1.0f);
        return (target - start) * elapsedTime * elapsedTime * elapsedTime * elapsedTime * elapsedTime + start;
    }

    public static float easeOutQuint(float start, float target, float elapsedTime, float duration) {
        // clamp elapsedTime so that it cannot be greater than duration
        elapsedTime = clampTime(elapsedTime, duration, 1.0f);
        elapsedTime--;
        return (target - start) * (elapsedTime * elapsedTime * elapsedTime * elapsedTime * elapsedTime + 1.0f) + start;
    }

    public static float easeInOutQuint(float start, float target, float elapsedTime, float duration) {
        // clamp elapsedTime so that it cannot be greater than duration
        elapsedTime = (elapsedTime > duration) ? 2.0f : elapsedTime / (duration / 2f);
        if (elapsedTime < 1) return (target - start) / 2 * elapsedTime * elapsedTime * elapsedTime * elapsedTime * elapsedTime + start;
        elapsedTime -= 2;
        return (target - start) / 2 * (elapsedTime * elapsedTime * elapsedTime * elapsedTime * elapsedTime + 2) + start;
    }

    public static float easeInSine(float start, float target, float elapsedTime, float duration) {
        // clamp elapsedTime to be <= duration
        if (elapsedTime > duration) { elapsedTime = duration; }
        return -(target - start) * Mathf.cos(elapsedTime / duration * (Mathf.PI / 2)) + (target - start) + start;
    }

    public static float easeOutSine(float start, float target, float elapsedTime, float duration) {
        if (elapsedTime > duration) { elapsedTime = duration; }
        return (target - start) * Mathf.sin(elapsedTime / duration * (Mathf.PI / 2)) + start;
    }

    public static float easeInOutSine(float start, float target, float elapsedTime, float duration) {
        // clamp elapsedTime to be <= duration
        if (elapsedTime > duration) { elapsedTime = duration; }
        return -(target - start) / 2 * (Mathf.cos(Mathf.PI * elapsedTime / duration) - 1) + start;
    }

    public static float easeInExpo(float start, float target, float elapsedTime, float duration) {
        // clamp elapsedTime to be <= duration
        if (elapsedTime > duration) { elapsedTime = duration; }
        return (target - start) * Mathf.pow(2, 10 * (elapsedTime / duration - 1)) + start;
    }

    public static float easeOutExpo(float start, float target, float elapsedTime, float duration) {
        // clamp elapsedTime to be <= duration
        if (elapsedTime > duration) { elapsedTime = duration; }
        return (target - start) * (-Mathf.pow(2, -10 * elapsedTime / duration) + 1) + start;
    }

    public static float easeInOutExpo(float start, float target, float elapsedTime, float duration) {
        // clamp elapsedTime so that it cannot be greater than duration
        elapsedTime = clampTime(elapsedTime, duration, 2.0f);
        if (elapsedTime < 1.0f) return (target - start) / 2 * Mathf.pow(2, 10 * (elapsedTime - 1)) + start;
        elapsedTime--;
        return (target - start) / 2.0f * (-Mathf.pow(2, -10 * elapsedTime) + 2) + start;
    }

    public static float easeInCirc(float start, float target, float elapsedTime, float duration) {
        // clamp elapsedTime so that it cannot be greater than duration
        elapsedTime = clampTime(elapsedTime, duration, 1.0f);
        return -(target - start) * (Mathf.sqrt(1.0f - elapsedTime * elapsedTime) - 1.0f) + start;
    }

    public static float easeOutCirc(float start, float target, float elapsedTime, float duration) {
        // clamp elapsedTime so that it cannot be greater than duration
        elapsedTime = clampTime(elapsedTime, duration, 1.0f);
        elapsedTime--;
        return (target - start) * Mathf.sqrt(1.0f - elapsedTime * elapsedTime) + start;
    }

    public static float easeInOutCirc(float start, float target, float elapsedTime, float duration) {
        // clamp elapsedTime so that it cannot be greater than duration
        elapsedTime = (elapsedTime > duration) ? 2.0f : elapsedTime / (duration / 2.0f);
        if (elapsedTime < 1.0f) return -(target - start) / 2.0f * (Mathf.sqrt(1.0f - elapsedTime * elapsedTime) - 1.0f) + start;
        elapsedTime -= 2.0f;
        return (target - start) / 2.0f * (Mathf.sqrt(1.0f - elapsedTime * elapsedTime) + 1.0f) + start;
    }
}
