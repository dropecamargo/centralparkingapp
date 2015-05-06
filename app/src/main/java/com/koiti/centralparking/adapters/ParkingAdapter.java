package com.koiti.centralparking.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.koiti.centralparking.R;
import com.koiti.centralparking.models.Parking;

import java.util.ArrayList;

/**
 * Created by @dropecamargo.
 */
public class ParkingAdapter extends ArrayAdapter<Parking> {

    private Context context;
    private ArrayList<Parking> parking;

    public ParkingAdapter(Context context, int viewResourceId, ArrayList<Parking> parking) {
        super(context, viewResourceId, parking);
        this.context = context;
        this.parking = parking;
    }

    static class ViewHolder{
        public TextView name;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.row_list_parking, parent, false);

            ViewHolder viewHolder = new ViewHolder();
            viewHolder.name = (TextView) convertView.findViewById(R.id.name);
            convertView.setTag(viewHolder);
        }

        ViewHolder holder = (ViewHolder) convertView.getTag();
        holder.name.setText(parking.get(position).getName());
        return convertView;
    }
}
