package com.ssdiscusskiny.app;

import android.content.Context;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import android.content.SharedPreferences;
import android.util.Log;

import com.ssdiscusskiny.R;
import com.ssdiscusskiny.generator.Grab;
import com.ssdiscusskiny.utils.PanelHandler;

public class Variables {
    public static boolean night_mode = false;
    public static int font_size =22;
    public static int display_font_size =22;
    //public static boolean isFirstRun = true;
    public static boolean ntfTimeChanged = false;
    public static boolean mOptionChanged = false;
    public static boolean showAlert = true;
    //public static final String HTML_PATTERN = "<(\"[^\"]*\"|'[^']*'|[^']*'|[^'\">])*>";
    public static boolean scrollTitle = false;
    public static int tWidth;
    public static String format = "";
    public static final ArrayList<String> dateIds = new  ArrayList<>();
    public static String lessons = "lessons";
    public static String context = "context";
    public static String title = "title";
    public static String point = "point";
    public static String quarterTopic="Sabbath School";
    public static String weekTopic="Discuss";
    public static boolean ntfShowed = false;
    public static boolean firstVidAd = true;
    public static boolean firstAdShow = true;
    //public static String userName = "[noUsername]";
    public static final String content = "contents";
    private Context mContext;
    //private final File bibleDir = new File(Environment.getExternalStorageDirectory(), ".ssdiscussfiles");

    public Variables(Context mContext) {
        this.mContext = mContext;
    }

    public static String getDftMsg(){
        //SharedPreferences appPrefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE);
        String dfUrl = "<img src=\"https://firebasestorage.googleapis.com/v0/b/sabbathschooldiscuss.appspot.com/o/images%2Fdefaults%2Ferror.png?alt=media&token=80e0eb81-5a47-4f78-8a06-643c8bbdede8\"/>"; //appPrefs.getString("df_show_url","");
        if (!dfUrl.isEmpty()){
            return "<p>"+dfUrl+"<br><font color=\"#810000\"><h1>Lesson not found</h1><br><b>&nbsp • Make sure your system date is collect<br>&nbsp • You have data connection<br>&nbsp • Try to close and restart this app.</b><br><br><i><small>&nbsp &nbsp &nbsp We apologize for this inconvenience!</small></i></font></p>";
        }
        else{
            return "<p><font color=\"#810000\"><h1>Lesson not found</h1><br><br><b>&nbsp • Make sure your  system date is collect<br>&nbsp • You have data connection<br>&nbsp • Try to close and restart this app.</b><br><br><i><small>&nbsp &nbsp &nbsp We apologize for this inconvenience!</small></i></font></p>";
        }
    }

    public static String getDftFindMsg(){
        //SharedPreferences appPrefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE);
        String dfUrl = "<img src=\"https://firebasestorage.googleapis.com/v0/b/sabbathschooldiscuss.appspot.com/o/images%2Fdefaults%2Ferror.png?alt=media&token=666e07f7-b012-46b6-8ad5-bf620155f0a4\"/>";
        if (!dfUrl.isEmpty()){
            return "<p>"+dfUrl+"<font color=\"#810000\"><h1>Lesson not found</h1><br>Please select a valid date or make sure the date is in current or past quarters.</font></p>";
        }
        else{
            return "<p><b><font color=\"#810000\"><br><h1>This lesson is not found</h1><br><br>Please select a valid date or make sure the date is in current or past quarters.</font></p>";
        }
    }

    public static String getDefaultImage(Context context){
        SharedPreferences appPrefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE);
        Log.d("URL_HOME", appPrefs.getString("df_show_url",""));
        return appPrefs.getString("df_show_url","");
    }

    public static String formatTitle(Context context,Calendar calendar){
        String prefix = context.getString(R.string.quarter_of);
        switch(Grab.quarter(calendar)){
            case "Q1":
                return prefix+" 1 "+Grab.year(calendar);
            case "Q2":
                return prefix+" 2 "+Grab.year(calendar);
            case "Q3":
                return prefix+" 3 "+Grab.year(calendar);
            case "Q4":
                return prefix+" 4 "+Grab.year(calendar);
            default: return "Sabbath School";
        }

    }
    public static String formatTitle(Context context, String year, String qTitle){
        String prefix = context.getString(R.string.quarter_of);
        switch(qTitle){
            case "Q1":
                return prefix+" 1 "+year;
            case "Q2":
                return prefix+" 2 "+year;
            case "Q3":
                return prefix+" 3 "+year;
            case "Q4":
                return prefix+" 4 "+year;
            default: return "Sabbath School";
        }

    }

    public static String formatTitle(String year, String qTitle){
        switch(qTitle){
            case "Q1":
                return "1 "+year;
            case "Q2":
                return "2 "+year;
            case "Q3":
                return "3 "+year;
            case "Q4":
                return "4 "+year;
            default: return "Sabbath School";
        }

    }

    public static String formatTitleShort(Calendar calendar){

        switch(Grab.quarter(calendar)){
            case "Q1":
                return "1 "+Grab.year(calendar);
            case "Q2":
                return "2 "+Grab.year(calendar);
            case "Q3":
                return "3 "+Grab.year(calendar);
            case "Q4":
                return "4 "+Grab.year(calendar);
            default: return "Sabbath School";
        }

    }

}
