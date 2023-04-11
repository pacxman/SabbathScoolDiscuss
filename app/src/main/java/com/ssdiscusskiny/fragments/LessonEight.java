package com.ssdiscusskiny.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKey;

import com.bhprojects.bibleprojectkiny.Texts;
import com.bhprojects.bibleprojectkiny.TextsAdapter;
import com.google.android.material.appbar.AppBarLayout;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.ssdiscusskiny.MainActivity;
import com.ssdiscusskiny.R;
import com.ssdiscusskiny.app.Variables;
import com.ssdiscusskiny.tools.TopProgressBar;
import com.ssdiscusskiny.generator.Grab;
import com.ssdiscusskiny.utils.PanelHandler;
import com.ssdiscusskiny.utils.Parser;

import java.util.ArrayList;
import java.util.List;

public class LessonEight extends Fragment {
    private RecyclerView recyclerView;
    private static String dateId="no id";
    private static TopProgressBar pbar;
    private static Parser parser;
    private FirebaseDatabase database;
    private DatabaseReference refLesson;
    private PanelHandler panelHandler;
    private SharedPreferences appPrefs,prefs;
    private AppBarLayout mApbar;
    private List<Texts> listTexts;
    private TextsAdapter textsAdapter;
    private final String TAG = getClass().getSimpleName();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // TODO: Implement this method
        View v = inflater.inflate(R.layout.main_page, container, false);
        recyclerView = v.findViewById(R.id.main_recyclerview);

        mApbar = ((MainActivity)getActivity()).getMAppBarLayout();

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        panelHandler = new PanelHandler(getContext());

        try {
            MasterKey masterKey = new MasterKey.Builder(getContext())
                    .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                    .build();

            appPrefs = EncryptedSharedPreferences.create(
                    getContext(),
                    "account_prefs",
                    masterKey,
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM);
        }catch(Exception ex){
            ex.printStackTrace();
            Log.e(TAG, ex.getMessage());
        }

        prefs = PreferenceManager.getDefaultSharedPreferences(getContext());

        listTexts = new ArrayList<>();
        textsAdapter = new TextsAdapter(getContext(), listTexts, dateId, null, panelHandler.titleTv, panelHandler.contentTv, panelHandler.versionSpn, panelHandler.mDialog, appPrefs);

        recyclerView.setAdapter(textsAdapter);
        textsAdapter.mAppBarLayout = ((MainActivity)getActivity()).getMAppBarLayout();

        if (prefs.getBoolean("night", false)){
            recyclerView.setBackgroundColor(ContextCompat.getColor(getContext(),R.color.background_dark));
        }else{
            recyclerView.setBackgroundColor(ContextCompat.getColor(getContext(),R.color.background_light));
        }

        database = FirebaseDatabase.getInstance();
        refLesson = database.getReference().child(Variables.content).child(Variables.lessons).child(Grab.year(parser.convertedCalendar(dateId))).child(Grab.quarter(parser.convertedCalendar(dateId))).child(Grab.lesson(parser.convertedCalendar(dateId))).child(dateId);

        parser.loadText(refLesson, textsAdapter, listTexts, pbar, null);

        final DatabaseReference tipRef = database.getReference().child(Variables.content).child(Variables.lessons).child(Grab.year(parser.convertedCalendar(dateId))).child(Grab.quarter(parser.convertedCalendar(dateId))).child(Grab.lesson(parser.convertedCalendar(dateId)));
        final DatabaseReference tipKeyRef = database.getReference().child(Variables.content).child(Variables.lessons).child(Grab.year(parser.convertedCalendar(dateId))).child(Grab.quarter(parser.convertedCalendar(dateId))).child(Grab.lesson(parser.convertedCalendar(dateId))).child(dateId);
        parser.loadTipContexts(tipRef, textsAdapter);
        parser.loadLessonOtherContext(tipKeyRef, textsAdapter);

        return v;
    }
    @Override
    public void onResume() {
        // TODO: Implement this method
        super.onResume();

        final InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);

        if (prefs.getBoolean("night", false)){
            recyclerView.setBackgroundColor(ContextCompat.getColor(getContext(),R.color.background_dark));
        }else{
            recyclerView.setBackgroundColor(ContextCompat.getColor(getContext(),R.color.background_light));
        }

        textsAdapter.notifyDataSetChanged();
        panelHandler.setDisplayTextSize();

    }
    public static Fragment newInstance(String dateId, Parser parse, TopProgressBar pb) {
        LessonEight f = new LessonEight();
        Bundle b = new Bundle();
        f.setArguments(b);
        pbar = pb;
        parser = parse;
        LessonEight.dateId = dateId;

        return f;
    }

}
