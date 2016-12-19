package com.skobbler.sdkdemo.util;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.skobbler.ngx.SKCoordinate;
import com.skobbler.ngx.routing.SKExtendedRoutePosition;
import com.skobbler.ngx.routing.SKRouteManager;
import com.skobbler.sdkdemo.costs.utils.Road;
import com.skobbler.sdkdemo.database.ResourcesDAO;

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

    public InputStream connectAndDownload(String lat, String lon){
        URL url = null;
        try {
            url = new URL("http://api.openweathermap.org/data/2.5/weather?lat="+lat+"&lon="+lon+"&APPID=07c24c31c3d139a6e42c38d0cf05321f");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        HttpURLConnection connection = null;
        try {
            connection = (HttpURLConnection) url.openConnection();

        BufferedReader reader =
                new BufferedReader(new InputStreamReader(connection.getInputStream()));

        StringBuffer json = new StringBuffer(1024);
        String tmp = "";

        while((tmp = reader.readLine()) != null)
            json.append(tmp).append("\n");
        reader.close();
        JSONObject jsonobject = new JSONObject(json.toString());
        if (jsonobject != null) {
            data.add(jsonobject);
        }
        if(jsonobject.getInt("cod") != 200) {
            System.out.println("Cancelled");
            return null;
        }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();}
        return null;
    }


    @Override
    protected Object doInBackground(Object[] params) {
        Log.d("ODPALAM", "BEJBE");
        List<SKCoordinate> coordinates= getCoordinatesForWeather((int) params[0]);
        for (SKCoordinate coordinate : coordinates){
            connectAndDownload(String.valueOf(coordinate.getLatitude()), String.valueOf(coordinate.getLongitude()));
        }
//        connectAndDownload((String) params[0], (String) params[1]);
        return null;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(Object o) {
        if(data!=null){
            for(JSONObject datum : data) {
                Log.d("my weather received", datum.toString());
            }
        }
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


