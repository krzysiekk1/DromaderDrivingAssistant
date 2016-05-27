package com.skobbler.debugkit.debugsettings;

import android.util.Pair;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.skobbler.debugkit.R;
import com.skobbler.ngx.map.SKCircle;
import com.skobbler.ngx.map.SKPolygon;
import com.skobbler.ngx.map.SKPolyline;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Tudor on 6/26/2015.
 */
public class OverlaysDebugSettings extends DebugSettings {

    private Map<Integer, Object> overlayMap = new HashMap<Integer, Object>();

    @Override
    List<Pair<String, Object>> defineKeyValuePairs() {
        List<Pair<String, Object>> pairs = new ArrayList<Pair<String, Object>>();
        pairs.add(new Pair<String, Object>(activity.getResources().getString(R.string.circle), null));
        pairs.add(new Pair<String, Object>(activity.getResources().getString(R.string.polygon), null));
        pairs.add(new Pair<String, Object>(activity.getResources().getString(R.string.polyline), null));
        pairs.add(new Pair<String, Object>(activity.getResources().getString(R.string.clear_all_overlays), null));
        pairs.add(new Pair<String, Object>(activity.getResources().getString(R.string.overlay_id), 0));
        pairs.add(new Pair<String, Object>(activity.getResources().getString(R.string.search_overlay), null));
        pairs.add(new Pair<String, Object>(activity.getResources().getString(R.string.remove_overlay), null));
        return pairs;
    }

    @Override
    int defineSpecificLayout() {
        return R.layout.settings_overlays;
    }

    @Override
    void defineSpecificListeners() {

        specificLayout.findViewById(R.id.circle).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DebugSettings.getInstanceForType(CircleDebugSettings.class).open(debugBaseLayout, OverlaysDebugSettings.this);
            }
        });

        specificLayout.findViewById(R.id.polygon_id).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DebugSettings.getInstanceForType(PolygonDebugSettings.class).open(debugBaseLayout, OverlaysDebugSettings.this);
            }
        });

        specificLayout.findViewById(R.id.polyline).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DebugSettings.getInstanceForType(PolylineDebugSettings.class).open(debugBaseLayout, OverlaysDebugSettings.this);
            }
        });

        specificLayout.findViewById(R.id.remove_overlay).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    int id = Integer.parseInt(((EditText) specificLayout.findViewById(R.id.overlay_id).findViewById(R.id.property_value)).getText().toString());
                    if (activity.getMapView().clearOverlay(id)) {
                        overlayMap.remove(id);
                        Toast.makeText(activity, getOverlayTypeForId(id) + " #" + id + " was removed", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(activity, "An overlay for id #" + id + " could not be found", Toast.LENGTH_SHORT).show();
                    }
                } catch (NumberFormatException e) {
                    Toast.makeText(activity, "Please enter a valid overlay id", Toast.LENGTH_SHORT).show();
                }
            }
        });

        specificLayout.findViewById(R.id.search_for_overlay).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    int id = Integer.parseInt(((EditText) specificLayout.findViewById(R.id.overlay_id).findViewById(R.id.property_value)).getText().toString());
                    if (overlayMap.get(id) != null) {
                        gotoOverlayWithId(id);
                    } else {
                        Toast.makeText(activity, "An overlay for id #" + id + " could not be found", Toast.LENGTH_SHORT).show();
                    }
                } catch (NumberFormatException e) {
                    Toast.makeText(activity, "Please enter a valid overlay id", Toast.LENGTH_SHORT).show();
                }
            }
        });

        specificLayout.findViewById(R.id.clear_all_overlays).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activity.getMapView().clearAllOverlays();
            }
        });
    }

    public Map<Integer, Object> getOverlayMap() {
        return overlayMap;
    }

    public String getOverlayTypeForId(int id) {
        Object overlay = overlayMap.get(id);
        if (overlay instanceof SKCircle) {
            return "circle";
        } else if (overlay instanceof SKPolygon) {
            return "polygon";
        } else if (overlay instanceof SKPolyline) {
            return "polyline";
        }
        return null;
    }

    private void gotoOverlayWithId(int id) {
        Object overlay = overlayMap.get(id);
        if (overlay != null) {
            if (overlay instanceof SKCircle) {
                activity.getMapView().centerMapOnPosition(((SKCircle) overlay).getCircleCenter());
            } else if (overlay instanceof SKPolygon) {
                activity.getMapView().centerMapOnPosition(((SKPolygon) overlay).getNodes().get(0));
            } else if (overlay instanceof SKPolyline) {
                activity.getMapView().centerMapOnPosition(((SKPolyline) overlay).getNodes().get(0));
            }
            Toast.makeText(activity, "Showing " + getOverlayTypeForId(id) + " #" + id, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(activity, "Overlay with id " + id + " could not be found", Toast.LENGTH_SHORT).show();
        }
    }
}