package com.skobbler.debugkit.debugsettings;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Pair;
import android.view.View;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;

import com.skobbler.debugkit.R;
import com.skobbler.ngx.cache.SKTilesCacheManager;
import com.skobbler.ngx.map.SKAnnotation;
import com.skobbler.ngx.map.SKCoordinateRegion;
import com.skobbler.ngx.map.SKMapCustomPOI;
import com.skobbler.ngx.map.SKMapPOI;
import com.skobbler.ngx.map.SKMapSurfaceListener;
import com.skobbler.ngx.map.SKMapViewHolder;
import com.skobbler.ngx.map.SKPOICluster;
import com.skobbler.ngx.map.SKScreenPoint;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by AlexandraP on 07.07.2015.
 */
public class MapCacheDebugSettings extends DebugSettings implements SKMapSurfaceListener{

    /*
    Number of bytes for cache limit
     */
    private long cacheSizeLimit = 17825792;
    /*
    The local tile cache size.
     */
    private long cacheSize = SKTilesCacheManager.getInstance().getCacheSize();
    /*
    Tiles older than seconds will be deleted
     */
    private long seconds = 1;

    @Override
    List<Pair<String, Object>> defineKeyValuePairs() {
        List<Pair<String, Object>> keyValuePairs = new ArrayList<Pair<String, Object>>();
        Context context = specificLayout.getContext();

        keyValuePairs.add(new Pair<String, Object>(context.getResources().getString(R.string.cache_info), null));
        keyValuePairs.add(new Pair<String, Object>(context.getResources().getString(R.string.cache_size) + ": " + cacheSize, null));
        keyValuePairs.add(new Pair<String, Object>(context.getResources().getString(R.string.set_cache_size), cacheSizeLimit));
        keyValuePairs.add(new Pair<String, Object>(context.getResources().getString(R.string.delete_cache), null));
        keyValuePairs.add(new Pair<String, Object>(context.getResources().getString(R.string.delete_all_cache), null));
        keyValuePairs.add(new Pair<String, Object>(context.getResources().getString(R.string.seconds), seconds));
        keyValuePairs.add(new Pair<String, Object>(context.getResources().getString(R.string.delete_older), null));
        return keyValuePairs;
    }

    @Override
    int defineSpecificLayout() {
        return R.layout.map_cache_debug_settings;
    }

    @Override
    void defineSpecificListeners() {

        final EditText cacheSizeLimitValue = (EditText) specificLayout.findViewById(R.id.cache_limit).findViewById(R.id.property_value);
        cacheSizeLimitValue.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                cacheSizeLimit = Long.parseLong(cacheSizeLimitValue.getText().toString());
                SKTilesCacheManager.getInstance().setCacheSizeLimit(cacheSizeLimit);
            }
        });

        SeekBar seekBarSeconds = (SeekBar) specificLayout.findViewById(R.id.map_cache_seconds).findViewById(R.id.property_seekbar);
        seekBarSeconds.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int value, boolean b) {
                ((TextView) specificLayout.findViewById(R.id.map_cache_seconds).findViewById(R.id.property_value)).setText( value + "");
                seconds = value;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        final View deleteAllCache = specificLayout.findViewById(R.id.delete_all_cache);
        deleteAllCache.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SKTilesCacheManager.getInstance().deleteAllCache();
            }
        });
        final View deleteCacheOlder = specificLayout.findViewById(R.id.delete_cache_older);
        deleteCacheOlder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SKTilesCacheManager.getInstance().deleteMapCacheOlderThan(seconds);
            }
        });


    }
    @Override
    void onOpened() {
        super.onOpened();
        activity.getMapHolder().setMapSurfaceListener(this);
    }

    @Override
    void onClose() {
        super.onClose();
        activity.getMapHolder().setMapSurfaceListener(activity);

    }
    @Override
    void applyCustomChangesToUI() {
        super.applyCustomChangesToUI();
        ((SeekBar) specificLayout.findViewById(R.id.map_cache_seconds).findViewById(R.id.property_seekbar)).setMax(200);
    }

    @Override
    public void onActionPan() {

    }

    @Override
    public void onActionZoom() {

    }

    @Override
    public void onSurfaceCreated(SKMapViewHolder skMapViewHolder) {

    }

    @Override
    public void onMapRegionChanged(SKCoordinateRegion skCoordinateRegion) {
        final TextView cacheSizeValue = (TextView) specificLayout.findViewById(R.id.local_tile_cache_size).findViewById(R.id.property_name);
        cacheSizeValue.setText("Cache size : " + SKTilesCacheManager.getInstance().getCacheSize());

    }

    @Override
    public void onMapRegionChangeStarted(SKCoordinateRegion skCoordinateRegion) {

    }

    @Override
    public void onMapRegionChangeEnded(SKCoordinateRegion skCoordinateRegion) {

    }

    @Override
    public void onDoubleTap(SKScreenPoint skScreenPoint) {

    }

    @Override
    public void onSingleTap(SKScreenPoint skScreenPoint) {

    }

    @Override
    public void onRotateMap() {

    }

    @Override
    public void onLongPress(SKScreenPoint skScreenPoint) {

    }

    @Override
    public void onInternetConnectionNeeded() {

    }

    @Override
    public void onMapActionDown(SKScreenPoint skScreenPoint) {

    }

    @Override
    public void onMapActionUp(SKScreenPoint skScreenPoint) {

    }

    @Override
    public void onPOIClusterSelected(SKPOICluster skpoiCluster) {

    }

    @Override
    public void onMapPOISelected(SKMapPOI skMapPOI) {

    }

    @Override
    public void onAnnotationSelected(SKAnnotation skAnnotation) {

    }

    @Override
    public void onCustomPOISelected(SKMapCustomPOI skMapCustomPOI) {

    }

    @Override
    public void onCompassSelected() {

    }

    @Override
    public void onCurrentPositionSelected() {

    }

    @Override
    public void onObjectSelected(int i) {

    }

    @Override
    public void onInternationalisationCalled(int i) {

    }

    @Override
    public void onBoundingBoxImageRendered(int i) {

    }

    @Override
    public void onGLInitializationError(String s) {

    }

    @Override
    public void onScreenshotReady(Bitmap bitmap) {

    }
}
