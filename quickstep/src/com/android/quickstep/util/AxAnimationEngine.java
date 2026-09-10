/*
 * Copyright 2025-2026 AxionOS
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.android.quickstep.util;

import android.os.SystemProperties;
import android.util.Log;
import android.view.animation.Interpolator;
import android.view.animation.PathInterpolator;

public final class AxAnimationEngine {
    private static final String TRACE_TAG = "AxAnimEngine";
    private static final String TRACE_PROPERTY = "persist.sys.ax.anim_trace";
    public static final long APP_LAUNCH_DURATION = 350L;
    public static final float APP_OPEN_HOME_SCALE = 0.90f;
    public static final long APP_OPEN_HOME_DURATION = 400L;
    public static final long WALLPAPER_APP_OPEN_DURATION = 400L;
    public static final long WALLPAPER_HOME_GESTURE_DURATION = 500L;
    public static final float WALLPAPER_APP_OPEN_ZOOM_OUT = 1f;
    public static final long HOME_GESTURE_WORKSPACE_DURATION = 400L;
    public static final float HOME_GESTURE_WORKSPACE_SCALE = 0.90f;
    public static final long APP_OPEN_WINDOW_ALPHA_DURATION = 180L;
    public static final long APP_OPEN_WINDOW_ALPHA_START_DELAY = 0L;
    public static final long APP_OPEN_STATUS_BAR_TRANSITION_DELAY = 120L;
    public static final float APP_OPEN_ICON_FOREGROUND_SCALE = 1.05f;
    public static final float APP_OPEN_SHADOW_RADIUS = 0f;
    public static final float APP_OPEN_ICON_ALPHA_START_PROGRESS = 0.02f;
    public static final float APP_OPEN_ICON_ALPHA_END_PROGRESS = 0.15f;
    public static final float HOME_GESTURE_ICON_TRACKING_POSITION = 0.5f;
    public static final float HOME_GESTURE_ICON_ALPHA_START_PROGRESS = 0.5f;
    public static final float HOME_GESTURE_ICON_ALPHA_END_PROGRESS = 0.9f;
    public static final float HOME_GESTURE_WIDGET_BACKGROUND_ALPHA_END_PROGRESS = 0.9f;
    public static final float HOME_GESTURE_ICON_MOVE_DAMPING = 0.88f;
    public static final float HOME_GESTURE_ICON_MOVE_STIFFNESS = 180f;
    public static final float HOME_GESTURE_ICON_MOVE_FRICTION = 0.45f;
    public static final float HOME_GESTURE_ICON_SCALE_DAMPING = 0.96f;
    public static final float HOME_GESTURE_ICON_SCALE_STIFFNESS = 280f;
    private static final float APP_OPEN_FULL_CORNER_RADIUS = 590f;
    private static final float APP_OPEN_FULL_CORNER_RADIUS_BASE_SIZE = 1440f;
    private static final float APP_OPEN_DEVICE_CORNER_RADIUS_DP = 40f;
    private static final float HOME_GESTURE_SCENE_CORNER_RADIUS_DP = 15f;

    public static final Interpolator APP_OPEN_WINDOW_POSITION_INTERPOLATOR =
            new PathInterpolator(0.15f, 0.9f, 0.1f, 1f);
    public static final Interpolator APP_OPEN_WINDOW_ALPHA_INTERPOLATOR =
            new PathInterpolator(0f, 1f, 0f, 1f);
    public static final Interpolator APP_OPEN_ICON_ALPHA_INTERPOLATOR =
            new PathInterpolator(0f, 0.8f, 0f, 1f);
    public static final Interpolator APP_OPEN_CORNER_RADIUS_INTERPOLATOR =
            new PathInterpolator(0.94f, 0f, 0.78f, 0.62f);
    public static final Interpolator APP_OPEN_HOME_INTERPOLATOR =
            new PathInterpolator(0.3f, 0f, 0.1f, 1f);
    public static final Interpolator HOME_GESTURE_WINDOW_ALPHA_INTERPOLATOR =
            new PathInterpolator(1f, 0f, 0.82f, 1f);
    public static final Interpolator HOME_GESTURE_ICON_SCALE_INTERPOLATOR =
            new PathInterpolator(0f, 0f, 0.9f, 1f);
    public static final Interpolator HOME_GESTURE_WORKSPACE_INTERPOLATOR =
            new PathInterpolator(0.3f, 0.9f, 0.5f, 1f);
    public static final Interpolator WALLPAPER_APP_OPEN_INTERPOLATOR =
            new PathInterpolator(0.05f, 0.3f, 0f, 1f);
    public static final Interpolator WALLPAPER_HOME_GESTURE_INTERPOLATOR =
            new PathInterpolator(0.05f, 0.3f, 0f, 0.99f);
    private static final int LUT_SIZE = 256;

    public static final float[] HOME_APP_OPEN_LUT =
            buildLut(APP_OPEN_HOME_INTERPOLATOR);
    public static final float[] APP_OPEN_WINDOW_POSITION_LUT =
            buildLut(APP_OPEN_WINDOW_POSITION_INTERPOLATOR);
    public static final float[] APP_OPEN_WINDOW_ALPHA_LUT =
            buildLut(APP_OPEN_WINDOW_ALPHA_INTERPOLATOR);
    public static final float[] APP_OPEN_ICON_ALPHA_LUT =
            buildLut(APP_OPEN_ICON_ALPHA_INTERPOLATOR);
    public static final float[] APP_OPEN_CORNER_RADIUS_LUT =
            buildLut(APP_OPEN_CORNER_RADIUS_INTERPOLATOR);
    public static final float[] HOME_GESTURE_WINDOW_ALPHA_LUT =
            buildLut(HOME_GESTURE_WINDOW_ALPHA_INTERPOLATOR);
    public static final float[] HOME_GESTURE_ICON_SCALE_LUT =
            buildLut(HOME_GESTURE_ICON_SCALE_INTERPOLATOR);
    public static final float[] HOME_GESTURE_WORKSPACE_LUT =
            buildLut(HOME_GESTURE_WORKSPACE_INTERPOLATOR);
    public static final float[] WALLPAPER_APP_OPEN_LUT =
            buildLut(WALLPAPER_APP_OPEN_INTERPOLATOR);
    public static final float[] WALLPAPER_HOME_GESTURE_LUT =
            buildLut(WALLPAPER_HOME_GESTURE_INTERPOLATOR);

    private static float[] buildLut(Interpolator interpolator) {
        float[] lut = new float[LUT_SIZE];
        for (int i = 0; i < LUT_SIZE; i++) {
            lut[i] = interpolator.getInterpolation(i / (float) (LUT_SIZE - 1));
        }
        return lut;
    }

    public static float lut(float[] table, float progress) {
        float p = progress < 0f ? 0f : (progress > 1f ? 1f : progress);
        float scaled = p * (LUT_SIZE - 1);
        int lo = (int) scaled;
        int hi = lo + 1 < LUT_SIZE ? lo + 1 : lo;
        float frac = scaled - lo;
        return table[lo] + (table[hi] - table[lo]) * frac;
    }

    private static final boolean IS_TRACING =
            SystemProperties.getBoolean(TRACE_PROPERTY, false);

    private AxAnimationEngine() {}

    public static void trace(String tag, String message) {
        if (IS_TRACING) {
            Log.d(TRACE_TAG, tag + ": " + message);
        }
    }

    public static boolean isTracing() {
        return IS_TRACING;
    }

    public static float getHomeGestureWindowAlpha(float progress) {
        return Math.max(0f, 1f - lut(HOME_GESTURE_WINDOW_ALPHA_LUT, progress));
    }

    public static float getHomeGestureIconScaleProgress(float progress) {
        return lut(HOME_GESTURE_ICON_SCALE_LUT, progress);
    }

    public static float getHomeGestureIconAlpha(float progress) {
        return getBoundProgress(
                progress,
                HOME_GESTURE_ICON_ALPHA_START_PROGRESS,
                HOME_GESTURE_ICON_ALPHA_END_PROGRESS);
    }

    public static float getHomeGestureIconForegroundScale(float progress) {
        return 1f
                + (1f - getBoundProgress(progress, 0.9f, 1f))
                        * (APP_OPEN_ICON_FOREGROUND_SCALE - 1f);
    }

    public static float getHomeGestureWidgetBackgroundAlpha(float progress) {
        return 1f
                - getBoundProgress(progress, 0f, HOME_GESTURE_WIDGET_BACKGROUND_ALPHA_END_PROGRESS);
    }

    public static float getAppOpenFullCornerRadius(int minScreenSize) {
        return minScreenSize == 0
                ? APP_OPEN_FULL_CORNER_RADIUS
                : minScreenSize
                        * APP_OPEN_FULL_CORNER_RADIUS
                        / APP_OPEN_FULL_CORNER_RADIUS_BASE_SIZE;
    }

    public static float getAppOpenStartRadius(float density) {
        return Math.max(0f, density) * APP_OPEN_DEVICE_CORNER_RADIUS_DP;
    }

    public static float getHomeRadius(float density, float fullscreenProgress) {
        float safeDensity = Math.max(0f, density);
        float sceneRadius = safeDensity * HOME_GESTURE_SCENE_CORNER_RADIUS_DP;
        float deviceRadius = safeDensity * APP_OPEN_DEVICE_CORNER_RADIUS_DP;
        return sceneRadius
                + (deviceRadius - sceneRadius) * boundToUnit(fullscreenProgress);
    }

    public static float getAppOpenCornerRadius(
            float startRadius, float endRadius, float radiusProgress) {
        return startRadius + (endRadius - startRadius) * radiusProgress;
    }

    private static float boundToUnit(float value) {
        return Math.max(0f, Math.min(1f, value));
    }

    private static float getBoundProgress(float progress, float start, float end) {
        float range = end - start;
        if (Math.abs(range) < 0.0001f) {
            return progress >= end ? 1f : 0f;
        }
        return boundToUnit((progress - start) / range);
    }
}
