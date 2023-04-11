package com.ssdiscusskiny.activities;

import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Callback;
import com.ssdiscusskiny.R;

import android.annotation.SuppressLint;
import android.content.pm.ApplicationInfo;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.ImageView;
import android.view.View;
import android.view.WindowManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import android.graphics.drawable.ColorDrawable;
import android.graphics.Color;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.palette.graphics.Palette;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKey;

import com.bhprojects.bibleprojectkiny.AppConstants;
import com.bhprojects.bibleprojectkiny.Texts;
import com.bhprojects.bibleprojectkiny.TextsAdapter;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import com.squareup.picasso.Picasso;

import com.ssdiscusskiny.MainActivity;
import com.ssdiscusskiny.SplashActivity;
import com.ssdiscusskiny.app.Variables;
import com.ssdiscusskiny.generator.Grab;
import com.ssdiscusskiny.monetize.Monetize;
import com.ssdiscusskiny.utils.PanelHandler;
import com.ssdiscusskiny.utils.Parser;
import com.unity3d.ads.IUnityAdsInitializationListener;
import com.unity3d.ads.IUnityAdsLoadListener;
import com.unity3d.ads.IUnityAdsShowListener;
import com.unity3d.ads.UnityAds;
import com.unity3d.ads.UnityAdsShowOptions;


public class LessonActivity extends AppCompatActivity implements IUnityAdsInitializationListener {
    private final String TAG = LessonActivity.class.getSimpleName();
    private String dateId;
    private int dayId;
    private FirebaseDatabase database;
    private DatabaseReference refWeekTitle, refDayTopic, refLesson, refQuarterTitle;
    private SharedPreferences prefs, appPrefs;
    private View pBarView;
    private AlertDialog alertProgressBar;
    private Toolbar toolbar;
    private Parser parser;
    private PanelHandler panelHandler;
    private String caller = "";
    private TextsAdapter textsAdapter;
    private RecyclerView recyclerView;
    private ImageView homeImage;
    private CollapsingToolbarLayout collapsingLayout;
    private String accountId;
    private Boolean testMode = false;
    private String adUnitId = "Interstitial_Android";
    private final String UNIT_ID = "5229649";
    private boolean isAdLoaded = false;
    private Handler handler = new Handler();
    private final HashMap<String, String> hashMap = new HashMap<>();
    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        // TODO: Implement this method
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lesson);

        getWindow().setNavigationBarColor(getResources().getColor(R.color.colorPrimaryDark));
        getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));

        toolbar = findViewById(R.id.toolbarLA);
        setSupportActionBar(toolbar);

        collapsingLayout = findViewById(R.id.toolbar_layout);

        prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        try {
            MasterKey masterKey = new MasterKey.Builder(LessonActivity.this)
                    .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                    .build();

            appPrefs = EncryptedSharedPreferences.create(
                    LessonActivity.this,
                    "account_prefs",
                    masterKey,
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM);

        }catch(Exception ex){
            ex.printStackTrace();
            Log.e(TAG, ex.getMessage());
        }

        accountId = appPrefs.getString("account_id", "");

        homeImage = findViewById(R.id.expandable_home_image);

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        pBarView = View.inflate(this, R.layout.pbar_alertdialog, null);
        alertProgressBar = new AlertDialog.Builder(this).create();
        alertProgressBar.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertProgressBar.setView(pBarView);

        parser = new Parser(this);

        panelHandler = new PanelHandler(this);

        String existName = appPrefs.getString("username", "");
        String existAccountId = appPrefs.getString("email", "");

        if (!existAccountId.isEmpty()) {
            appPrefs.edit().putString("account_id", existAccountId).commit();
            appPrefs.edit().remove("email").commit();
        }
        if (!existName.isEmpty()) {
            appPrefs.edit().putString("account_name", existName).commit();
            appPrefs.edit().remove("username").commit();
        }

        Bundle extras = getIntent().getExtras();

        if (extras != null) {
            dateId = extras.getString("date_value", "");
            dayId = extras.getInt("day_id", -1);

            Log.v(TAG, dateId+" DAY ID: "+dayId);
            if (extras.containsKey("caller")) {
                caller = extras.getString("caller");
                Log.v(TAG, caller);
            }

        } else {
            Calendar cal = Calendar.getInstance();
            cal.setFirstDayOfWeek(Calendar.SUNDAY);
            SimpleDateFormat sdf = new SimpleDateFormat("dd_MM_yyyy");
            dateId = sdf.format(cal.getTime());
            dayId = cal.get(Calendar.DAY_OF_WEEK);
            loadDedicatedMessage();
        }

        //Initializing
        if (Monetize.monetize()&&caller==null) UnityAds.initialize(getApplicationContext(), UNIT_ID, testMode, this);

        Log.v(TAG, "DAY ID "+dayId);

        database = FirebaseDatabase.getInstance();

        refWeekTitle = database.getReference().child(Variables.content).child(Variables.lessons).child(Grab.year(parser.convertedCalendar(dateId))).child(Grab.quarter(parser.convertedCalendar(dateId))).child(Grab.lesson(parser.convertedCalendar(dateId))).child(Variables.title);
        refDayTopic = database.getReference().child(Variables.content).child(Variables.lessons).child(Grab.year(parser.convertedCalendar(dateId))).child(Grab.quarter(parser.convertedCalendar(dateId))).child(Grab.lesson(parser.convertedCalendar(dateId))).child(dateId).child(Variables.point);
        refLesson = database.getReference().child(Variables.content).child(Variables.lessons).child(Grab.year(parser.convertedCalendar(dateId))).child(Grab.quarter(parser.convertedCalendar(dateId))).child(Grab.lesson(parser.convertedCalendar(dateId))).child(dateId);
        refQuarterTitle = database.getReference().child(Variables.content).child(Variables.lessons).child(Grab.year(parser.convertedCalendar(dateId))).child(Grab.quarter(parser.convertedCalendar(dateId))).child("subject");

        List<Texts> listTexts = new ArrayList<>();
        textsAdapter = new TextsAdapter(LessonActivity.this, listTexts, dateId, null, panelHandler.titleTv, panelHandler.contentTv, panelHandler.versionSpn, panelHandler.mDialog, appPrefs);

        recyclerView.setAdapter(textsAdapter);

        collapsingLayout.setExpandedTitleTextAppearance(R.style.ExpandedAppBar);
        collapsingLayout.setCollapsedTitleTextAppearance(R.style.CollapsedAppBar);

        parser.loadText(refLesson, textsAdapter, listTexts, null, alertProgressBar);
        prefetchTitles();

        loadHomeImage(dateId,FirebaseStorage.getInstance());

        if (accountId.isEmpty()){
            startActivity(new Intent(LessonActivity.this, SplashActivity.class));
            finish();
        }

    }

    @Override
    protected void onResume() {
        // TODO: Implement this method
        super.onResume();

        collapsingLayout.setExpandedTitleMarginBottom(42);

        AppConstants.fontSize = Integer.parseInt(prefs.getString("font", "18"));
        AppConstants.isNightMode = prefs.getBoolean("night", false);
        AppConstants.referanceClickable = prefs.getBoolean("underline", false);
        AppConstants.referenceColor = prefs.getString("recolor", "#0000ff");
        AppConstants.font = prefs.getString("font_family", "cambo");

        try {
            if (!AppConstants.isDefFont) {
                if (AppConstants.font.contains("slabo")) {
                    collapsingLayout.setExpandedTitleTypeface(Typeface.createFromAsset(getAssets(), "Slabo27px-Regular.ttf"));
                    collapsingLayout.setCollapsedTitleTypeface(Typeface.createFromAsset(getAssets(), "Slabo27px-Regular.ttf"));
                } else if (AppConstants.font.contains("cambo")) {
                    collapsingLayout.setExpandedTitleTypeface(Typeface.createFromAsset(getAssets(), "cambo-regular.ttf"));
                    collapsingLayout.setCollapsedTitleTypeface(Typeface.createFromAsset(getAssets(), "cambo-regular.ttf"));
                } else if (AppConstants.font.contains("bitter")) {
                    collapsingLayout.setExpandedTitleTypeface(Typeface.createFromAsset(getAssets(), "Bitter-Regular.ttf"));
                    collapsingLayout.setCollapsedTitleTypeface(Typeface.createFromAsset(getAssets(), "Bitter-Regular.ttf"));
                } else if (AppConstants.font.contains("tnr")) {
                    collapsingLayout.setExpandedTitleTypeface(Typeface.createFromAsset(getAssets(), "times_new_roman.ttf"));
                    collapsingLayout.setCollapsedTitleTypeface(Typeface.createFromAsset(getAssets(), "times_new_roman.ttf"));
                } else if (AppConstants.font.contains("lato")) {
                    collapsingLayout.setExpandedTitleTypeface(Typeface.createFromAsset(getAssets(), "Lato-Regular.ttf"));
                    collapsingLayout.setCollapsedTitleTypeface(Typeface.createFromAsset(getAssets(), "Lato-Regular.ttf"));
                } else if (AppConstants.font.contains("andada")) {
                    collapsingLayout.setExpandedTitleTypeface(Typeface.createFromAsset(getAssets(), "AndadaPro-Regular.ttf"));
                    collapsingLayout.setCollapsedTitleTypeface(Typeface.createFromAsset(getAssets(), "AndadaPro-Regular.ttf"));
                } else if (AppConstants.font.contains("ptserif")) {
                    collapsingLayout.setExpandedTitleTypeface(Typeface.createFromAsset(getAssets(), "PTSerif-Regular.ttf"));
                    collapsingLayout.setCollapsedTitleTypeface(Typeface.createFromAsset(getAssets(), "PTSerif-Regular.ttf"));
                } else {
                    collapsingLayout.setExpandedTitleTypeface(Typeface.createFromAsset(getAssets(), "Slabo27px-Regular.ttf"));
                    collapsingLayout.setCollapsedTitleTypeface(Typeface.createFromAsset(getAssets(), "Slabo27px-Regular.ttf"));
                }
            } else {
                collapsingLayout.setExpandedTitleTypeface(Typeface.DEFAULT);
                collapsingLayout.setCollapsedTitleTypeface(Typeface.DEFAULT);
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

        if (prefs.getBoolean("night", false)) {
            recyclerView.setBackgroundColor(ContextCompat.getColor(this, R.color.background_dark));
        } else {
            recyclerView.setBackgroundColor(ContextCompat.getColor(this, R.color.background_light));
        }
        textsAdapter.notifyDataSetChanged();
        panelHandler.setDisplayTextSize();
    }

    @Override
    public void onBackPressed() {
        // TODO: Implement this method
        handler.removeCallbacks(exitRunnable);
        if (!caller.isEmpty()) {
            super.onBackPressed();
        } else if (isAdLoaded) {
            UnityAds.show((LessonActivity.this), adUnitId, new UnityAdsShowOptions(), showListener);
            handler.postDelayed(exitRunnable, 3500);
        }else{
            panelHandler.showExit();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // TODO: Implement this method
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.lesson, menu);
        MenuItem item = menu.findItem(R.id.comments);
        if (caller != null ) {
            item.setVisible(false);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // TODO: Implement this method
        int menuId = item.getItemId();
        switch (menuId) {

            case R.id.about_quarter:
                Intent aboutQuarter = new Intent(this, IntroActivity.class);
                aboutQuarter.putExtra("date_value", dateId);
                startActivity(aboutQuarter);
                break;
            case R.id.comments:
                Intent intent = new Intent(this, MainActivity.class);
                intent.putExtra("opencmnt", "opencmnt");
                startActivity(intent);
                finish();
                break;
            case R.id.share:
                panelHandler.shareApplication();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 10) {
            ApplicationInfo app = getApplicationContext().getApplicationInfo();
            File tempFile = new File(getExternalCacheDir() + "/ExtractedApk/");
            tempFile = new File(tempFile.getPath() + "/" + getString(app.labelRes));
            if (tempFile.delete()) Log.d(TAG, "CACHE CLEARED");
            tempFile = new File(getExternalCacheDir() + "/ExtractedApk/");
            if (tempFile.delete()) Log.d(TAG, "CACHE CLEARED");
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
    }

    @Override
    protected void onDestroy() {
        // TODO: Implement this method
        super.onDestroy();
        if (caller.isEmpty()){
            getIntent().replaceExtras(new Bundle());
            getIntent().setAction("");
            getIntent().setData(null);
            getIntent().setFlags(0);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        Thread thread = new Thread() {
            public void run() {
                //Logic
                if (appPrefs.contains("email")) {
                    panelHandler.getAccessState(database, appPrefs.getString("email", ""));
                }
            }
        };
        thread.start();
    }

    @Override
    public void onInitializationComplete() {
        loadInterstitialAd();
    }

    @Override
    public void onInitializationFailed(UnityAds.UnityAdsInitializationError error, String message) {
        Log.e(TAG, message);
        isAdLoaded = false;
    }

    private IUnityAdsLoadListener loadListener = new IUnityAdsLoadListener() {
        @Override
        public void onUnityAdsAdLoaded(String placementId) {
            isAdLoaded = true;
        }

        @Override
        public void onUnityAdsFailedToLoad(String placementId, UnityAds.UnityAdsLoadError error, String message) {
            Log.e("UnityAdsExample", "Unity Ads failed to load ad for " + placementId + " with error: [" + error + "] " + message);
            isAdLoaded = false;
        }
    };

    private IUnityAdsShowListener showListener = new IUnityAdsShowListener() {
        @Override
        public void onUnityAdsShowFailure(String placementId, UnityAds.UnityAdsShowError error, String message) {
            Log.e(TAG, "Unity Ads failed to show ad for " + placementId + " with error: [" + error + "] " + message);
            loadInterstitialAd();
        }

        @Override
        public void onUnityAdsShowStart(String placementId) {
            Log.v(TAG, "onUnityAdsShowStart: " + placementId);
        }

        @Override
        public void onUnityAdsShowClick(String placementId) {
            Log.v(TAG, "onUnityAdsShowClick: " + placementId);
        }

        @Override
        public void onUnityAdsShowComplete(String placementId, UnityAds.UnityAdsShowCompletionState state) {
            Log.v(TAG, "onUnityAdsShowComplete: " + placementId);
            loadInterstitialAd();
        }
    };

    Runnable exitRunnable = ()-> panelHandler.showExit();

    public void loadInterstitialAd() {
        new Thread(()->UnityAds.load(adUnitId, loadListener)).start();
    }

    private void loadHomeImage(String key, FirebaseStorage storage){

        final StorageReference imageRef = storage.getReference().child("images").child(Grab.year(parser.convertedCalendar(key))).child(Grab.quarter(parser.convertedCalendar(key))).child(Grab.lesson(parser.convertedCalendar(key))+".jpg");
        Log.v(TAG, imageRef.toString());

        final SharedPreferences urlPrefs = getSharedPreferences("urls", MODE_PRIVATE);

        final String imageKey = urlKey(parser.convertedCalendar(key));

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
            //Save this image link as it may be used even offline
            final String uriUrl = String.valueOf(uri);
            if (!url.equals(uriUrl)){
                //
                Log.v(TAG, "URL CHANGE DETECTED");
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
            }else{
                Log.v("HOMEIMAGE", "URL DIDN'T CHANGE");
            }

        }).addOnFailureListener(exception -> {
            // Handle any errors
            Log.v("HOMEIMAGE", "FAILED "+exception.getMessage());
        });

    }

    private void detectColor() {
        runOnUiThread(()->{
            Drawable d = homeImage.getDrawable();
            Bitmap bitmap = ((BitmapDrawable) d).getBitmap();

            Palette.from(bitmap).generate(p -> {
                Palette.Swatch vibrant = p.getDominantSwatch();

                try {
                    getWindow().setStatusBarColor(vibrant.getRgb());
                    getWindow().setNavigationBarColor(vibrant.getRgb());

                    collapsingLayout.setContentScrimColor(vibrant.getRgb());
                    collapsingLayout.setStatusBarScrimColor(vibrant.getRgb());

                } catch (Exception ex) {
                    ex.printStackTrace();
                    getWindow().setStatusBarColor(ContextCompat.getColor(this,R.color.colorPrimaryDark));
                    getWindow().setNavigationBarColor(ContextCompat.getColor(this,R.color.colorPrimaryDark));

                    collapsingLayout.setContentScrimColor(ContextCompat.getColor(this,R.color.colorPrimaryDark));
                    collapsingLayout.setStatusBarScrimColor(ContextCompat.getColor(this,R.color.colorPrimaryDark));

                    Log.e(TAG, "Vibrant wasn't detected!");
                }
            });
        });

    }
    private String urlKey(Calendar cal){
        return Grab.year(cal)+"_"+Grab.quarter(cal)+"_"+Grab.lesson(cal);
    }

    private void loadDedicatedMessage(){

        final String userKey = accountId.contains("@") ? accountId.substring(0, accountId.indexOf("@")) : accountId;
        Log.v(TAG, "Loading dedicated message on "+userKey);
        final DatabaseReference userMsgRef = database.getReference()
                .child(Variables.content)
                .child("messages")
                .child("provided")
                .child(userKey);
        userMsgRef.keepSynced(true);


        userMsgRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                HashMap<String, String> msgMap = (HashMap<String, String>) snapshot.getValue();
                if (msgMap!=null){
                    String key = msgMap.get("key");
                    String body = msgMap.get("body");
                    String action = msgMap.get("extra");
                    int extra = 1;

                    if (action!=null){
                        extra = action.equals("confined") ? 2 : 1;
                    }

                    if (!appPrefs.getBoolean(key+"_showed", false)){
                        panelHandler.showNotificationDialog("New message", body, extra)
                                .setOnDismissListener(dialogInterface -> {
                                    appPrefs.edit().putBoolean(key+"_showed", true).apply();
                                });
                    }else{
                        Log.v(TAG, "Msg shown!");
                    }
                }else {
                    Log.e(TAG, "NULL Hashmap");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, error.getMessage());
            }
        });
    }
    private void prefetchTitles(){
        refWeekTitle = database.getReference().child(Variables.content).child(Variables.lessons).child(Grab.year(parser.convertedCalendar(dateId))).child(Grab.quarter(parser.convertedCalendar(dateId))).child(Grab.lesson(parser.convertedCalendar(dateId))).child(Variables.title);
        refDayTopic = database.getReference().child(Variables.content).child(Variables.lessons).child(Grab.year(parser.convertedCalendar(dateId))).child(Grab.quarter(parser.convertedCalendar(dateId))).child(Grab.lesson(parser.convertedCalendar(dateId))).child(dateId).child(Variables.point);
        refQuarterTitle = database.getReference().child(Variables.content).child(Variables.lessons).child(Grab.year(parser.convertedCalendar(dateId))).child(Grab.quarter(parser.convertedCalendar(dateId))).child("subject");

        final String qKey = Grab.quarter(parser.convertedCalendar(dateId));
        final String lKey = Grab.lesson(parser.convertedCalendar(dateId));

        refQuarterTitle.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                snapshot.getRef().removeEventListener(this);
                String quarter = snapshot.getValue(String.class);
                quarter = (quarter != null && !quarter.isEmpty()) ? quarter : getString(R.string.app_name);
                hashMap.put(qKey, quarter);
                refWeekTitle.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot2) {
                        snapshot2.getRef().removeEventListener(this);
                        String lesson = snapshot2.getValue(String.class);
                        lesson = (lesson != null && !lesson.isEmpty()) ? lesson : getString(R.string.app_lang);
                        hashMap.put(lKey, lesson);
                        refDayTopic.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot3) {
                                snapshot3.getRef().removeEventListener(this);
                                String point = snapshot3.getValue(String.class);
                                point = (point != null && !point.isEmpty()) ? point : Grab.quarter(parser.convertedCalendar(dateId)) + " " + Grab.year(parser.convertedCalendar(dateId));
                                hashMap.put(dateId, point);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Log.e(TAG, error.getMessage()+" "+error.getDetails());
                            }
                        });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e(TAG, error.getMessage()+" "+error.getDetails());
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, error.getMessage()+" "+error.getDetails());
            }
        });
        //schedule handler
        handler.postDelayed(() -> parser.loadTitles(collapsingLayout, hashMap, null, dayId, dateId, false), 3000);
    }

}
