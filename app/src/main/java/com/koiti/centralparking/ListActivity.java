package com.koiti.centralparking;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.koiti.centralparking.adapters.ParkingAdapter;
import com.koiti.centralparking.models.Parking;
import com.koiti.centralparking.rest.ParkingController;
import com.koiti.centralparking.utils.ParkingProgressDialog;

import java.util.ArrayList;


public class ListActivity extends Activity {

    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        getActionBar().setDisplayHomeAsUpEnabled(true);

        listView = (ListView) findViewById(R.id.list_parking);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Parking parking = (Parking) listView.getItemAtPosition(position);
                Intent intent = new Intent(ListActivity.this, ParkingActivity.class);
                intent.putExtra("PARKING", parking.toJSON());
                startActivity(intent);
            }
        });

        // call AsynTask to perform network operation on separate thread
        new parkingSearchTask().execute();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_list, menu);
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
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void updateListView(ArrayList<Parking> tweets){
        listView.setAdapter(new ParkingAdapter(ListActivity.this, R.layout.row_list_parking, tweets));
    }

    private class parkingSearchTask extends AsyncTask<Object, Void, ArrayList<Parking>> {
        private ParkingProgressDialog progressDialog;

        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            progressDialog = new ParkingProgressDialog(ListActivity.this);
            progressDialog.setMessage(getString(R.string.msg_progress));
            progressDialog.show();
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
                updateListView(data);
            }
        }
    }
}
