package com.ssdiscusskiny;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.ContextWrapper;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKey;

import android.os.Bundle;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.RelativeLayout;
import android.provider.Settings;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.File;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ssdiscusskiny.app.Variables;
import com.ssdiscusskiny.downloaders.FileDownloader;
import com.ssdiscusskiny.utils.PanelHandler;
import com.ssdiscusskiny.generator.Randoms;

import android.net.ParseException;
import android.os.AsyncTask;

import java.net.URL;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.io.IOException;

import android.animation.ObjectAnimator;
import android.animation.AnimatorInflater;
import android.animation.Animator;
import android.view.animation.AlphaAnimation;
import android.view.animation.AccelerateInterpolator;

import java.util.Calendar;

import android.view.animation.DecelerateInterpolator;
import android.animation.AnimatorListenerAdapter;

import java.util.Map;
import java.util.concurrent.TimeUnit;


public class SplashActivity extends AppCompatActivity {

    private final String TAG = SplashActivity.class.getSimpleName();
    private ImageView logo;
    private SharedPreferences appPrefs;
    private PanelHandler pHandle;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference ref;
    private String android_id;
    private AlertDialog alrtPbar;
    private TextView tvbar;
    private RelativeLayout leftRow;
    private RelativeLayout rightRow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        try {
            MasterKey masterKey = new MasterKey.Builder(SplashActivity.this)
                    .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                    .build();

            appPrefs = EncryptedSharedPreferences.create(
                    SplashActivity.this,
                    "account_prefs",
                    masterKey,
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM);

            SharedPreferences depPreferences = getSharedPreferences("app_prefs", MODE_PRIVATE);

            Map<String, ?> allEntries = depPreferences.getAll();
            for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
                Log.d(TAG, entry.getKey() + " : " + entry.getValue().toString());
                Object o = entry.getValue();
                if (o instanceof String){
                    appPrefs.edit().putString(entry.getKey(), entry.getValue().toString()).commit();
                }
                if (o instanceof Integer){
                    appPrefs.edit().putInt(entry.getKey(), Integer.parseInt(entry.getValue().toString())).commit();
                }
                if (o instanceof Long){
                    appPrefs.edit().putLong(entry.getKey(), Long.parseLong(entry.getValue().toString())).commit();
                }
                if (o instanceof Boolean){
                    appPrefs.edit().putBoolean(entry.getKey(), Boolean.parseBoolean(entry.getValue().toString())).commit();
                }


                depPreferences.edit().remove(entry.getKey()).commit();
            }

        }catch(Exception ex){
            ex.printStackTrace();
            Log.e(TAG, ex.getMessage());
        }

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

        leftRow = findViewById(R.id.left_row);
        rightRow = findViewById(R.id.right_row);

        if (appPrefs.contains("splashLColor")) {

            try {
                leftRow.setBackgroundColor(Color.parseColor(appPrefs.getString("splashLColor", "#161F3A")));
            } catch (ParseException e) {
                System.out.println("Invalid color code " + e.getMessage());
            }


        } else {
            File coverFile = this.getFilesDir();
            File file = new File(coverFile, FileDownloader.skinName);
            Log.d("SPLASH_CALL", "Folder " + file.toString());
            if (coverFile.exists()) {
                Log.d("SPLASH_CALL", "Folder exist");
            }
            if (file.exists()) {
                Log.d("SPLASH_CALL", "File exist");
                Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
                leftRow.setBackground(new BitmapDrawable(getResources(), bitmap));
            } else {
                Log.d("SPLASH_CALL", "File not exist");
                Bitmap bm = BitmapFactory.decodeResource(getResources(), R.drawable.blue_leather);
                leftRow.setBackground(new BitmapDrawable(getResources(), bm));
            }

        }
        if (appPrefs.contains("splashRColor")) {
            try {
                rightRow.setBackgroundColor(Color.parseColor(appPrefs.getString("splashRColor", "#FFFFFF")));
            } catch (ParseException e) {
                System.out.println("Invalid color code " + e.getMessage());
            }

        } else {
            rightRow.setBackgroundColor(Color.parseColor("#EFEFEF"));
        }

        pHandle = new PanelHandler(this);

        logo = findViewById(R.id.logo);

        alrtPbar = new AlertDialog.Builder(this).create();
        View pBarView = View.inflate(this, R.layout.pbar_alertdialog, null);
        alrtPbar.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alrtPbar.setView(pBarView);
        tvbar = pBarView.findViewById(R.id.pbartext);

        android_id = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);
        firebaseDatabase = FirebaseDatabase.getInstance();
        ref = firebaseDatabase.getReference();

        final TextView splashMark = findViewById(R.id.splash_mark_tv);
        final Calendar calendar = Calendar.getInstance();

        toggleFullscreen();
        pHandle.getAppState(firebaseDatabase);

        if (appPrefs.getBoolean("firstRun", true)) {
            firstRun();
            splashMark.setText(R.string.start_theme);
        } else if (appPrefs.contains("account_id")) {
            loadThemeImage();
            splashMark.setText(Variables.formatTitleShort(calendar));
            splashMark.setTextColor(Color.WHITE);
        } else if (!appPrefs.getBoolean("firstRun", true) && !appPrefs.contains("account_id")) {
            firstRun();
            splashMark.setText(R.string.start_theme);
        }

    }

    @Override
    public void onBackPressed() {
        // TODO: Implement this method
        //super.onBackPressed();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        toggleFullscreen();
    }

    private void loadThemeImage() {
        ContextWrapper contextWrapper = new ContextWrapper(getApplicationContext());
        File directory = contextWrapper.getFilesDir();
        File themeFile = new File(directory, FileDownloader.themeName);

        if (themeFile.exists()) {
            Bitmap bitmap = BitmapFactory.decodeFile(themeFile.getAbsolutePath());
            logo.setImageBitmap(bitmap);
        } else {
            logo.setImageResource(R.drawable.ic_logo);
        }
       switchLogo(Randoms.toss(appPrefs.getBoolean("firstRun", true)));
    }

    private void checkProfile() {
        if (appPrefs.contains("account_id")) {
            //Start MainActivity
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            overridePendingTransition(0, 0);
            finish();

        } else {
            //Start Login Screen and finish this Activity
            Intent loginIntent = new Intent(SplashActivity.this, LoginActivity.class);
            loginIntent.putExtra("login", "setEmail");
            loginIntent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            loginIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(loginIntent);
            overridePendingTransition(0, 0);
            finish();
        }
    }

    private void toggleFullscreen() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().getDecorView().setSystemUiVisibility(PanelHandler.flags());
            final View decorView = getWindow().getDecorView();
            decorView.setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
                @Override
                public void onSystemUiVisibilityChange(int visibility) {
                    if ((visibility & View.SYSTEM_UI_FLAG_FULLSCREEN) == 0) {
                        decorView.setSystemUiVisibility(PanelHandler.flags());
                    }
                }
            });
        }
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
    }

    private void firstRun() {

        tvbar.setText(R.string.wait_msg);
        alrtPbar.setCancelable(false);
        alrtPbar.show();
        new TestInternet().execute();

    }

    class TestInternet extends AsyncTask<Void, Void, Boolean> {
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
        }

        @Override
        protected void onPostExecute(Boolean result) {
            alrtPbar.dismiss();
            if (!result) { // code if not connected

                pHandle.showNetError(0, getString(R.string.reconnect_msg))
                        .setOnClickListener(v-> {
                            if (pHandle.mAlertDialog != null) {
                                pHandle.mAlertDialog.dismiss();
                            }
                            tvbar.setText(getString(R.string.retrying));
                            alrtPbar.show();
                            new TestInternet().execute();
                        });

            } else { // code if connected
                tvbar.setText(getString(R.string.getting_access));
                ref.child("admin").child("access").addValueEventListener(
                        new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                String access = snapshot.getValue(String.class);
                                if (access!=null){
                                    if (access.equals("on")){
                                        alrtPbar.dismiss();
                                        loadThemeImage();
                                    }else{
                                        alrtPbar.dismiss();
                                        pHandle.showNotificationDialog("Access denied", getString(R.string.app_name)+" team didn't provide an access to this app, you can contact us within email at "+getString(R.string.m_email), 2);
                                    }
                                }else{
                                    alrtPbar.dismiss();
                                    pHandle.showNotificationDialog("Access denied", getString(R.string.app_name)+" team didn't provide an access to this app, you can contact us within email at "+getString(R.string.m_email), 2);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        }
                );
            }
        }
    }

    public void switchLogo(int type) {
        switch (type) {
            case 0:
                setCenter();
                final Animation aniSlide = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.zoom_out);
                aniSlide.setStartOffset(3000);
                Animation logo_animD = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.anim_theme);
                logo_animD.setAnimationListener(new Animation.AnimationListener() {

                    @Override
                    public void onAnimationStart(Animation animation) {
                        // TODO: Implement this method
                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        // TODO: Implement this method
                        logo.startAnimation(aniSlide);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {
                        // TODO: Implement this method
                    }


                });
                aniSlide.setAnimationListener(new Animation.AnimationListener() {

                    @Override
                    public void onAnimationStart(Animation animation) {
                        // TODO: Implement this method
                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        // TODO: Implement this method
                        checkProfile();
                        logo.setVisibility(View.GONE);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {
                        // TODO: Implement this method
                    }


                });
                logo.startAnimation(logo_animD);
                break;
            case 1:
                logo.clearAnimation();
                setCenter();
                Animation transAnim1 = AnimationUtils.loadAnimation(SplashActivity.this, R.anim.trans_anim);
                transAnim1.setStartOffset(5000);
                transAnim1.setDuration(4000);
                transAnim1.setFillAfter(true);
                //transAnim1.setInterpolator(new BounceInterpolator());
                transAnim1.setAnimationListener(new Animation.AnimationListener() {

                    @Override
                    public void onAnimationStart(Animation animation) {
                        //Log.i(TAG, "Starting button dropdown animation");
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {
                        // TODO Auto-generated method stub

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        checkProfile();
                    }
                });
                logo.startAnimation(transAnim1);

                break;
            case 2:
                setCenter();
                logo.clearAnimation();
                final ObjectAnimator anim = (ObjectAnimator) AnimatorInflater.loadAnimator(SplashActivity.this, R.animator.flipping_x);
                anim.setStartDelay(2000);
                anim.setTarget(logo);
                anim.setDuration(7000);
                anim.addListener(new Animator.AnimatorListener() {

                    @Override
                    public void onAnimationStart(Animator animator) {
                    }

                    @Override
                    public void onAnimationEnd(Animator animator) {
                        checkProfile();
                    }

                    @Override
                    public void onAnimationCancel(Animator animator) {
                    }

                    @Override
                    public void onAnimationRepeat(Animator animator) {
                    }


                });
                anim.start();
                break;
            case 3:
                setCenter();
                Animation logo_anim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.anim_theme);
                logo_anim.setAnimationListener(new Animation.AnimationListener() {

                    @Override
                    public void onAnimationStart(Animation animation) {
                        // TODO: Implement this method
                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        // TODO: Implement this method
                        checkProfile();
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {
                        // TODO: Implement this method
                    }


                });
                logo.startAnimation(logo_anim);
                break;
            case 4:
                logo.clearAnimation();
                setCenter();
                final ObjectAnimator anim3 = (ObjectAnimator) AnimatorInflater.loadAnimator(SplashActivity.this, R.animator.flipping);
                anim3.setStartDelay(2000);
                anim3.setTarget(logo);
                anim3.setDuration(7500);
                anim3.addListener(new Animator.AnimatorListener() {

                    @Override
                    public void onAnimationStart(Animator animator) {
                    }

                    @Override
                    public void onAnimationEnd(Animator animator) {
                        checkProfile();
                    }

                    @Override
                    public void onAnimationCancel(Animator animator) {
                    }

                    @Override
                    public void onAnimationRepeat(Animator animator) {
                    }


                });
                Animation logo_anim2 = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.anim_theme);
                logo_anim2.setAnimationListener(new Animation.AnimationListener() {

                    @Override
                    public void onAnimationStart(Animation animation) {
                        // TODO: Implement this method
                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        // TODO: Implement this method
                        anim3.start();
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {
                        // TODO: Implement this method
                    }


                });
                logo.startAnimation(logo_anim2);
                break;
            case 5:
                logo.clearAnimation();
                setCenter();
                final ObjectAnimator anim4 = (ObjectAnimator) AnimatorInflater.loadAnimator(SplashActivity.this, R.animator.flipping);
                anim4.setStartDelay(4000);
                anim4.setTarget(logo);
                anim4.setDuration(7000);
                anim4.addListener(new Animator.AnimatorListener() {

                    @Override
                    public void onAnimationStart(Animator animator) {
                    }

                    @Override
                    public void onAnimationEnd(Animator animator) {
                        checkProfile();
                    }

                    @Override
                    public void onAnimationCancel(Animator animator) {
                    }

                    @Override
                    public void onAnimationRepeat(Animator animator) {
                    }


                });
                anim4.start();
                break;
            case 6:
                setCenter();
                Animation fadeOut = new AlphaAnimation(1, -1);
                fadeOut.setInterpolator(new AccelerateInterpolator()); //and this
                fadeOut.setFillAfter(false);
                fadeOut.setStartOffset(1500);
                fadeOut.setDuration(6000);
                fadeOut.setAnimationListener(new Animation.AnimationListener() {

                    @Override
                    public void onAnimationStart(Animation animation) {
                        // TODO: Implement this method
                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        // TODO: Implement this method
                        //Bitmap bm = BitmapFactory.decodeResource(getResources(), R.drawable.pm_txt);
                        checkProfile();
                        logo.setVisibility(View.GONE);


                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {
                        // TODO: Implement this method
                    }


                });
                logo.startAnimation(fadeOut);
                break;
            case 7:
                setCenter();
                Animation fadeOut2 = new AlphaAnimation(1, -1);
                fadeOut2.setInterpolator(new AccelerateInterpolator()); //and this
                fadeOut2.setFillAfter(false);
                fadeOut2.setStartOffset(2000);
                fadeOut2.setDuration(6000);
                fadeOut2.setAnimationListener(new Animation.AnimationListener() {

                    @Override
                    public void onAnimationStart(Animation animation) {
                        // TODO: Implement this method
                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        // TODO: Implement this method
                        logo.setVisibility(View.VISIBLE);
                        checkProfile();

                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {
                        // TODO: Implement this method
                    }


                });
                logo.startAnimation(fadeOut2);
                break;
            case 8:
                setCenter();
                Animation shake = AnimationUtils.loadAnimation(this, R.anim.shake_on_x);
                shake.setAnimationListener(new Animation.AnimationListener() {

                    @Override
                    public void onAnimationStart(Animation animation) {
                        // TODO: Implement this method
                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        // TODO: Implement this method
                        logo.setVisibility(View.VISIBLE);
                        checkProfile();

                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {
                        // TODO: Implement this method
                    }


                });
                logo.startAnimation(shake);
                break;
            case 9:
                setCenter();
                final ObjectAnimator oa1 = ObjectAnimator.ofFloat(logo, "scaleX", 1f, 0f);
                //final ObjectAnimator oa2 = ObjectAnimator.ofFloat(logo, "scaleX", 1f, 0f);
                oa1.setInterpolator(new DecelerateInterpolator());
                //oa2.setInterpolator(new AccelerateDecelerateInterpolator());

                oa1.setStartDelay(6000);
                oa1.setDuration(3000);
//                oa2.setStartDelay(1000);
//                oa2.setDuration(5000);

                oa1.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        checkProfile();
                    }
                });


                oa1.start();
                break;
            case 10:
                setCenter();
                Animation zoomingBounce = AnimationUtils.loadAnimation(SplashActivity.this, R.anim.zoomin_bounce);
                logo.setAnimation(zoomingBounce);
                zoomingBounce.setStartOffset(2000);
                zoomingBounce.setAnimationListener(new Animation.AnimationListener() {

                    @Override
                    public void onAnimationStart(Animation animation) {
                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        try {
                            TimeUnit.SECONDS.sleep(2);
                            checkProfile();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {
                    }

                });
                break;
        }
    }

    private void setCenter() {
        RelativeLayout.LayoutParams layoutParams =
                (RelativeLayout.LayoutParams) logo.getLayoutParams();

        layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
        logo.setLayoutParams(layoutParams);
    }

    //    private void setVertCenter() {
//        RelativeLayout.LayoutParams layoutParams = 
//            (RelativeLayout.LayoutParams)logo.getLayoutParams();
//
//        layoutParams.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE);
//        logo.setLayoutParams(layoutParams);
//    }
    private void setTop() {
        RelativeLayout.LayoutParams layoutParams =
                (RelativeLayout.LayoutParams) logo.getLayoutParams();
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);
        layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);

        logo.setLayoutParams(layoutParams);
    }

    /*private int getDisplayHeight() {
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        return size.y;
    }*/

}
