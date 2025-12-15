package com.zoepapp;

import android.content.Context;
import android.text.Html;
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
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.stfalcon.imageviewer.StfalconImageViewer;

import java.util.List;

public class SiteincidentAdapter extends RecyclerView.Adapter<SiteincidentAdapter.ViewHolder> {

    private List<ModelSiteIncident> items;
    private int selectedItem = -1;
    Context context;
    private OnIncidentLongClickListener listener;

    public SiteincidentAdapter(Context context, List<ModelSiteIncident> items, OnIncidentLongClickListener listener) {
        this.items = items;
        this.context = context;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_incident, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        ModelSiteIncident item = items.get(position);

        RequestOptions requestOptions = new RequestOptions()
                .fitCenter()
                .placeholder(R.drawable.loading)
                .error(R.drawable.no_image)
                .fallback(R.drawable.no_image)
                .transform(new RoundedCorners(25));

        Glide.with(context)
                .load(item.getImageThumbnail())
                .apply(requestOptions)
                .into(holder.imgIncident);

        holder.txtFindings.setText(
                Html.fromHtml("<b>Findings: </b>" + item.getFindings())
        );
        holder.txtRemedialActions.setText(
                Html.fromHtml("<b>Remedial Actions: </b>" + item.getRemedialAction())
        );
        holder.txtTimeFrame.setText(
                Html.fromHtml("<b>Time Frame: </b>" + item.getTimeFrame())
        );
        holder.txtSeverity.setText(
                Html.fromHtml("<b>Severity: </b>" + item.getSeverity())
        );

        // Full screen image
        holder.imgIncident.setOnClickListener(v -> {
            if (item.getImageThumbnail() != null && !item.getImageThumbnail().isEmpty()) {
                new StfalconImageViewer.Builder<>(
                        context,
                        new String[]{item.getImageThumbnail()},
                        (imageView, image) ->
                                Glide.with(context)
                                        .load(item.getImage())
                                        .into(imageView)
                ).show();
            }
        });

        // 🔔 REQUEST REVIEW BUTTON LOGIC (THIS IS THE IMPORTANT PART)
        holder.btnRequestReview.setOnClickListener(v -> {

            Snackbar.make(
                    holder.itemView,
                    "⚠ Requested reviews from Facility Managers may carry an extra charge.",
                    Snackbar.LENGTH_LONG
            ).setAction("CONTINUE", actionView -> {
                // TODO: Call request review API here
                // requestIncidentReview(item.getSiteIncidentID());
            }).show();
        });

        holder.itemView.setOnClickListener(v -> {
            selectedItem = position;
            notifyDataSetChanged();
        });

        holder.bind(item, listener);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    // ===================== VIEW HOLDER =====================
    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView txtFindings, txtRemedialActions, txtTimeFrame, txtSeverity;
        ImageView imgIncident;
        MaterialButton btnRequestReview; // ✅ DECLARED HERE

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            txtFindings = itemView.findViewById(R.id.txtFindings);
            txtSeverity = itemView.findViewById(R.id.txtSeverity);
            txtRemedialActions = itemView.findViewById(R.id.txtRemedialActions);
            txtTimeFrame = itemView.findViewById(R.id.txtTimeFrame);
            imgIncident = itemView.findViewById(R.id.imgIncident);

            btnRequestReview = itemView.findViewById(R.id.btnRequestReview); // ✅ BOUND HERE
        }

        public void bind(ModelSiteIncident incident, OnIncidentLongClickListener listener) {
            itemView.setOnLongClickListener(v -> {
                if (listener != null && incident != null) {
                    // listener.OnIncidentLongClick(incident);
                    return true;
                }
                return false;
            });
        }
    }
}
