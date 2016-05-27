package com.skobbler.debugkit.debugsettings;

import android.content.Context;
import android.util.DisplayMetrics;
import android.util.Pair;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

import com.skobbler.debugkit.R;
import com.skobbler.ngx.SKCoordinate;
import com.skobbler.ngx.SKMaps;
import com.skobbler.ngx.map.SKAnimationSettings;
import com.skobbler.ngx.map.SKAnnotation;
import com.skobbler.ngx.map.SKAnnotationView;
import com.skobbler.ngx.map.SKMapSettings;
import com.skobbler.ngx.map.SKPulseAnimationSettings;
import com.skobbler.ngx.map.SKScreenPoint;
import com.skobbler.ngx.positioner.SKPosition;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mirceab on 03.06.2015.
 */
public class AnnotationDebugSettings extends DebugSettings {
    /**
     * Custom view for adding an annotation
     */
    private RelativeLayout customView;
    /**
     * Annotation View checked
     */
    private boolean annotationViewChecked = false;
    /**
     * Update Annotation
     */
    private boolean updateAnnotationCheck = false;
    /**
     * Animation duration
     */
    private int animatationDurationProgress = 10;
    /**
     * Animation type
     */
    private SKAnimationSettings skAnimationType = SKAnimationSettings.ANIMATION_NONE;
    /**
     * Annotation type
     */
    private int skAnnotation = SKAnnotation.SK_ANNOTATION_TYPE_RED;
    /**
     * Animation easing type
     */
    private SKAnimationSettings.SKEasingType skEasingType = SKAnimationSettings.SKEasingType.EASE_LINEAR;
    /**
     * Identifier
     */
    private int identifier = 0;
    /**
     * Remove identifier
     */
    private int removeIdentifier = 0;
    /**
     * Offset X
     */
    private float offsetX = 0;
    /**
     * Offset Y
     */
    private float offsetY = 0;

    /**
     * Minimum zoom level
     */
    private int minimumZoomLevel = 5;
    /**
     * Minimum tap zoom level
     */
    private float minimumTapZoomLevel = 5;

    /**
     * Latitude
     */
    private double latitude = 37.7765;
    /**
     * longitude
     */
    private double longitude = -122.4200;


    @Override
    List<Pair<String, Object>> defineKeyValuePairs() {
        List<Pair<String, Object>> keyValuePairs = new ArrayList<Pair<String, Object>>();
        Context context = specificLayout.getContext();
        keyValuePairs.add(new Pair<String, Object>(context.getResources().getString(R.string.annotation_settings), null));
        keyValuePairs.add(new Pair<String, Object>(context.getResources().getString(R.string.annotation_identifier), identifier));
        keyValuePairs.add(new Pair<String, Object>(context.getResources().getString(R.string.annotation_type), context.getResources().getString(R.string.annotation_color_red)));
        keyValuePairs.add(new Pair<String, Object>(context.getResources().getString(R.string.offset_X), offsetX));
        keyValuePairs.add(new Pair<String, Object>(context.getResources().getString(R.string.offset_Y), offsetY));
        keyValuePairs.add(new Pair<String, Object>(context.getResources().getString(R.string.latitude), latitude));
        keyValuePairs.add(new Pair<String, Object>(context.getResources().getString(R.string.longitude), longitude));
        keyValuePairs.add(new Pair<String, Object>(context.getResources().getString(R.string.min_zoom), minimumZoomLevel));
        keyValuePairs.add(new Pair<String, Object>(context.getResources().getString(R.string.minimum_tap_zoom), minimumTapZoomLevel));
        keyValuePairs.add(new Pair<String, Object>(context.getResources().getString(R.string.annotation_view), null));
        keyValuePairs.add(new Pair<String, Object>(context.getResources().getString(R.string.animation_settings), null));
        keyValuePairs.add(new Pair<String, Object>(context.getResources().getString(R.string.animation_type),context.getResources().getString(R.string.annotation_none)));
        keyValuePairs.add(new Pair<String, Object>(context.getResources().getString(R.string.animation_easing_type), skEasingType));
        keyValuePairs.add(new Pair<String, Object>(context.getResources().getString(R.string.animation_duration), animatationDurationProgress));
        keyValuePairs.add(new Pair<String, Object>(context.getResources().getString(R.string.annotation_actions), null));
        keyValuePairs.add(new Pair<String, Object>(context.getResources().getString(R.string.annotation_add), null));
        keyValuePairs.add(new Pair<String, Object>(context.getResources().getString(R.string.annotation_bring_to_front), null));
        keyValuePairs.add(new Pair<String, Object>(context.getResources().getString(R.string.annotation_update), null));
        keyValuePairs.add(new Pair<String, Object>(context.getResources().getString(R.string.annotation_remove_identifier), removeIdentifier));
        keyValuePairs.add(new Pair<String, Object>(context.getResources().getString(R.string.annotation_remove), null));
        keyValuePairs.add(new Pair<String, Object>(context.getResources().getString(R.string.annotation_clear_all), null));
        return keyValuePairs;
    }

    @Override
    int defineSpecificLayout() {
        return R.layout.annotation_debug_kit_customization;
    }

    @Override
    void defineSpecificListeners() {
        specificLayout.findViewById(R.id.annotation_type).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DebugSettings.getInstanceForType(AnnotationType.class).open(debugBaseLayout, AnnotationDebugSettings.this);
            }
        });
        specificLayout.findViewById(R.id.animation_type).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DebugSettings.getInstanceForType(AnimationType.class).open(debugBaseLayout, AnnotationDebugSettings.this);
            }
        });
        specificLayout.findViewById(R.id.animation_easing_type).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DebugSettings.getInstanceForType(AnimationEasingType.class).open(debugBaseLayout, AnnotationDebugSettings.this);
            }
        });
        final SeekBar animationDurationSeekBar = (SeekBar) specificLayout.findViewById(R.id.animation_duration).findViewById(R.id.property_seekbar);
        final TextView animationDurationValue = (TextView) specificLayout.findViewById(R.id.animation_duration).findViewById(R.id.property_value);
        animationDurationSeekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                animatationDurationProgress = progress;
                animationDurationValue.setText(animatationDurationProgress + "");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        final SeekBar annotationMinimumTapZoomLevelSeekBar = (SeekBar) specificLayout.findViewById(R.id.annotation_minimum_tap_zoomlevel).findViewById(R.id.property_seekbar);
        final TextView annotationMinimumTapZoomLevelSeekBarValue = (TextView) specificLayout.findViewById(R.id.annotation_minimum_tap_zoomlevel).findViewById(R.id.property_value);
        annotationMinimumTapZoomLevelSeekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                minimumTapZoomLevel = progress;
                annotationMinimumTapZoomLevelSeekBarValue.setText(minimumTapZoomLevel + "");
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
                activity.addTestAnnotationAtPosition(annotationPosition);
            }
        });
        final View annotationView = specificLayout.findViewById(R.id.annotation_view);
        annotationView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CheckBox annotationCheckBox = (CheckBox) annotationView.findViewById(R.id.property_value);
                annotationCheckBox.setChecked(!annotationCheckBox.isChecked());
                if (annotationCheckBox.isChecked()) {
                    annotationViewChecked = true;
                }
                else {
                    annotationViewChecked=false;
                }
                //activity.getMapView().deleteAllAnnotationsAndCustomPOIs();
                prepareAnnotations();
            }
        });
        final View addAnnotationButton = specificLayout.findViewById(R.id.annotation_add);
        final EditText annotationIdentifier = (EditText) specificLayout.findViewById(R.id.annotation_identifier).findViewById(R.id.property_value);
        final EditText annotationLongitude = (EditText) specificLayout.findViewById(R.id.annotation_longitude).findViewById(R.id.property_value);
        final EditText annotationlatitude = (EditText) specificLayout.findViewById(R.id.annotation_latitude).findViewById(R.id.property_value);
        final EditText annotationMinimumZoomLevel = (EditText) specificLayout.findViewById(R.id.annotation_minimum_zoomlevel).findViewById(R.id.property_value);

        addAnnotationButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                updateAnnotationCheck = false;
                longitude = Double.parseDouble(annotationLongitude.getText().toString());
                latitude = Double.parseDouble(annotationlatitude.getText().toString());
                minimumZoomLevel = Integer.parseInt(annotationMinimumZoomLevel.getText().toString());
                identifier = Integer.parseInt(annotationIdentifier.getText().toString());
                prepareAnnotations();
            }
        });
        final View removeAnnotationButton = specificLayout.findViewById(R.id.annotation_clear_all);
        removeAnnotationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               // activity.getMapView().deleteAllAnnotationsAndCustomPOIs();
            }
        });
        final View removeAnnotationIdentifier = specificLayout.findViewById(R.id.annotation_remove);
        removeAnnotationIdentifier.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText removeIdentifierFromAnnotation = (EditText) specificLayout.findViewById(R.id.annotation_remove_identifier).findViewById(R.id.property_value);
                removeIdentifier = Integer.parseInt(removeIdentifierFromAnnotation.getText().toString());
                activity.getMapView().deleteAnnotation(removeIdentifier);
            }
        });
        final View updateAnnotationButton = specificLayout.findViewById(R.id.annotation_update);
        updateAnnotationButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                longitude = Double.parseDouble(annotationLongitude.getText().toString());
                latitude = Double.parseDouble(annotationlatitude.getText().toString());
                minimumZoomLevel = Integer.parseInt(annotationMinimumZoomLevel.getText().toString());
                identifier = Integer.parseInt(annotationIdentifier.getText().toString());
                updateAnnotationCheck = true;
                prepareAnnotations();
            }
        });
        final View bringToFrondButton = specificLayout.findViewById(R.id.annotation_bring_to_front);
        bringToFrondButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                identifier = Integer.parseInt(annotationIdentifier.getText().toString());
                activity.getMapView().bringToFrontAnnotationWithID(identifier);
            }
        });

    }
    /**
     * Draws annotations on map
     */
    private void prepareAnnotations() {

        // Add annotation using texture ID - from the json files.
        // get the annotation object
        if (annotationViewChecked) {
            SKAnnotationView annotationView = new SKAnnotationView();
            // // add an annotation with a view
            SKAnnotation annotationFromView = new SKAnnotation(15);
            annotationFromView.setLocation(new SKCoordinate(longitude, latitude));
            annotationFromView.setMininumZoomLevel(5);
            annotationView = new SKAnnotationView();
            customView =
                    (RelativeLayout) ((LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(
                            R.layout.layout_custom_view, null, false);
            //  If width and height of the view  are not power of 2 the actual size of the image will be the next power of 2 of max(width,height).
            annotationView.setView(customView);
            annotationFromView.setAnnotationView(annotationView);
            activity.getMapView().addAnnotation(annotationFromView, SKAnimationSettings.ANIMATION_NONE);
        } else {
            SKAnnotation annotation1;
            if (identifier == 0) {
                annotation1 = new SKAnnotation(10);
            } else {
                annotation1 = new SKAnnotation(identifier);
            }
            if (updateAnnotationCheck == false) {
                skAnimationType.setDuration(animatationDurationProgress);
                // set annotation location
                annotation1.setLocation(new SKCoordinate(longitude, latitude));

                // set offset
                annotation1.setOffset(new SKScreenPoint(offsetX, offsetY));

                // set minimum zoom level at which the annotation should be visible
                annotation1.setMininumZoomLevel(minimumZoomLevel);

                // set the annotation's type
                annotation1.setAnnotationType(skAnnotation);

                // render annotation on map
                activity.getMapView().addAnnotation(annotation1, skAnimationType);
            } else {
                annotation1.setLocation(new SKCoordinate(longitude, latitude));
                annotation1.setOffset(new SKScreenPoint(offsetX, offsetY));
                annotation1.setMininumZoomLevel(minimumZoomLevel);
                annotation1.setAnnotationType(skAnnotation);
                activity.getMapView().updateAnnotation(annotation1);
            }
        }

        // set map zoom level
        activity.getMapView().setZoom(15);
        // center map on a position
        activity.getMapView().centerMapOnPosition(new SKCoordinate(longitude, latitude));
    }

    @Override
    void onChildChanged(DebugSettings changedChild) {
        super.onChildChanged(changedChild);
        if (changedChild instanceof AnnotationType) {
            switch (((AnnotationType) changedChild).getCurrentSelectedIndex()) {
                case 0:
                    skAnnotation = SKAnnotation.SK_ANNOTATION_TYPE_RED;
                    ((TextView) specificLayout.findViewById(R.id.annotation_type).findViewById(R.id.property_value)).setText("Red");
                    break;
                case 1:
                    skAnnotation = SKAnnotation.SK_ANNOTATION_TYPE_BLUE;
                    ((TextView) specificLayout.findViewById(R.id.annotation_type).findViewById(R.id.property_value)).setText("Blue");
                    break;
                case 2:
                    skAnnotation = SKAnnotation.SK_ANNOTATION_TYPE_GREEN;
                    ((TextView) specificLayout.findViewById(R.id.annotation_type).findViewById(R.id.property_value)).setText("Green");
                    break;
                case 3:
                    skAnnotation = SKAnnotation.SK_ANNOTATION_TYPE_PURPLE;
                    ((TextView) specificLayout.findViewById(R.id.annotation_type).findViewById(R.id.property_value)).setText("Purple");
                    break;
                case 4:
                    skAnnotation = SKAnnotation.SK_ANNOTATION_TYPE_MARKER;
                    ((TextView) specificLayout.findViewById(R.id.annotation_type).findViewById(R.id.property_value)).setText("Marker");
                    break;
                case 5:
                    skAnnotation = SKAnnotation.SK_ANNOTATION_TYPE_DESTINATION_FLAG;
                    ((TextView) specificLayout.findViewById(R.id.annotation_type).findViewById(R.id.property_value)).setText("Destination Flag");
                    break;
            }

        } else if (changedChild instanceof AnimationType) {
            switch (((AnimationType) changedChild).getCurrentSelectedIndex()) {
                case 0:
                    skAnimationType = SKAnimationSettings.ANIMATION_NONE;
                    ((TextView) specificLayout.findViewById(R.id.animation_type).findViewById(R.id.property_value)).setText(skAnimationType.toString());
                    break;
                case 1:
                    skAnimationType = SKAnimationSettings.ANIMATION_PIN_DROP;
                    ((TextView) specificLayout.findViewById(R.id.animation_type).findViewById(R.id.property_value)).setText(skAnimationType.toString());
                    break;
                case 2:
                    skAnimationType = SKAnimationSettings.ANIMATION_POP_OUT;
                    ((TextView) specificLayout.findViewById(R.id.animation_type).findViewById(R.id.property_value)).setText(skAnimationType.toString());
                    break;
                case 3:
                    skAnimationType = SKAnimationSettings.ANIMATION_PULSE_CCP;
                    ((TextView) specificLayout.findViewById(R.id.animation_type).findViewById(R.id.property_value)).setText(skAnimationType.toString());
                    break;
            }
        } else if (changedChild instanceof AnimationEasingType) {
            switch (((AnimationEasingType) changedChild).getCurrentSelectedIndex()) {
                case 0:
                    skEasingType = SKAnimationSettings.SKEasingType.EASE_LINEAR;
                    ((TextView) specificLayout.findViewById(R.id.animation_easing_type).findViewById(R.id.property_value)).setText(skEasingType.toString());
                case 1:
                    skEasingType = SKAnimationSettings.SKEasingType.EASE_IN_OUT_QUAD;
                    ((TextView) specificLayout.findViewById(R.id.animation_easing_type).findViewById(R.id.property_value)).setText(skEasingType.toString());
                case 2:
                    skEasingType = SKAnimationSettings.SKEasingType.EASE_OUT_QUAD;
                    ((TextView) specificLayout.findViewById(R.id.animation_easing_type).findViewById(R.id.property_value)).setText(skEasingType.toString());
                case 3:
                    skEasingType = SKAnimationSettings.SKEasingType.EASE_IN_OUT_QUAD;
                    ((TextView) specificLayout.findViewById(R.id.animation_easing_type).findViewById(R.id.property_value)).setText(skEasingType.toString());
                case 4:
                    skEasingType = SKAnimationSettings.SKEasingType.EASE_IN_CUBIC;
                    ((TextView) specificLayout.findViewById(R.id.animation_easing_type).findViewById(R.id.property_value)).setText(skEasingType.toString());
                case 5:
                    skEasingType = SKAnimationSettings.SKEasingType.EASE_OUT_CUBIC;
                    ((TextView) specificLayout.findViewById(R.id.animation_easing_type).findViewById(R.id.property_value)).setText(skEasingType.toString());
                case 6:
                    skEasingType = SKAnimationSettings.SKEasingType.EASE_IN_OUT_CUBIC;
                    ((TextView) specificLayout.findViewById(R.id.animation_easing_type).findViewById(R.id.property_value)).setText(skEasingType.toString());
                case 7:
                    skEasingType = SKAnimationSettings.SKEasingType.EASE_IN_QUART;
                    ((TextView) specificLayout.findViewById(R.id.animation_easing_type).findViewById(R.id.property_value)).setText(skEasingType.toString());
                case 8:
                    skEasingType = SKAnimationSettings.SKEasingType.EASE_OUT_QUART;
                    ((TextView) specificLayout.findViewById(R.id.animation_easing_type).findViewById(R.id.property_value)).setText(skEasingType.toString());
                case 9:
                    skEasingType = SKAnimationSettings.SKEasingType.EASE_IN_OUT_QUART;
                    ((TextView) specificLayout.findViewById(R.id.animation_easing_type).findViewById(R.id.property_value)).setText(skEasingType.toString());
                case 10:
                    skEasingType = SKAnimationSettings.SKEasingType.EASE_IN_QUINT;
                    ((TextView) specificLayout.findViewById(R.id.animation_easing_type).findViewById(R.id.property_value)).setText(skEasingType.toString());
                case 11:
                    skEasingType = SKAnimationSettings.SKEasingType.EASE_OUT_QUINT;
                    ((TextView) specificLayout.findViewById(R.id.animation_easing_type).findViewById(R.id.property_value)).setText(skEasingType.toString());
                case 12:
                    skEasingType = SKAnimationSettings.SKEasingType.EASE_IN_OUT_QUINT;
                    ((TextView) specificLayout.findViewById(R.id.animation_easing_type).findViewById(R.id.property_value)).setText(skEasingType.toString());
                case 13:
                    skEasingType = SKAnimationSettings.SKEasingType.EASE_IN_SINE;
                    ((TextView) specificLayout.findViewById(R.id.animation_easing_type).findViewById(R.id.property_value)).setText(skEasingType.toString());
                case 14:
                    skEasingType = SKAnimationSettings.SKEasingType.EASE_OUT_SINE;
                    ((TextView) specificLayout.findViewById(R.id.animation_easing_type).findViewById(R.id.property_value)).setText(skEasingType.toString());
                case 15:
                    skEasingType = SKAnimationSettings.SKEasingType.EASE_IN_OUT_SINE;
                    ((TextView) specificLayout.findViewById(R.id.animation_easing_type).findViewById(R.id.property_value)).setText(skEasingType.toString());
                case 16:
                    skEasingType = SKAnimationSettings.SKEasingType.EASE_IN_EXPO;
                    ((TextView) specificLayout.findViewById(R.id.animation_easing_type).findViewById(R.id.property_value)).setText(skEasingType.toString());
                case 17:
                    skEasingType = SKAnimationSettings.SKEasingType.EASE_OUT_EXPO;
                    ((TextView) specificLayout.findViewById(R.id.animation_easing_type).findViewById(R.id.property_value)).setText(skEasingType.toString());
                case 18:
                    skEasingType = SKAnimationSettings.SKEasingType.EASE_IN_OUT_EXPO;
                    ((TextView) specificLayout.findViewById(R.id.animation_easing_type).findViewById(R.id.property_value)).setText(skEasingType.toString());
            }
        }
    }

    @Override
    void applyCustomChangesToUI() {
        SeekBar durationSeekBar = (SeekBar) specificLayout.findViewById(R.id.animation_duration).findViewById(R.id.property_seekbar);
        durationSeekBar.setMax(1000);
        durationSeekBar.setProgress(200);
        ((SeekBar) specificLayout.findViewById(R.id.annotation_minimum_tap_zoomlevel).findViewById(R.id.property_seekbar)).setMax(180);
//        ((CheckBox) specificLayout.findViewById(R.id.continuous_check).findViewById(R.id.property_value)).setChecked(true);
    }
}

