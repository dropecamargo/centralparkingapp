package com.koiti.centralparking;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;


public class ParkingActivity extends Activity {

    private JSONObject parking;
    public static final String TAG = "ParkingActivity";

    public TextView name;
    public TextView address;
    public TextView schedule;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parking);

        Intent intent = getIntent();
        String jsonParking = intent.getStringExtra("PARKING");

        try {
            // Get data
            parking = new JSONObject(jsonParking);

            name = (TextView) findViewById(R.id.name);
            name.setText(parking.getString("name"));

            address = (TextView) findViewById(R.id.address);
            address.setText(parking.getString("address"));

            schedule = (TextView) findViewById(R.id.schedule);
            schedule.setText(parking.getString("schedule"));
        } catch (JSONException e) {
            Log.e(TAG, "JSONObject parking error: " + Log.getStackTraceString(e));
        } catch (Exception e) {
            Log.e(TAG, "Error: " + Log.getStackTraceString(e));
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_parking, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
