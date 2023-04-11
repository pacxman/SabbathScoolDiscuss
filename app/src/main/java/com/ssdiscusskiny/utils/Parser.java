package com.ssdiscusskiny.utils;

import com.bhprojects.bibleprojectkiny.SimpleFormatter;

import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.ssdiscusskiny.R;

import android.content.res.Resources;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.Window;
import android.widget.GridLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.os.Handler;
import android.graphics.Color;

import com.bhprojects.bibleprojectkiny.Texts;
import com.bhprojects.bibleprojectkiny.TextsAdapter;
import com.ssdiscusskiny.app.Variables;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

import com.google.firebase.database.ValueEventListener;

import com.ssdiscusskiny.generator.Grab;
import com.ssdiscusskiny.tools.TopProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import org.apache.commons.validator.Var;

import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.InvalidParameterSpecException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class Parser {
    private final String TAG = Parser.class.getSimpleName();
    private Context mContext;
    private final Handler handler = new Handler();
    private final Handler handler2 = new Handler();
    private final Handler handler3 = new Handler();
    private TRunnable r1;
    private SRunnable r2;
    private String subTitle = "";
    private AnimationAutoTextScroller textScroller;
    private AlertDialog textDialog;
    private SimpleFormatter simpleText;
    private TitleChanger titleChanger;
    private boolean isLessonAccessible = true;
    private String[] addsTxt = {null,null};
    private HashMap<String, String> titleMaps = new HashMap<>();
    private boolean firstInit = false;

    public Parser(Context mContext) {
        this.mContext = mContext;

        textDialog = new AlertDialog.Builder(mContext).create();
        View dialogView = LayoutInflater.from(mContext)
                .inflate(R.layout.text_display_dialog, null);

        textDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        textDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        textDialog.setView(dialogView);
        textDialog.setCanceledOnTouchOutside(true);

        Window window = textDialog.getWindow();
        window.setLayout(GridLayout.LayoutParams.MATCH_PARENT, GridLayout.LayoutParams.MATCH_PARENT);
        window.setGravity(Gravity.CENTER);

        Spinner version = dialogView.findViewById(R.id.versionSpinner);
        TextView content = dialogView.findViewById(R.id.tvContent);

        content.setTextSize(TypedValue.COMPLEX_UNIT_SP, Variables.display_font_size);

        View dismissBtn = dialogView.findViewById(R.id.dismiss);
        dismissBtn.setOnClickListener(v -> textDialog.dismiss());

        simpleText = new SimpleFormatter(mContext, isPluginAvailable(), null, null, null, textDialog);

    }

    private void setSubtitle(TextView tvSubTitle, String dayTitle, final String subTitle) {
        handler3.removeCallbacks(r2);
        r2 = new SRunnable(tvSubTitle, subTitle);
        if (tvSubTitle != null)
            tvSubTitle.setText(simpleText.formatLine(dayTitle, tvSubTitle, true));
        handler3.postDelayed(r2, 5000);
    }

    public void loadText(DatabaseReference refLesson, final TextsAdapter textsAdapter, final List<Texts> textsList, final TopProgressBar topBar, final AlertDialog alertProgress) {
        Log.v(TAG, refLesson.getKey());
        if (topBar != null && alertProgress == null) {
            topBar.setVisibility(View.VISIBLE);
        } else if (topBar == null && alertProgress != null) {
            alertProgress.show();
        }
        refLesson.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dSnapshot) {
                // TODO: Implement this method

                HashMap<String, String> lessonMap = (HashMap<String, String>) dSnapshot.getValue();

                if (lessonMap!=null){
                    String state = lessonMap.containsKey("state") ? lessonMap.get("state") : "1";

                    if (lessonMap.containsKey(Variables.context)){
                        isLessonAccessible = true;
                        if (state.equals("1")||state.isEmpty()){

                            String lessonText = lessonMap.get(Variables.context);
                            if (lessonText != null && !lessonText.isEmpty()) {
                                textsList.clear();
                                textsAdapter.canHighlight = true;
                                lessonText = lessonText.replace("</p>", "<d>");
                                lessonText = lessonText.replace("<p>", "");
                                String[] contents = lessonText.split("<d>");

                                for (int i = 0; i < contents.length; i++) {
                                    textsList.add(new Texts(contents[i]));
                                }
                                textsAdapter.notifyDataSetChanged();

                                if (addsTxt[0]!=null) textsAdapter.onSpecialMessageChange(addsTxt[0], null);
                                if (addsTxt[1]!=null) textsAdapter.onSpecialMessageChange(null, addsTxt[1]);

                            } else {
                                textsList.clear();
                                
                                if (topBar != null && alertProgress == null) {
                                    textsAdapter.canHighlight = false;
                                    lessonText = Variables.getDftMsg();
                                    lessonText = lessonText.replace("</p>", "<d>");
                                    lessonText = lessonText.replace("<p>", "");
                                    String[] contents = lessonText.split("<d>");

                                    for (int i = 0; i < contents.length; i++) {
                                        textsList.add(new Texts(contents[i]));
                                    }
                                    textsAdapter.notifyDataSetChanged();

                                } else if (topBar == null && alertProgress != null) {
                                    textsAdapter.canHighlight = false;
                                    
                                    lessonText = Variables.getDftFindMsg();
                                    lessonText = lessonText.replace("</p>", "<d>");
                                    lessonText = lessonText.replace("<p>", "");
                                    String[] contents = lessonText.split("<d>");

                                    for (int i = 0; i < contents.length; i++) {
                                        textsList.add(new Texts(contents[i]));
                                    }

                                    textsAdapter.notifyDataSetChanged();
                                }

                            }
                        }else {
                            //
                            textsList.clear();
                            textsAdapter.canHighlight = false;
                            isLessonAccessible = false;
                            String stateOffMsg = mContext.getString(R.string.access_denied);
                            stateOffMsg = stateOffMsg.replace("</p>", "<d>");
                            stateOffMsg = stateOffMsg.replace("<p>", "");
                            String[] contents = stateOffMsg.split("<d>");

                            for (int i = 0; i < contents.length; i++) {
                                textsList.add(new Texts(contents[i]));
                            }

                            textsAdapter.notifyDataSetChanged();

                            textsAdapter.onSpecialMessageRemoved("", "");
                        }

                    }
                }else{
                    textsList.clear();
                    if (topBar != null && alertProgress == null) {
                        textsAdapter.canHighlight = false;
                        
                        String lessonText = Variables.getDftMsg();
                        lessonText = lessonText.replace("</p>", "<d>");
                        lessonText = lessonText.replace("<p>", "");
                        String[] contents = lessonText.split("<d>");

                        for (int i = 0; i < contents.length; i++) {
                            textsList.add(new Texts(contents[i]));
                        }
                        textsAdapter.notifyDataSetChanged();

                    } else if (topBar == null && alertProgress != null) {
                        textsAdapter.canHighlight = false;
                        
                        String lessonText = Variables.getDftFindMsg();
                        lessonText = lessonText.replace("</p>", "<d>");
                        lessonText = lessonText.replace("<p>", "");
                        String[] contents = lessonText.split("<d>");

                        for (int i = 0; i < contents.length; i++) {
                            textsList.add(new Texts(contents[i]));
                        }

                        textsAdapter.notifyDataSetChanged();
                    }
                }

                if (topBar != null && alertProgress == null) {
                    topBar.setVisibility(View.GONE);
                } else if (topBar == null && alertProgress != null) {
                    alertProgress.dismiss();
                }

            }

            @Override
            public void onCancelled(DatabaseError dbError) {
                // TODO: Implement this method
            }


        });
    }

    private void startRepeatingTask(TextView tvTitle, final String quarterName, final String lessonTitle) {
        r1 = new TRunnable(tvTitle, quarterName, lessonTitle);
        r1.run();
    }

    private void stopRepeatingTask() {
        handler.removeCallbacks(r1);
    }
    private void delayShow(final TextView tvTitle, final String quarterName, final String lessonTitle) {
        try {
            tvTitle.setText(simpleText.formatLine(lessonTitle, tvTitle, true));
        } catch (NullPointerException ex) {
            ex.printStackTrace();
        }
        handler2.postDelayed(() -> {
            // Do something after 5s = 5000ms
            try {
                tvTitle.setText(simpleText.formatLine(quarterName, tvTitle, true));
            } catch (NullPointerException ex) {
                ex.printStackTrace();
            }
        }, 20000);


    }
    private class TRunnable implements Runnable {
        private TextView tvTitle;
        private String quarterName;
        private String lessonTitle;

        public TRunnable(TextView tvTile, final String quarterName, final String lessonTitle) {
            this.tvTitle = tvTile;
            this.quarterName = quarterName;
            this.lessonTitle = lessonTitle;
        }

        @Override
        public void run() {
            try {
                delayShow(tvTitle, quarterName, lessonTitle);
            } finally {
                // 100% guarantee that this always happens, even if
                // update method throws an exception
                handler.postDelayed(this, 30000);
            }
        }


    }
    private class SRunnable implements Runnable {
        private TextView tvSubTitle;
        private String subTitle;

        public SRunnable(TextView tvSubTitle, String subTitle) {
            this.tvSubTitle = tvSubTitle;
            this.subTitle = subTitle;
            textScroller = new AnimationAutoTextScroller(tvSubTitle, Variables.tWidth, simpleText);
        }

        @Override
        public void run() {
            tvSubTitle.setText(simpleText.formatLine(subTitle, tvSubTitle, true));
            textScroller.setScrollingText(subTitle);
            if (Variables.scrollTitle) textScroller.start();

        }

    }
    public static String getDay(int index) {
        if (index == 7 || index == 8) {
            return "KU ISABATO";
        } else {
            switch (index) {
                case 0:
                    return "KU ISABATO ISHIZE";
                case 1:
                    return "KU WA MBERE";
                case 2:
                    return "KU WA KABIRI";
                case 3:
                    return "KU WA GATATU";
                case 4:
                    return "KU WA KANE";
                case 5:
                    return "KU WA GATANU";
                case 6:
                    return "KU WA GATANDATU";
                default:
                    return "-- -- ----";
            }

        }
    }
    public void runCounter(SharedPreferences prefs) {
        int runs = prefs.getInt("runcounter", 0);
        runs = runs + 1;
        prefs.edit().putInt("runcounter", runs).commit();

    }
    public int getRunCount(SharedPreferences prefs) {
        return prefs.getInt("runcounter", 0);
    }
    private boolean isPluginAvailable() {
        SharedPreferences preferences = mContext.getSharedPreferences("app_prefs", mContext.MODE_PRIVATE);
        return preferences.getBoolean("bibleCompleted", false);
    }
    public static String regexTime(String value) {
        Pattern pattern = Pattern.compile("([\\d]{1,2}:[\\d]{1,2}|[\\d]{1,2}:[\\d]{1,2} [aApP][mM])");
        Matcher matcher = pattern.matcher(value);
        matcher.find();
        return matcher.group();
    }
    public static String regexTimezone(String value) {
        Matcher matcher = Pattern.compile("([a-zA-Z0-9]*)/([a-zA-Z0-9]*)", Pattern.CASE_INSENSITIVE).matcher(value);
        while (matcher.find()) {
            return matcher.group(0);
        }
        return "";
    }
    public static String regexDate(String value) {
        Matcher m = Pattern.compile("(\\d{1,2}/\\d{1,2}/\\d{4}|\\d{1,2}/\\d{1,2}/\\d{1,2}|\\d{1,2}/\\d{1,2})", Pattern.CASE_INSENSITIVE).matcher(value);
        m.find();
        return m.group();
    }
    public void loadTipContexts(DatabaseReference refToLesson, final TextsAdapter textsAdapter) {
        refToLesson.child("tip")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String content = snapshot.getValue(String.class);
                        Log.v(TAG, String.valueOf(isLessonAccessible));
                        if (content != null && isLessonAccessible) {
                            if (textsAdapter.quoteTxt[0].isEmpty()) textsAdapter.quoteTxt[0] = content;
                            textsAdapter.onSpecialMessageChange(content, null);
                            addsTxt[0] = content;
                        } else {
                            textsAdapter.onSpecialMessageRemoved("", null);
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

    }
    public void loadLessonOtherContext(DatabaseReference refToKey, final TextsAdapter textsAdapter) {
        refToKey.child("tip")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String content = snapshot.getValue(String.class);
                        Log.v(TAG, String.valueOf(isLessonAccessible));
                        if (content != null && isLessonAccessible) {
                            if (textsAdapter.quoteTxt[1].isEmpty()) textsAdapter.quoteTxt[1] = content;
                            textsAdapter.onSpecialMessageChange(null,content);
                            addsTxt[1] = content;

                        } else {
                            textsAdapter.onSpecialMessageRemoved(null, "");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
    public Date getWeekStartDate() {
        Calendar calendar = Calendar.getInstance();
        while (calendar.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY) {
            calendar.add(Calendar.DATE, -1);
        }
        return calendar.getTime();
    }
    public void formatWeek(@Nullable List<String> fragmentTitleList) {
        Variables.dateIds.clear();
        ArrayList<String> l1 = new ArrayList<>();
        ArrayList<String> l2 = new ArrayList<>();
        Calendar cal = Calendar.getInstance();
        cal.setFirstDayOfWeek(Calendar.SUNDAY);
        cal.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
        cal.setTime(getWeekStartDate());

        SimpleDateFormat format = new SimpleDateFormat("EEEE\ndd/MM/yyyy");
        SimpleDateFormat formatId = new SimpleDateFormat("dd_MM_yyyy");

        for (int i = 0; i <= 8; i++) {
            if (i == 0) {
                cal.add(Calendar.DATE, -1);
            }
            l1.add(format.format(cal.getTime()));
            Variables.dateIds.add(formatId.format(cal.getTime()));
            cal.add(Calendar.DATE, 1);

        }
        // get prev week
        cal.setTime(getWeekStartDate());
        if (fragmentTitleList != null) {
            fragmentTitleList.clear();
            fragmentTitleList.addAll(l2);
            fragmentTitleList.addAll(l1);
        }
    }
    public Date convertDateId(String dateId) {
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
    class TitleChanger implements Runnable {
        String[] titles;
        CollapsingToolbarLayout collapsingToolbarLayout;
        int index = 0;

        public TitleChanger(String[] titles, CollapsingToolbarLayout collapsingToolbarLayout) {
            super();
            this.titles = titles;
            this.collapsingToolbarLayout = collapsingToolbarLayout;
            Log.v(TAG, "T Size: " + titles.length);
        }

        public void run() {
            Log.v(TAG, "T INDEX: " + index);
            collapsingToolbarLayout.setTitle(titles[index]);
            index++;
            if (index >= titles.length) index = 0;
            handler.postDelayed(this, 15000);
        }
    }
    public void loadTitles(CollapsingToolbarLayout collapsingToolbarLayout, HashMap<String, String> mapTitles, @Nullable HashMap<Integer, String> mapPoints, final int dayIndex, @Nullable String dateKey, final boolean isTabVisible) {

        Log.v(TAG, "INDEX: " + dayIndex);

        handler.removeCallbacks(titleChanger);

        if (titleChanger != null) handler.removeCallbacks(titleChanger);

        if (Variables.dateIds.size() <= 0) formatWeek(null);

        final String date = dateKey==null ? Variables.dateIds.get(dayIndex).replace("_", "/") : dateKey.replace("_", "/");

        if (!isTabVisible) collapsingToolbarLayout.setTitle(getDay(dayIndex) + ", " + date);
        else collapsingToolbarLayout.setTitle(getDay(dayIndex));

        final String dateValue = dateKey==null ? Variables.dateIds.get(dayIndex) : dateKey;

        String q = Grab.quarter(convertedCalendar(dateValue));
        String l = Grab.lesson(convertedCalendar(dateValue));

        String quarter = mapTitles.get(q);
        String lesson = mapTitles.get(l);
        String title;
        if (dateKey == null) {
            assert mapPoints != null;
            title = mapPoints.get(dayIndex);
        } else {
            title = mapTitles.get(dateKey);
        }

        final String[] titles = {title, lesson, quarter};

        Log.v(TAG, title+" "+lesson+" "+quarter);

        titleChanger = new TitleChanger(titles, collapsingToolbarLayout);
        handler.postDelayed(titleChanger, 5500);
    }

    public int dpToPx(int dp) {
        Resources r = mContext.getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }

}
