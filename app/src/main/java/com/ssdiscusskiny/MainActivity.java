package com.ssdiscusskiny;

import android.annotation.SuppressLint;

import android.Manifest;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.ContextWrapper;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.app.PendingIntent;
import android.app.AlarmManager;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.content.Intent;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.content.pm.ApplicationInfo;
import android.graphics.Color;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ImageButton;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.view.View;
import android.view.MenuItem;
import android.view.Menu;
import android.view.WindowManager;
import android.view.MenuInflater;
import android.graphics.drawable.ColorDrawable;
import android.preference.PreferenceManager;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.palette.graphics.Palette;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKey;
import androidx.slidingpanelayout.widget.SlidingPaneLayout;
import androidx.viewpager.widget.ViewPager;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLConnection;
import java.util.Collections;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import java.io.File;
import java.io.IOException;


import com.bhprojects.bibleprojectkiny.AppConstants;
import com.bhprojects.bibleprojectkiny.Consts;
import com.bhprojects.bibleprojectkiny.HighlightsActivity;
import com.bhprojects.bibleprojectkiny.NotesActivity;
import com.bhprojects.bibleprojectkiny.VersesActivity;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import com.ssdiscusskiny.activities.LessonActivity;
import com.ssdiscusskiny.activities.LibraryActivity;
import com.ssdiscusskiny.activities.ReadActivity;
import com.ssdiscusskiny.activities.IntroActivity;
import com.ssdiscusskiny.activities.VideosActivity;
import com.ssdiscusskiny.adapters.ChatAdapter;
import com.ssdiscusskiny.adapters.MainPageAdapter;
import com.ssdiscusskiny.app.AppRater;
import com.ssdiscusskiny.app.Variables;
import com.ssdiscusskiny.chat.Chat;
import com.ssdiscusskiny.downloaders.FileDownloader;
import com.ssdiscusskiny.generator.Grab;
import com.ssdiscusskiny.generator.Randoms;
import com.ssdiscusskiny.monetize.Monetize;
import com.ssdiscusskiny.preferences.Settings;
import com.ssdiscusskiny.receivers.AlarmReceiver;
import com.ssdiscusskiny.activities.StoryActivity;
import com.ssdiscusskiny.services.LessonBackgroundLoader;
import com.ssdiscusskiny.tools.TopProgressBar;
import com.ssdiscusskiny.utils.PanelHandler;
import com.ssdiscusskiny.utils.Parser;
import com.ssdiscusskiny.utils.SwipeHelper;

import com.unity3d.ads.IUnityAdsInitializationListener;
import com.unity3d.ads.IUnityAdsLoadListener;
import com.unity3d.ads.IUnityAdsShowListener;
import com.unity3d.ads.UnityAds;
import com.unity3d.ads.UnityAdsShowOptions;

import android.os.AsyncTask;

import java.net.URL;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;

import android.util.Log;

import org.apache.commons.lang.StringUtils;
import org.json.JSONObject;

import java.io.OutputStreamWriter;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.TimeZone;

import org.json.JSONException;


public class MainActivity extends AppCompatActivity implements IUnityAdsInitializationListener {

    private final String TAG = MainActivity.class.getSimpleName();
    private TabLayout tab;
    private ViewPager pager;
    private MainPageAdapter mpAdapter;
    private FloatingActionButton fabHand;
    private final List<String> mFragmentTitleList = new ArrayList<>();
    private String android_id;
    private int indexPage;
    private EditText commentEdit;
    private View sendBtn;
    private RecyclerView mRecyclerView;
    private ChatAdapter chatAdapter;
    private ImageButton imbClose;
    private FirebaseDatabase database;
    private DatabaseReference refQC;
    private boolean
            activeNet = false,
            weekSwitch = false,
            reloadImg = false,
            fOnCreate = true,
            didCheck = false,
            alreadySlided = false,
            showedUseCase = false,
            isFirstRun = true,
            isFirstOpen = true,
            pRequestOnFirstRun = false;
    private TopProgressBar tpb;
    private ArrayList<String> comments = new ArrayList<>();
    private ArrayList<Chat> chats = new ArrayList<>();
    private PendingIntent alarmIntent;
    private AlarmManager alarmMgr;
    private SharedPreferences prefs, appPrefs;
    private SharedPreferences.Editor shpEditor;
    private Parser parser;
    private PanelHandler panelHandler;
    private SlidingPaneLayout mSlidingLayout;
    private View pBarView;
    private AlertDialog alertProgressBar;
    private final String authKey = "AAAAi0MqFYc:APA91bFixfpXvr7qFJRbrX8sM_oFe6j0p7p4yPRb5HpvQ5XOWsNrPaQBIs1sjanDaLs1SSFQklkR-Kfv5gP3SfhPLdIb7GxiRYS8G67zCN0DdhUnE3nBmvfWCzeavKdbQzR69pIcnZbv";   // Your FCM AUTH key
    private final String FMCurl = "https://fcm.googleapis.com/fcm/send";
    private final String topic = "ssdiscusskiny";
    private LinearLayoutManager layoutManager;
    private ImageView homeImage;
    private AppBarLayout mAppBarLayout;
    private CollapsingToolbarLayout collapsingLayout;
    private ImageView missionView;
    private Toolbar mToolbar;
    final Handler handler = new Handler(Looper.getMainLooper());
    private int slideCount = 0;
    private RelativeLayout showCaseLayout;
    private String accountId = "";
    private String accountName = "";
    private boolean accountValidated = false;
    private RelativeLayout replyHolder;
    private ImageView closeReply;
    private TextView replyTv, replySenderTv;
    private String replyID = null, replyHintTxt = null, commentSender = null, displayed = "";
    private Boolean testMode = true;
    private String adUnitId = "Interstitial_Android";
    private String UNIT_ID = "5229649";
    //private boolean playAdFirst = false;
    private boolean isAdLoaded = false;
    final HashMap<Integer, String> hashMap = new HashMap<>();
    final HashMap<String, String> hashMap2 = new HashMap<>();
    private int index;


    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        startService(new Intent(
                this,
                LessonBackgroundLoader.class
        ));

        getWindow().setNavigationBarColor(getResources().getColor(R.color.colorPrimaryDark));
        getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));

        mSlidingLayout = findViewById(R.id.sliding_pane_layout);
        mSlidingLayout.setPanelSlideListener(new SliderListener());

        mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        Variables.tWidth = mToolbar.getWidth();

        tpb = findViewById(R.id.pb);
        android_id = android.provider.Settings.Secure.getString(this.getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);

        homeImage = findViewById(R.id.expandedImage);
        missionView = findViewById(R.id.c_mission);

        PreferenceManager.setDefaultValues(this, R.xml.prefs, false);
        prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        try {
            MasterKey masterKey = new MasterKey.Builder(MainActivity.this)
                    .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                    .build();

            appPrefs = EncryptedSharedPreferences.create(
                    MainActivity.this,
                    "account_prefs",
                    masterKey,
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM);

            shpEditor = appPrefs.edit();

        }catch(Exception ex){
            ex.printStackTrace();
            Log.e(TAG, ex.getMessage());
        }

        panelHandler = new PanelHandler(this);
        parser = new Parser(this);

        commentEdit = findViewById(R.id.message_txt);
        sendBtn = findViewById(R.id.send);

        imbClose = findViewById(R.id.closeDrawer);

        replyHolder = findViewById(R.id.reply_holder);
        closeReply = findViewById(R.id.close_reply);
        replyTv = findViewById(R.id.reply_tv);
        replySenderTv = findViewById(R.id.reply_sender_tv);

        if (Monetize.monetize())
            UnityAds.initialize(getApplicationContext(), UNIT_ID, testMode, this);

        loadPreferences(appPrefs);

        tab = findViewById(R.id.tab_layout);
        pager = findViewById(R.id.viewpager);

        collapsingLayout = findViewById(R.id.toolbar_layout);
        showCaseLayout = findViewById(R.id.show_case_layout);

        parser.formatWeek(mFragmentTitleList);
        mpAdapter = new MainPageAdapter(MainActivity.this, getSupportFragmentManager(), mFragmentTitleList, parser, tpb);

        pBarView = View.inflate(this, R.layout.pbar_alertdialog, null);
        alertProgressBar = new AlertDialog.Builder(this).create();
        alertProgressBar.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertProgressBar.setView(pBarView);

        mRecyclerView = findViewById(R.id.chats_recycler);

        layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        layoutManager.setStackFromEnd(true);

        mRecyclerView.setLayoutManager(layoutManager);

        chatAdapter = new ChatAdapter(this, accountName, chats);
        mRecyclerView.setAdapter(chatAdapter);

        if (isFirstOpen && !isFirstRun) {
            deleteCache(getApplicationContext());
        }

        tab.setTabTextColors(Color.parseColor("#7FD0D4"), Color.parseColor("#7FFFD4"));
        tab.setTabMode(TabLayout.MODE_SCROLLABLE);
        pager.setAdapter(mpAdapter);
        pager.setOffscreenPageLimit(8);
        tab.setupWithViewPager(pager);

        selectPage(Calendar.getInstance().get(Calendar.DAY_OF_WEEK), tab, pager);
        indexPage = pager.getCurrentItem();
        index = indexPage;

        database = FirebaseDatabase.getInstance();
        
        refQC = database.getReference().child(Variables.content).child("comments");

        fabHand = findViewById(R.id.handle);

        database.getReference().child("data").child("app").child("version").keepSynced(true);

        new Thread(MainActivity.this::saveTitles).start();
        new Thread(MainActivity.this::savePluginUrl).start();

        mAppBarLayout = findViewById(R.id.appBarLayout);

        mAppBarLayout.addOnOffsetChangedListener((appBarLayout, verticalOffset) -> {
            if (Math.abs(verticalOffset) == appBarLayout.getTotalScrollRange()) {
                // Collapsed
                fabHand.hide();
            } else if (verticalOffset == 0) {
                // Expanded
                fabHand.show();
            }  // Somewhere in between


        });

        //refDayTopic = database.getReference().child(Variables.content).child(Variables.lessons).child(Grab.year(parser.convertedCalendar(Variables.dateIds.get(indexPage)))).child(Grab.quarter(parser.convertedCalendar(Variables.dateIds.get(indexPage)))).child(Grab.lesson(parser.convertedCalendar(Variables.dateIds.get(indexPage)))).child(Variables.dateIds.get(indexPage)).child(Variables.point);

        prefetchTitles();

        loadHomeImage(indexPage, FirebaseStorage.getInstance());

        collapsingLayout.setExpandedTitleTextAppearance(R.style.ExpandedAppBar);
        collapsingLayout.setCollapsedTitleTextAppearance(R.style.CollapsedAppBar);
        tab.setTabTextColors(ContextCompat.getColor(this, R.color.whisper), ContextCompat.getColor(this, R.color.whisper));

        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int index) {


                MainActivity.this.index = index;
                handler.removeCallbacks(loadRunnable);
                handler.postDelayed(loadRunnable, 1500);

                if (index == 7) weekSwitch = true;
                if (weekSwitch) reloadImg = true;

                if (weekSwitch && index < 7) {
                    reloadImg = true;
                    weekSwitch = false;
                }
                if (indexPage == 7 && index == 6) {
                    reloadImg = true;
                    weekSwitch = true;
                }
                loadHomeImage(index, FirebaseStorage.getInstance());

                slideCount++;

                if (slideCount % 14 == 0) {
                    if (UnityAds.isInitialized()) {
                        //Show ad reason
                        if (Variables.firstAdShow) {
                            panelHandler.showNotificationDialog(getString(R.string.app_name), getString(R.string.ads_reason_new), 1)
                                    .setOnDismissListener(dialogInterface -> {
                                        loadInterstitialAd();
                                        Variables.firstAdShow = false;
                                        savePreferences(shpEditor);
                                    });

                        } else {
                            loadInterstitialAd();
                        }

                    }

                }
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onPageScrollStateChanged(int arg0) {
                // TODO Auto-generated method stub
            }
        });

        FirebaseMessaging.getInstance().subscribeToTopic("ssdiscusskiny");
        FirebaseMessaging.getInstance().subscribeToTopic("webtopic");

        refQC.addChildEventListener(commentsChildListener);

        fabHand.setOnClickListener(clickListener);
        imbClose.setOnClickListener(clickListener);
        sendBtn.setOnClickListener(clickListener);

        if (!appPrefs.getBoolean("firstRun", true)) {

            new TestInternet(1).execute();
            AppRater.app_launched(this);
            parser.runCounter(appPrefs);

            runOnUiThread(() -> panelHandler.loadPend(true, database));

            runOnUiThread(() -> checkForIntro());

            runOnUiThread(() -> checkCurrentResources(false));

        }

        showedUseCase = appPrefs.getBoolean("showed_case", false);
        if (!showedUseCase) {
            handler.postDelayed(showCaseAndWait, 8500);
        }

        if (appPrefs.getBoolean("firstRun", true)) {
            isFirstRun = false;
            savePreferences(shpEditor);
        }

        if (getIntent().getAction() != null) {
            String action = getIntent().getAction();
            Log.d(TAG, action);
            if (action.equals("CLEAR NOTIFICATION")) {
                NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                notificationManager.cancel(100);
            }

            Animation animShake = AnimationUtils.loadAnimation(this, R.anim.anim_shake);
            fabHand.startAnimation(animShake);

        }

        Bundle calledExtra = getIntent().getExtras();

        String action = "";

        if (calledExtra != null) {
            if (calledExtra.containsKey("action")) {
                action = calledExtra.getString("action");
            }
            if (calledExtra.containsKey("splashLColor")) {
                appPrefs.edit().putString("splashLColor", calledExtra.getString("splashLColor")).commit();
            }
            if (calledExtra.containsKey("splashRColor")) {
                appPrefs.edit().putString("splashRColor", calledExtra.getString("splashRColor")).commit();
            }
            if (calledExtra.containsKey("dltClrs")) {
                appPrefs.edit().remove("splashRColor")
                        .remove("splashLColor")
                        .commit();
            }
            if (calledExtra.containsKey("opencmnt")) {
                mSlidingLayout.openPane();
                NotificationManager manager = (NotificationManager) getApplicationContext()
                        .getSystemService(NOTIFICATION_SERVICE);

                manager.cancelAll();
            }

            if (calledExtra.containsKey("TAB")) {
                int tabIndex = calledExtra.getInt("TAB");
                selectPage(tabIndex, tab, pager);
            }

        }

        if (!appPrefs.getBoolean("firstRun", true)) {
            String logImageInfo = appPrefs.getString("startImgData", "wantToDownLogo");
            if (logImageInfo.equals("wantToDownLogo") || action.equals("wantToDownLogo")) {
                downloadFile(FileDownloader.themeName, appPrefs);
            }
            String bibleData = appPrefs.getString("bibleData", "wantToDownBible");
            if (bibleData.equals("bibleData") || action.equals("wantToDownBible")) {
                panelHandler.downloadPlugin(appPrefs, fetchPluginUrl(), true);
            }

        }
        if (appPrefs.contains("bibleCompleted")) {
            if (!appPrefs.getBoolean("bibleCompleted", false)) {
                //ContextWrapper contextWrapper = new ContextWrapper(getApplicationContext());
                File directory = getDatabasePath(FileDownloader.bibleFName);

                if (directory.exists()) {
                    directory.delete();
                }
            }
        }

        Thread threadB = new Thread() {
            public void run() {
                //Logic
                loadDedicatedMessage();

                database.getReference().child(Variables.content).child(Variables.lessons).keepSynced(true);
                DatabaseReference refSplash = database.getReference().child("data").child("splash");
                refSplash.child("left").addValueEventListener(new ValueEventListener() {

                    @Override
                    public void onDataChange(DataSnapshot ds) {
                        String left = ds.getValue(String.class);
                        if (left != null) {
                            if (!left.equals("default")) {
                                appPrefs.edit().putString("splashLColor", left).commit();
                            } else {
                                if (appPrefs.contains("splashLColor")) {
                                    appPrefs.edit().remove("splashLColor")
                                            .commit();
                                }
                            }

                        } else {
                            if (appPrefs.contains("splashLColor")) {
                                appPrefs.edit().remove("splashLColor")
                                        .commit();
                            }

                        }
                        ds.getRef().removeEventListener(this);

                    }

                    @Override
                    public void onCancelled(DatabaseError de) {
                    }


                });
                refSplash.child("right").addValueEventListener(new ValueEventListener() {

                    @Override
                    public void onDataChange(DataSnapshot ds) {
                        String right = ds.getValue(String.class);
                        if (right != null) {
                            if (!right.equals("default")) {
                                appPrefs.edit().putString("splashRColor", right).commit();
                            } else {
                                if (appPrefs.contains("splashRColor")) {
                                    appPrefs.edit().remove("splashRColor")
                                            .commit();
                                }
                            }

                        } else {
                            if (appPrefs.contains("splashRColor")) {
                                appPrefs.edit().remove("splashRColor")
                                        .commit();
                            }
                        }
                        ds.getRef().removeEventListener(this);

                    }

                    @Override
                    public void onCancelled(DatabaseError de) {
                    }

                });
            }
        };
        threadB.start();

        Consts.receiverClass = LessonActivity.class;

        if (parser.getRunCount(appPrefs) >= 3) {
            handler.postDelayed(() -> checkPluginVersion(), 5000);
        }
        if (parser.getRunCount(appPrefs) >= 7) {
            handler.postDelayed(checkStory, 120000);
            handler.postDelayed(ratingRunnable, 180000);
            handler.postDelayed(videoRunnable, 300000);
        }

        if (!appPrefs.getBoolean("showed_ref_hint", false) && panelHandler.canComment(null, accountName)) {
            commentEdit.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    commentEdit.removeTextChangedListener(this);
                    panelHandler.showNotificationDialog(getString(R.string.write_title), getString(R.string.write_ref_hint), 1)
                            .setOnDismissListener(dialogInterface -> {
                                appPrefs.edit().putBoolean("showed_ref_hint", true).apply();
                            });
                }

                @Override
                public void afterTextChanged(Editable editable) {

                }
            });
        }
        new SwipeHelper(this, mRecyclerView) {
            @Override
            public void instantiateUnderlayButton(RecyclerView.ViewHolder viewHolder, List<UnderlayButton> underlayButtons) {
                underlayButtons.add(new SwipeHelper.UnderlayButton(
                        MainActivity.this,
                        getString(R.string.report),
                        R.drawable.ic_report_24,
                        ContextCompat.getColor(MainActivity.this, R.color.transparent),
                        pos -> {
                            // TODO: onDelete
                            mRecyclerView.getAdapter().notifyItemChanged(pos);
                            Log.v(TAG, "REPORT " + chats.get(pos).getId());
                            Chat chat = chats.get(pos);
                            reportComment(chat);

                        }
                ));

                underlayButtons.add(new SwipeHelper.UnderlayButton(
                        MainActivity.this,
                        getString(R.string.go_to_lesson),
                        R.drawable.ic_goto_link_24,
                        ContextCompat.getColor(MainActivity.this, R.color.transparent),
                        pos -> {
                            // TODO: OnTransfer
                            Chat chat = chats.get(pos);
                            final String key = chat.getDate().replace("/", "_");

                            int dayId = -1;
                            Calendar c = Calendar.getInstance();

                            try {
                                c.setTime(new SimpleDateFormat("dd_MM_yyyy").parse(key));
                                c.setFirstDayOfWeek(Calendar.SUNDAY);
                                dayId = c.get(Calendar.DAY_OF_WEEK);
                            } catch (ParseException e) {
                                e.printStackTrace();
                                Log.e(TAG, "Parse error: " + e.getMessage());
                            }

                            Intent lessonIntent = new Intent(MainActivity.this, LessonActivity.class);
                            lessonIntent.putExtra("date_value", key);
                            lessonIntent.putExtra("day_id", dayId);
                            lessonIntent.putExtra("caller", "OPEN");
                            lessonIntent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                            lessonIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                            startActivity(lessonIntent);
                            mRecyclerView.getAdapter().notifyItemChanged(pos);
                        }
                ));
                underlayButtons.add(new SwipeHelper.UnderlayButton(
                        MainActivity.this,
                        getString(R.string.reply),
                        R.drawable.ic_reply,
                        ContextCompat.getColor(MainActivity.this, R.color.transparent),
                        pos -> {
                            // TODO: OnUnshare
                            Chat chat = chats.get(pos);
                            String msg = chat.getMessage();

                            mRecyclerView.getAdapter().notifyItemChanged(pos);

                            replyTv.setText(msg);

                            final String repId = chat.getId().equals(accountName) ? "You" : chat.getId();

                            replySenderTv.setText(repId);
                            replyID = chat.getMessageId();
                            commentSender = chat.getId();

                            int msgLength = msg.length();

                            if (msgLength > 111) {
                                int needed = msgLength / 3;
                                replyHintTxt = msg.substring(0, needed) + "...";
                            } else replyHintTxt = msg;
                            replyHolder.setVisibility(View.VISIBLE);
                        }
                ));
            }
        };

        closeReply.setOnClickListener(v -> {
            replyID = null;
            replyHintTxt = null;
            v.startAnimation(AnimationUtils.loadAnimation(MainActivity.this, R.anim.image_click));
            replyHolder.setVisibility(View.GONE);
            replySenderTv.setText("");
            replyTv.setText("");
        });

        new Thread(()-> load(database)).start();

    }//End of on create

    @Override
    public void onBackPressed() {
        // TODO: Implement this method

        handler.removeCallbacks(exitRunnable);
        if (isAdLoaded){
            if (Variables.firstAdShow) {
                panelHandler.showNotificationDialog(getString(R.string.app_name), getString(R.string.ads_reason_new), 1)
                        .setOnDismissListener(dialogInterface -> {
                            UnityAds.show((MainActivity.this), adUnitId, new UnityAdsShowOptions(), showListener);
                            handler.postDelayed(exitRunnable, 3500);
                            Variables.firstAdShow = false;
                            savePreferences(shpEditor);
                        });
            } else {
                UnityAds.show((MainActivity.this), adUnitId, new UnityAdsShowOptions(), showListener);
                handler.postDelayed(exitRunnable, 3500);
            }
            if (Variables.firstVidAd && !Variables.firstAdShow) {
                panelHandler.showToast(getString(R.string.m_first_reward));
                Variables.firstVidAd = false;
                savePreferences(shpEditor);
            }


        }else {
            panelHandler.showExit();
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        if (getUpdateAppCode() != 0) {
            if (getUpdateAppCode() <= panelHandler.getVersionCode()) {
                removeUpdateFile();
            }
        }
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
    protected void onResume() {
        // TODO: Implement this method
        super.onResume();

        AppConstants.fontSize = Integer.parseInt(prefs.getString("font", "18"));
        AppConstants.isNightMode = prefs.getBoolean("night", false);
        AppConstants.referanceClickable = prefs.getBoolean("underline", false);
        AppConstants.referenceColor = prefs.getString("recolor", "#0000ff");
        AppConstants.isDefFont = prefs.getBoolean("defFont", false);
        AppConstants.markImportant = prefs.getBoolean("mark", true);
        AppConstants.font = prefs.getString("font_family", "cambo");
        AppConstants.saveOpenedReference = prefs.getBoolean("save_references", true);

        Variables.font_size = Integer.parseInt(prefs.getString("font", "18"));
        Variables.showAlert = prefs.getBoolean("showAlert", true);
        Variables.scrollTitle = prefs.getBoolean("scroll_title", false);
        Variables.display_font_size = Integer.parseInt(prefs.getString("display_size", "18"));

        if (prefs.getBoolean("show_tabs", false)) {
            collapsingLayout.setExpandedTitleMarginBottom(164);
            tab.setVisibility(View.VISIBLE);
        } else {
            collapsingLayout.setExpandedTitleMarginBottom(42);
            tab.setVisibility(View.GONE);
        }

        panelHandler.contentTv.setTextSize(TypedValue.COMPLEX_UNIT_SP, Variables.display_font_size);

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

        if (!isFirstRun) {
            Variables.night_mode = prefs.getBoolean("night", false);
        }
        if (Variables.ntfTimeChanged) {
            setAlarm();
            Variables.ntfTimeChanged = false;
            Log.d(TAG, "Time changed");
        }
        if (Variables.mOptionChanged) {
            runOnUiThread(() -> checkForMission());
            Variables.mOptionChanged = false;
            Log.d(TAG, "Mission option changed");
        }

       // parser.loadTitles(collapsingLayout, refQuarterTitle, refWeekTitle, refDayTopic, indexPage, null, prefs.getBoolean("show_tabs", false));


    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        Log.d("ACTIVITYLIFE", "onAttachedToWindow()");
        runOnUiThread(() -> {
            checkForMission();
            panelHandler.generateFeatureDescription(parser, appPrefs);
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "ON STOP");
    }

    @Override
    protected void onDestroy() {
        // TODO: Implement this method
        super.onDestroy();
        setAlarm();

        refQC.removeEventListener(commentsChildListener);
        Variables.ntfShowed = false;
        panelHandler.handler.removeCallbacks(panelHandler.waitRun);
        panelHandler.waitRun = null;

        handler.removeCallbacks(videoRunnable);
        handler.removeCallbacks(ratingRunnable);
        handler.removeCallbacks(checkStory);

        refQC = null;
        savePreferences(shpEditor);
        Variables.dateIds.clear();
        if (accountValidated) {
            Thread thread = new Thread() {
                @Override
                public void run() {
                    super.run();
                    updateLogs();
                }
            };

            thread.start();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // TODO: Implement this method
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);

        MenuItem itemChangeName = menu.findItem(R.id.changename);
        if (!panelHandler.canComment(null, accountName)) {
            itemChangeName.setVisible(false);
        }

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // TODO: Implement this method
        int menuId = item.getItemId();

        switch (menuId) {
            case R.id.setts:
                Intent prefs = new Intent(getApplicationContext(), Settings.class);
                prefs.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                prefs.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(prefs);
                overridePendingTransition(0, 0);
                break;
            case R.id.gotoLesson:
                panelHandler.goToLesson();
                break;
            case R.id.read_verse:
                readVerse();
                break;
            case R.id.share:
                panelHandler.shareApplication();
                break;
            case R.id.about:
                startActivity(new Intent(this, ReadActivity.class));
                break;
            case R.id.library:
                startActivity(new Intent(this, LibraryActivity.class));
                break;
            case R.id.dwldFile:
                new TestInternet(2).execute();
                break;
            case R.id.about_quarter:
                Intent aboutQuarter = new Intent(this, IntroActivity.class);
                aboutQuarter.putExtra("date_value", Variables.dateIds.get(pager.getCurrentItem()));
                startActivity(aboutQuarter);
                break;
            case R.id.changename:
                panelHandler.buttonDialog(getString(R.string.change_name_hint) + " " + accountName, getString(R.string.change_username), getString(R.string.dismiss), getString(R.string.yes)).setOnClickListener(v -> {
                    panelHandler.mAlertDialog.dismiss();
                    Intent changeName = new Intent(MainActivity.this, LoginActivity.class);
                    changeName.putExtra("change_name", accountName);
                    startActivity(changeName);
                    finish();
                });
                break;
            case R.id.insideStory:
                Intent intent = new Intent(this, StoryActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                //put some extra
                startActivity(intent);
                overridePendingTransition(0, 0);
                break;
            case R.id.verse_records:
                panelHandler.uiSetters();
                startActivity(new Intent(this, VersesActivity.class));
                break;
            case R.id.go_to_highlight:
                startActivity(new Intent(this, HighlightsActivity.class));
                break;
            case R.id.go_to_notes:
                startActivity(new Intent(this, NotesActivity.class));
                break;
            case R.id.watch_video:
                startActivity(new Intent(this, VideosActivity.class));
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            //Download file now
            //Check File in wrapper
            ContextWrapper wrapper = new ContextWrapper(this);
            File pluginFile = wrapper.getDatabasePath(FileDownloader.bibleFName);
            if ((!pluginFile.exists() && !pRequestOnFirstRun) || isFirstOpen || isFirstRun) {
                downloadFile(FileDownloader.themeName, appPrefs);
                downloadSkinFile(FileDownloader.skinName, appPrefs);
                panelHandler.downloadPlugin(appPrefs, fetchPluginUrl(), false);
            }

        }
        pRequestOnFirstRun = true;
    }

    @Override
    protected void onUserLeaveHint() {
        Log.d("USER_LEAVE_HINT", "LEAVE DETECTED");
        super.onUserLeaveHint();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 10) {
            ApplicationInfo app = getApplicationContext().getApplicationInfo();
            File tempFile = new File(getExternalCacheDir() + "/ExtractedApk/");
            tempFile = new File(tempFile.getPath() + "/" + getString(app.labelRes));
            if (tempFile.delete()) Log.d("CLEAR_CACHE", "CACHE CLEARED");
            tempFile = new File(getExternalCacheDir() + "/ExtractedApk/");
            if (tempFile.delete()) Log.d("CLEAR_CACHE", "CACHE CLEARED");
        }
    }

    Runnable ratingRunnable = new Runnable() {
        @Override
        public void run() {
            panelHandler.showRateDialog(parser.getRunCount(appPrefs), accountId, appPrefs);
        }
    };

    Runnable videoRunnable = () -> loadTrendingVid();

    Runnable checkStory = () -> checkStory();

    Runnable showCaseAndWait = this::showUseCases;

    Runnable exitRunnable = ()-> panelHandler.showExit();

    Runnable loadRunnable = ()-> parser.loadTitles(collapsingLayout, hashMap2, hashMap, index, null, prefs.getBoolean("show_tabs", false));

    @Override
    public void onInitializationComplete() {
        loadInterstitialAd();
    }

    @Override
    public void onInitializationFailed(UnityAds.UnityAdsInitializationError error, String message) {
        Log.e(TAG, message);
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
            isAdLoaded = false;
            loadInterstitialAd();
        }
    };

    public void loadInterstitialAd() {
        new Thread(()->UnityAds.load(adUnitId, loadListener)).start();
    }

    private class SliderListener extends
            SlidingPaneLayout.SimplePanelSlideListener {
        @Override
        public void onPanelOpened(View panel) {

            new TestInternet(1).execute();

            NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            notificationManager.cancel(100);
            if (!panelHandler.canComment(findViewById(R.id.btSheet), accountName)) {
                panelHandler.createUsernameAlert();
                Log.d("SLIDING_DRAWER", "OPENING");
            } else {
                panelHandler.canComment(findViewById(R.id.btSheet), accountName);
                if (!prefs.getBoolean("comment_hint_shown", false)) {
                    //panelHandler.showToast(getString(R.string.comment_hint));
                    prefs.edit().putBoolean("comment_hint_shown", true).apply();
                    //Show use case scroll animation
                }

                if (!prefs.getBoolean("show_comments_case", false)) {
                    showCommentUseCase();
                    prefs.edit().putBoolean("show_comments_case", true).apply();
                }

            }

        }

        @Override
        public void onPanelClosed(View panel) {
            commentEdit.clearFocus();
            hideKeyboardFrom(MainActivity.this, commentEdit);
        }
    }

    public void selectPage(int pageIndex, TabLayout tablayout, ViewPager viewPager) {
        tablayout.setScrollPosition(pageIndex, 0f, true);
        viewPager.setCurrentItem(pageIndex);
    }

    private void setAlarm() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, Integer.parseInt(prefs.getString("ntfHr", "8")));
        cal.set(Calendar.MINUTE, 00);
        cal.set(Calendar.SECOND, 00);
        Intent intent = new Intent(this, AlarmReceiver.class);
        alarmIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_MUTABLE);
        if (alarmMgr != null) {
            alarmMgr.cancel(alarmIntent);
        }
        alarmMgr = (AlarmManager) this.getSystemService(this.ALARM_SERVICE);
        alarmMgr.setInexactRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), AlarmManager.INTERVAL_DAY, alarmIntent);

        String curDate = new SimpleDateFormat("dd-MM-yyyy").format(Calendar.getInstance().getTime());
        SharedPreferences alarmPrefs = getSharedPreferences("Alarm", MODE_PRIVATE);

        alarmPrefs.edit().putString("f_alarm_date", curDate).apply();

    }

    private void hideKeyboardFrom(Context context, EditText editText) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(AppCompatActivity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
    }

	/*private void sendTokenId() {
		if (appPrefs.contains("fToken") && !Variables.userName.equals("[noUsername]")) {
			String refreshedToken = appPrefs.getString("fToken", "noToken");
			refUsers.child().child(Variables.userName).child("token").setValue(refreshedToken);
		}
	}*/

    private void savePreferences(SharedPreferences.Editor editor) {
        editor.putBoolean("firstRun", isFirstRun);
        //editor.putString("username", Variables.userName);
        editor.putBoolean("firstvidad", Variables.firstVidAd);
        editor.putBoolean("firstadshow", Variables.firstAdShow);
        editor.putBoolean("showed_case", showedUseCase);
        editor.putBoolean("first_open", isFirstOpen);
        editor.putBoolean("requested_permission", pRequestOnFirstRun);
        editor.apply();
    }

    private void loadPreferences(SharedPreferences sharedPreference) {

        //
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

        isFirstRun = sharedPreference.getBoolean("firstRun", isFirstRun);
        Variables.firstVidAd = sharedPreference.getBoolean("firstvidad", Variables.firstVidAd);
        Variables.firstAdShow = sharedPreference.getBoolean("firstadshow", Variables.firstAdShow);
        isFirstOpen = sharedPreference.getBoolean("first_open", true);
        accountId = sharedPreference.getString("account_id","");
        accountName = sharedPreference.getString("account_name", "[noUsername]");
        pRequestOnFirstRun = sharedPreference.getBoolean("requested_permission", pRequestOnFirstRun);
    }

    View.OnClickListener clickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            // TODO: Implement this method
            v.startAnimation(AnimationUtils.loadAnimation(MainActivity.this, R.anim.image_click));
            int id = v.getId();
            switch (id) {
                case R.id.handle:
                    mSlidingLayout.openPane();
                    Log.d(TAG, "Opening drawer " + mSlidingLayout.isSlideable());
                    break;

                case R.id.closeDrawer:
                    boolean o = mSlidingLayout.closePane();
                    hideKeyboardFrom(MainActivity.this, commentEdit);
                    Log.d(TAG, "Closing drawer " + o);
                    break;
                case R.id.send:
                    replyHolder.setVisibility(View.GONE);
                    String comment = commentEdit.getText().toString();
                    if (!comment.isEmpty() && panelHandler.canComment(null, accountName)) {
                        new CommentProcessor().execute(comment);
                    } else {
                        panelHandler.createUsernameAlert();
                    }

                    break;
            }
        }

    };

    class TestInternet extends AsyncTask<Void, Void, Boolean> {

        int action;

        public TestInternet(int action) {
            this.action = action;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                URL url = new URL("https://www.google.com/");
                HttpURLConnection urlc = (HttpURLConnection) url.openConnection();
                urlc.setConnectTimeout(3000);
                urlc.connect();
                if (urlc.getResponseCode() == 200) {
                    return true;
                }
            } catch (MalformedURLException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
                return false;
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                return false;
            }
            return false;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (action == 2 && !alertProgressBar.isShowing()) {
                alertProgressBar.setCancelable(false);
                alertProgressBar.show();
            }

        }

        @Override
        protected void onPostExecute(Boolean result) {

            if (alertProgressBar.isShowing()) {
                alertProgressBar.dismiss();
            }
            if (!result) {
                if (action == 1) {
                    activeNet = false;
                }
                if (action == 2) {
                    panelHandler.showNetError(1, getString(R.string.net_error_title))
                            .setOnClickListener(v -> {
                                if (panelHandler.mAlertDialog != null) {
                                    panelHandler.mAlertDialog.dismiss();
                                }
                                new TestInternet(action).execute();
                            });
                    if (panelHandler.mAlertDialog != null) {
                        panelHandler.mAlertDialog.show();
                    }
                }
                if (action == 3) {
                    if (getUpdateAppCode() == 0) {
                        panelHandler.showNetError(1, getString(R.string.update_net_error))
                                .setOnClickListener(v -> {
                                    panelHandler.mAlertDialog.dismiss();
                                    alertProgressBar.show();
                                    new TestInternet(action).execute();
                                });
                    } else {
                        panelHandler.buttonDialog(getString(R.string.new_version_text), getString(R.string.new_version), getString(R.string.not_now), getString(R.string.install))
                                .setOnClickListener(v -> {
                                    if (isStoragePermissionGranted()) {
                                        runOnUiThread(() -> {
                                            panelHandler.mAlertDialog.dismiss();
                                            File f = updatesFolder();
                                            File newFile = new File(f.getPath() + "/" + getString(R.string.app_name).replace(" ", "_") + ".apk");
                                            if (newFile.exists()) {

                                                Uri apkUri = FileProvider.getUriForFile(MainActivity.this, getPackageName() + ".provider", newFile);

                                                Intent installIntent = new Intent(Intent.ACTION_VIEW);

                                                installIntent.setDataAndType(apkUri, "application/vnd.android.package-archive");
                                                installIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                installIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

                                                startActivity(installIntent);
                                            }
                                        });
                                    }
                                });

                    }

                }
            } else {
                runOnUiThread(()->{
                    new Thread(MainActivity.this::fetchUpdates).start();
                });
                database.getReference()
                        .child("registry")
                        .child("users")
                        .child("creds")
                        .child(accountId)
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (!snapshot.exists()) {
                                    panelHandler.buttonDialog(getString(R.string.crucial_error), getString(R.string.app_name), "", getString(R.string.continue_hint))
                                            .setOnClickListener(v -> {
                                                finish();
                                                appPrefs.edit().remove("account_id").commit();
                                                appPrefs.edit().remove("account_name").commit();
                                                startActivity(new Intent(MainActivity.this, SplashActivity.class));
                                            });
                                } else {
                                    accountValidated = true;
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                if (action == 1) {
                    activeNet = true;
                    if (!didCheck) {
                        if (isStoragePermissionGranted()) {
                            checkForLogo();
                            checkForSkin();
                        }
                    }
                } else if (action == 2) {
                    //First ask permission
                    if (isStoragePermissionGranted()) {
                        panelHandler.downloadPlugin(appPrefs, fetchPluginUrl(), false);
                    }
                } else if (action == 3) {
                    Log.d(TAG, "TEST NET COMPLETED");
                }
            }
        }
    }

    private void loadHomeImage(int index, FirebaseStorage storage) {

        final StorageReference imageRef = storage.getReference().child("images").child(Grab.year(parser.convertedCalendar(Variables.dateIds.get(index)))).child(Grab.quarter(parser.convertedCalendar(Variables.dateIds.get(index)))).child(Grab.lesson(parser.convertedCalendar(Variables.dateIds.get(index))) + ".jpg");
        Log.v(TAG, imageRef.toString());

        final SharedPreferences urlPrefs = getSharedPreferences("urls", MODE_PRIVATE);

        final String imageKey = urlKey(parser.convertedCalendar(Variables.dateIds.get(index)));

        final String url = urlPrefs.getString(imageKey, "default");

        if (fOnCreate || reloadImg) {
            Log.v(TAG, "" + index);
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
                // Pass it to Picasso to download, show in ImageView and caching
                //Save this image link as it may be used even offline
                final String uriUrl = String.valueOf(uri);
                if (!url.equals(uriUrl)) {
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
                } else {
                    Log.v(TAG, "URL DIDN'T CHANGE");
                }

            }).addOnFailureListener(exception -> {
                // Handle any errors
                Log.v(TAG, "FAILED " + exception.getMessage());
            });
        }
        fOnCreate = false;
        reloadImg = false;

    }

    private void detectColor() {

        runOnUiThread(() -> {
            Drawable d = homeImage.getDrawable();
            Bitmap bitmap = ((BitmapDrawable) d).getBitmap();

            Palette.from(bitmap).generate(p -> {
                Palette.Swatch vibrant = p.getDominantSwatch();
                // Use generated instance
                try {
                    getWindow().setStatusBarColor(vibrant.getRgb());
                    getWindow().setNavigationBarColor(vibrant.getRgb());

                    collapsingLayout.setContentScrimColor(vibrant.getRgb());
                    collapsingLayout.setStatusBarScrimColor(vibrant.getRgb());

                    fabHand.setBackgroundTintList(ColorStateList.valueOf(vibrant.getRgb()));

                } catch (Exception ex) {
                    ex.printStackTrace();
                    getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));
                    getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));
                    collapsingLayout.setContentScrimColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));
                    collapsingLayout.setStatusBarScrimColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));

                    Log.e(TAG, "Vibrant wasn't detected!");
                }
            });
        });

    }

    private String urlKey(Calendar cal) {
        return Grab.year(cal) + "_" + Grab.quarter(cal) + "_" + Grab.lesson(cal);
    }

    private void downloadFile(String file, SharedPreferences appPrefs) {
        FileDownloader fileDownloader = new FileDownloader(this, panelHandler);
        try {
            fileDownloader.downloadResources(fileDownloader.refStartImg, file, appPrefs);
        } catch (IOException e) {
            panelHandler.showToast(getString(R.string.unable_to_down));
        }
    }

    private void downloadSkinFile(String file, SharedPreferences appPrefs) {
        FileDownloader fileDownloader = new FileDownloader(this, panelHandler);
        try {
            fileDownloader.downloadResources(fileDownloader.refSkinImg, file, appPrefs);
        } catch (IOException e) {
            panelHandler.showToast(getString(R.string.unable_to_down));
        }
    }

    private void readVerse() {

        final AlertDialog mDialog = new AlertDialog.Builder(this).create();

        View dialogView = LayoutInflater.from(this)
                .inflate(R.layout.open_verse_dialog, null);
        mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mDialog.setView(dialogView);

        mDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        mDialog.setCanceledOnTouchOutside(true);

        Window window = mDialog.getWindow();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        window.setGravity(Gravity.CENTER);

        Button okBtn = dialogView.findViewById(R.id.request_verse);
        Button closeBtn = dialogView.findViewById(R.id.close_dlg);

        final EditText verseInput = dialogView.findViewById(R.id.reference_input);

        okBtn.setOnClickListener(v -> {
            String reference = verseInput.getText().toString();
            if (!reference.isEmpty()) {
                mDialog.dismiss();
                panelHandler.readVerse(reference);
            } else {
                verseInput.setError(getString(R.string.empty_ref));
            }
        });
        closeBtn.setOnClickListener(v -> mDialog.dismiss());
        mDialog.show();
    }

    /*private boolean checkFiLeInWrapper(String fileName) {
        ContextWrapper contextWrapper=new ContextWrapper(getApplicationContext());
        File directory = contextWrapper.getDir(FileDownloader.folderOfFiles, MODE_PRIVATE);
        File file = new File(directory, fileName);

        if (file.exists()) {
            return true;
        } else {
            return false;
        }
    }*/

    private void checkForLogo() {
        DatabaseReference logoRef = database.getReference().child("data").child("logo_info");
        DatabaseReference dfShowImg = database.getReference().child("data").child("df_show_url");
        logoRef.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot ds) {
                String info = ds.getValue(String.class);
                ds.getRef().removeEventListener(this);
                if (info != null) {
                    if (!info.isEmpty()) {
                        if (!info.equals(appPrefs.getString("logo_info", "no info"))) {
                            if (isStoragePermissionGranted()) {
                                downloadFile(FileDownloader.themeName, appPrefs);
                                appPrefs.edit().putString("logo_info", info).commit();
                            }

                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError de) {
            }


        });
        dfShowImg.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot ds) {
                String infos = ds.getValue(String.class);
                ds.getRef().removeEventListener(this);
                if (infos != null) {
                    if (!infos.isEmpty()) {
                        if (!infos.equals(appPrefs.getString("df_show_url", ""))) {
                            downloadFile(FileDownloader.themeName, appPrefs);
                            appPrefs.edit().putString("df_show_url", infos).commit();
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError de) {
            }


        });
    }

    private void checkForSkin() {
        DatabaseReference logoRef = database.getReference().child("data").child("skin_info");
        logoRef.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot ds) {
                String infos = ds.getValue(String.class);
                ds.getRef().removeEventListener(this);
                if (infos != null) {
                    if (!infos.isEmpty()) {
                        if (!infos.equals(appPrefs.getString("skin_data", "no info"))) {
                            if (isStoragePermissionGranted()) {
                                downloadSkinFile(FileDownloader.skinName, appPrefs);
                                appPrefs.edit().putString("skin_data", infos).commit();
                            }
                        }
                    }
                } else {
                    File dir = MainActivity.this.getFilesDir();
                    File file = new File(dir, "im002.png");
                    file.delete();
                }
            }

            @Override
            public void onCancelled(DatabaseError de) {
            }


        });
    }

    private String notifyMsg(String title, String body) throws IOException, JSONException {
        FirebaseMessaging.getInstance().unsubscribeFromTopic("ssdiscusskiny");
        String string6;
        HttpURLConnection httpUrlConnection = (HttpURLConnection) new URL(FMCurl).openConnection();

        httpUrlConnection.setUseCaches(false);
        httpUrlConnection.setDoInput(true);
        httpUrlConnection.setDoOutput(true);
        httpUrlConnection.setRequestMethod("POST");

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("key=");
        stringBuilder.append(authKey);
        httpUrlConnection.setRequestProperty("Authorization", stringBuilder.toString());
        httpUrlConnection.setRequestProperty("Content-Type", "application/json");

        JSONObject jSONObject = new JSONObject();

        StringBuilder stringBuilder2 = new StringBuilder();
        stringBuilder2.append("/topics/");
        stringBuilder2.append(topic.trim());
        jSONObject.put("to", stringBuilder2.toString());

        JSONObject jSONObject2 = new JSONObject();
        jSONObject2.put("click_action", "OPEN_ACTIVITY_2");
        jSONObject2.put("title", title);
        jSONObject2.put("body", body);
        jSONObject2.put("sound", "default");

        JSONObject js3 = new JSONObject();
        js3.put("opencmnt", "opencmnt");

        jSONObject.put("notification", jSONObject2);
        jSONObject.put("data", js3);

        OutputStreamWriter outputStreamWriter = new OutputStreamWriter(httpUrlConnection.getOutputStream());

        outputStreamWriter.write(jSONObject.toString());
        outputStreamWriter.flush();

        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(httpUrlConnection.getInputStream()));

        while ((string6 = bufferedReader.readLine()) != null) {
            System.out.println(string6);
            return string6;
        }

        return "";
    }

    class Pusher extends AsyncTask<Void, Void, String> {

        String msg;

        public Pusher(String msg) {
            this.msg = msg;
        }

        @Override
        protected String doInBackground(Void... params) {


            try {
                String sender = accountName;

                return notifyMsg(sender, Randoms.pushMsg(msg));
            } catch (IOException e) {
                e.printStackTrace();
                return "";
            } catch (JSONException e) {
                e.printStackTrace();
                return "";
            }


        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String result) {
            FirebaseMessaging.getInstance().subscribeToTopic("ssdiscusskiny");
        }
    }

    private boolean isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                return true;
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                return false;
            }
        } else { //permission is automatically granted on sdk<23 upon installation
            return true;
        }
    }

    private void updateLogs() {

        DatabaseReference databaseReference = database.getReference().child("registry").child("users").child("logs").child(accountId);
        Calendar calendar = Calendar.getInstance();
        databaseReference.child("last_seen").setValue(new SimpleDateFormat("dd/MM/yyyy HH:mm").format(calendar.getTime()));
        databaseReference.child("app_version").setValue(panelHandler.getVersionCode());

    }

    public AppBarLayout getMAppBarLayout() {
        return mAppBarLayout;
    }

    public void checkForIntro() {
        final Calendar calendar = Calendar.getInstance();
        final DatabaseReference qrtIntroRef = database.getReference().child(Variables.content).child(Variables.lessons).child(Grab.year(calendar)).child(Grab.quarter(calendar)).child("intro");
        qrtIntroRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getValue() != null) {
                    if (!appPrefs.getBoolean(Grab.quarter(calendar) + "_read", false)) {
                        //Request to read intro
                        panelHandler.readRequest(MainActivity.this);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    ChildEventListener commentsChildListener = new ChildEventListener() {
        @Override
        public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
            String commentKey = snapshot.getKey();
            if (commentKey != null && !commentKey.equals("details")) {
                refQC.child(commentKey).addListenerForSingleValueEvent(commentListener);
            }
        }

        @Override
        public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
            Object o = snapshot.getValue();


            if (o instanceof Map<?, ?>) {
                final String key = snapshot.getKey();
                final String message = snapshot.child("message").getValue(String.class);
                final String timeStamp = snapshot.child("timestamp").getValue(String.class);
                final String userName = snapshot.child("user").getValue(String.class);
                final String sender = snapshot.child("sender").getValue(String.class);

                if (message != null && timeStamp != null && userName != null) {
                    Iterator<Chat> iterator = chats.iterator();
                    while (iterator.hasNext()) {

                        Chat next = iterator.next();
                        if (next.getMessageId().equals(key)) {

                            final String[] timeStamps = timeStamp.split(" ");

                            final String date = timeStamps[0];
                            final String time = timeStamps[1];
                            final String zone = timeStamps[2];

                            next.setMessage(message);
                            next.setId(userName);
                            if (sender != null) next.setSender(sender);
                            next.setDate(date);
                            next.setTime(time);
                            next.setZone(zone);

                            break;
                        }
                    }

                    chatAdapter.notifyDataSetChanged();
                }
            }

        }

        @Override
        public void onChildRemoved(@NonNull DataSnapshot snapshot) {
            Iterator<Chat> iterator = chats.iterator();
            while (iterator.hasNext()) {
                Chat next = iterator.next();
                if (next.getMessageId().equals(snapshot.getKey())) {
                    iterator.remove();
                    break;
                }
            }

            chatAdapter.notifyDataSetChanged();
            layoutManager.setStackFromEnd(true);

            mRecyclerView.scrollToPosition(chatAdapter.getItemCount() - 1);

        }

        @Override
        public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {

        }
    };

    ValueEventListener commentListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            snapshot.getRef().removeEventListener(this);


            Object o = snapshot.getValue();
            if (o instanceof Map<?, ?>) {
                final String key = snapshot.getKey();
                final String message = snapshot.child("message").getValue(String.class);
                final String timeStamp = snapshot.child("timestamp").getValue(String.class);
                final String userName = snapshot.child("user").getValue(String.class);
                final String repKey = snapshot.child("reply").getValue(String.class);
                final String repHint = snapshot.child("respond").getValue(String.class);
                final String sender = snapshot.child("sender").getValue(String.class);

                if (message != null && timeStamp != null && userName != null) {
                    final String[] timeStamps = timeStamp.split(" ");

                    final String date = timeStamps[0];
                    final String time = timeStamps[1];
                    final String zone = timeStamps[2];

                    Chat chat = new Chat(message, key, userName, time, date, zone);

                    if (repKey != null) chat.setReplyId(repKey);
                    if (repHint != null) chat.setReplyHintText(repHint);
                    if (sender != null) chat.setSender(sender);


                    if (chats.size()>1){
                        //Check prev chat signature we suppose to be on top of list
                        Chat c = chats.get(chats.size()-1);

                        //We will disallow chat of same signature

                        if (!chat.chatSignature().equals(c.chatSignature())){
                            chats.add(chat);
                        }else{
                            Log.v(TAG, "Chat of same signature ignored");
                        }

                    }else{
                        chats.add(chat);
                    }


                    Collections.sort(chats);

                    chatAdapter.notifyDataSetChanged();
                    layoutManager.setStackFromEnd(true);

                    mRecyclerView.scrollToPosition(chatAdapter.getItemCount() - 1);
                } else {
                    Log.e(TAG, "COMMENT DISCARDED " + snapshot.getKey());
                }
            } else {
                Log.e(TAG, "UNMAPPED COMMENT IGNORED");
            }


            //Try to not remove this listener
        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {

        }
    };

    private String fetchPluginUrl(){
        String url = "https://www.dropbox.com/s/yaen3ckay27w8pq/kiny_bible?dl=1";
        String randomUrl = "";
        SharedPreferences urlPreferences = getSharedPreferences("plugin_urls", MODE_PRIVATE);
        Map<String, ?> set = urlPreferences.getAll();

        List<String> urls = new ArrayList<>();
        for (Map.Entry<String, ?> entry : set.entrySet()){
            Object o = entry.getValue();
            if ( o instanceof String){
                String value = (String)entry.getValue();
                urls.add(value);
            }

        }
        if (urls.size()>=1){
            Random random = new Random();
            randomUrl = urls.get(random.nextInt(urls.size()));
        }
        Log.v(TAG, "Random url: "+randomUrl);
        return randomUrl.isEmpty() ? url : randomUrl;
    }

    private void checkForMission() {

        final DatabaseReference mRef = database.getReference().child(Variables.content).child("missions").child("pend");

        mRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                snapshot.getRef().removeEventListener(this);
                snapshot.getRef().keepSynced(true);
                if (snapshot.hasChild("content")) {
                    missionView.setOnClickListener(v -> {
                        Intent missionIntent = new Intent(MainActivity.this, StoryActivity.class);
                        missionIntent.putExtra("s_type", "open_p_mission");
                        missionIntent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        missionIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                        startActivity(missionIntent);

                        overridePendingTransition(0, 0);

                    });
                } else {
                    missionView.setOnClickListener(null);
                }
                if (snapshot.exists()) {
                    mRef.child("logo_url").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot s) {
                            s.getRef().keepSynced(true);
                            s.getRef().removeEventListener(this);
                            if (s.exists()) {
                                String iconUrl = s.getValue(String.class);
                                if (iconUrl != null) {
                                    if (prefs.getBoolean("show_mission", true)) {
                                        missionView.setVisibility(View.VISIBLE);
                                        Picasso.get()
                                                .load(iconUrl)
                                                .resize(parser.dpToPx(48), parser.dpToPx(48))
                                                .into(missionView);

                                        //Set listener for
                                    } else {
                                        missionView.setVisibility(View.GONE);
                                    }
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                } else {
                    missionView.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    class CommentProcessor extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... strings) {

            String finalTxt = "";
            String[] texts = strings[0].split(" ");

            for (int i = 0; i < texts.length; i++) {
                String text = texts[i];
                if (text.startsWith("<<")) {
                    int count = StringUtils.countMatches(text, "<");
                    if (count == 2) text = text.replace("<<", "[");
                }

                if (text.endsWith(">>")) {
                    int count = StringUtils.countMatches(text, ">");
                    if (count == 2) text = text.replace(">>", "]");
                }

                if (i + 1 < texts.length) finalTxt += text + " ";
                else finalTxt += text;
            }
            return finalTxt;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            //Send comment
            String comment = s;
            final TimeZone timeZone = TimeZone.getDefault();
            final DatabaseReference push = refQC.push();
            final String pushKey = push.getKey();
            final Calendar cal = Calendar.getInstance();
            final String msg = comment;

            final String commentSet = comment + "#" + accountName + " " + new SimpleDateFormat("dd/MM/yyyy HH:mm").format(cal.getTime()) + " ⦿" + timeZone.getID();

            final String timestamp = new SimpleDateFormat("dd/MM/yyyy HH:mm").format(cal.getTime()) + " Asia/Dubai";

            chatAdapter.db.insertComment(pushKey, commentSet);

            Map<String, String> data = new HashMap<>();
            data.put("message", comment);
            data.put("user", accountName);
            data.put("timestamp", timestamp);

            if (replyID != null) data.put("reply", replyID);
            if (replyHintTxt != null) data.put("respond", replyHintTxt);
            if (commentSender != null) data.put("sender", commentSender);

            replyID = null;
            replyHintTxt = null;
            commentSender = null;

            refQC.child(pushKey).setValue(data, (error, ref) -> {
                Log.v(TAG, "COMMENT PUSHED");
                //Clear rep id if not null
                replyHolder.setVisibility(View.GONE);
                replyTv.setText("");
                replySenderTv.setText("");

                runOnUiThread(() -> {
                    chatAdapter.db.clearNotSent(pushKey);
                    chatAdapter.notifyDataSetChanged();
                    //new Pusher(msg).execute();
                    //We are not sure what can happen if run in offline state
                });

            });
            commentEdit.setText("");
        }
    }

    /*class DownloadFile extends AsyncTask<String, String, String> {
        final AlertDialog alertProgressbar;
        View pBarView;
        final TextView tvProgress;
        final ProgressBar progressBar;

        public DownloadFile() {
            alertProgressbar = new AlertDialog.Builder(MainActivity.this).create();
            pBarView = View.inflate(MainActivity.this, R.layout.download_dialog, null);
            alertProgressbar.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            alertProgressbar.setView(pBarView);

            tvProgress = pBarView.findViewById(R.id.pbartext);
            progressBar = pBarView.findViewById(R.id.progressBar);
            progressBar.setMax(100);

            alertProgressbar.setCancelable(false);
        }

        *//**
         * Before starting background thread Show Progress Bar Dialog
         *//*
        @Override
        protected void onPreExecute() {
            alertProgressbar.show();
            panelHandler.showToast(getString(R.string.download_warn));
            super.onPreExecute();
        }

        *//**
         * Downloading file in background thread
         *//*
        @Override
        protected String doInBackground(String... f_url) {
            int count;
            try {
                URL url = new URL(f_url[0]);
                URLConnection connection = url.openConnection();
                connection.connect();

                // this will be useful so that you can show a tipical 0-100%
                // progress bar
                int lengthOfFile = connection.getContentLength();

                // download the file
                InputStream input = new BufferedInputStream(url.openStream(),
                        8192);

                File file = updatesFolder();

                // Output stream
                OutputStream output = new FileOutputStream(file.getAbsolutePath()
                        + "/" + getString(R.string.app_name).replace(" ", "_") + ".apk");

                byte data[] = new byte[1024];

                long total = 0;

                while ((count = input.read(data)) != -1) {
                    total += count;
                    // publishing the progress....
                    // After this onProgressUpdate will be called
                    publishProgress("" + (int) ((total * 100) / lengthOfFile));

                    // writing data to file
                    output.write(data, 0, count);
                }

                // flushing output
                output.flush();

                // closing streams
                output.close();
                input.close();

            } catch (Exception e) {
                Log.e("Error: ", e.getMessage());
            }

            return null;
        }

        *//**
         * Updating progress bar
         *//*
        protected void onProgressUpdate(String... progress) {
            // setting progress percentage
            tvProgress.setText(progress[0] + "% ");
            progressBar.setProgress(Integer.parseInt(progress[0]));
        }

        *//**
         * After completing background task Dismiss the progress dialog
         **//*
        @Override
        protected void onPostExecute(String file_url) {
            // dismiss the dialog after the file was downloaded
            alertProgressbar.dismiss();

            File f = updatesFolder();
            File newFile = new File(f.getPath() + "/" + getString(R.string.app_name).replace(" ", "_") + ".apk");
            if (newFile.exists()) {

                Uri apkUri = FileProvider.getUriForFile(MainActivity.this, getPackageName() + ".provider", newFile);

                Intent installIntent = new Intent(Intent.ACTION_VIEW);

                installIntent.setDataAndType(apkUri, "application/vnd.android.package-archive");
                installIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                installIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

                startActivity(installIntent);
            }
            Log.d(TAG, "DONE");

        }

    }*/

    /*public void checkUpdates() {

        final DatabaseReference refVData = database.getReference().child("data").child("app").child("v_data");

        refVData.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                final Long v_code = snapshot.child("v_code").getValue(Long.class);
                if (v_code != null) {
                    if (panelHandler.getVersionCode() < v_code) {
                        //Load new version data and fire download task

                        if (getUpdateAppCode() != 0) {
                            if (getUpdateAppCode() <= panelHandler.getVersionCode()) {
                                //Remove the file from updates folder,
                                Log.d("CHECKUPDATES", "This update is already installed");
                                removeUpdateFile();
                            } else if (getUpdateAppCode() > panelHandler.getVersionCode()) {
                                //Fire install prompt to the user
                                panelHandler.buttonDialog(getString(R.string.new_version_text), getString(R.string.new_version), getString(R.string.not_now), getString(R.string.install))
                                        .setOnClickListener(v -> {
                                            if (isStoragePermissionGranted()) {
                                                runOnUiThread(() -> {
                                                    panelHandler.mAlertDialog.dismiss();
                                                    File f = updatesFolder();
                                                    File newFile = new File(f.getPath() + "/" + getString(R.string.app_name).replace(" ", "_") + ".apk");
                                                    if (newFile.exists()) {

                                                        Intent installIntent;

                                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                                            Uri apkUri = FileProvider.getUriForFile(MainActivity.this, getPackageName() + ".provider", newFile);
                                                            installIntent = new Intent(Intent.ACTION_VIEW);
                                                            installIntent.setDataAndType(apkUri, "application/vnd.android.package-archive");
                                                            installIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                            installIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                                                        } else {
                                                            Uri apkUri = Uri.fromFile(newFile);
                                                            installIntent = new Intent(Intent.ACTION_VIEW);
                                                            installIntent.setDataAndType(apkUri, "application/vnd.android.package-archive");
                                                            installIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                        }


                                                        if (installIntent != null)
                                                            startActivity(installIntent);
                                                    }
                                                });
                                            }
                                        });
                            }
                        } else {
                            new TestInternet(3).execute();
                        }

                        Log.d("CHECKUPDATES", "NEW V CODE");
                        //End check code
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }*/

    private void savePluginUrl(){
        database.getReference()
                .child("data")
                .child("urls")
                .child("plugin")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        snapshot.getRef().removeEventListener(this);
                        HashMap<String, String> hashMap = (HashMap<String, String>)snapshot.getValue();
                        Set<String> keys = hashMap.keySet();
                        SharedPreferences urlPrefs = getSharedPreferences("plugin_urls", MODE_PRIVATE);
                        Map<String, ?> prefKeys = urlPrefs.getAll();

                        for (Map.Entry<String, ?> entry : prefKeys.entrySet()){
                            if (!hashMap.containsKey(entry.getKey())) urlPrefs.edit().remove(entry.getKey()).commit();
                        }
                        String prevValue = "";
                        for (String key : keys){
                            String value = hashMap.get(key);
                            value = value!=null ? value : "https://www.dropbox.com/s/yaen3ckay27w8pq/kiny_bible?dl=1";
                            if(!prevValue.equals(value))urlPrefs.edit().putString(key, value).commit();
                            prevValue = value;
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e(TAG, error.getMessage()+" "+error.getDetails());
                    }
                });
    }

    private void fetchUpdates() {

        Log.d(TAG, "START FETCH...");

        database.getReference().child("data").child("app").child("version")
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                snapshot.getRef().keepSynced(true);
                                HashMap<String, String> versionData = (HashMap<String, String>) snapshot.getValue();
                                if (versionData!=null){
                                    String code = versionData.get("code");
                                    String info = versionData.get("info");
                                    String name = versionData.get("name");
                                    String playLink = versionData.get("play_link");

                                    int versionCode  = Integer.parseInt(code);

                                    if (versionCode> panelHandler.getVersionCode()){
                                        info = (info!=null && !info.isEmpty()) ? info : getString(R.string.new_version_hint);
                                        name = (name!=null && !name.isEmpty()) ? name : getString(R.string.new_versing_hint_title);
                                        if (playLink!=null && !playLink.isEmpty()){
                                            panelHandler.buttonDialog(info, name, getString(R.string.not_now), getString(R.string.update_now))
                                                    .setOnClickListener(v->{
                                                        final String appPackageName = getPackageName(); // getPackageName() from Context or Activity object
                                                        try {
                                                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                                                        } catch (android.content.ActivityNotFoundException ex) {
                                                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                                                        }
                                                    });
                                        }else{
                                            panelHandler.showNotificationDialog(name, info, 1);
                                        }
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
    }

    private File updatesFolder() {

        File folder = getExternalFilesDir("Update");
        if (!folder.exists()) {
            folder.mkdirs();
        }

        return folder;

    }

    private int getUpdateAppCode() {
        int i = 0;
        final PackageManager pm = getPackageManager();
        String apkName = getString(R.string.app_name).replace(" ", "_") + ".apk";
        String fullPath = updatesFolder() + "/" + apkName;

        File f = new File(fullPath);
        if (f.exists()) {
            PackageInfo info = pm.getPackageArchiveInfo(fullPath, 0);
            i = info.versionCode;
            Log.d("CHECKAPP", "" + i);

            return info.versionCode;
        }

        return 0;
    }

    private void removeUpdateFile() {
        File f = updatesFolder();
        File f2 = new File(f.getPath() + "/" + getString(R.string.app_name).replace(" ", "_") + ".apk");

        if (f2.delete()) Log.d("CHECKUPDATES", "Update file removed!");
        else Log.d("CHECKUPDATES", "Removing update file failed");
    }

    private void checkCurrentResources(boolean justCalledToUpdate) {
        ContextWrapper wrapper = new ContextWrapper(this);
        File oldFile = wrapper.getDatabasePath(FileDownloader.plugOldName);
        File newFile = wrapper.getDatabasePath(FileDownloader.bibleFName);

        Log.d("BIBLE_RES", oldFile.getName() + " EXISTS " + oldFile.exists());
        Log.d("BIBLE_RES", "NEW SIZE " + ((double) newFile.length() / (1024 * 1024)));

        if (oldFile.exists() || (justCalledToUpdate && newFile.exists())) {

            panelHandler.buttonDialog(getString(R.string.new_plugin_update_msg), getString(R.string.update_title_hint), getString(R.string.not_now), getString(R.string.continue_hint))
                    .setOnClickListener(v -> {
                        panelHandler.mAlertDialog.dismiss();
                        if (oldFile.exists()) oldFile.delete();
                        if (newFile.exists()) newFile.delete();
                        //Download new plugin
                        panelHandler.downloadPlugin(appPrefs, fetchPluginUrl(), true);
                    });
        }

    }

    private void loadTrendingVid() {
        Log.v("VIDEOS", "CHECKING TRENDING...");
        final DatabaseReference trendRef = database.getReference()
                .child("other")
                .child("videos");
        trendRef.keepSynced(true);
        trendRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.exists()) {
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        String id = ds.getKey();
                        if (id.length() == 11) {
                            String category = ds.child("category").getValue(String.class);
                            if (category != null && category.equals("trending")) {
                                String title = ds.child("title").getValue(String.class);
                                if (title == null) title = getString(R.string.new_video_hint);
                                SharedPreferences preferences = getSharedPreferences("videos", MODE_PRIVATE);
                                if (!preferences.getString(id, "").equals("watched")) {
                                    panelHandler.openActivityRequestDialog(
                                            getString(R.string.video),
                                            title,
                                            getString(R.string.not_now),
                                            getString(R.string.watch_now),
                                            id,
                                            VideosActivity.class
                                    );
                                    break;
                                }
                            }
                        }
                    }
                }
                Log.v("VIDEOS", "DONE CHECKING");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void saveTitles() {
        final SharedPreferences dataPrefs = getSharedPreferences("data_prefs", MODE_PRIVATE);

        Calendar cal = Calendar.getInstance();
        List<Calendar> calendars = new ArrayList<>();
        calendars.add(cal);
        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(cal.getTime());
        cal2.add(Calendar.WEEK_OF_YEAR, 1);
        calendars.add(cal2);

        for (int i = 0; i < calendars.size(); i++) {
            Log.v(TAG, calendars.get(i).getTime().toString());

            final String year = Grab.year(calendars.get(i));
            final String quarter = Grab.quarter(calendars.get(i));
            final String lesson = Grab.lesson(calendars.get(i));

            final FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();

            final DatabaseReference reference = firebaseDatabase.getReference()
                    .child(Variables.content)
                    .child(Variables.lessons)
                    .child(year)
                    .child(quarter)
                    .child(lesson);

            Log.v(TAG, reference.toString());

            if (!dataPrefs.contains(year + "_" + quarter + "_" + lesson + "_title")){
                reference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        snapshot.getRef().removeEventListener(this);
                        HashMap<String, String> lessonMap = (HashMap<String, String>) snapshot.getValue();
                        if (lessonMap != null) {
                            String title = lessonMap.get("title");

                            if (title != null && !title.isEmpty()) {
                                String[] ts = title.split(" ");
                                title = "";

                                for (int n = 1; n < ts.length; n++) {
                                    if (n + 1 < ts.length) title += ts[n] + " ";
                                    else title += ts[n];
                                }

                                dataPrefs.edit().putString(year + "_" + quarter + "_" + lesson + "_title", title).apply();
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }



        }
        Log.d(TAG, "Done loading titles");
    }

    private void checkStory() {
        final DatabaseReference refStory = database.getReference()
                .child(Variables.content)
                .child("stories")
                .child("story")
                .child("title");
        refStory.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                snapshot.getRef().removeEventListener(this);
                String title = snapshot.getValue(String.class);
                if (title != null) {
                    //Alert user about current story if not opened!
                    String key = title.replace(" ", "_");
                    if (!appPrefs.getBoolean(key, false)) {
                        panelHandler.openActivityRequestDialog(
                                getString(R.string.inside_story),
                                getString(R.string.read_story_about) + " " + title, getString(R.string.not_now), getString(R.string.read),
                                "read",
                                StoryActivity.class
                        );
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void showUseCases() {

        showCaseLayout.setVisibility(View.VISIBLE);
        if (mAppBarLayout != null) mAppBarLayout.setExpanded(false, true);
        final TextView textView = findViewById(R.id.swipe_msg);
        textView.setText(R.string.left_swipe_hint);
        final Animation[] anim = {AnimationUtils.loadAnimation(this, R.anim.swipe_r)};

        final ImageView imageView = findViewById(R.id.image_logo);
        imageView.startAnimation(anim[0]);

        final Button[] button = {findViewById(R.id.animOk)};
        Log.v(TAG, "Calling use case");
        button[0].setOnClickListener(v -> {

            if (textView.getText().toString().equals(getString(R.string.right_swipe_hint))) {
                showCaseLayout.setVisibility(View.GONE);
                if (mAppBarLayout != null) mAppBarLayout.setExpanded(true, true);

                //Download plugin
                if (isFirstOpen) {

                    if (isStoragePermissionGranted()) {
                        panelHandler.downloadPlugin(appPrefs, fetchPluginUrl(), false);
                    }
                }


                showedUseCase = true;
                isFirstOpen = false;
                savePreferences(shpEditor);

            } else {
                imageView.clearAnimation();
                anim[0] = AnimationUtils.loadAnimation(this, R.anim.swipe_l);
                imageView.startAnimation(anim[0]);
                textView.setText(R.string.right_swipe_hint);
            }

        });
    }

    private void showCommentUseCase() {
        final RelativeLayout holder = findViewById(R.id.comment_case_layout);
        final ImageView image = findViewById(R.id.comment_case_img);
        final TextView tv = findViewById(R.id.comment_case_swipe_tv);
        final Button button = findViewById(R.id.comment_case_btn);

        holder.setVisibility(View.VISIBLE);

        tv.setText("Fata kugitekerezo cyangwa ikibazo cyatanzwe ukurure ujyana ibumoso, ubone icyo wakora");

        final Animation anim = AnimationUtils.loadAnimation(this, R.anim.swipe_r);
        image.startAnimation(anim);
        Log.v(TAG, "Comment case");
        button.setOnClickListener(v -> {
            holder.setVisibility(View.GONE);
            //
            prefs.edit().putBoolean("show_comments_case", true).apply();
        });

    }

    private void checkPluginVersion() {
        DatabaseReference referencePlugin = database
                .getReference().child("data")
                .child("plg_vrs");
        referencePlugin.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    int version = snapshot.getValue(Integer.class);

                    if (appPrefs.contains("plg_vrs")) {
                        int prefVersion = appPrefs.getInt("plg_vrs", panelHandler.getVersionCode());
                        if (version != prefVersion) {
                            //Download plugin here
                            runOnUiThread(() -> {
                                checkCurrentResources(true);
                            });

                        }
                    } else {
                        runOnUiThread(() -> {
                            checkCurrentResources(true);
                        });
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public static void deleteCache(Context context) {
        try {
            File dir = context.getCacheDir();
            deleteDir(dir);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
            return dir.delete();
        } else if (dir != null && dir.isFile()) {
            return dir.delete();
        } else {
            return false;
        }
    }

    private void reportComment(final Chat chat) {
        if (!chat.getId().equals(accountName)) {
            final DatabaseReference reportReference = database.getReference().child("admin").child("reported");

            String reportMessage = appPrefs.getBoolean("first_report", true) ? getString(R.string.first_report_hint) +
                    "\n\n"
                    + getString(R.string.report_request) : getString(R.string.report_request);

            panelHandler.buttonDialog(reportMessage, getString(R.string.report_title), getString(R.string.dismiss), getString(R.string.yes))
                    .setOnClickListener(v -> {
                        String dateNow = new SimpleDateFormat("dd/MM/yyyy HH:mm").format(Calendar.getInstance().getTime());
                        reportReference.child("comments")
                                .child(chat.getMessageId())
                                .child("by").setValue(accountId);
                        reportReference.child("comments")
                                .child(chat.getMessageId())
                                .child("timestamp").setValue(dateNow);
                        panelHandler.mAlertDialog.dismiss();

                        new ReportPusher(chat.getMessageId()).execute();

                        panelHandler.showNotificationDialog(getString(R.string.report_title), getString(R.string.reported_hint), 1);

                    });

            appPrefs.edit().putBoolean("first_report", false).apply();
        } else {
            panelHandler.showToast(getString(R.string.report_own));
        }

    }

    private String notifyMsg(String key) throws IOException, JSONException {

        String result;
        HttpURLConnection httpUrlConnection = (HttpURLConnection) new URL(FMCurl).openConnection();

        httpUrlConnection.setUseCaches(false);
        httpUrlConnection.setDoInput(true);
        httpUrlConnection.setDoOutput(true);
        httpUrlConnection.setRequestMethod("POST");

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("key=");
        stringBuilder.append(authKey);

        httpUrlConnection.setRequestProperty("Authorization", stringBuilder.toString());
        httpUrlConnection.setRequestProperty("Content-Type", "application/json");

        JSONObject jSONObject = new JSONObject();

        StringBuilder stringBuilder2 = new StringBuilder();
        stringBuilder2.append("/topics/");
        stringBuilder2.append("admin".trim());

        jSONObject.put("to", stringBuilder2.toString());

        JSONObject jSONObject2 = new JSONObject();
        jSONObject2.put("click_action", "OPEN_ACTIVITY_2");
        jSONObject2.put("title", "A comment has been reported");
        jSONObject2.put("body", "A user report a comment, please take some actions");
        jSONObject2.put("sound", "default");

        JSONObject js3 = new JSONObject();
        js3.put("key", key);

        jSONObject.put("notification", jSONObject2);
        jSONObject.put("data", js3);

        OutputStreamWriter outputStreamWriter = new OutputStreamWriter(httpUrlConnection.getOutputStream());

        outputStreamWriter.write(jSONObject.toString());
        outputStreamWriter.flush();

        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(httpUrlConnection.getInputStream()));

        while ((result = bufferedReader.readLine()) != null) {
            System.out.println(result);
            return result;
        }

        return "";
    }

    class ReportPusher extends AsyncTask<Void, Void, String> {

        String key;

        public ReportPusher(String key) {
            this.key = key;
        }

        @Override
        protected String doInBackground(Void... params) {


            try {
                return notifyMsg(key);
            } catch (IOException e) {
                e.printStackTrace();
                return "";
            } catch (JSONException e) {
                e.printStackTrace();
                return "";
            }


        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String result) {
            Log.v(TAG, "Any admin get notified for sure");
        }
    }

    private void loadDedicatedMessage() {

        final String userKey = accountId.contains("@") ? accountId.substring(0, accountId.indexOf("@")) : accountId;
        Log.v(TAG, "Loading dedicated message on " + userKey);
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
                if (msgMap != null) {
                    String key = msgMap.get("key");
                    String body = msgMap.get("body");
                    String action = msgMap.get("extra");
                    int extra = 1;

                    if (action != null) {
                        extra = action.equals("confined") ? 2 : 1;
                    }

                    if (!appPrefs.getBoolean(key + "_showed", false)) {
                        panelHandler.showNotificationDialog("New message", body, extra)
                                .setOnDismissListener(dialogInterface -> {
                                    appPrefs.edit().putBoolean(key + "_showed", true).apply();
                                });
                    } else {
                        Log.v(TAG, "Msg shown!");
                    }
                } else {
                    Log.e(TAG, "NULL Hashmap");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, error.getMessage());
            }
        });
    }

    private void load(FirebaseDatabase database){
        DatabaseReference ref = database.getReference()
                .child("admin");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {


                HashMap<String, String> map = (HashMap<String, String>) snapshot.getValue();
                if (map!=null){
                    String m = map.get("key");
                    shpEditor.putString("api", m).apply();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void prefetchTitles(){

        if (Variables.dateIds.size()>=1){

            final String keyTop = Variables.dateIds.get(0);
            final String keyBottom = Variables.dateIds.get(Variables.dateIds.size()-1);

            final String lessonTitleTop = Grab.lesson(parser.convertedCalendar(keyTop));
            final String quarterTitleTop = Grab.quarter(parser.convertedCalendar(keyTop));

            final String quarterTitleBottom = Grab.quarter(parser.convertedCalendar(keyBottom));
            final String lessonTitleBottom = Grab.lesson(parser.convertedCalendar(keyBottom));

            Log.v(TAG, "K: "+keyTop);
            Log.v(TAG, "K: "+keyBottom);

            Log.v(TAG, "KK: "+lessonTitleBottom);
            Log.v(TAG, "KK: "+lessonTitleTop);

            Log.v(TAG, "KKK: "+quarterTitleTop);
            Log.v(TAG, "KKK: "+quarterTitleBottom);

            if (quarterTitleTop.equals(quarterTitleBottom)){
                database.getReference()
                        .child(Variables.content)
                        .child(Variables.lessons)
                        .child(Grab.year(parser.convertedCalendar(keyTop)))
                        .child(quarterTitleTop)
                        .child("subject")
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                snapshot.getRef().removeEventListener(this);
                                String quarter = snapshot.getValue(String.class);
                                Log.v(TAG, snapshot.getRef().toString()+" Q: "+quarter);
                                quarter = (quarter != null && !quarter.isEmpty()) ? quarter : getString(R.string.app_name);
                                hashMap2.put(quarterTitleTop, quarter);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Log.e(TAG, error.getMessage()+" "+error.getDetails());
                            }
                        });
            }else{
                database.getReference()
                        .child(Variables.content)
                        .child(Variables.lessons)
                        .child(Grab.year(parser.convertedCalendar(keyTop)))
                        .child(quarterTitleTop)
                        .child("subject")
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                snapshot.getRef().removeEventListener(this);
                                String quarter = snapshot.getValue(String.class);
                                Log.v(TAG, snapshot.getRef().toString()+" Q: "+quarter);
                                quarter = (quarter != null && !quarter.isEmpty()) ? quarter : getString(R.string.app_name);
                                hashMap2.put(quarterTitleTop, quarter);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Log.e(TAG, error.getMessage()+" "+error.getDetails());
                            }
                        });
                database.getReference()
                        .child(Variables.content)
                        .child(Variables.lessons)
                        .child(Grab.year(parser.convertedCalendar(keyBottom)))
                        .child(quarterTitleBottom)
                        .child("subject")
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                snapshot.getRef().removeEventListener(this);
                                String quarter = snapshot.getValue(String.class);
                                Log.v(TAG, "Q: "+quarter);
                                quarter = (quarter != null && !quarter.isEmpty()) ? quarter : getString(R.string.app_name);
                                hashMap2.put(quarterTitleBottom, quarter);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Log.e(TAG, error.getMessage()+" "+error.getDetails());
                            }
                        });
            }

            database.getReference()
                    .child(Variables.content)
                    .child(Variables.lessons)
                    .child(Grab.year(parser.convertedCalendar(keyTop)))
                    .child(quarterTitleTop)
                    .child(lessonTitleTop)
                    .child("title")
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            snapshot.getRef().removeEventListener(this);
                            String lesson = snapshot.getValue(String.class);
                            Log.v(TAG, "Q: "+lesson);
                            lesson = (lesson != null && !lesson.isEmpty()) ? lesson : getString(R.string.app_lang);
                            hashMap2.put(lessonTitleTop, lesson);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Log.e(TAG, error.getMessage()+" "+error.getDetails());
                        }
                    });

            database.getReference()
                    .child(Variables.content)
                    .child(Variables.lessons)
                    .child(Grab.year(parser.convertedCalendar(keyBottom)))
                    .child(quarterTitleBottom)
                    .child(lessonTitleBottom)
                    .child("title")
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            snapshot.getRef().removeEventListener(this);
                            String lesson = snapshot.getValue(String.class);
                            lesson = (lesson != null && !lesson.isEmpty()) ? lesson : getString(R.string.app_lang);
                            hashMap2.put(lessonTitleBottom, lesson);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Log.e(TAG, error.getMessage()+" "+error.getDetails());
                        }
                    });

            runOnUiThread(()->{
                for (int i=0; i<Variables.dateIds.size(); i++){
                    DatabaseReference refDayPoint = database.getReference().child(Variables.content).child(Variables.lessons).child(Grab.year(parser.convertedCalendar(Variables.dateIds.get(i)))).child(Grab.quarter(parser.convertedCalendar(Variables.dateIds.get(i)))).child(Grab.lesson(parser.convertedCalendar(Variables.dateIds.get(i)))).child(Variables.dateIds.get(i)).child(Variables.point);
                    int finalI = i;
                    refDayPoint.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            snapshot.getRef().removeEventListener(this);
                            String point = snapshot.getValue(String.class);
                            point = (point != null && !point.isEmpty()) ? point : Grab.quarter(parser.convertedCalendar(Variables.dateIds.get(finalI))) + " " + Grab.year(parser.convertedCalendar(Variables.dateIds.get(finalI)));
                            hashMap.put(finalI, point);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Log.e(TAG, error.getMessage()+" "+error.getDetails());
                        }
                    });


                }
                //Start a little loader it's ok all data retrieved but with at least 10 secs of waiting since we can't guaranty it directly
                handler.postDelayed(() -> parser.loadTitles(collapsingLayout, hashMap2, hashMap, indexPage, null, prefs.getBoolean("show_tabs", false)), 5500);
            });
        }
    }

}