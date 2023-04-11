package com.ssdiscusskiny.adapters;

import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.ssdiscusskiny.R;
import com.ssdiscusskiny.data.Story;
import com.ssdiscusskiny.fragments.ReadFragment;

import java.util.Arrays;
import java.util.List;

public class StoryAdapter extends RecyclerView.Adapter<StoryAdapter.Holder> {
    final String TAG = StoryAdapter.class.getSimpleName();
    private final List<Story> stories;
    private final AppCompatActivity activity;

    private boolean nightMode;

    public StoryAdapter(AppCompatActivity activity, List<Story> stories, boolean nightMode) {
        this.activity = activity;
        this.stories = stories;
        this.nightMode = nightMode;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.content_dashboard, parent, false);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        final Story story = stories.get(position);

        holder.titleTv.setText(story.getTitle());

        Picasso.get()
                .load(story.getImageUrl())
                .error(R.mipmap.default_story_image)
                .into(holder.storyCover, new Callback() {
                    @Override
                    public void onSuccess() {
                        Log.v(TAG, "IMAGE LOADED");
                    }

                    @Override
                    public void onError(Exception e) {
                        Log.v(TAG, "IMAGE LOAD FAILED");
                    }
                });

    }

    @Override
    public int getItemCount() {
        return stories.size();
    }

    class Holder extends RecyclerView.ViewHolder{

        private ImageView storyCover;
        private TextView titleTv;
        public Holder(@NonNull View itemView) {
            super(itemView);
            storyCover = itemView.findViewById(R.id.item_icon);
            titleTv = itemView.findViewById(R.id.item_title);

            itemView.setOnClickListener(v->{
                final Story story = stories.get(getAdapterPosition());
                Log.v(TAG, Arrays.asList(story.getKeys()).toString());
                //Fire this key to read fragment

                activity.getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_holder, new ReadFragment().newInstance(story))
                        .addToBackStack(null)
                        .commit();

                itemView.setBackgroundColor(Color.parseColor("#424242"));
                if (nightMode) titleTv.setTextColor(Color.WHITE);
                else titleTv.setTextColor(Color.BLACK);

            });
        }
    }
}
