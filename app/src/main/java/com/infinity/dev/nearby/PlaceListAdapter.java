package com.infinity.dev.nearby;

import android.content.Context;
import android.graphics.Color;
import android.location.Location;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import java.util.List;

public class PlaceListAdapter extends RecyclerView.Adapter<PlaceListAdapter.ViewHolder> {

    Context context;
    List<PlaceBean> list;
    Location loc;

    static class ViewHolder extends RecyclerView.ViewHolder{
        TextView name;
        RatingBar rating;
        TextView address;
        TextView isOpen;
        TextView distance;
        TextView time;

        public ViewHolder(View itemView) {
            super(itemView);
            name = (TextView)itemView.findViewById(R.id.name);
            rating = (RatingBar)itemView.findViewById(R.id.rating);
            address = (TextView)itemView.findViewById(R.id.address);
            isOpen = (TextView)itemView.findViewById(R.id.isOpen);
            distance = (TextView)itemView.findViewById(R.id.distance);
            time = (TextView)itemView.findViewById(R.id.time);
        }
    }

    public PlaceListAdapter(Context context, List<PlaceBean> list, Location loc){
        this.context = context;
        this.list = list;
        this.loc = loc;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.places_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.name.setText(list.get(position).getName());
        holder.rating.setRating(list.get(position).getRating());
        if(list.get(position).isOpen()) {
            holder.isOpen.setTextColor(Color.parseColor("#2E7D32"));
            holder.isOpen.setText("Currently Open");
        }
        else {
            holder.isOpen.setTextColor(Color.parseColor("#D50000"));
            holder.isOpen.setText("Currently Closed");
        }
        holder.address.setText(list.get(position).getVicinity());
        double distance = distance(loc.getLatitude(), loc.getLongitude(), list.get(position).getLatitude(), list.get(position).getLongitude(), "K");
        holder.time.setText(String.format("%.2f", ((distance / 5) * 60)) + " min");
        if(distance < 1)
            holder.distance.setText(String.format("%.0f", distance * 1000) + " m");
        else
            holder.distance.setText(String.format("%.2f", distance) + " km");
    }

    private static double distance(double lat1, double lon1, double lat2, double lon2, String unit) {
        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;
        if (unit == "K") {
            dist = dist * 1.609344;
        } else if (unit == "N") {
            dist = dist * 0.8684;
        }

        return (dist);
    }

    private static double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    private static double rad2deg(double rad) {
        return (rad * 180 / Math.PI);
    }
}

