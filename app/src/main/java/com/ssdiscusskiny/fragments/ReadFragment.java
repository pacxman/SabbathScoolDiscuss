package com.ssdiscusskiny.fragments;

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
import android.widget.ImageView;

import com.bhprojects.bibleprojectkiny.Texts;
import com.bhprojects.bibleprojectkiny.TextsAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.ssdiscusskiny.R;
import com.ssdiscusskiny.activities.IntroActivity;
import com.ssdiscusskiny.activities.LessonActivity;
import com.ssdiscusskiny.activities.StoryActivity;
import com.ssdiscusskiny.app.Variables;
import com.ssdiscusskiny.data.Story;
import com.ssdiscusskiny.generator.Grab;
import com.ssdiscusskiny.utils.PanelHandler;
import com.ssdiscusskiny.utils.Parser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ReadFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ReadFragment extends Fragment {

    private final String TAG = ReadFragment.class.getSimpleName();
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    //private String[] params;
    private Story story;
    private static final String KEYS = "story";
    private FirebaseDatabase database;
    private PanelHandler panelHandler;
    private RecyclerView recyclerView;
    private View pBarView;
    private AlertDialog alertProgressBar;
    private ImageView homeImage;

    private SharedPreferences preferences;

    // TODO: Rename and change types of parameters

    public ReadFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static ReadFragment newInstance(Story story) {
        ReadFragment fragment = new ReadFragment();
        Bundle args = new Bundle();
        args.putParcelable("story", story);

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        PreferenceManager.setDefaultValues(getContext(), R.xml.prefs, false);
        preferences = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());

        setHasOptionsMenu(true);
        homeImage = ((StoryActivity) getActivity()).getImageView();
        if (getArguments() != null) {
            story = getArguments().getParcelable("story");
        }else{
            //Load directly story

        }
        database = FirebaseDatabase.getInstance();
        panelHandler = new PanelHandler(getContext());


    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                getActivity().onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View contentView = inflater.inflate(R.layout.fragment_stories, container, false);
        // Inflate the layout for this fragment
        recyclerView = contentView.findViewById(R.id.content_rec);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        if (preferences.getBoolean("night", false)) {
            recyclerView.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.background_dark));
        } else {
            recyclerView.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.background_light));
        }

        pBarView = View.inflate(getContext(), R.layout.pbar_alertdialog, null);
        alertProgressBar = new AlertDialog.Builder(getContext()).create();
        alertProgressBar.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertProgressBar.setView(pBarView);
        final Parser parser = new Parser(getContext());
        final List<Texts> listTexts = new ArrayList<>();
        final SharedPreferences appPrefs = getContext().getSharedPreferences("app_prefs", getContext().MODE_PRIVATE);

        if (story != null) {
            final TextsAdapter textsAdapter = new TextsAdapter(getContext(), listTexts, story.getKeys().replace(",", "_"), null, panelHandler.titleTv, panelHandler.contentTv, panelHandler.versionSpn, panelHandler.mDialog, appPrefs);
            recyclerView.setAdapter(textsAdapter);
            final String[] keys = story.getKeys().split(",");
            final DatabaseReference storyReference = database.getReference()
                    .child(Variables.content)
                    .child("stories")
                    .child(keys[0])
                    .child(keys[1])
                    .child(keys[2]);

            parser.loadText(storyReference, textsAdapter, listTexts, null, alertProgressBar);

        }else{
            //Load story
            alertProgressBar.show();
            database.getReference()
                    .child(Variables.content)
                    .child("stories")
                    .child("story")
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            String title = snapshot.child("title").getValue(String.class);
                            String imageUrl = snapshot.child("cover").getValue(String.class);

                            imageUrl = imageUrl==null ? "no_url" : imageUrl;

                            if (title!=null&&!title.isEmpty()){
                                ((StoryActivity)getActivity()).getCollapsingToolbarLayout().setTitle(title);
                                final String key = title.replace(" ", "_");
                                final TextsAdapter textsAdapter = new TextsAdapter(getContext(), listTexts, key, null, panelHandler.titleTv, panelHandler.contentTv, panelHandler.versionSpn, panelHandler.mDialog, appPrefs);
                                recyclerView.setAdapter(textsAdapter);
                                appPrefs.edit().putBoolean(key, true).apply();
                                final DatabaseReference storyReference = database.getReference()
                                        .child(Variables.content)
                                        .child("stories")
                                        .child("story");
                                parser.loadText(storyReference, textsAdapter, listTexts, null, alertProgressBar);

                                String finalImageUrl = imageUrl;
                                getActivity().runOnUiThread(()->{
                                    Picasso.get()
                                            .load(finalImageUrl)
                                            .error(R.mipmap.story_home_image)
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
                                });


                            }else{
                                alertProgressBar.dismiss();
                                Log.v(TAG, "Story must have a title!");
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            alertProgressBar.dismiss();
                            Log.v(TAG, "Lesson failed: "+error.getMessage());
                        }
                    });

        }

        return contentView;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (story != null) {
            ((StoryActivity) getActivity()).getCollapsingToolbarLayout().setTitle(story.getTitle());
            Picasso.get()
                    .load(story.getImageUrl())
                    .error(R.mipmap.story_home_image)
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
        } else {
            ((StoryActivity) getActivity()).getCollapsingToolbarLayout().setTitle("Inside story");
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

    }
}