package com.ssdiscusskiny.activities;


import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.palette.graphics.Palette;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bhprojects.bibleprojectkiny.AppConstants;
import com.bhprojects.bibleprojectkiny.Texts;
import com.bhprojects.bibleprojectkiny.TextsAdapter;

import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import com.ssdiscusskiny.R;

import com.ssdiscusskiny.app.Variables;
import com.ssdiscusskiny.generator.Grab;
import com.ssdiscusskiny.utils.PanelHandler;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class IntroActivity extends AppCompatActivity {

    private final String TAG = IntroActivity.class.getSimpleName();
    private RecyclerView recyclerView;
    private TextsAdapter textsAdapter;
    private PanelHandler panelHandler;
    private List<Texts> textsList;
    private View pBarView;
    private AlertDialog alertProgressBar;
    private SharedPreferences appPreference;
    private String dateId, introduction;
    private Toolbar toolbar;
    private ImageView homeImage;
    private CollapsingToolbarLayout collapsingToolbarLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);

        getWindow().setNavigationBarColor(getResources().getColor(R.color.colorPrimaryDark));
        getWindow().setStatusBarColor(Color.TRANSPARENT);

        appPreference = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        panelHandler = new PanelHandler(this);

        collapsingToolbarLayout = findViewById(R.id.collapsing_toolbar);

        Calendar calendar = Calendar.getInstance();

        homeImage = findViewById(R.id.home_image);
        recyclerView = findViewById(R.id.recycler_view);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        Bundle bundle = getIntent().getExtras();
        if (bundle == null) {
            dateId = new SimpleDateFormat("dd_MM_yyyy").format(Calendar.getInstance().getTime());
            collapsingToolbarLayout.setTitle(Variables.formatTitle(IntroActivity.this, calendar));
        } else {
            dateId = bundle.getString("date_value", new SimpleDateFormat("dd_MM_yyyy").format(Calendar.getInstance().getTime()));
            collapsingToolbarLayout.setTitle(Variables.formatTitle(IntroActivity.this, convertedCalendar(dateId)));
            Log.d("Quarter", "date id " + dateId);
        }

        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference refQTitle = database.getReference().child(Variables.content).child("lessons").child(Grab.year(convertedCalendar(dateId))).child(Grab.quarter(convertedCalendar(dateId))).child("subject");
        final DatabaseReference qrtIntroRef = database.getReference().child(Variables.content).child("lessons").child(Grab.year(convertedCalendar(dateId))).child(Grab.quarter(convertedCalendar(dateId))).child("intro");

        pBarView = View.inflate(this, R.layout.pbar_alertdialog, null);
        alertProgressBar = new AlertDialog.Builder(this).create();
        alertProgressBar.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertProgressBar.setView(pBarView);

        alertProgressBar.show();

        qrtIntroRef.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dsnap) {
                introduction = dsnap.getValue(String.class);
                if (introduction != null) {
                    refQTitle.addValueEventListener(new ValueEventListener() {

                        @Override
                        public void onDataChange(DataSnapshot snap) {
                            String headTitle = snap.getValue(String.class);
                            textsList = new ArrayList<>();
                            if (headTitle != null && !headTitle.isEmpty()) {
                                //textsList.add(new Texts(1, "<p><h3>" + headTitle + "</h3></p>"));
                                collapsingToolbarLayout.setTitle(headTitle);
                            }
                            introduction = introduction.replace("</p>", "<d>");
                            introduction = introduction.replace("<p>", "");
                            String[] contents = introduction.split("<d>");
                            //textsList = new ArrayList<>();
                            for (int i = 0; i < contents.length; i++) {
                                textsList.add(new Texts(contents[i]));
                            }
                            SharedPreferences appPrefs = getSharedPreferences("app_prefs", MODE_PRIVATE);
                            textsAdapter = new TextsAdapter(IntroActivity.this, textsList, "intro_" + Grab.quarter(convertedCalendar(dateId)), null, panelHandler.titleTv, panelHandler.contentTv, panelHandler.versionSpn, panelHandler.mDialog, appPrefs);

                            recyclerView.setAdapter(textsAdapter);
                            alertProgressBar.dismiss();
                            refQTitle.removeEventListener(this);
                            Calendar calendar = Calendar.getInstance();
                            appPrefs.edit().putBoolean(Grab.quarter(calendar) + "_read", true).apply();
                        }

                        @Override
                        public void onCancelled(DatabaseError error) {
                        }


                    });
                } else {
                    introduction = "<p><br><br></p><b>"+getString(R.string.no_intro_prefix)+"</b></p>";
                    alertProgressBar.dismiss();
                    introduction = introduction.replace("</p>", "<d>");
                    introduction = introduction.replace("<p>", "");
                    String[] contents = introduction.split("<d>");
                    textsList = new ArrayList<>();
                    for (int i = 0; i < contents.length; i++) {
                        textsList.add(new Texts(contents[i]));
                    }
                    SharedPreferences appPrefs = getSharedPreferences("app_prefs", MODE_PRIVATE);
                    textsAdapter = new TextsAdapter(IntroActivity.this, textsList, "intro_" + Grab.quarter(convertedCalendar(dateId)), null, panelHandler.titleTv, panelHandler.contentTv, panelHandler.versionSpn, panelHandler.mDialog, appPrefs);
                    textsAdapter.canHighlight = false;
                    recyclerView.setAdapter(textsAdapter);
                }
                qrtIntroRef.removeEventListener(this);
            }

            @Override
            public void onCancelled(DatabaseError dError) {
            }


        });

        runOnUiThread(() -> {
            loadHomeImage(dateId, FirebaseStorage.getInstance());
        });

        collapsingToolbarLayout.setExpandedTitleTextAppearance(R.style.ExpandedAppBar);
        collapsingToolbarLayout.setCollapsedTitleTextAppearance(R.style.CollapsedAppBar);

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);

    }

    @Override
    protected void onResume() {
        super.onResume();

        AppConstants.fontSize = Integer.parseInt(appPreference.getString("font", "18"));
        AppConstants.isNightMode = appPreference.getBoolean("night", false);
        AppConstants.referanceClickable = appPreference.getBoolean("underline", false);
        AppConstants.referenceColor = appPreference.getString("recolor", "#0000ff");
        AppConstants.font = appPreference.getString("font_family", "serif");
        Variables.display_font_size = Integer.parseInt(appPreference.getString("display_size", "18"));

        if (appPreference.getBoolean("night", false)) {
            recyclerView.setBackgroundColor(ContextCompat.getColor(this, R.color.background_dark));
        } else {
            recyclerView.setBackgroundColor(ContextCompat.getColor(this, R.color.background_light));
        }

        panelHandler.setDisplayTextSize();

        try {
            if (!AppConstants.isDefFont) {
                if (AppConstants.font.contains("slabo")) {
                    collapsingToolbarLayout.setExpandedTitleTypeface(Typeface.createFromAsset(getAssets(), "Slabo27px-Regular.ttf"));
                    collapsingToolbarLayout.setCollapsedTitleTypeface(Typeface.createFromAsset(getAssets(), "Slabo27px-Regular.ttf"));
                } else if (AppConstants.font.contains("cambo")) {
                    collapsingToolbarLayout.setExpandedTitleTypeface(Typeface.createFromAsset(getAssets(), "cambo-regular.ttf"));
                    collapsingToolbarLayout.setCollapsedTitleTypeface(Typeface.createFromAsset(getAssets(), "cambo-regular.ttf"));
                } else if (AppConstants.font.contains("bitter")) {
                    collapsingToolbarLayout.setExpandedTitleTypeface(Typeface.createFromAsset(getAssets(), "Bitter-Regular.ttf"));
                    collapsingToolbarLayout.setCollapsedTitleTypeface(Typeface.createFromAsset(getAssets(), "Bitter-Regular.ttf"));
                } else if (AppConstants.font.contains("tnr")) {
                    collapsingToolbarLayout.setExpandedTitleTypeface(Typeface.createFromAsset(getAssets(), "times_new_roman.ttf"));
                    collapsingToolbarLayout.setCollapsedTitleTypeface(Typeface.createFromAsset(getAssets(), "times_new_roman.ttf"));
                } else {
                    collapsingToolbarLayout.setExpandedTitleTypeface(Typeface.createFromAsset(getAssets(), "Slabo27px-Regular.ttf"));
                    collapsingToolbarLayout.setCollapsedTitleTypeface(Typeface.createFromAsset(getAssets(), "Slabo27px-Regular.ttf"));
                }
            } else {
                collapsingToolbarLayout.setExpandedTitleTypeface(Typeface.DEFAULT);
                collapsingToolbarLayout.setCollapsedTitleTypeface(Typeface.DEFAULT);
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

    }

    private Date convertDateId(String dateId) {
        try {
            return new SimpleDateFormat("dd_MM_yyyy").parse(dateId);
        } catch (ParseException e) {
            return Calendar.getInstance().getTime();
        }
    }

    public Calendar convertedCalendar(String dateId) {
        Calendar cal = Calendar.getInstance();

        int year = Integer.parseInt(new SimpleDateFormat("yyyy").format(convertDateId(dateId)));
        int month = Integer.parseInt(new SimpleDateFormat("MM").format(convertDateId(dateId)));
        int date = Integer.parseInt(new SimpleDateFormat("dd").format(convertDateId(dateId)));

        cal.set(year, month - 1, date);

        return cal;
    }

    private void loadHomeImage(String date, FirebaseStorage storage) {

        final StorageReference imageRef = storage.getReference().child("images").child(Grab.year(convertedCalendar(date))).child(Grab.quarter(convertedCalendar(date))).child("cover.jpg");

        Log.v("HOME_IMAGE", imageRef.toString());

        final SharedPreferences urlPrefs = getSharedPreferences("urls", MODE_PRIVATE);

        final String imageKey = urlKey(convertedCalendar(date));

        final String url = urlPrefs.getString(imageKey, "default");

        Picasso.get()
                .load(url)
                .error(R.mipmap.default_image)
                .into(homeImage, new Callback() {
                    @Override
                    public void onSuccess() {
                        detectColor();
                    }

                    @Override
                    public void onError(Exception e) {
                        detectColor();
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
                        .error(R.mipmap.default_image)
                        .into(homeImage, new Callback() {
                            @Override
                            public void onSuccess() {
                                detectColor();
                            }

                            @Override
                            public void onError(Exception e) {
                                detectColor();
                            }
                        });
            } else {
                Log.v("HOMEIMAGE", "URL DIDN'T CHANGE");
            }

        }).addOnFailureListener(exception -> {
            // Handle any errors
            Log.v("HOMEIMAGE", "FAILED " + exception.getMessage());
        });

    }

    private void detectColor() {
        Drawable d = homeImage.getDrawable();
        Bitmap bitmap = ((BitmapDrawable) d).getBitmap();

        Palette.from(bitmap).generate(p -> {
            // Use generated instance
            Palette.Swatch vibrant = p.getDarkVibrantSwatch();

            try {
                getWindow().setStatusBarColor(vibrant.getRgb());
                getWindow().setNavigationBarColor(vibrant.getRgb());
                collapsingToolbarLayout.setContentScrimColor(vibrant.getRgb());
                collapsingToolbarLayout.setStatusBarScrimColor(vibrant.getRgb());
            }catch(Exception ex){
                ex.printStackTrace();
                Log.e(TAG, "Vibrant wasn't detected!");
                collapsingToolbarLayout.setContentScrimColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));
                collapsingToolbarLayout.setStatusBarScrimColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));
            }
        });
    }

    private String urlKey(Calendar cal) {
        return Grab.year(cal) + "_" + Grab.quarter(cal) + "_" + Grab.lesson(cal)+"_"+"cover";
    }

    /*private void loadImage(Context context, String url) {

        final

        if (url != null) {
            if (!url.isEmpty()) {
                mTarget = new Target() {
                    @Override
                    public void onBitmapLoaded(final Bitmap bitmap, Picasso.LoadedFrom from) {

                        imageView.setImageBitmap(bitmap);
                    }

                    @Override
                    public void onBitmapFailed(Drawable errorDrawable) {

                    }

                    @Override
                    public void onPrepareLoad(Drawable placeHolderDrawable) {

                    }
                };

                Picasso.with(context)
                        .load(url)
                        .into(mTarget);
            } else {
                //imageView.setImageResource(R.drawable.image_pm);
            }
        } else {
            //imageView.setImageResource(R.drawable.image_pm);
        }

    }*/
}