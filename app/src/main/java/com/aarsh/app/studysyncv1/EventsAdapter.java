package com.aarsh.app.studysyncv1;

import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class EventsAdapter extends RecyclerView.Adapter<EventsAdapter.ViewHolder> {

    private List<EventItem> eventItems;
    private Handler handler = new Handler();

    public EventsAdapter(List<EventItem> eventItems) {
        this.eventItems = eventItems;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_event, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        EventItem eventItem = eventItems.get(position);
        holder.textView.setText(eventItem.getText());

        if (!TextUtils.isEmpty(eventItem.getImageUrl())) {
            Glide.with(holder.imageView.getContext()).load(eventItem.getImageUrl()).into(holder.imageView);
        }

        // Start auto-scrolling in the background
        startAutoScroll(holder.scrollView);
    }

    @Override
    public int getItemCount() {
        return eventItems.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textView;
        ImageView imageView;
        ScrollView scrollView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.textView);
            imageView = itemView.findViewById(R.id.imageView);
            scrollView = itemView.findViewById(R.id.scrollView);
        }
    }

    private void startAutoScroll(final ScrollView scrollView) {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // Use runOnUiThread to perform UI-related operations safely
                if (scrollView != null) {
                    scrollView.post(new Runnable() {
                        @Override
                        public void run() {
                            scrollView.scrollBy(0, 1); // You can adjust the scroll speed here
                        }
                    });
                }

                handler.postDelayed(this, 50); // Adjust the delay as needed
            }
        }, 100); // Initial delay before the first scroll
    }
}


