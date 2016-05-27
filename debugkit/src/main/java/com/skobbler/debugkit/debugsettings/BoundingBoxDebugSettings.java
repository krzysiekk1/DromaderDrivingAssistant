package com.skobbler.debugkit.debugsettings;

import android.content.Context;
import android.os.Environment;
import android.text.format.DateFormat;
import android.util.Pair;
import android.view.View;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;

import com.skobbler.debugkit.R;
import com.skobbler.debugkit.util.DebugKitUtils;
import com.skobbler.ngx.SKCoordinate;
import com.skobbler.ngx.map.SKBoundingBox;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by AlexandraP on 03.07.2015.
 */
public class BoundingBoxDebugSettings extends  DebugSettings {

    /**
     * top left latitude
     */
    private double topLeftLatitude = 46.776868;

    /**
     * top left longitude
     */
    private double topLeftLongitude = 23.589567;

    /**
     * bottom right latitude;
     */
    private double bottomRightLatitude = 46.769814;

    /**
     * bottom right longitude
     */
    private double bottomRightLongitude = 23.596433;
    /**
     * width for screenshot
     */
    private int imageWidth = 320;
    /**
     * height for screenshot
     */
    private int imageHeight = 480;
    /**
     * padingWidth padding in pixels, from left and right of the screen
     */
    private int paddingWidth = 320;
    /**
     * paddingHeight padding in pixels, from top and bottom of the screen
     */
    private int paddingHeight = 480;
    /**
     * filePath path to the output file
     */
    private String filePath;


    @Override
    List<Pair<String, Object>> defineKeyValuePairs() {
        List<Pair<String, Object>> keyValuePairs = new ArrayList<Pair<String, Object>>();
        Context context = specificLayout.getContext();

        keyValuePairs.add(new Pair<String, Object>(context.getResources().getString(R.string.settings_title), null));
        keyValuePairs.add(new Pair<String, Object>(context.getResources().getString(R.string.top_left_latitude), topLeftLatitude));
        keyValuePairs.add(new Pair<String, Object>(context.getResources().getString(R.string.top_left_longitude), topLeftLongitude));
        keyValuePairs.add(new Pair<String, Object>(context.getResources().getString(R.string.bottom_right_latitude), bottomRightLatitude));
        keyValuePairs.add(new Pair<String, Object>(context.getResources().getString(R.string.bottom_right_longitude), bottomRightLongitude));
        keyValuePairs.add(new Pair<String, Object>(context.getResources().getString(R.string.image_width), imageWidth));
        keyValuePairs.add(new Pair<String, Object>(context.getResources().getString(R.string.image_height), imageHeight));
        keyValuePairs.add(new Pair<String, Object>(context.getResources().getString(R.string.renderer), null));
        keyValuePairs.add(new Pair<String, Object>(context.getResources().getString(R.string.fit_bounding_box_title), null));
        keyValuePairs.add(new Pair<String, Object>(context.getResources().getString(R.string.padding_width), paddingWidth));
        keyValuePairs.add(new Pair<String, Object>(context.getResources().getString(R.string.padding_height), paddingHeight));
        keyValuePairs.add(new Pair<String, Object>(context.getResources().getString(R.string.fit_bounding_box_button), null));
        return keyValuePairs;
    }

    @Override
    int defineSpecificLayout() {
        return R.layout.settings_bounding_box;
    }

    @Override
    void defineSpecificListeners() {
        SeekBar seekBarImageWidth = (SeekBar) specificLayout.findViewById(R.id.image_size_width).findViewById(R.id.property_seekbar);
        seekBarImageWidth.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int value, boolean b) {
                ((TextView) specificLayout.findViewById(R.id.image_size_width).findViewById(R.id.property_value)).setText( value + "");
                imageWidth = value;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        SeekBar seekBarImageHeight = (SeekBar) specificLayout.findViewById(R.id.image_size_height).findViewById(R.id.property_seekbar);
        seekBarImageHeight.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int value, boolean b) {
                ((TextView) specificLayout.findViewById(R.id.image_size_height).findViewById(R.id.property_value)).setText( value + "");
                imageHeight = value;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        SeekBar seekBarPaddingWidth = (SeekBar) specificLayout.findViewById(R.id.padding_width).findViewById(R.id.property_seekbar);
        seekBarPaddingWidth.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int value, boolean b) {
                ((TextView) specificLayout.findViewById(R.id.padding_width).findViewById(R.id.property_value)).setText( value + "");
                paddingWidth = value;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        SeekBar seekBarPaddingHeight = (SeekBar) specificLayout.findViewById(R.id.padding_height).findViewById(R.id.property_seekbar);
        seekBarPaddingHeight.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int value, boolean b) {
                ((TextView) specificLayout.findViewById(R.id.padding_height).findViewById(R.id.property_value)).setText( value + "");
                paddingHeight = value;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        final EditText topLeftLatitudeValue = (EditText) specificLayout.findViewById(R.id.top_left_latitude).findViewById(R.id.property_value);
        final EditText topLeftLongitudeValue = (EditText) specificLayout.findViewById(R.id.top_left_longitude).findViewById(R.id.property_value);
        final EditText bottomRightLatitudeValue = (EditText) specificLayout.findViewById(R.id.bottom_right_latitude).findViewById(R.id.property_value);
        final EditText bottomRightLongitudeValue = (EditText) specificLayout.findViewById(R.id.bottom_right_longitude).findViewById(R.id.property_value);

        final View fitBoudingBox = specificLayout.findViewById(R.id.fit_bounding_box_button);
        fitBoudingBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                topLeftLatitude = Double.parseDouble(topLeftLatitudeValue.getText().toString());
                topLeftLongitude = Double.parseDouble(topLeftLongitudeValue.getText().toString());
                bottomRightLatitude = Double.parseDouble(bottomRightLatitudeValue.getText().toString());
                bottomRightLongitude = Double.parseDouble(bottomRightLongitudeValue.getText().toString());

                SKBoundingBox skBoundingBox = new SKBoundingBox(topLeftLatitude,topLeftLongitude,bottomRightLatitude,bottomRightLongitude);
                activity.getMapView().fitBoundingBox(skBoundingBox,paddingWidth,paddingHeight);
            }
        });
        final View renderBoundingBox = specificLayout.findViewById(R.id.bounding_box_render_button);
        renderBoundingBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                topLeftLatitude = Double.parseDouble(topLeftLatitudeValue.getText().toString());
                topLeftLongitude = Double.parseDouble(topLeftLongitudeValue.getText().toString());
                bottomRightLatitude = Double.parseDouble(bottomRightLatitudeValue.getText().toString());
                bottomRightLongitude = Double.parseDouble(bottomRightLongitudeValue.getText().toString());

                SKBoundingBox skBoundingBox = new SKBoundingBox(topLeftLatitude,topLeftLongitude,bottomRightLatitude,bottomRightLongitude);
                Date date = new Date();
                CharSequence dateTimeBoudingBox  = DateFormat.format("MM-dd-yy hh-mm-ss", date.getTime());
                filePath = Environment.getExternalStorageDirectory() +"/"+Environment.DIRECTORY_DCIM + "/" + "Camera/" + dateTimeBoudingBox.toString() + ".jpg";
                activity.getMapView().renderMapBoundingBoxToFile(skBoundingBox,imageWidth,imageHeight,filePath);
            }
        });

    }

    public String getFilePath() {
        return filePath;
    }

    @Override
    void applyCustomChangesToUI() {
        super.applyCustomChangesToUI();
        ((SeekBar) specificLayout.findViewById(R.id.image_size_width).findViewById(R.id.property_seekbar)).setMax(320);
        ((SeekBar) specificLayout.findViewById(R.id.image_size_height).findViewById(R.id.property_seekbar)).setMax(480);
        ((SeekBar) specificLayout.findViewById(R.id.padding_width).findViewById(R.id.property_seekbar)).setMax(320);
        ((SeekBar) specificLayout.findViewById(R.id.padding_height).findViewById(R.id.property_seekbar)).setMax(480);
    }
}
