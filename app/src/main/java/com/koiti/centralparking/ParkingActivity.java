package com.koiti.centralparking;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;


public class ParkingActivity extends Activity {

    private JSONObject parking;
    private String jsonParking;
    private String phone;
    public static final String TAG = "ParkingActivity";

    public TextView name;
    public ImageView image;
    public TextView address;
    public TextView schedule;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parking);

        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        getActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        jsonParking = intent.getStringExtra("PARKING");

        try {
            // Get data
            parking = new JSONObject(jsonParking);

            name = (TextView) findViewById(R.id.name);
            name.setText(parking.getString("name"));

            image = (ImageView) findViewById(R.id.image);
            String url = parking.getString("image");
            if(url != null && !url.equals("")) {
//                try {
                    URL newurl = new URL(parking.getString("image"));
                    Bitmap mImage = BitmapFactory.decodeStream(newurl.openConnection().getInputStream());
                    image.setImageBitmap(mImage);
//                }catch (Exception e){ }
            }

            address = (TextView) findViewById(R.id.address);
            address.setText(parking.getString("address"));

            schedule = (TextView) findViewById(R.id.schedule);
            schedule.setText(parking.getString("schedule"));

            phone = parking.getString("phone");

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
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.action_call:
                if(phone != null) {
                    Intent callIntent = new Intent(Intent.ACTION_CALL);
                    callIntent.setData(Uri.parse("tel:" + phone.toString()));
                    startActivity(callIntent);
                }else{
                    Toast.makeText(this,R.string.action_call_no_phone, Toast.LENGTH_LONG).show();
                }
                return true;
            case R.id.action_map:
                Intent intent = new Intent(ParkingActivity.this, RouteActivity.class);
                intent.putExtra("PARKING", jsonParking);
                startActivity(intent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
