package com.ssdiscusskiny.receivers;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.ssdiscusskiny.MainActivity;
import com.ssdiscusskiny.R;
import com.ssdiscusskiny.activities.LessonActivity;
import com.ssdiscusskiny.app.SSDiscuss;
import com.ssdiscusskiny.generator.Grab;
import com.ssdiscusskiny.services.NotificationService;
import com.ssdiscusskiny.utils.PanelHandler;

import java.util.Calendar;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.text.ParseException;

public class AlarmReceiver extends BroadcastReceiver {
    //private boolean notified = false;

    private String TAG = getClass().getSimpleName();
    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: Implement this method
        Log.d("BOOTRECEIVER", "ALARM RECEIVED");
        PreferenceManager.setDefaultValues(context, R.xml.prefs, false);
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        final SharedPreferences pref = context.getSharedPreferences("Alarm", context.MODE_PRIVATE);
        final SharedPreferences dataPrefs = context.getSharedPreferences("data_prefs", Context.MODE_PRIVATE);

        final Editor editor = pref.edit();
        final Calendar calNow = Calendar.getInstance();

        boolean notified = pref.getBoolean("notified", false);

        SimpleDateFormat formater = new SimpleDateFormat("dd-MM-yyyy");

        if (!pref.contains("prevNotifDate")) {
            /*//Get first date alarm was set on
            String fAlarmDate = pref.getString("f_alarm_date", new SimpleDateFormat("dd-MM-yyyy").format(calNow));
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(convertString(fAlarmDate));
            calendar.set(Calendar.HOUR, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);

            calNow.set(Calendar.HOUR, 0);
            calNow.set(Calendar.MINUTE, 0);
            calNow.set(Calendar.SECOND, 0);
            calNow.set(Calendar.MILLISECOND, 0);

            if (calNow.compareTo(calendar)>0){
                //Fire the alarm service since alarm set date is behind one day of now calendar
                Intent serviceIntent = new Intent(context, NotificationService.class);
                if (!appPrefs.getBoolean("firstRun", true)){
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        context.startForegroundService(serviceIntent);
                    } else {
                        context.startService(serviceIntent);
                    }
                }
            }*/

            editor.putString("prevNotifDate", formater.format(calNow.getTime()));
            editor.putBoolean("notified", true);
            Log.d("ALARMRECEIVER", "ALARM_RECEIVED");

            editor.commit();
        } else {
            Date lastNotifDate = convertString(pref.getString("prevNotifDate", formater.format(calNow.getTime())));
            Date currentDate = convertString(formater.format(calNow.getTime()));

            //Let check if current date is greater than or equal to the last Notified date
            if (currentDate.after(lastNotifDate) || currentDate.equals(lastNotifDate)) {
                //check if both current date and last Notified date are equal
                if (lastNotifDate.equals(currentDate)) {
                    if (notified) {
                        //Alarm was fired on same day
                        //Toast.makeText(context, "Alarm already notified today", Toast.LENGTH_LONG).show();
                        System.out.printf("Alarm already notified");
                    } else {
                        //Let fire notification and save changes
                        notified = true;
                        editor.putString("prevNotifDate", formater.format(calNow.getTime()));
                        editor.putBoolean("notified", notified);
                        Intent serviceIntent = new Intent(context, NotificationService.class);
                        //Check if the user have run application
                        //Create notification

                        loadNotification(prefs, dataPrefs, context);


                        /*if (!appPrefs.getBoolean("firstRun", true)){
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                context.startForegroundService(serviceIntent);
                            } else {
                                context.startService(serviceIntent);
                            }
                        }*/

                        editor.commit();

                    }
                }
                //Let check if current date is greater than last notified date
                if (currentDate.after(lastNotifDate)) {
                    //if (notified){
                    notified = true;
                    editor.putString("prevNotifDate", formater.format(calNow.getTime()));
                    editor.putBoolean("notified", notified);
                    //Intent serviceIntent = new Intent(context, NotificationService.class);

                    loadNotification(prefs, dataPrefs, context);

                    /*if (!appPrefs.getBoolean("firstRun", true)) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            context.startForegroundService(serviceIntent);
                        } else {
                            context.startService(serviceIntent);
                        }
                    }*/

                    editor.commit();

                    //}else{

                    //}
                }
            } else if (currentDate.before(lastNotifDate)) {
                //this is an error date not well adjusted
                //String lastWrongDate = formater.format(calNow.getTime());
                //editor.putString("lastWrongDate", lastWrongDate);

                //	if (lastNotifDate.before(currentDate)||lastNotifDate.equals(currentDate)){
                notified = true;
                editor.putString("prevNotifDate", formater.format(calNow.getTime()));
                editor.putBoolean("notified", notified);
                editor.commit();
                //	}

                PanelHandler panel = new PanelHandler(context);
                panel.showToast(context.getString(R.string.app_name) + context.getString(R.string.alarm_error));
                editor.putBoolean("notified", notified);

                editor.commit();
            }
        }

    }

    private Date convertString(String stringDate) {
        Date date = Calendar.getInstance().getTime();
        try {
            date = null;
            date = new SimpleDateFormat("dd-MM-yyyy").parse(stringDate);
        } catch (ParseException e) {

        }

        return date;
    }

    public void createNotification(Context context, boolean notify, String title, String text) {

        final Calendar calNow = Calendar.getInstance();
        calNow.setFirstDayOfWeek(Calendar.SUNDAY);
        int dayId = calNow.get(Calendar.DAY_OF_WEEK);
        Log.v(TAG, dayId+" "+calNow.getTime());

        SimpleDateFormat sdf = new SimpleDateFormat("dd_MM_yyyy");
        String dateId = sdf.format(calNow.getTime());

        Intent snoozeIntent = new Intent( context, MainActivity.class);

        snoozeIntent.setAction("CLEAR NOTIFICATION");
        snoozeIntent.putExtra("TAB", dayId);

        PendingIntent snoozePendingIntent =
                PendingIntent.getActivity(context, 0, snoozeIntent, PendingIntent.FLAG_IMMUTABLE);

        // Create an explicit intent for an Activity in your app
        Intent intentCal = new Intent(context, LessonActivity.class);
        intentCal.putExtra("date_value", dateId);
        intentCal.putExtra("day_id", dayId);
        //intentCal.putExtra("caller", "READ");

        intentCal.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intentCal, PendingIntent.FLAG_IMMUTABLE);

        /*text = text.replace("[", "");
        text = text.replace("]", "");

        text = text.replace("{'", "[");
        text = text.replace("'}", "]");

        text = text.replace("<i>", "");
        text = text.replace("</i>", "");

        text = text.replace("<b>", "");
        text = text.replace("</b>", "");

        text = text.replace("<p>", "");
        text = text.replace("</p>", "");*/

        NotificationCompat.Action action =
                new NotificationCompat.Action.Builder(R.drawable.ic_read_notif,
                        context.getString(R.string.read_comment),
                        snoozePendingIntent)
                        .build();

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, SSDiscuss.CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_icon)
                .setContentTitle(title)
                .setContentText(text)
                .setColor(context.getResources().getColor(R.color.colorPrimary))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                //Set the intent that will fire when the user taps the notification
                .setContentIntent(pendingIntent)
                .addAction(action)
                .setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);// notificationId is a unique int for each notification that you must define


        if (notify) {
            notificationManager.notify(100, builder.build());
        }

    }

    private void loadNotification(SharedPreferences prefs, SharedPreferences dataPrefs, Context context){

        final String mKey = Grab.year(Calendar.getInstance())+"_"+Grab.quarter(Calendar.getInstance())+"_"+Grab.lesson(Calendar.getInstance());

        String topic = dataPrefs.getString(mKey+"_title", "");

        if (topic.isEmpty()) topic = context.getString(R.string.app_name);

        final boolean notify = prefs.getBoolean("notify", true);

        createNotification(context, notify, topic, context.getString(R.string.default_notification_title));

    }


}
