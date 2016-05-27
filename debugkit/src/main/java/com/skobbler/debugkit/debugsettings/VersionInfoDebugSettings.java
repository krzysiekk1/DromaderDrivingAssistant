package com.skobbler.debugkit.debugsettings;

import android.util.Pair;

import com.skobbler.debugkit.R;
import com.skobbler.ngx.versioning.SKVersionInformation;
import com.skobbler.ngx.versioning.SKVersioningManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Tudor on 7/2/2015.
 */
public class VersionInfoDebugSettings extends DebugSettings {
    @Override
    List<Pair<String, Object>> defineKeyValuePairs() {
        SKVersioningManager versionManager = SKVersioningManager.getInstance();
        int mapVersion = versionManager.getLocalMapVersion();
        SKVersionInformation versionInfo = versionManager.getSKVersionInformation(mapVersion);
        List<Pair<String, Object>> pairs = new ArrayList<Pair<String, Object>>();
        pairs.add(new Pair<String, Object>(activity.getResources().getString(R.string.map_version) + " " + mapVersion, null));
        pairs.add(new Pair<String, Object>(activity.getResources().getString(R.string.routing_version) + " " + versionInfo.getRouterVersion(), null));
        pairs.add(new Pair<String, Object>(activity.getResources().getString(R.string.namebrowser_version) + " " + versionInfo.getNameBrowserVersion(), null));

        return pairs;
    }

    @Override
    int defineSpecificLayout() {
        return R.layout.settings_version_info;
    }

    @Override
    void defineSpecificListeners() {
    }
}
