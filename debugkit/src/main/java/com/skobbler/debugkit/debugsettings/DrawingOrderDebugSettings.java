package com.skobbler.debugkit.debugsettings;

import android.util.Pair;

import com.skobbler.debugkit.R;
import com.skobbler.ngx.SKCoordinate;
import com.skobbler.ngx.map.SKAnimationSettings;
import com.skobbler.ngx.map.SKAnnotation;
import com.skobbler.ngx.map.SKCircle;
import com.skobbler.ngx.map.SKMapSurfaceView;
import com.skobbler.ngx.map.SKScreenPoint;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Tudor on 6/5/2015.
 */
public class DrawingOrderDebugSettings extends SingleChoiceListDebugSettings {
    @Override
    List<String> defineChoices() {
        List<String> choices = new ArrayList<String>();
        choices.add(activity.getResources().getString(R.string.annotations_over_objects));
        choices.add(activity.getResources().getString(R.string.objects_over_annotations));
        return choices;
    }

    @Override
    int defineInitialSelectionIndex() {
        return 0;
    }

    @Override
    int defineSpecificLayout() {
        return R.layout.settings_drawing_order;
    }

    @Override
    void defineSpecificListeners() {
        super.defineSpecificListeners();
    }

    @Override
    void onOpened() {
        int y = activity.getWindowManager().getDefaultDisplay().getHeight()/2;
        int x = (82 * activity.getWindowManager().getDefaultDisplay().getWidth()) / 100;
        SKCoordinate center = activity.getMapView().pointToCoordinate(new SKScreenPoint(x, y));

        SKCircle circle = new SKCircle();
        circle.setCircleCenter(center);
        circle.setColor(new float[]{1, 0.5f, 0, 0.85f});
        circle.setOutlineColor(new float[]{1, 1, 1, 1});
        circle.setOutlineDottedPixelsSkip(6);
        circle.setOutlineDottedPixelsSolid(10);
        circle.setNumberOfPoints(150);
        circle.setIdentifier(1000);
        circle.setOutlineSize(3);
        circle.setRadius(300);
        activity.getMapView().addCircle(circle);

        SKAnnotation annotation = new SKAnnotation(2000);
        annotation.setLocation(center);
        annotation.setMininumZoomLevel(5);
        annotation.setAnnotationType(SKAnnotation.SK_ANNOTATION_TYPE_RED);
        activity.getMapView().addAnnotation(annotation, SKAnimationSettings.ANIMATION_NONE);
    }
}