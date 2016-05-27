package com.skobbler.debugkit.debugsettings;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.util.Pair;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.skobbler.debugkit.R;
import com.skobbler.debugkit.activity.DebugMapActivity;
import com.skobbler.ngx.SKCoordinate;
import com.skobbler.ngx.map.SKAnimationSettings;
import com.skobbler.ngx.map.SKAnnotation;
import com.skobbler.ngx.map.SKCoordinateRegion;
import com.skobbler.ngx.map.SKMapCustomPOI;
import com.skobbler.ngx.map.SKMapPOI;
import com.skobbler.ngx.map.SKMapSettings;
import com.skobbler.ngx.map.SKMapSurfaceListener;
import com.skobbler.ngx.map.SKMapViewHolder;
import com.skobbler.ngx.map.SKPOICluster;
import com.skobbler.ngx.map.SKScreenPoint;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Tudor on 6/3/2015.
 */
public class MapDebugSettings extends DebugSettings implements SKMapSurfaceListener {

    private SKMapSettings.SKMapFollowerMode currentFollowerMode = SKMapSettings.SKMapFollowerMode.NONE;

    private SKAnnotation testAnnotation;

    @Override
    List<Pair<String, Object>> defineKeyValuePairs() {
        List<Pair<String, Object>> pairs = new ArrayList<Pair<String, Object>>();
        Context context = specificLayout.getContext();
        pairs.add(new Pair<String, Object>(context.getResources().getString(R.string.follower_mode), currentFollowerMode.toString()));
        pairs.add(new Pair<String, Object>(context.getResources().getString(R.string.show_current_position), true));
        pairs.add(new Pair<String, Object>(context.getResources().getString(R.string.show_heading_indicator), true));
        pairs.add(new Pair<String, Object>(context.getResources().getString(R.string.show_accuracy_circle), true));
        pairs.add(new Pair<String, Object>(context.getResources().getString(R.string.center_on_current_pos), null));
        pairs.add(new Pair<String, Object>(context.getResources().getString(R.string.rotation), true));
        pairs.add(new Pair<String, Object>(context.getResources().getString(R.string.panning), true));
        pairs.add(new Pair<String, Object>(context.getResources().getString(R.string.zooming), true));
        pairs.add(new Pair<String, Object>(context.getResources().getString(R.string.annotation_zoom_limit), activity.getMapView().getMapSettings().getMinimumZoomForTapping() * 10));
        pairs.add(new Pair<String, Object>(context.getResources().getString(R.string.compass_options), null));
        pairs.add(new Pair<String, Object>(context.getResources().getString(R.string.trail_settings), null));
        pairs.add(new Pair<String, Object>(context.getResources().getString(R.string.enable_inertia), true));
        pairs.add(new Pair<String, Object>(context.getResources().getString(R.string.zoom_levels), null));
        pairs.add(new Pair<String, Object>(context.getResources().getString(R.string.map_state), null));
        pairs.add(new Pair<String, Object>(context.getResources().getString(R.string.enable_3d), false));
        pairs.add(new Pair<String, Object>(context.getResources().getString(R.string.camera_settings), null));
        pairs.add(new Pair<String, Object>(context.getResources().getString(R.string.poi_options), null));
        pairs.add(new Pair<String, Object>(context.getResources().getString(R.string.one_ways), true));
        pairs.add(new Pair<String, Object>(context.getResources().getString(R.string.street_badges), true));
        pairs.add(new Pair<String, Object>(context.getResources().getString(R.string.drawing_order), activity.getResources().getString(R.string.annotations_over_objects)));
        pairs.add(new Pair<String, Object>(context.getResources().getString(R.string.show_bicycle_lanes), false));
        pairs.add(new Pair<String, Object>(context.getResources().getString(R.string.zoom_with_anchor), false));
        pairs.add(new Pair<String, Object>(context.getResources().getString(R.string.show_house_numbers), true));
        pairs.add(new Pair<String, Object>(context.getResources().getString(R.string.new_instance), null));
        return pairs;
    }

    @Override
    int defineSpecificLayout() {
        return R.layout.settings_map;
    }

    public void addTestAnnotationAtPosition(SKCoordinate position) {
        if (testAnnotation == null) {
            testAnnotation = new SKAnnotation(155000);
            testAnnotation.setLocation(position);
            testAnnotation.setMininumZoomLevel(3);
            testAnnotation.setAnnotationType(SKAnnotation.SK_ANNOTATION_TYPE_GREEN);
            activity.getMapView().addAnnotation(testAnnotation, SKAnimationSettings.ANIMATION_NONE);
        } else {
            testAnnotation.setLocation(position);
            activity.getMapView().updateAnnotation(testAnnotation);
        }
    }

    private void updateTestAnnotation() {
        if (testAnnotation != null) {
            if (testAnnotation.getAnnotationType() == SKAnnotation.SK_ANNOTATION_TYPE_RED) {
                testAnnotation.setAnnotationType(SKAnnotation.SK_ANNOTATION_TYPE_GREEN);
            } else {
                testAnnotation.setAnnotationType(SKAnnotation.SK_ANNOTATION_TYPE_RED);
            }
            activity.getMapView().updateAnnotation(testAnnotation);
        }
    }


    @Override
    void defineSpecificListeners() {
        specificLayout.findViewById(R.id.heading_mode).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DebugSettings.getInstanceForType(FollowerModeDebugSettings.class).open(debugBaseLayout, MapDebugSettings.this);
            }
        });

        final TextView zoomLimitValue = (TextView) specificLayout.findViewById(R.id.annotation_zoom_limit).findViewById(R.id.property_value);
        zoomLimitValue.setText((Float.parseFloat(zoomLimitValue.getText().toString()) / 10) + "");
        SeekBar zoomLimitSeekBar = (SeekBar) specificLayout.findViewById(R.id.annotation_zoom_limit).findViewById(R.id.property_seekbar);
        zoomLimitSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                zoomLimitValue.setText((float) i / 10 + "");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                activity.getMapView().getMapSettings().setMinimumZoomForTapping((float) seekBar.getProgress() / 10);
                final Display display =
                        ((WindowManager) activity.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
                SKCoordinate annotationPosition = activity.getMapView().pointToCoordinate(new SKScreenPoint(0.85f * display.getWidth(), 0.5f * display.getHeight()));
                addTestAnnotationAtPosition(annotationPosition);
            }
        });

        final View showCcpView = specificLayout.findViewById(R.id.show_current_position);
        showCcpView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CheckBox checkBox = (CheckBox) showCcpView.findViewById(R.id.property_value);
                checkBox.setChecked(!checkBox.isChecked());
                activity.getMapView().getMapSettings().setCurrentPositionShown(checkBox.isChecked());
            }
        });

        final View showHeadingIndicatorView = specificLayout.findViewById(R.id.show_heading_indicator);
        showHeadingIndicatorView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CheckBox checkBox = (CheckBox) showHeadingIndicatorView.findViewById(R.id.property_value);
                checkBox.setChecked(!checkBox.isChecked());
                activity.getMapView().showHeadingIndicator(checkBox.isChecked());
            }
        });

        final View showCircle = specificLayout.findViewById(R.id.show_accuracy_circle);
        showCircle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CheckBox checkBox = (CheckBox) showCircle.findViewById(R.id.property_value);
                checkBox.setChecked(!checkBox.isChecked());
                activity.getMapView().showAccuracyCircle(checkBox.isChecked());
            }
        });

        specificLayout.findViewById(R.id.center_on_current_position).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activity.getMapView().centerMapOnCurrentPosition();
            }
        });

        final View panning = specificLayout.findViewById(R.id.panning);
        panning.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CheckBox checkBox = (CheckBox) panning.findViewById(R.id.property_value);
                checkBox.setChecked(!checkBox.isChecked());
                activity.getMapView().getMapSettings().setMapPanningEnabled(checkBox.isChecked());
            }
        });

        final View rotation = specificLayout.findViewById(R.id.rotation);
        rotation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CheckBox checkBox = (CheckBox) rotation.findViewById(R.id.property_value);
                checkBox.setChecked(!checkBox.isChecked());
                activity.getMapView().getMapSettings().setMapRotationEnabled(checkBox.isChecked());
            }
        });

        final View zoom = specificLayout.findViewById(R.id.zooming);
        zoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CheckBox checkBox = (CheckBox) zoom.findViewById(R.id.property_value);
                checkBox.setChecked(!checkBox.isChecked());
                activity.getMapView().getMapSettings().setMapZoomingEnabled(checkBox.isChecked());
            }
        });

        final View inertia = specificLayout.findViewById(R.id.enable_inertia);
        inertia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CheckBox checkBox = (CheckBox) inertia.findViewById(R.id.property_value);
                checkBox.setChecked(!checkBox.isChecked());
                activity.getMapView().getMapSettings().setInertiaPanningEnabled(checkBox.isChecked());
                activity.getMapView().getMapSettings().setInertiaRotatingEnabled(checkBox.isChecked());
                activity.getMapView().getMapSettings().setInertiaZoomingEnabled(checkBox.isChecked());
            }
        });

        final View mode3d = specificLayout.findViewById(R.id.enable_3d);
        mode3d.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CheckBox checkBox = (CheckBox) mode3d.findViewById(R.id.property_value);
                checkBox.setChecked(!checkBox.isChecked());
                activity.getMapView().getMapSettings().setMapDisplayMode(checkBox.isChecked() ? SKMapSettings.SKMapDisplayMode.MODE_3D : SKMapSettings.SKMapDisplayMode.MODE_2D);
            }
        });

        final View oneWays = specificLayout.findViewById(R.id.one_ways);
        oneWays.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CheckBox checkBox = (CheckBox) oneWays.findViewById(R.id.property_value);
                checkBox.setChecked(!checkBox.isChecked());
                activity.getMapView().getMapSettings().setOneWayArrows(checkBox.isChecked());
            }
        });

        final View bicycleLanes = specificLayout.findViewById(R.id.show_bicyle_lanes);
        bicycleLanes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CheckBox checkBox = (CheckBox) bicycleLanes.findViewById(R.id.property_value);
                checkBox.setChecked(!checkBox.isChecked());
                activity.getMapView().getMapSettings().setShowBicycleLanes(checkBox.isChecked());
            }
        });

        final View houseNumbers = specificLayout.findViewById(R.id.show_house_numbers);
        houseNumbers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CheckBox checkBox = (CheckBox) houseNumbers.findViewById(R.id.property_value);
                checkBox.setChecked(!checkBox.isChecked());
                activity.getMapView().getMapSettings().setHouseNumbersShown(checkBox.isChecked());
            }
        });

        final View streetNamesAsPopups = specificLayout.findViewById(R.id.street_badges);
        streetNamesAsPopups.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CheckBox checkBox = (CheckBox) streetNamesAsPopups.findViewById(R.id.property_value);
                checkBox.setChecked(!checkBox.isChecked());
                activity.getMapView().getMapSettings().setStreetNamePopupsShown(checkBox.isChecked());
            }
        });

        final View zoomWithAnchor = specificLayout.findViewById(R.id.zoom_with_center_anchor);
        zoomWithAnchor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CheckBox checkBox = (CheckBox) zoomWithAnchor.findViewById(R.id.property_value);
                checkBox.setChecked(!checkBox.isChecked());
                activity.getMapView().getMapSettings().setZoomWithAnchorEnabled(checkBox.isChecked());
            }
        });

        specificLayout.findViewById(R.id.compass_options).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DebugSettings.getInstanceForType(CompassDebugSettings.class).open(debugBaseLayout, MapDebugSettings.this);
            }
        });

        specificLayout.findViewById(R.id.zoom_levels).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DebugSettings.getInstanceForType(ZoomLimitDebugSettings.class).open(debugBaseLayout, MapDebugSettings.this);
            }
        });

        specificLayout.findViewById(R.id.drawing_order).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DebugSettings.getInstanceForType(DrawingOrderDebugSettings.class).open(debugBaseLayout, MapDebugSettings.this);
            }
        });

        specificLayout.findViewById(R.id.map_state).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DebugSettings.getInstanceForType(MapStateDebugSettings.class).open(debugBaseLayout, MapDebugSettings.this);
            }
        });

        specificLayout.findViewById(R.id.trail_settings).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DebugSettings.getInstanceForType(TrailDebugSettings.class).open(debugBaseLayout, MapDebugSettings.this);
            }
        });

        specificLayout.findViewById(R.id.camera_settings).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DebugSettings.getInstanceForType(CameraDebugSettings.class).open(debugBaseLayout, MapDebugSettings.this);
            }
        });

        specificLayout.findViewById(R.id.poi_options).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DebugSettings.getInstanceForType(PoiDisplayDebugSettings.class).open(debugBaseLayout, MapDebugSettings.this);
            }
        });

        specificLayout.findViewById(R.id.new_instance).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activity.startActivity(new Intent(activity, DebugMapActivity.class));
            }
        });
    }

    @Override
    void applyCustomChangesToUI() {
        super.applyCustomChangesToUI();
        ((SeekBar) specificLayout.findViewById(R.id.annotation_zoom_limit).findViewById(R.id.property_seekbar)).setMax(180);
    }

    @Override
    void onChildChanged(DebugSettings changedChild) {
        super.onChildChanged(changedChild);
        if (changedChild instanceof FollowerModeDebugSettings) {
            currentFollowerMode = SKMapSettings.SKMapFollowerMode.values()[((FollowerModeDebugSettings) changedChild).getCurrentSelectedIndex()];
            ((TextView) specificLayout.findViewById(R.id.heading_mode).findViewById(R.id.property_value)).setText(currentFollowerMode.toString());
            activity.getMapView().getMapSettings().setFollowerMode(currentFollowerMode);
        } else if (changedChild instanceof DrawingOrderDebugSettings) {
            List<SKMapSettings.SKDrawingOrderType> drawingOrder = new ArrayList<SKMapSettings.SKDrawingOrderType>();
            if (((DrawingOrderDebugSettings) changedChild).getCurrentSelectedIndex() == 0) {
                drawingOrder.add(SKMapSettings.SKDrawingOrderType.DRAWABLE_OBJECTS);
                drawingOrder.add(SKMapSettings.SKDrawingOrderType.CUSTOM_POIS);
                ((TextView) specificLayout.findViewById(R.id.drawing_order).findViewById(R.id.property_value)).setText(activity.getResources().getString(R.string.annotations_over_objects));
            } else {
                drawingOrder.add(SKMapSettings.SKDrawingOrderType.CUSTOM_POIS);
                drawingOrder.add(SKMapSettings.SKDrawingOrderType.DRAWABLE_OBJECTS);
                ((TextView) specificLayout.findViewById(R.id.drawing_order).findViewById(R.id.property_value)).setText(activity.getResources().getString(R.string.objects_over_annotations));
            }
            activity.getMapView().getMapSettings().setDrawingOrder(drawingOrder);
        }
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
        if (skAnnotation == testAnnotation) {
            updateTestAnnotation();
        }
    }

    @Override
    public void onCustomPOISelected(SKMapCustomPOI skMapCustomPOI) {

    }

    @Override
    public void onCompassSelected() {

    }

    @Override
    public void onCurrentPositionSelected() {
        Toast.makeText(specificLayout.getContext(), "Current position tapped!", Toast.LENGTH_LONG).show();
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
