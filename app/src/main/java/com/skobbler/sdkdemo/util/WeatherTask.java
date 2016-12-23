package com.skobbler.sdkdemo.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.skobbler.ngx.SKCoordinate;
import com.skobbler.ngx.map.SKAnimationSettings;
import com.skobbler.ngx.map.SKAnnotation;
import com.skobbler.ngx.map.SKAnnotationView;
import com.skobbler.ngx.map.SKMapSurfaceView;
import com.skobbler.ngx.routing.SKExtendedRoutePosition;
import com.skobbler.ngx.routing.SKRouteManager;
import com.skobbler.sdkdemo.R;
import com.skobbler.sdkdemo.costs.utils.Road;
import com.skobbler.sdkdemo.database.ResourcesDAO;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * Created by Jakub Solawa on 18.12.2016.
 */

public class WeatherTask extends AsyncTask {

    List<JSONObject> data = new ArrayList<JSONObject>();
    List<Bitmap> icons = new ArrayList<Bitmap>();
    private SKMapSurfaceView mapView;
    private RelativeLayout customView;
    String iconString;
    ImageView imgView;
    ImageView imgView2;
    LayoutInflater inflater;
    List<SKCoordinate> coordinates;
    Bitmap myIcon = null;
    Bitmap myIcon2 = null;

    public InputStream connectAndDownload(String lat, String lon) {
        URL url = null;
        JSONObject jsonObj = null;
        try {
            url = new URL("http://api.openweathermap.org/data/2.5/weather?lat=" + lat + "&lon=" + lon + "&APPID=07c24c31c3d139a6e42c38d0cf05321f");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        HttpURLConnection connection = null;
        try {
            connection = (HttpURLConnection) url.openConnection();

            BufferedReader reader =
                    new BufferedReader(new InputStreamReader(connection.getInputStream()));

            StringBuffer json = new StringBuffer(8192);
            String tmp = "";

            while ((tmp = reader.readLine()) != null)
                json.append(tmp).append("\n");
            reader.close();
            JSONObject jsonobject = new JSONObject(json.toString());
            if (jsonobject != null) {
                data.add(jsonobject);
            }
            if (jsonobject.getInt("cod") != 200) {
                System.out.println("Cancelled");
                return null;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (data != null) {
            for (JSONObject datum : data) {
                try {
                    jsonObj = datum.getJSONObject("weather");
                    iconString = jsonObj.getString("icon");
                    InputStream in = new URL("http://openweathermap.org/img/w/" + iconString + ".png").openStream();
                    icons.add(BitmapFactory.decodeStream(in));
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

        }
            return null;

    }


    @Override
    protected Object doInBackground(Object[] params) {
        coordinates = getCoordinatesForWeather((int) params[0]);
        this.mapView = (SKMapSurfaceView) params[1];
        this.inflater = (LayoutInflater) params[2];
        this.imgView = (ImageView) params[3];
        this.imgView2 = (ImageView) params[4];
        for (SKCoordinate coordinate : coordinates){
            connectAndDownload(String.valueOf(coordinate.getLatitude()), String.valueOf(coordinate.getLongitude()));
        }

        try {
            InputStream in = new java.net.URL("http://openweathermap.org/img/w/10d.png").openStream();
            this.myIcon = BitmapFactory.decodeStream(in);
        } catch (Exception e) {
            Log.e("Error", e.getMessage());
            System.out.println("BUOND");
            e.printStackTrace();
        }


        return null;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

//    public copyImgView (ImageView imgView){
//        this.imgView2 = imgView
//    }

    @Override
    protected void onPostExecute(Object o) {
        JSONObject array = null;
        String lat = null;
        String lon = null;

        if(data!=null){
            for(JSONObject datum : data) {

                List<String> list = new ArrayList<String>();
                try {
                    array = datum.getJSONObject("coord");
                    lat = array.getString("lon");
                    lon = array.getString("lat");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
            SKCoordinate coordinate = new SKCoordinate(50,19.9);
            SKAnnotation annotationWithTextureId = new SKAnnotation(10);
            annotationWithTextureId.setLocation(coordinate);
            annotationWithTextureId.setMininumZoomLevel(5);

            SKAnnotationView annotationView = new SKAnnotationView();
            customView = (RelativeLayout)(inflater.inflate(R.layout.layout_custom_view, null, false));
//            imgView.setImageBitmap(myIcon);
//            annotationView.setView(imgView);
//            annotationWithTextureId.setAnnotationView(annotationView);
//            mapView.addAnnotation(annotationWithTextureId, SKAnimationSettings.ANIMATION_NONE);


            SKCoordinate coordinate2 = new SKCoordinate(50,17.6);
            SKAnnotation annotationWithTextureId2 = new SKAnnotation(11);
            annotationWithTextureId2.setLocation(coordinate2);
            annotationWithTextureId2.setMininumZoomLevel(5);
            SKAnnotationView annotationView2 = new SKAnnotationView();
//            RelativeLayout customView2 = (RelativeLayout)(inflater.inflate(R.layout.layout_custom_view, null, false));
            imgView2.setImageBitmap(myIcon2);
//            annotationView.setView(imgView2);
//            annotationWithTextureId2.setAnnotationView(annotationView2);
//            mapView.addAnnotation(annotationWithTextureId2, SKAnimationSettings.ANIMATION_NONE);

        }
//        SKAnnotation annotationFromView = new SKAnnotation(11);
//        annotationFromView.setLocation(new SKCoordinate(37.761349, -122.423573));
//        annotationFromView.setMininumZoomLevel(5);
//        SKAnnotationView annotationView = new SKAnnotationView();
//        customView =
//                (RelativeLayout) ((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(
//                        R.layout.layout_custom_view, null, false);
//        annotationView.setView(findViewById(R.id.customView));
//        annotationFromView.setAnnotationView(annotationView);
//        mapView.addAnnotation(annotationFromView, SKAnimationSettings.ANIMATION_NONE);
//        mapView.setZoom(13);




    }

    private static List<SKCoordinate> getCoordinatesForWeather (int routeID) {
        List<SKCoordinate> coordinates = new ArrayList<SKCoordinate>();
        int i = 0;
        List<SKExtendedRoutePosition> positions = SKRouteManager.getInstance().getExtendedRoutePointsForRouteByUniqueId(routeID);
        for (SKExtendedRoutePosition pos : positions) {
            if (i % 3333 == 0) {
                coordinates.add(new SKCoordinate(pos.getCoordinate().getLongitude(), pos.getCoordinate().getLatitude()));
            }
            i++;
        }
        i--;
        if (i % 3333 != 0) {
            coordinates.add(new SKCoordinate(positions.get(i).getCoordinate().getLongitude(), positions.get(i).getCoordinate().getLatitude()));
        }
        return coordinates;
    }
}


