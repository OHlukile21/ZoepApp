package com.zoepapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;

import java.util.List;

public class FacilityAdapter extends RecyclerView.Adapter<FacilityAdapter.ViewHolder> {
    private List<ModelFacility> items;
    private int selectedItem = -1;
    Context context;
    private OnSiteClickListener listener;

    public FacilityAdapter(Context context, List<ModelFacility> items, OnSiteClickListener listener) {
        this.items = items;
        this.context = context;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_site, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ModelFacility item = items.get(position);

        RequestOptions requestOptions = new RequestOptions()
                .fitCenter()
                .placeholder(R.drawable.loading) // A local GIF in res/drawable
                .error(R.drawable.no_image)
                .fallback(R.drawable.no_image)  // Fallback image for errors
                .transform(new RoundedCorners(25)); // Apply rounded corners


        Glide.with(context)
                .load("")
                .apply(requestOptions)
                .into(holder.imgSite);



        holder.txtTitle.setText(item.getName());
        holder.txtType.setText("Property Type: "+item.getType());
        holder.txtAddress.setText("Address: "+item.getAddress());
        holder.txtNextInspectionDate.setText("Next Inspection: ");
        holder.txtLastInspectionDate.setText("Last Inspection: ");
        holder.txtNumberOfDefects.setText("Number of Incidents: "+item.getNumOfIncidents());

        holder.bind(item, listener);

    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtTitle,txtAddress,txtType,txtLastInspectionDate,txtNextInspectionDate,txtNumberOfDefects;

        ImageView imgSite;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtTitle = itemView.findViewById(R.id.txtTitle);
            txtAddress = itemView.findViewById(R.id.txtAddress);
            txtType = itemView.findViewById(R.id.txtType);
            txtNextInspectionDate = itemView.findViewById(R.id.txtNextInspectionDate);
            txtLastInspectionDate = itemView.findViewById(R.id.txtLastInspectionDate);
            txtNumberOfDefects = itemView.findViewById(R.id.txtNumberOfDefects);
            imgSite = itemView.findViewById(R.id.imgSite);
        }

        public void bind(ModelFacility site, OnSiteClickListener listener) {
            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.OnSiteClick(site);
                }
            });
        }
    }
}
