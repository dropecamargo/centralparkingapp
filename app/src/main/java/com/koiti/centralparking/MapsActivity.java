package com.koiti.centralparking;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.koiti.centralparking.models.Parking;
import com.koiti.centralparking.rest.ParkingController;
import com.koiti.centralparking.utils.ConstantsUtils;
import com.koiti.centralparking.utils.ParkingProgressDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MapsActivity extends FragmentActivity {

    public static final String TAG = "MapsActivity";

    private GoogleMap mMap;
    private LocationManager locationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        setUpMapIfNeeded();

        if (googlePlayServices()) {
            // UISettings map
            mMap.getUiSettings().setZoomControlsEnabled(true);

            // Enable current location
            mMap.setMyLocationEnabled(true);

            // Get LocationManager object from System Service LOCATION_SERVICE
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

            // Get Current Location
            Criteria criteria = new Criteria();
            String provider = locationManager.getBestProvider(criteria, true);
            Location currentLocation = locationManager.getLastKnownLocation(provider);

            if (currentLocation != null) {
                // Show the current location in Google Map
                mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude())));
            }

            // Zoom in the Google Map
            mMap.animateCamera(CameraUpdateFactory.zoomTo(13));

            mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(final Marker marker) {
                    Intent intent = new Intent(MapsActivity.this, ParkingActivity.class);
                    intent.putExtra("PARKING", marker.getSnippet());
                    startActivity(intent);
                    return true;
                }
            });


                // call AsynTask to perform network operation on separate thread
            new parkingSearchTask().execute();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
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
        mMap.addMarker(new MarkerOptions().position(new LatLng(0, 0)).title("Marker"));
    }

    private void updateMap(ArrayList<Parking> data){
        for (Parking parking : data) {
            if(parking.getLatitude() != null && parking.getLongitude() != null) {
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(new LatLng(parking.getLatitude(), parking.getLongitude()));
                markerOptions.title(parking.getName());
                markerOptions.snippet(parking.toJSON());
                mMap.addMarker(markerOptions);
            }
        }
    }

    private class parkingSearchTask extends AsyncTask<Object, Void, ArrayList<Parking>> {
        private ParkingProgressDialog progressDialog;

        @Override
        protected void onPreExecute(){
            progressDialog = new ParkingProgressDialog(MapsActivity.this);
            progressDialog.setMessage(getString(R.string.msg_progress));
            progressDialog.show();
            super.onPreExecute();
        }

        @Override
        protected ArrayList<Parking> doInBackground(Object... param) {
//            if(NetworkUtils.haveNetworkConnection(MapsActivity.this)){
            return ParkingController.get();
//            }else{
//                return dbOperations.getStatusUpdates();
//            }
        }

        @Override
        protected void onPostExecute(ArrayList<Parking> data){
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

            if (!data.isEmpty()) {
                updateMap(data);
            }
        }
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
}
