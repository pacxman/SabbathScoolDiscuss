package com.ssdiscusskiny.activities;

import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;

import com.bhprojects.bibleprojectkiny.AppConstants;
import com.google.android.material.appbar.AppBarLayout;
import com.ssdiscusskiny.R;
;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class ReadActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private TextView textView;

    private View pBarView;
    private AlertDialog alertProgressBar;
    private NestedScrollView nestedScrollView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read);

        textView = findViewById(R.id.mTv);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            getWindow().setNavigationBarColor(getResources().getColor(R.color.redPrimary));
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }

        nestedScrollView = findViewById(R.id.nscroll);

        AppBarLayout mAppBarLayout = findViewById(R.id.app_bar);
        mAppBarLayout.addOnOffsetChangedListener((appBarLayout, verticalOffset) -> {
            if (Math.abs(verticalOffset) == appBarLayout.getTotalScrollRange()) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    getWindow().setNavigationBarColor(getResources().getColor(R.color.redPrimary));
                    getWindow().setStatusBarColor(getResources().getColor(R.color.redPrimary));
                }
            } else if (verticalOffset == 0) {
                // Expanded
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    getWindow().setStatusBarColor(Color.TRANSPARENT);
                }
            } else {
                // Somewhere in between
            }

        });

        /*Bitmap bm = BitmapFactory.decodeResource(getResources(), R.drawable.default_header);
        homeImage.setImageBitmap(bm);*/

        pBarView = View.inflate(this, R.layout.pbar_alertdialog, null);
        alertProgressBar = new AlertDialog.Builder(this).create();
        alertProgressBar.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertProgressBar.setView(pBarView);

        new readTextFile().execute();

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);

    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            if (!AppConstants.isDefFont) {
                if (AppConstants.font.contains("slabo")) {
                    textView.setTypeface(Typeface.createFromAsset(getAssets(), "Slabo27px-Regular.ttf"));
                }else if (AppConstants.font.contains("cambo")) {
                    textView.setTypeface(Typeface.createFromAsset(getAssets(), "cambo-regular.ttf"));
                } else if (AppConstants.font.contains("bitter")) {
                    textView.setTypeface(Typeface.createFromAsset(getAssets(), "Bitter-Regular.ttf"));
                }else if (AppConstants.font.contains("tnr")) {
                    textView.setTypeface(Typeface.createFromAsset(getAssets(), "times_new_roman.ttf"));
                } else {
                    textView.setTypeface(Typeface.createFromAsset(getAssets(), "Slabo27px-Regular.ttf"));
                }
            } else {
                textView.setTypeface(Typeface.DEFAULT);
            }
        }catch (NullPointerException e){
            e.printStackTrace();
        }
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, AppConstants.fontSize);
        if (AppConstants.isNightMode){
            nestedScrollView.setBackgroundColor(ContextCompat.getColor(this,R.color.background_dark));
            textView.setTextColor(ContextCompat.getColor(this,R.color.white));
        }else{
            nestedScrollView.setBackgroundColor(ContextCompat.getColor(this,R.color.background_light));
            textView.setTextColor(ContextCompat.getColor(this,R.color.black));
        }
    }

    /*We expect this text to be larger so we use asynctask
    private String loadPrivacy(){
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(getAssets().open("privacy.txt")));
            String line;
            while(( line = reader.readLine()) != null){
                privacy += line;
            }
        }catch (IOException e){
            Log.d("IOEXCEPTION", e.getMessage());
        }finally {
            if (reader != null){
                try{
                    reader.close();
                } catch (IOException e){
                    Log.d("IOEXCEPTION", e.getMessage());
                }
            }
        }
        return  privacy;
    }*/

    class readTextFile extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... params) {
            String privacy="";
            String result;
            BufferedReader reader = null;
            try {
                reader = new BufferedReader(new InputStreamReader(getAssets().open("privacy.txt")));
                while((result = reader.readLine()) != null){
                    privacy+=result;
                }
                reader.close();
            }catch (IOException e){
                Log.d("IOEXCEPTION", e.getMessage());
            }finally {
                if (reader != null){
                    try{
                        reader.close();
                    } catch (IOException e){
                        Log.d("IOEXCEPTION", e.getMessage());
                    }
                }
            }
            return privacy;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (!alertProgressBar.isShowing()){
                alertProgressBar.show();
            }
        }

        @Override
        protected void onPostExecute(String result) {
            if (alertProgressBar.isShowing()){
                alertProgressBar.dismiss();
            }
            textView.setText(Html.fromHtml(result));
        }
    }

}