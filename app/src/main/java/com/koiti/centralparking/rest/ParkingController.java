package com.koiti.centralparking.rest;


import android.util.Log;

import com.koiti.centralparking.models.Parking;
import com.koiti.centralparking.utils.ConstantsUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by @dropecamargo.
 */
public class ParkingController {

    public static final String TAG = "ParkingController";

    public static ArrayList<Parking> get() {

        HttpURLConnection httpConnection = null;
        BufferedReader bufferedReader = null;
        StringBuilder response = new StringBuilder();

        ArrayList<Parking> data = new ArrayList<Parking>();
        try {
            URL url = new URL(ConstantsUtils.URL_GET_PARKING);
            httpConnection = (HttpURLConnection) url.openConnection();
            httpConnection.setRequestMethod("GET");

            httpConnection.setRequestProperty("Content-Type", "application/json");
            httpConnection.connect();
            bufferedReader = new BufferedReader(new InputStreamReader(httpConnection.getInputStream()));

            String line;
            while ((line = bufferedReader.readLine()) != null){
                response.append(line);
            }

            JSONArray jsonArray = new JSONArray(response.toString());

            JSONObject jsonObject;
            for (int i = 0; i < jsonArray.length(); i++) {
                jsonObject = (JSONObject) jsonArray.get(i);

                Parking parking = new Parking();
                parking.setId(jsonObject.getString("parq_id"));
                parking.setName(jsonObject.getString("parq_nombre"));
                parking.setImage(jsonObject.getString("parq_imagen"));
                parking.setAddress(jsonObject.getString("parq_direccion"));
                parking.setPhone(jsonObject.getString("parq_telefono"));
                parking.setAvailability(jsonObject.getInt("parq_cuposd"));
                parking.setCapacity(jsonObject.getInt("parq_cupost"));
                parking.setSchedule(jsonObject.getString("parq_horario"));
                parking.setLatitude(jsonObject.getDouble("parq_latitud"));
                parking.setLongitude(jsonObject.getDouble("parq_longitud"));
                data.add(i, parking);
            }
        } catch (Exception e) {
            Log.e(TAG, "GET error: " + Log.getStackTraceString(e));
        }finally {
            if(httpConnection != null){
                httpConnection.disconnect();
            }
        }
        return data;
    }
}
