package com.skobbler.debugkit.debugsettings;

import android.graphics.drawable.ColorDrawable;
import android.util.Pair;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;

import com.skobbler.debugkit.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Tudor on 7/3/2015.
 */
public class ScreenshotDebugSettings extends DebugSettings {

    private boolean isFrameShown = true;

    private boolean isContinuousScreenhotOn;

    @Override
    List<Pair<String, Object>> defineKeyValuePairs() {
        List<Pair<String, Object>> pairs = new ArrayList<Pair<String, Object>>();
        pairs.add(new Pair<String, Object>(activity.getResources().getString(R.string.show_frame), true));
        pairs.add(new Pair<String, Object>(activity.getResources().getString(R.string.continuous_screenshot), false));
        pairs.add(new Pair<String, Object>(activity.getResources().getString(R.string.take_screenshot), null));
        pairs.add(new Pair<String, Object>(activity.getResources().getString(R.string.clear_screenshot), null));
        return pairs;
    }

    @Override
    int defineSpecificLayout() {
        return R.layout.settings_screenshot;
    }

    @Override
    void defineSpecificListeners() {
        View showFrameView = specificLayout.findViewById(R.id.show_frame);
        final CheckBox showFrameCheckBox = (CheckBox) showFrameView.findViewById(R.id.property_value);
        showFrameView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showFrameCheckBox.setChecked(!showFrameCheckBox.isChecked());
                isFrameShown = showFrameCheckBox.isChecked();
                activity.findViewById(R.id.screenshot_layout).setVisibility(isFrameShown ? View.VISIBLE : View.GONE);
            }
        });

        View constinuousView = specificLayout.findViewById(R.id.continuous_screenshot);
        final CheckBox continuousCheckBox = (CheckBox) constinuousView.findViewById(R.id.property_value);
        constinuousView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                continuousCheckBox.setChecked(!continuousCheckBox.isChecked());
                isContinuousScreenhotOn = continuousCheckBox.isChecked();
            }
        });

        specificLayout.findViewById(R.id.take_screenshot).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activity.getMapView().requestScreenshot();
            }
        });

        specificLayout.findViewById(R.id.clear_screenshot).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((ImageView) activity.findViewById(R.id.screenshot_view)).setImageDrawable(new ColorDrawable(0xffffff));
            }
        });
    }

    public boolean isFrameShown() {
        return isFrameShown;
    }

    public boolean isContinuousScreenshotOn() {
        return isContinuousScreenhotOn;
    }

    @Override
    void onOpened() {
        super.onOpened();
        if (isFrameShown) {
            activity.findViewById(R.id.screenshot_layout).setVisibility(View.VISIBLE);
        }
    }

    @Override
    void onClose() {
        super.onClose();
        activity.findViewById(R.id.screenshot_layout).setVisibility(View.GONE);
    }
}
