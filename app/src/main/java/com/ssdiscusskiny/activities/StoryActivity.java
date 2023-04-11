package com.ssdiscusskiny.activities;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Build;

import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bhprojects.bibleprojectkiny.AppConstants;
import com.bhprojects.bibleprojectkiny.Texts;
import com.bhprojects.bibleprojectkiny.TextsAdapter;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.util.TypedValue;
import android.widget.ImageView;

import android.graphics.Color;

import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.squareup.picasso.Picasso;
import com.ssdiscusskiny.R;
import com.ssdiscusskiny.app.Variables;
import com.ssdiscusskiny.fragments.ReadFragment;
import com.ssdiscusskiny.fragments.StoriesFragment;
import com.ssdiscusskiny.generator.Grab;
import com.ssdiscusskiny.utils.PanelHandler;

import android.preference.PreferenceManager;

import org.apache.commons.lang.StringUtils;

import android.view.WindowManager;

public class StoryActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private CollapsingToolbarLayout collapsingToolbarLayout;
    private ImageView imageView;
    private FragmentManager fragmentManager;
    private FragmentTransaction fragmentTransaction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_story);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        collapsingToolbarLayout = findViewById(R.id.collapsing_toolbar);

        collapsingToolbarLayout.setTitle(getString(R.string.inside_story));

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setNavigationBarColor(getResources().getColor(R.color.colorPrimary));
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }

        imageView = findViewById(R.id.home_image);

        AppBarLayout mAppBarLayout = findViewById(R.id.app_bar);
        mAppBarLayout.addOnOffsetChangedListener((appBarLayout, verticalOffset) -> {
            if (Math.abs(verticalOffset) == appBarLayout.getTotalScrollRange()) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    getWindow().setNavigationBarColor(getResources().getColor(R.color.colorPrimary));
                    getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimary));
                }
            } else if (verticalOffset == 0) {
                // Expanded
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    getWindow().setNavigationBarColor(getResources().getColor(R.color.imgBackground));
                    getWindow().setStatusBarColor(Color.TRANSPARENT);
                }
            } else {
                // Somewhere in between
            }

        });

        fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();

        Bundle bundle = getIntent().getExtras();
        if (bundle==null){
            fragmentTransaction.replace(R.id.fragment_holder, new StoriesFragment());
        }else{
            fragmentTransaction.replace(R.id.fragment_holder, new ReadFragment());
        }

        fragmentTransaction.commit();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }



    public CollapsingToolbarLayout getCollapsingToolbarLayout() {
        return collapsingToolbarLayout;
    }

    public ImageView getImageView() {
        return imageView;
    }

    private int dpToPx(int dp) {
        Resources r = getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }

}
