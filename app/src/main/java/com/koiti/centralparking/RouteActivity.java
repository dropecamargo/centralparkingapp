package com.koiti.centralparking;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.koiti.centralparking.rest.DirectionController;
import com.koiti.centralparking.utils.ConstantsUtils;
import com.koiti.centralparking.utils.DirectionsJSONParser;
import com.koiti.centralparking.utils.ParkingProgressDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class RouteActivity extends FragmentActivity implements LocationListener {

    private JSONObject parking;
    private String jsonParking;

    public String name;
    public Double latitude_dest = 0.0;
    public Double longitude_dest = 0.0;

    public static final String TAG = "RouteActivity";

    private GoogleMap mMap;
    private LocationManager locationManager;
    public Location location;
    public String provider;

    // The minimum distance to change updates in metters
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 0;

    // The minimum time beetwen updates in milliseconds
    private static final long MIN_TIME_BW_UPDATES = 0;
    // flag for GPS Status
    boolean isGPSEnabled = false;
    // flag for network status
    boolean isNetworkEnabled = false;

    public Double latitude = 0.0;
    public Double longitude = 0.0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        getActionBar().setDisplayHomeAsUpEnabled(true);

        // Get LocationManager object from System Service LOCATION_SERVICE
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            // Enable GPS
            disabledGPSAlert();
        }

        Intent intent = getIntent();
        jsonParking = intent.getStringExtra("PARKING");

        try {
            // Get data
            parking = new JSONObject(jsonParking);
            name = parking.getString("name");

            // Get Destination
            latitude_dest = parking.getDouble("latitude");
            longitude_dest = parking.getDouble("longitude");

            Criteria criteria = new Criteria();
            provider = locationManager.getBestProvider(criteria, false);

            // Get LocationManager object from System Service LOCATION_SERV
            locationManager = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);

            // getting GPS status
            isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

            // getting network status
            isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            // Get Current Location
            if (!isGPSEnabled && !isNetworkEnabled) {
                // location service disabled
                Log.d(TAG, "GPSEnabled false isNetworkEnabled false");
                Log.d(TAG, "GPSEnabled provider "+provider);
                if(provider!=null && !provider.equals("")) {
                    location = locationManager.getLastKnownLocation(provider);
                    RouteActivity.this.updateGPSCoordinates(location);
                }
            } else {
                // if GPS Enabled get lat/long using GPS Services
                if (isGPSEnabled) {
                    locationManager.requestLocationUpdates(
                            LocationManager.GPS_PROVIDER, MIN_TIME_BW_UPDATES,
                            MIN_DISTANCE_CHANGE_FOR_UPDATES, this);

                    Log.d("GPS Enabled", "GPS Enabled");

                    if (locationManager != null) {
                        location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                        RouteActivity.this.updateGPSCoordinates(location);
                    }
                }

                // First get location from Network Provider
                if (isNetworkEnabled) {
                    if (location == null) {
                        locationManager.requestLocationUpdates(
                                LocationManager.NETWORK_PROVIDER,
                                MIN_TIME_BW_UPDATES,
                                MIN_DISTANCE_CHANGE_FOR_UPDATES, this);

                        Log.d("Network", "Network");

                        if (locationManager != null) {
                            location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                            RouteActivity.this.updateGPSCoordinates(location);
                        }
                    }

                }
            }

            setUpMapIfNeeded();
            if (googlePlayServices()) {
                // UISettings map
                mMap.getUiSettings().setZoomControlsEnabled(true);

                // Enable current location
                mMap.setMyLocationEnabled(true);

                Log.d(TAG, "Data "+(latitude+ " - "+longitude+ " - "+latitude_dest+ " - "+longitude_dest));

                if (latitude != null && longitude != null) {
                    // Show the current location in Google Map
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(location.getLatitude(), location.getLongitude())));

                    // Getting URL to the Google Directions //API
                    String url = DirectionController.getDirectionsUrl(new LatLng(latitude, longitude), new LatLng(latitude_dest, longitude_dest));

                    // Start downloading json data from Google Directions API
                    new DirectionSearchTask().execute(url.toString());
                }

                // Zoom in the Google Map
                mMap.animateCamera(CameraUpdateFactory.zoomTo(13));
            }
        } catch (JSONException e) {
            Toast.makeText(RouteActivity.this, R.string.null_pointer_exception, Toast.LENGTH_LONG).show();
            RouteActivity.this.finish();
            Log.e(TAG, "JSONObject parking error: " + Log.getStackTraceString(e));
        } catch (Exception e) {
            Toast.makeText(RouteActivity.this, R.string.null_pointer_exception, Toast.LENGTH_LONG).show();
            RouteActivity.this.finish();
            Log.e(TAG, "Error: " + Log.getStackTraceString(e));
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
        locationManager.requestLocationUpdates(provider, 400, 1, this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        locationManager.removeUpdates(this);
    }

    /**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated.. This will ensure that we only ever
     * call {@link #setUpMap()} once when {@link #mMap} is not null.
     * <p/>
     * If it isn't installed {@link SupportMapFragment} (and
     * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt for the user to
     * install/update the Google Play services APK on their device.
     * <p/>
     * A user can return to this FragmentActivity after following the prompt and correctly
     * installing/updating/enabling the Google Play services. Since the FragmentActivity may not
     * have been completely destroyed during this process (it is likely that it would only be
     * stopped or paused), {@link #onCreate(Bundle)} may not be called again so we should call this
     * method in {@link #onResume()} to guarantee that it will be called.
     */
    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
        }
    }

    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a marker near Africa.
     * <p/>
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */
    private void setUpMap() {
        mMap.addMarker(new MarkerOptions().position(new LatLng(latitude_dest, longitude_dest)).title(name));
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
        }
        return super.onOptionsItemSelected(item);
    }

    public void updateGPSCoordinates(Location location) {
        if (location != null) {
            latitude = location.getLatitude();
            longitude = location.getLongitude();
        }
    }


    @Override
    public void onLocationChanged(Location location) {
        RouteActivity.this.updateGPSCoordinates(location);
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }

    private boolean googlePlayServices() {
        int isAvailable = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (isAvailable == ConnectionResult.SUCCESS) {
            return true;
        }else if (GooglePlayServicesUtil.isUserRecoverableError(isAvailable)) {
            GooglePlayServicesUtil.getErrorDialog(isAvailable, this, ConstantsUtils.GPS_ERRORDIALOG_REQUEST).show();
        }else {
            Toast.makeText(getApplicationContext(), R.string.exception_gps, Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    private void disabledGPSAlert(){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(RouteActivity.this);
        alertDialog.setTitle(R.string.action_gps_title);
        alertDialog.setMessage(R.string.action_gps_message);
        alertDialog.setPositiveButton(R.string.config, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent callGPSSettingIntent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(callGPSSettingIntent);
            }
        });
        alertDialog.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        alertDialog.show();
    }

    private class DirectionSearchTask extends AsyncTask<String, Void, String> {
        private ParkingProgressDialog progressDialog;

        @Override
        protected void onPreExecute(){
            progressDialog = new ParkingProgressDialog(RouteActivity.this);
            progressDialog.setMessage(getString(R.string.msg_progress));
            progressDialog.show();
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... param) {
            // if(NetworkUtils.haveNetworkConnection(MapsActivity.this)){
            // Fetching the data from web service
            return DirectionController.get(param[0]);

            // }else{
            //    return dbOperations.getStatusUpdates();
            // }
        }

        @Override
        protected void onPostExecute(String data){
            // Dialog dismiss
            try {
                if ((this.progressDialog != null) && this.progressDialog.isShowing()) {
                    this.progressDialog.dismiss();
                }
            } catch (final IllegalArgumentException e) {
            } catch (final Exception e) {
            } finally {
                this.progressDialog = null;
            }
            super.onPostExecute(data);

            if (!data.isEmpty()) {
                // Invokes the thread for parsing the JSON data
                new DirectionParserTask().execute(data);
            }
        }
    }

    /** A class to parse the Google Places in JSON format */
    private class DirectionParserTask extends AsyncTask<String, Integer, List<List<HashMap<String,String>>> > {
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {
            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try{
                jObject = new JSONObject(jsonData[0]);
                DirectionsJSONParser parser = new DirectionsJSONParser();

                // Starts parsing data
                routes = parser.parse(jObject);
            }catch(Exception e){
                return null;
            }
            return routes;
        }

        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {

            ArrayList<LatLng> points = null;
            PolylineOptions lineOptions = null;
            MarkerOptions markerOptions = new MarkerOptions();

            // Traversing through all the routes
            for(int i=0;i<result.size();i++){
                points = new ArrayList<LatLng>();
                lineOptions = new PolylineOptions();

                // Fetching i-th route
                List<HashMap<String, String>> path = result.get(i);

                // Fetching all the points in i-th route
                for(int j=0;j<path.size();j++){
                    HashMap<String,String> point = path.get(j);

                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);

                    points.add(position);
                }

                // Adding all the points in the route to LineOptions
                lineOptions.addAll(points);
                lineOptions.width(5);
                lineOptions.color(Color.RED);

            }

            if(lineOptions != null) {
                // Drawing polyline in the Google Map for the i-th route
                mMap.addPolyline(lineOptions);
                // Display alert message
                Toast.makeText(RouteActivity.this, R.string.action_route_alert, Toast.LENGTH_LONG).show();
            }
        }
    }
}
