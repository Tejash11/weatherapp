package com.example.weatherapp;

import android.content.Context;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class weatheradapter extends RecyclerView.Adapter<weatheradapter.ViewHolder> {
    Context context;
    ArrayList<weathermodal> weathermodalArrayList;
    public weatheradapter(Context context, ArrayList<weathermodal> weathermodalArrayList) {
        this.context = context;
        this.weathermodalArrayList = weathermodalArrayList;
    }

    @NonNull
    @Override
    public weatheradapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.weather_recyclerview,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull weatheradapter.ViewHolder holder, int position) {
        weathermodal modal  = weathermodalArrayList.get(position);
        holder.temperature.setText(modal.getTemperature()+"°c");
        holder.windspeed.setText(modal.getWindspeed()+"km/h");
        Picasso.get().load("http:".concat(modal.getIcon())).into(holder.condition);
        SimpleDateFormat input = new SimpleDateFormat("yyyy-mm-dd hh:mm");
        SimpleDateFormat output = new SimpleDateFormat("hh:mm aa");
        try
        {
            Date t = input.parse(modal.getTime());
            holder.time.setText(output.format(t));
        }
        catch (ParseException e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return weathermodalArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView time, temperature, windspeed;
        ImageView condition;
        public ViewHolder(@NonNull View itemView)
        {
            super(itemView);
            time = itemView.findViewById(R.id.time);
            temperature = itemView.findViewById(R.id.temp);
            windspeed = itemView.findViewById(R.id.windspeed);
            condition = itemView.findViewById(R.id.condition);
        }
    }
}
