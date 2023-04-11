package com.ssdiscusskiny.fragments;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.ssdiscusskiny.R;
import com.ssdiscusskiny.activities.StoryActivity;
import com.ssdiscusskiny.adapters.StoryAdapter;
import com.ssdiscusskiny.app.Variables;
import com.ssdiscusskiny.data.Story;
import com.ssdiscusskiny.utils.PanelHandler;

import java.util.ArrayList;
import java.util.List;

public class StoriesFragment extends Fragment {

    private final String TAG = StoriesFragment.class.getSimpleName();
    private RecyclerView recyclerView;
    private List<Story> stories = new ArrayList<>();
    private StoryAdapter storyAdapter;
    private MenuItem itemRefresh;
    private DatabaseReference referenceStories;

    private View pBarView;
    private AlertDialog alertProgressBar;

    private SharedPreferences preferences;

    public StoriesFragment() {
        // Required empty public constructor
    }


    public static StoriesFragment newInstance() {
        StoriesFragment fragment = new StoriesFragment();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        PreferenceManager.setDefaultValues(getContext(), R.xml.prefs, false);
        preferences = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
    }

    @Override
    public void onResume() {
        super.onResume();
        ((StoryActivity)getActivity()).getCollapsingToolbarLayout().setTitle(getString(R.string.inside_story));
        final ImageView homeImage = ((StoryActivity)getActivity()).getImageView();
        Picasso.get()
                .load(R.mipmap.story_home_image)
                .into(homeImage, new Callback() {
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View contentView = inflater.inflate(R.layout.fragment_stories, container, false);
        // Inflate the layout for this fragment
        recyclerView = contentView.findViewById(R.id.content_rec);

        pBarView = View.inflate(getContext(), R.layout.pbar_alertdialog, null);
        alertProgressBar = new AlertDialog.Builder(getContext()).create();
        alertProgressBar.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertProgressBar.setView(pBarView);

        storyAdapter = new StoryAdapter((AppCompatActivity)getActivity() , stories, preferences.getBoolean("night", false));

        referenceStories = FirebaseDatabase.getInstance()
                .getReference().child(Variables.content)
                .child("stories");

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(storyAdapter);

        if (preferences.getBoolean("night", false)) {
            recyclerView.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.background_dark));
        } else {
            recyclerView.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.background_light));
        }

        alertProgressBar.show();
        referenceStories.addListenerForSingleValueEvent(storyListener);


        return contentView;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.stories, menu);
        itemRefresh = menu.findItem(R.id.refresh);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                getActivity().finish();
                break;
            case R.id.refresh:
                if (itemRefresh!=null) itemRefresh.setVisible(false);
                alertProgressBar.show();
                referenceStories.addListenerForSingleValueEvent(storyListener);
                break;
            case R.id.story:
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_holder, new ReadFragment()).addToBackStack( "read" ).commit();
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    ValueEventListener storyListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {

            stories.clear();

            for (DataSnapshot child : snapshot.getChildren()){
                final String key1 = child.getKey();
                Log.v(TAG, "K1: "+key1);

                if (!key1.equals("story")){
                    for (DataSnapshot child2 : child.getChildren()){
                        final String key2 = child2.getKey();
                        Log.v(TAG, "K2: "+key2);
                        for (DataSnapshot child3 : child2.getChildren()){
                            final String key3 = child3.getKey();
                            Log.v(TAG, "K2: "+key3);
                            final String title  = child3.child("title").getValue(String.class);
                            final String keys = key1+","+key2+","+key3;
                            final String imgUrl = child3.child("cover").getValue(String.class);

                            stories.add(new Story(keys, title, imgUrl));
                        }
                    }
                }

            }
            /*
            if (!key1.equals("story")){
                    for (DataSnapshot child2 : snapshot.getChildren()){
                        final String key2 = child2.getKey();
                        Log.v(TAG, "K2: "+key2);
                        for (DataSnapshot child3 : snapshot.getChildren()){
                            final String key3 = child3.getKey();
                            Log.v(TAG, "K2: "+key3);
                            final String title  = child3.child("title").getValue(String.class);
                            final String[] keys = {key1, key2, key3};
                            final String imgUrl = child3.child("cover").getValue(String.class);

                            stories.add(new Story(keys, title, imgUrl));
                        }
                    }
                }
             */
            alertProgressBar.dismiss();
            Log.v(TAG, "Adapter size: "+stories.size());
            storyAdapter.notifyDataSetChanged();

            if (stories.size()==0) {
                new PanelHandler(getContext()).showNotificationDialog("Story", getString(R.string.no_story), 1);
                recyclerView.setVisibility(View.GONE);
            }else {
                recyclerView.setVisibility(View.VISIBLE);
            }
            if (itemRefresh!=null) itemRefresh.setVisible(true);

        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {

        }
    };
}