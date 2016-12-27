package com.skobbler.sdkdemo.util;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
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
    int routeId;
    static int annotationId = 20;
    static int annotationIdPrev = 20;
    private String packageName;
    private SKMapSurfaceView mapView;
    private RelativeLayout customView;
    String iconString;
    View view;
    LayoutInflater inflater;
    List<SKCoordinate> coordinates;
    Resources resources = null;

    public InputStream connectAndDownload(String lat, String lon) {
        URL url = null;
        JSONObject jsonObj = null;
        try {
            url = new URL("http://api.openweathermap.org/data/2.5/forecast?lat=" + lat + "&lon=" + lon + "&APPID=07c24c31c3d139a6e42c38d0cf05321f");
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

        return null;
    }


    @Override
    protected Object doInBackground(Object[] params) {
        this.routeId = (int) params[0];
        this.coordinates = getCoordinatesForWeather(routeId);
        this.mapView = (SKMapSurfaceView) params[1];
        this.inflater = (LayoutInflater) params[2];
        this.view = (View) params[3];
        this.resources = (Resources) params[4];
        this.packageName = (String) params[5];
        for (SKCoordinate coordinate : this.coordinates){
            connectAndDownload(String.valueOf(coordinate.getLatitude()), String.valueOf(coordinate.getLongitude()));
        }
        return null;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(Object o) {
        JSONObject object;
        JSONArray weatherArray;
        JSONArray listArray;
        String lat = null;
        String lon = null;
        String icon = null;
        String temp = null;
        for (int i = annotationIdPrev; i < annotationId;  i++){
            mapView.deleteAnnotation(i);
        }
        annotationIdPrev = annotationId;
        SKAnnotationView annotationView1 = new SKAnnotationView();
        customView = (RelativeLayout) (inflater.inflate(R.layout.layout_custom_view, null, false));
        ImageView imgView = (ImageView) view;
        int time = SKRouteManager.getInstance().getRouteInfo(routeId).getEstimatedTime();
        if (data != null) {
            int timeInterval = time / data.size();
            int timeSum = 0;
            int weatherInterval = 0;
            for (JSONObject datum : data) {
                timeSum += timeInterval;
                if (timeSum >= 10800){
                    weatherInterval++;
                    timeSum -= 10800;
                }
                List<String> list = new ArrayList<String>();
                try {
                    object = datum.getJSONObject("city");
                    JSONObject coordObject = object.getJSONObject("coord");
                    listArray = datum.getJSONArray("list");
                    JSONObject obj = listArray.getJSONObject(weatherInterval);
                    lat = coordObject.getString("lat");
                    lon = coordObject.getString("lon");
                    JSONObject mainObject = obj.getJSONObject("main");
                    temp = mainObject.getString("temp");
                    weatherArray = obj.getJSONArray("weather");
                    icon = weatherArray.getJSONObject(0).getString("icon");
                } catch (JSONException e) {
                    e.printStackTrace();
                }



                SKCoordinate coordinate = new SKCoordinate(Double.parseDouble(lat), Double.parseDouble(lon));
                SKAnnotation annotation1 = new SKAnnotation(annotationId);
                annotationId++;
                annotation1.setLocation(coordinate);
                annotation1.setMininumZoomLevel(5);
                String resourceString = "a" + icon;
                Bitmap bm;
                bm = BitmapFactory.decodeResource(resources, resources.getIdentifier(resourceString, "drawable", packageName));
                int tempCelsius = (int) (Double.parseDouble(temp) - 273);
                String textToBitmap = Integer.toString(tempCelsius) + "°";
                bm = addTextToBitmap(bm, textToBitmap);
                imgView.setImageBitmap(bm);
                annotationView1.setView(view);
                annotation1.setAnnotationView(annotationView1);
                mapView.addAnnotation(annotation1, SKAnimationSettings.ANIMATION_NONE);
            }
        }
    }



    private Bitmap addTextToBitmap(Bitmap bm, String textToBitmap) {
        Bitmap.Config bitmapConfig = bm.getConfig();
        // set default bitmap config if none
        if (bitmapConfig == null) {
            bitmapConfig = Bitmap.Config.ARGB_8888;
        }
        // resource bitmaps are imutable, so we need to convert it to mutable one
        bm = bm.copy(bitmapConfig, true);
        Canvas canvas = new Canvas(bm);
        // new antialised Paint
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        // text color
        paint.setColor(Color.BLACK);
        // text size in pixels
        paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        paint.setTextSize(60);
        // draw text to the Canvas center
        Rect bounds = new Rect();
        paint.getTextBounds(textToBitmap, 0, textToBitmap.length(), bounds);
        int x = (bm.getWidth() - bounds.width())/2;
        int y = (bm.getHeight() + bounds.height())/2;
        canvas.drawText(textToBitmap, x, y, paint);
        return bm;
    }






    private static List<SKCoordinate> getCoordinatesForWeather (int routeID) {
        List<SKCoordinate> coordinates = new ArrayList<SKCoordinate>();
        int i = 0;
        List<SKExtendedRoutePosition> positions = SKRouteManager.getInstance().getExtendedRoutePointsForRouteByUniqueId(routeID);
        for (SKExtendedRoutePosition pos : positions) {
            if (i % 2500 == 0) {
                coordinates.add(new SKCoordinate(pos.getCoordinate().getLongitude(), pos.getCoordinate().getLatitude()));
            }
            i++;
        }
        i--;
        if (i % 2500 != 0) {
            coordinates.add(new SKCoordinate(positions.get(i).getCoordinate().getLongitude(), positions.get(i).getCoordinate().getLatitude()));
        }
        return coordinates;
    }
}


