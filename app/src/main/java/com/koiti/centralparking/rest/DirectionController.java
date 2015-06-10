package com.koiti.centralparking.rest;

import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by @dropecamargo.
 */
public class DirectionController {

    public static String getDirectionsUrl(LatLng origin, LatLng dest){

        // Origin of route
        String str_origin = "origin="+origin.latitude+","+origin.longitude;
        // Destination of route
        String str_dest = "destination="+dest.latitude+","+dest.longitude;

        // Sensor enabled
        String sensor = "sensor=false";
        // Building the parameters to the web service
        String parameters = str_origin+"&"+str_dest+"&"+sensor;

        // Output format
        String output = "json";
        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/"+output+"?"+parameters;
        return url;
    }

    public static String get(String urlDirection) {
        String data = "";

        HttpURLConnection httpConnection = null;
        BufferedReader bufferedReader = null;
        StringBuilder response = new StringBuilder();

        try{
            URL url = new URL(urlDirection);
            httpConnection = (HttpURLConnection) url.openConnection();
            httpConnection.setRequestMethod("GET");

            httpConnection.setRequestProperty("Content-Type", "application/json");
            httpConnection.connect();

            bufferedReader = new BufferedReader(new InputStreamReader(httpConnection.getInputStream()));

            String line;
            while ((line = bufferedReader.readLine()) != null){
                response.append(line);
            }
            data = response.toString();

            bufferedReader.close();
        }catch(Exception e){
            Log.d("While downloading", e.toString());
        }finally{
            if(httpConnection != null){
                httpConnection.disconnect();
            }
        }
        return data;
    }
}