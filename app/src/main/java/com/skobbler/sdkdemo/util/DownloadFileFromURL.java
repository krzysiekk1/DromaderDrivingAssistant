package com.skobbler.sdkdemo.util;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import com.skobbler.sdkdemo.database.ResourcesDAO;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Arrays;

import javax.xml.transform.Result;

/**
 * Created by Jakub Solawa on 11.12.2016.
 */

public class DownloadFileFromURL extends AsyncTask {

    private ArrayList<String> urlArray = new ArrayList<>(Arrays.asList("https://www.dropbox.com/s/x31ys8324wwkpaw/avg_fuel_costs.csv?dl=1", "https://www.dropbox.com/s/o3ix0p8fzdmipcr/tolls.csv?dl=1", "https://www.dropbox.com/s/um1a2fu866m74lv/vignette_highways.csv?dl=1"));

    public InputStream connectAndDownload(String address){
        URL url = null;
        InputStream input = null;
        try {
            url = new URL(address);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        URLConnection connection = null;
        try {
            connection = url.openConnection();
            connection.connect();
            input = url.openStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return input;
    }


    @Override
    protected Object doInBackground(Object[] params) {
        InputStream inputStreamFuel = connectAndDownload(urlArray.get(0));
        InputStream inputStreamTolls = connectAndDownload(urlArray.get(1));
        InputStream inputStreamVignette = connectAndDownload(urlArray.get(2));

        ResourcesDAO resourcesDAO = ResourcesDAO.getInstance((Context) params[0]);
        resourcesDAO.updateDatabase(resourcesDAO.getDatabase(), inputStreamTolls, inputStreamVignette, inputStreamFuel);

        return null;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(Object o) {
        super.onPostExecute(o);
    }
}
