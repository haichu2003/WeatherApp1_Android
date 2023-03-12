package com.example.weathernow;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

//interface to listen to click event and send data
interface onCityClickListener {
    void onTextClick(String data);
}
public class weatherForecastAdapter extends RecyclerView.Adapter<weatherForecastVH> {
    List<String> items;
    onCityClickListener listener;

    public weatherForecastAdapter(List<String> items, onCityClickListener listener) {
        this.items = items;
        this.listener = listener;
    }

    @NonNull
    @Override
    public weatherForecastVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item, parent, false);
        return  new weatherForecastVH(view).linkAdapter(this);
    }

    @Override public void onBindViewHolder(@NonNull weatherForecastVH holder, int position) {

        //TODO: add image description of weather of each known city (in the future)

        holder.textView.setText(items.get(position));

        //listen to click event and send clicked city name to MenuActivity
        holder.itemView.findViewById(R.id.cityName).setOnClickListener(view -> listener.onTextClick(items.get(holder.getAdapterPosition()).toString()));
    }

    @Override public int getItemCount() {
        return this.items.size();
    }

}

class weatherForecastVH extends RecyclerView.ViewHolder {
    TextView textView;

    private weatherForecastAdapter adapter;

    public weatherForecastVH(@NonNull View itemView) {
        super(itemView);
        textView = itemView.findViewById(R.id.cityName);
        itemView.findViewById(R.id.cityName).setOnClickListener(view -> {
        });
    }

    public weatherForecastVH linkAdapter(@NonNull weatherForecastAdapter adapter) {
        this.adapter = adapter;
        return this;
    }
}