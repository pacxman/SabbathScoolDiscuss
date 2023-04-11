package com.sdahymnalkiny;

import android.app.Dialog;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.StyleRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.slidingpanelayout.widget.SlidingPaneLayout;
import androidx.appcompat.widget.SearchView;
import com.google.android.material.bottomsheet.BottomSheetDialog;


import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private SlidingPaneLayout hymnsPanel;
    private Toolbar toolbar;
    private ImageButton expndSearch;
    private ListView mListView;
    private HymnAdapter hAdapter;
    private DatabaseManager dbM;
    //private ArrayList<Hymns> hymnListCopy;
    private SharedPreferences sharedPreference;
    private View hiddenPanel;

    private SearchView searchView;
    private MenuItem arrow;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        sharedPreference = getSharedPreferences(Variables.mainPref, MODE_PRIVATE);
        if (isNightModeEnabled())
        {
            setAppTheme(R.style.AppTheme_Base_Night);
        }
        else
        {
            setAppTheme(R.style.AppTheme_Base_Light);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbM = new DatabaseManager(this);
        hideSoftKeyboard();

        hiddenPanel = findViewById(R.id.bottom_sheet);

        toolbar = findViewById(R.id.toolbar);
        hymnsPanel = findViewById(R.id.sliding_pane_layout);
        hymnsPanel.setPanelSlideListener(panelSlideListener);

        searchView = findViewById(R.id.searchView);
        searchView.setSubmitButtonEnabled(true);

        expndSearch = findViewById(R.id.search);
        mListView = findViewById(R.id.listView);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayShowHomeEnabled(false);


        LoadHymn loader = new LoadHymn();
        LinearLayout showBar = findViewById(R.id.showbar);
        showBar.setVisibility(View.VISIBLE);
        Variables.hymnList.clear();
        loader.execute();

        expndSearch.setOnClickListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        hAdapter = new HymnAdapter(MainActivity.this, dbM, R.layout.hymn_holder, Variables.hymnList);
        hAdapter.notifyDataSetChanged();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        arrow = menu.findItem(R.id.options);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.keypad:
                showKepad();
                break;
            case R.id.options:
                slideUpDown();
                if (arrow != null){
                    arrow.getActionView().startAnimation(
                            AnimationUtils.loadAnimation(this, R.anim.rotate) );
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id){
            case R.id.search:
                hymnsPanel.openPane();
                break;
        }
    }

    SlidingPaneLayout.PanelSlideListener panelSlideListener = new SlidingPaneLayout.PanelSlideListener(){
        @Override
        public void onPanelOpened(@NonNull View panel) {
            expndSearch.setImageResource(android.R.drawable.ic_menu_close_clear_cancel);
        }

        @Override
        public void onPanelClosed(@NonNull View panel) {
            hideSoftKeyboard();
            expndSearch.setImageResource(R.drawable.ic_list);
        }

        @Override
        public void onPanelSlide(@NonNull View panel, float slideOffset) {

        }
    };

    private void setAppTheme(@StyleRes int style)
    {
        setTheme(style);
    }

    private void hideSoftKeyboard(){
        // Check if no view has focus:
   