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
package com.android.launcher3;

import static com.android.launcher3.LauncherAnimUtils.SCALE_PROPERTY;
import static com.android.launcher3.LauncherState.ALL_APPS;
import static com.android.launcher3.LauncherState.OVERVIEW;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.util.Pair;
import android.view.View;

import com.android.app.animation.Animations;
import com.android.app.animation.Interpolators;
import com.android.launcher3.uioverrides.QuickstepLauncher;
import com.android.quickstep.util.AxAnimationEngine;

import java.util.ArrayList;
import java.util.List;

final class AxContentsAnimator {
    private Token mToken;
    private boolean mUpdatesPaused;

    static boolean shouldHandleAppOpen(QuickstepLauncher launcher, boolean isAppOpening) {
        return isAppOpening && !launcher.isInState(ALL_APPS) && !launcher.isInState(OVERVIEW);
    }

    Pair<AnimatorSet, Runnable> getAppOpenAnimator(
            QuickstepLauncher launcher, DeviceProfile deviceProfile, int startDelay) {
        List<View> views = getHomeContentViews(launcher, deviceProfile);
        List<Float> startScales = new ArrayList<>(views.size());
        for (View view : views) {
            startScales.add(view.getScaleX());
        }
        views.forEach(Animations.Companion::cancelOngoingAnimation);

        Token token = new Token();
        mToken = token;
        AnimatorSet animator = new AnimatorSet();
        if (!mUpdatesPaused) {
            mUpdatesPaused = true;
            launcher.pauseExpensiveViewUpdates();
        }

        final float[] lut = AxAnimationEngine.HOME_APP_OPEN_LUT;
        final float targetScale = AxAnimationEngine.APP_OPEN_HOME_SCALE;
        final int viewCount = views.size();

        ValueAnimator progress = ValueAnimator.ofFloat(0f, 1f);
        progress.setDuration(AxAnimationEngine.APP_OPEN_HOME_DURATION);
        progress.setInterpolator(Interpolators.LINEAR);
        progress.addUpdateListener(animation -> {
            float p = (float) animation.getAnimatedValue();
            float eased = AxAnimationEngine.lut(lut, p);
            for (int i = 0; i < viewCount; i++) {
                View v = views.get(i);
                float s = startScales.get(i);
                SCALE_PROPERTY.set(v, s + (targetScale - s) * eased);
            }
        });

        views.forEach(view -> view.setLayerType(View.LAYER_TYPE_HARDWARE, null));

        animator.play(progress);
        animator.setStartDelay(startDelay);

        Runnable endListener =
                () -> {
                    boolean isCurrent = (mToken == token);
                    if (isCurrent) {
                        mToken = null;
                    }

                    for (int i = 0; i < viewCount; i++) {
                        views.get(i).setLayerType(View.LAYER_TYPE_NONE, null);
                    }

                    if (isCurrent) {
                        for (int i = 0; i < viewCount; i++) {
                            SCALE_PROPERTY.set(views.get(i), 1f);
                            Animations.Companion.setOngoingAnimation(views.get(i), null);
                        }
                        if (mUpdatesPaused) {
                            mUpdatesPaused = false;
                            launcher.resumeExpensiveViewUpdates();
                        }
                    }
                };
        animator.addListener(
                new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        endListener.run();
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {
                        endListener.run();
                    }
                });
        return Pair.create(animator, endListener);
    }

    private static List<View> getHomeContentViews(
            QuickstepLauncher launcher, DeviceProfile deviceProfile) {
        List<View> views = new ArrayList<>();
        views.add(launcher.getWorkspace());

        Hotseat hotseat = launcher.getHotseat();
        if (deviceProfile.isTaskbarPresent) {
            if (!deviceProfile.isQsbInline) {
                views.add(hotseat.getQsb());
            }
        } else {
            views.add(hotseat);
        }
        return views;
    }

    private record Token() {}
}
