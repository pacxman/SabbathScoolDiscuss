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

import com.ssdiscusskiny.data.ContentDataItem;
import com.ssdiscusskiny.R;
import com.ssdiscusskiny.fragments.TitleFragment;

import java.util.List;

public class ContentAdapter extends RecyclerView.Adapter<ContentAdapter.ViewHolder>{

    private final Context mContext;
    private final List<ContentDataItem> mList;
    private AppCompatActivity activity;


    public ContentAdapter(Context mContext, AppCompatActivity activity, List<ContentDataItem> mList){
        this.mContext = mContext;
        this.mList = mList;
        this.activity = activity;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.content_dashboard, parent, false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        final ContentDataItem contentDataItem = mList.get(position);

        holder.itemTitle.setText(contentDataItem.getItemTitle());
        Log.v("DATAHOLDER", contentDataItem.getFirstKey()+" "+contentDataItem.getSecondKey()+" "+contentDataItem.getThirdKey());
        loadImage(contentDataItem.getFirstKey(), contentDataItem.getSecondKey(), contentDataItem.getThirdKey(), FirebaseStorage.getInstance(), holder.itemIcon);

    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        public ImageView itemIcon;
        public TextView itemTitle;

        ViewHolder(View v){
           super(v);

           itemIcon = v.findViewById(R.id.item_icon);
           itemTitle = v.findViewById(R.id.item_title);
           v.setOnClickListener(view -> {
               final ContentDataItem contentDataItem = mList.get(getAdapterPosition());
               TitleFragment titleFragment = TitleFragment.newInstance(contentDataItem.getFirstKey(), contentDataItem.getSecondKey(), contentDataItem.getThirdKey(), contentDataItem.getItemTitle());
               activity.getSupportFragmentManager().beginTransaction()
                       .replace(R.id.library_fragment_frame, titleFragment)
                       .addToBackStack(null)
                       .commit();
           });
        }
    }

    private void loadImage(String key1, String key2, String key3, FirebaseStorage storage, ImageView imageView){

        Log.v("CONTENTADAP", key1+" "+key2+" "+key3);
        final StorageReference imageRef = storage.getReference().child("images").child(key1).child(key2).child(key3+".jpg");
        Log.v("HOMEIMAGE", imageRef.toString());

        final SharedPreferences urlPrefs = mContext.getSharedPreferences("urls", Context.MODE_PRIVATE);

        final String imageKey = urlKey(key1, key2, key3);

        final String url = urlPrefs.getString(imageKey, "default");

        Picasso.get()
                .load(url)
                .error(R.mipmap.default_image)
                .into(imageView, new Callback() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError(Exception e) {

                    }
                });

        imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
            //Save this image link as it may be used even offline
            final String uriUrl = String.valueOf(uri);
            if (!url.equals(uriUrl)){
                //
                Log.v("HOMEIMAGE", "URL CHANGE DETECTED");
                urlPrefs.edit()
                        .putString(imageKey, uriUrl).apply();
                Picasso.get()
                        .load(uriUrl)
                        .error(R.mipmap.default_image)
                        .into(imageView, new Callback() {
                            @Override
                            public void onSuccess() {

                            }

                            @Override
                            public void onError(Exception e) {

                            }
                        });
            }else{
                Log.v("HOMEIMAGE", "URL DIDN'T CHANGE");
            }

        }).addOnFailureListener(exception -> {
            // Handle any errors
            Log.v("HOMEIMAGE", "FAILED "+exception.getMessage());
        });

    }

    private String urlKey(String s1, String s2, String s3){
        return s1+"_"+s2+"_"+s3;
    }

}
