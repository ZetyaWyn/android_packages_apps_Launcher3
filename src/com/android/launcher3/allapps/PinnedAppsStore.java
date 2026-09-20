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
package com.android.launcher3.allapps;

import static com.android.launcher3.LauncherPrefsExt.PINNED_APPS;

import android.content.ComponentName;
import android.content.Context;

import androidx.annotation.Nullable;

import com.android.launcher3.allapps.AllAppsStore;
import com.android.launcher3.ConstantItem;
import com.android.launcher3.LauncherPrefs;
import com.android.launcher3.model.data.AppInfo;
import com.android.launcher3.model.data.ItemInfo;
import com.android.launcher3.model.data.WorkspaceItemInfo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public final class PinnedAppsStore {

    private PinnedAppsStore() { }

    public static boolean isPinned(Context context, ItemInfo itemInfo) {
        return isPinned(context, itemInfo, PINNED_APPS);
    }

    public static boolean isPinned(Context context, ItemInfo itemInfo,
            ConstantItem<Set<String>> item) {
        String key = encode(context, itemInfo);
        return key != null && LauncherPrefs.get(context).get(item).contains(key);
    }

    public static void setPinned(Context context, ItemInfo itemInfo, boolean pinned) {
        setPinned(context, itemInfo, pinned, PINNED_APPS);
    }

    public static void setPinned(Context context, ItemInfo itemInfo, boolean pinned,
            ConstantItem<Set<String>> item) {
        String key = encode(context, itemInfo);
        if (key == null) {
            return;
        }
        Set<String> pinnedApps = new LinkedHashSet<>(LauncherPrefs.get(context).get(item));
        if (pinned ? pinnedApps.add(key) : pinnedApps.remove(key)) {
            LauncherPrefs.get(context).put(item, pinnedApps);
        }
    }

    public static Set<String> getPinnedKeys(Context context) {
        return getPinnedKeys(context, PINNED_APPS);
    }

    public static Set<String> getPinnedKeys(Context context, ConstantItem<Set<String>> item) {
        return LauncherPrefs.get(context).get(item);
    }

    public static List<WorkspaceItemInfo> getPinnedWorkspaceItems(
            Context context, AllAppsStore allAppsStore) {
        return getPinnedWorkspaceItems(context, allAppsStore, PINNED_APPS);
    }

    public static List<WorkspaceItemInfo> getPinnedWorkspaceItems(
            Context context, AllAppsStore allAppsStore, ConstantItem<Set<String>> item) {
        List<AppInfo> appInfos = new ArrayList<>();
        Collections.addAll(appInfos, allAppsStore.getApps());
        appInfos = getPinnedApps(context, appInfos, item);

        List<WorkspaceItemInfo> items = new ArrayList<>(appInfos.size());
        for (AppInfo appInfo : appInfos) {
            items.add(appInfo.makeWorkspaceItem(context));
        }
        return items;
    }

    public static List<AppInfo> getPinnedApps(Context context, List<AppInfo> apps) {
        return getPinnedApps(context, apps, PINNED_APPS);
    }

    public static List<AppInfo> getPinnedApps(Context context, List<AppInfo> apps,
            ConstantItem<Set<String>> item) {
        Set<String> pinnedApps = LauncherPrefs.get(context).get(item);
        if (pinnedApps.isEmpty()) {
            return Collections.emptyList();
        }

        List<AppInfo> appInfos = new ArrayList<>();
        for (AppInfo appInfo : apps) {
            if (pinnedApps.contains(encode(context, appInfo))) {
                appInfos.add(appInfo);
            }
        }
        appInfos.sort(new AppInfoComparator(context));
        return appInfos;
    }

    @Nullable
    public static String encode(Context context, ItemInfo itemInfo) {
        ComponentName componentName = itemInfo.getTargetComponent();
        if (componentName == null) {
            return null;
        }
        return AllAppsFolderStore.encodeAppKey(context, componentName, itemInfo.user);
    }
}
