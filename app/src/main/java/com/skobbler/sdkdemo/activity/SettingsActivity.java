package com.skobbler.sdkdemo.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.skobbler.sdkdemo.R;

/**
 * Created by Krzysiek
 */

public class SettingsActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);
    }

    @SuppressLint("ResourceAsColor")
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.app_settings_button:
                startActivity(new Intent(this, AppSettingsActivity.class));
                break;
            case R.id.navi_settings_button:
                startActivity(new Intent(this, NaviSettingsActivity.class));
                break;
            default:
                break;
        }
    }

}
