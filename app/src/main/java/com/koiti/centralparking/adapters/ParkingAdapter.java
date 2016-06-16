package com.koiti.centralparking.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.koiti.centralparking.R;
import com.koiti.centralparking.models.Parking;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by @dropecamargo.
 */
public class ParkingAdapter extends RecyclerView.Adapter<ParkingAdapter.ViewHolder> {

//    private Context context;
//    private ArrayList<Parking> parking;

//    public ParkingAdapter(Context context, int viewResourceId, ArrayList<Parking> parking) {
//        super(context, viewResourceId, parking);
//        this.context = context;
//        this.parking = parking;
//    }
//
//    @Override
//    public View getView(int position, View convertView, ViewGroup parent){
//        if (convertView == null) {
//            convertView = LayoutInflater.from(context).inflate(R.layout.row_list_parking, parent, false);
//
//            ViewHolder viewHolder = new ViewHolder();
//            viewHolder.name = (TextView) convertView.findViewById(R.id.name);
//            convertView.setTag(viewHolder);
//        }
//
//        ViewHolder holder = (ViewHolder) convertView.getTag();
//        holder.name.setText(parking.get(position).getName());
//        return convertView;
//    }
    private Context context;
    private ArrayList<Parking> pusheenArrayList;
    private int itemLayout;


    public ParkingAdapter(Context context, ArrayList<Parking> data,  int itemLayout) {
        this.context = context;
        this.pusheenArrayList = data;
        this.itemLayout = itemLayout;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView image;
        public TextView name;
//        private TextView pasTime;

        public ViewHolder(View itemView) {

            super(itemView);
            image = (ImageView) itemView.findViewById(R.id.image);
            name = (TextView) itemView.findViewById(R.id.name);
//            pasTime = (TextView) itemView.findViewById(R.id.passtime);

        }
    }

    @Override
    public ParkingAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int i) {
        View itemLayoutView = LayoutInflater.from(parent.getContext()).inflate(itemLayout, parent, false);
        ViewHolder viewHolder = new ViewHolder(itemLayoutView);
        return viewHolder;
    }


    @Override
    public void onBindViewHolder(ParkingAdapter.ViewHolder viewHolder, int position) {

        Parking parking = pusheenArrayList.get(position);

        viewHolder.name.setText(parking.getName());
//        viewHolder.pasTime.setText(pusheen.getPasTime());
        String url = parking.getImage();
        if(url != null && !url.equals("")) {
            Picasso.with(this.context).load(url.trim())
                    .placeholder(R.drawable.melbourne_central)
                    .error(R.drawable.melbourne_central)
                    .into(viewHolder.image);
        }
//        if (pusheen.getId()!=null) {
//            switch (pusheen.getId()) {
//                case 1:
//                    viewHolder.image.setImageResource(R.drawable.pusheen);
//                    break;
//
//                case 2:
//                    viewHolder.image.setImageResource(R.drawable.pusheen2);
//                    break;
//
//                case 3:
//                    viewHolder.image.setImageResource(R.drawable.pusheen3);
//                    break;
//
//                case 4:
//                    viewHolder.image.setImageResource(R.drawable.pusheen4);
//                    break;
//
//                case 5:
//                    viewHolder.image.setImageResource(R.drawable.pusheen5);
//                    break;
//            }
//
//        }else{
//            viewHolder.image.setImageResource(R.drawable.pusheen);
//        }
        viewHolder.itemView.setTag(parking);
    }


    @Override
    public int getItemCount() {
        return this.pusheenArrayList.size();
    }
}
