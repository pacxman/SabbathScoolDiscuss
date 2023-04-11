package com.ssdiscusskiny.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;
import com.ssdiscusskiny.R;
import com.ssdiscusskiny.data.ChildItem;
import com.ssdiscusskiny.fragments.TopicFragment;
import com.ssdiscusskiny.tools.RoundedCorners;
import com.ssdiscusskiny.utils.MaskTransformation;

import java.util.List;

public class ChildAdapter extends RecyclerView.Adapter<ChildAdapter.ViewHolder>{

    private final List<ChildItem> list;
    private final Context mContext;

    private final Transformation transformation;

    private AppCompatActivity activity;


    public ChildAdapter(Context mContext, AppCompatActivity activity, List<ChildItem> list){
        this.mContext = mContext;
        this.list = list;
        this.transformation = new MaskTransformation(mContext, R.drawable.rounded_convers_transformation);
        this.activity = activity;
    }

    @NonNull
    @Override
    public ChildAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.m_adapter_view, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChildAdapter.ViewHolder holder, int position) {

        ChildItem childItem = list.get(position);
        holder.imageView.setTag(position);

        // sets the image to the imageview from our itemHolder class

        loadQImage(list.get(position).getFirstKey(), list.get(position).getSecondKey(), FirebaseStorage.getInstance(), holder.imageView);

        //holder.imageView.setImageResource();
        //Use library to load bitmap

        holder.textView.setText(childItem.getName());

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        public ImageView imageView;
        public TextView textView;

        public ViewHolder(View v){
            super(v);

            imageView = v.findViewById(R.id.cover_view);
            textView = v.findViewById(R.id.m_desc_tv);

            v.setOnClickListener(view->{

                //Load a fragment


                ChildItem childDataItem = list.get(getAdapterPosition());
                TopicFragment topicFragment = TopicFragment.newInstance(childDataItem.getFirstKey(), childDataItem.getSecondKey(), childDataItem.getName());

                activity.getSupportFragmentManager().beginTransaction()
                        .replace(R.id.library_fragment_frame, topicFragment)
                        .addToBackStack(null)
                        .commit();
            });

        }

    }


    private void loadQImage(String year, String quarter, FirebaseStorage storage, ImageView imageView) {

        final StorageReference imageRef = storage.getReference().child("images").child(year).child(quarter).child("cover.jpg");

        Log.v("HOME_IMAGE", imageRef.toString());

        final SharedPreferences urlPrefs = mContext.getSharedPreferences("urls", Context.MODE_PRIVATE);

        final String imageKey = urlKey(year, quarter);

        final String url = urlPrefs.getString(imageKey, "default");

        Picasso.get()
                .load(url)
                .error(R.mipmap.l_default)
                .placeholder(R.mipmap.image_placeholder)
                .transform(new RoundedCorners())
                .into(imageView, new Callback() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError(Exception e) {

                    }
                });

        imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
            //Pass it to Picasso to download, show in ImageView and caching
            //Save this image link as it may be used even offline
            final String uriUrl = String.valueOf(uri);
            if (!url.equals(uriUrl)) {
                //
                Log.v("HOMEIMAGE", "URL CHANGE DETECTED");
                urlPrefs.edit()
                        .putString(imageKey, uriUrl).apply();
                Picasso.get()
                        .load(uriUrl)
                        .error(R.mipmap.default_cover)
                        .placeholder(R.mipmap.image_placeholder)
                        .transform(new RoundedCorners())
                        .into(imageView, new Callback() {
                            @Override
                            public void onSuccess() {

                            }

                            @Override
                            public void onError(Exception e) {

                            }
                        });
            } else {
                Log.v("HOMEIMAGE", "URL DIDN'T CHANGE");
            }

        }).addOnFailureListener(exception -> {
            // Handle any errors
            exception.printStackTrace();
            Log.v("HOMEIMAGE", "FAILED " + exception.getMessage());
        });

    }

    private String urlKey(String year, String quarter) {
        return year + "_" + quarter +"_"+"cover";
    }

}
