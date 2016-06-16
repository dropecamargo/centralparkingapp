package com.koiti.centralparking;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;
import java.util.StringTokenizer;


public class ParkingActivity extends ActionBarActivity {

    private JSONObject parking;
    private String jsonParking;
    private String phone;
    public static final String TAG = "ParkingActivity";

    public TextView name;
    public ImageView image;
    public TextView address;
    public TextView schedule;
    public TextView capacity;
    public TextView availability;
    public TextView rates;
    public TextView agreement;
    public TextView monthly;
    public TextView information;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parking);

        setToolbar();

        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

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
                Picasso.with(this).load(url.trim())
                    .placeholder(R.drawable.melbourne_central)
                    .error(R.drawable.melbourne_central)
                    .into(image);
            }

            address = (TextView) findViewById(R.id.address);
            address.setText(parking.getString("address"));

            schedule = (TextView) findViewById(R.id.schedule);
            schedule.setText(parking.getString("schedule"));

            phone = parking.getString("phone");

            capacity = (TextView) findViewById(R.id.capacity);
            capacity.setText(parking.getString("capacity"));

            availability = (TextView) findViewById(R.id.availability);
            availability.setText(parking.getString("availability"));

            information = (TextView) findViewById(R.id.information);
            StringBuffer info = new StringBuffer();
            info.append("Email: ").append(parking.getString("email")).append("\n");
            info.append("Teléfono: ").append(phone);
            information.setText(info.toString());

            rates = (TextView) findViewById(R.id.rates);
            rates.setText(parking.getString("rates"));

            agreement = (TextView) findViewById(R.id.agreement);
            agreement.setText( parking.getString("agreement").equals("1") ? "SI" : "NO" );

            monthly = (TextView) findViewById(R.id.monthly);
            monthly.setText( parking.getString("monthly").equals("1") ? "SI" : "NO" );
        } catch (JSONException e) {
            Log.e(TAG, "JSONObject parking error: " + Log.getStackTraceString(e));
        } catch (Exception e) {
            Log.e(TAG, "Error: " + Log.getStackTraceString(e));
        }
    }

    public void setToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.activity_toolbar);
        if (null != toolbar) {
            toolbar.setTitle(R.string.title_activity_list);
            toolbar.setNavigationIcon(R.drawable.abc_ic_ab_back_mtrl_am_alpha);

            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    NavUtils.navigateUpFromSameTask(ParkingActivity.this);
                }
            });
        }
        setSupportActionBar(toolbar);
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
