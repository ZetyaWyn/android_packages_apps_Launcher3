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

import static com.android.launcher3.LauncherPrefsExt.ALL_APPS_SMART_DRAWER_PINNED_APPS;

import android.content.Context;

import com.android.launcher3.allapps.AllAppsStore;
import com.android.launcher3.allapps.PinnedAppsStore;
import com.android.launcher3.model.data.AppInfo;
import com.android.launcher3.model.data.ItemInfo;
import com.android.launcher3.model.data.WorkspaceItemInfo;

import java.util.List;
import java.util.Set;

public final class AxSmartDrawerPinnedStore {

    private AxSmartDrawerPinnedStore() { }

    public static boolean isPinned(Context context, ItemInfo itemInfo) {
        return PinnedAppsStore.isPinned(context, itemInfo, ALL_APPS_SMART_DRAWER_PINNED_APPS);
    }

    public static void setPinned(Context context, ItemInfo itemInfo, boolean pinned) {
        PinnedAppsStore.setPinned(context, itemInfo, pinned, ALL_APPS_SMART_DRAWER_PINNED_APPS);
    }

    public static Set<String> getPinnedKeys(Context context) {
        return PinnedAppsStore.getPinnedKeys(context, ALL_APPS_SMART_DRAWER_PINNED_APPS);
    }

    public static List<WorkspaceItemInfo> getPinnedWorkspaceItems(
            Context context, AllAppsStore allAppsStore) {
        return PinnedAppsStore.getPinnedWorkspaceItems(context, allAppsStore, ALL_APPS_SMART_DRAWER_PINNED_APPS);
    }

    public static List<AppInfo> getPinnedApps(Context context, List<AppInfo> apps) {
        return PinnedAppsStore.getPinnedApps(context, apps, ALL_APPS_SMART_DRAWER_PINNED_APPS);
    }
}
