package com.skobbler.sdkdemo.activity;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import com.skobbler.sdkdemo.R;


public class TracksActivity extends Activity {
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tracks);
        
        final AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setMessage(getString(R.string.gpx_license_notification_text));
        alertDialog.setCancelable(true);
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, getString(R.string.ok_text),
                new DialogInterface.OnClickListener() {
                    
                    public void onClick(DialogInterface dialog, int id) {
                        alertDialog.cancel();
                    }
                });
        alertDialog.show();
       
        
    }
    
    
    public void onMenuOptionClick(View v) {
        Intent intent = new Intent(TracksActivity.this, TrackElementsActivity.class);
        switch (v.getId()) {
            case R.id.gpx_chicago:
                intent.putExtra(Intent.EXTRA_TEXT, "Route_5_Chicago_city_track.gpx");
                break;
            case R.id.gpx_route_1:
                intent.putExtra(Intent.EXTRA_TEXT, "Route_1_BerlinUnterDenLinden_BerlinHohenzollerndamm.gpx");
                break;
            case R.id.gpx_route_2:
                intent.putExtra(Intent.EXTRA_TEXT, "Route_2_BerlinUnterDenLinden_BerlinGrunewaldstrasse.gpx");
                break;
            case R.id.gpx_route_3:
                intent.putExtra(Intent.EXTRA_TEXT, "Route_3_MunchenOskarVonMillerRing_Herterichstrasse.gpx");
                break;
            case R.id.gpx_route_4:
                intent.putExtra(Intent.EXTRA_TEXT, "Route_4_Berlin_Hamburg.gpx");
                break;
            default:
                break;
        }
        startActivityForResult(intent, MapActivity.TRACKS);
    }
    
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case MapActivity.TRACKS:
                    setResult(RESULT_OK);
                    this.finish();
                    break;
                default:
                    break;
            }
        }
    }
    
    
}
