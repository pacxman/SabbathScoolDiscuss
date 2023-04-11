package com.ssdiscusskiny.services;

import com.ssdiscusskiny.MainActivity;
import com.ssdiscusskiny.R;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.util.Calendar;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.text.ParseException;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import com.google.firebase.database.ValueEventListener;

import com.ssdiscusskiny.activities.LessonActivity;
import com.ssdiscusskiny.app.SSDiscuss;
import com.ssdiscusskiny.app.Variables;
import com.ssdiscusskiny.generator.Grab;
import com.ssdiscusskiny.utils.Parser;

import android.app.Notification;
import android.app.PendingIntent;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class NotificationService extends Service {
    private FirebaseDatabase database;
    private DatabaseReference refWeekTitle, refDayTopic;

    private String dateId = "", title = "";

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Implement this method
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // TODO: Implement this method
        System.out.println("**Alarm service fired**");
        Calendar cal = Calendar.getInstance();
        cal.setFirstDayOfWeek(Calendar.SUNDAY);
        SimpleDateFormat sdf = new SimpleDateFormat("dd_MM_yyyy");

        final int day = cal.get(Calendar.DAY_OF_WEEK);

        dateId = sdf.format(cal.getTime());
        database = FirebaseDatabase.getInstance();

        refWeekTitle = database.getReference().child(Variables.content).child(Variables.lessons).child(Grab.year(convertedCalendar(dateId))).child(Grab.quarter(convertedCalendar(dateId))).child(Grab.lesson(convertedCalendar(dateId))).child(Variables.title);
        refDayTopic = database.getReference().child(Variables.content).child(Variables.lessons).child(Grab.year(convertedCalendar(dateId))).child(Grab.quarter(convertedCalendar(dateId))).child(Grab.lesson(convertedCalendar(dateId))).child(dateId).child(Variables.point);

        PreferenceManager.setDefaultValues(this, R.xml.prefs, false);
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);

        title = getString(R.string.app_name);

        refWeekTitle.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot ds) {
                // TODO: Implement this method
                ds.getRef().removeEventListener(this);

                if (ds.getValue(String.class) != null) {
                    title = ds.getValue(String.class);
                    refDayTopic.addListenerForSingleValueEvent(new ValueEventListener() {

                        @Override
                        public void onDataChange(DataSnapshot dsnap) {
                            // TODO: Implement this method
                            dsnap.getRef().removeEventListener(this);
                            String v = dsnap.getValue(String.class);
                            if (v != null) {
                                createNotification(getBaseContext(), prefs.getBoolean("notify", true), title, v);
                                System.out.println("***Alarm 1***");
                            } else {
                                createNotification(getBaseContext(), prefs.getBoolean("notify", true), title, getString(R.string.notif_miss_prefix) + " " + Parser.getDay(day));
                                System.out.println("***Alarm 2***");
                            }
                            System.out.println("***Alarm called***");
                        }

                        @Override
                        public void onCancelled(DatabaseError dberror) {
                            // TODO: Implement this method
                        }


                    });
                } else {
                    createNotification(getBaseContext(), prefs.getBoolean("notify", true), getString(R.string.app_name), getString(R.string.read_hint) + Parser.getDay(day));
                }

            }

            @Override
            public void onCancelled(DatabaseError db) {
                // TODO: Implement this method
            }


        });

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onCreate() {
        // TODO: Implement this method
        super.onCreate();
    }

    public void createNotification(Context context, boolean notify, String title, String text) {

        final Calendar calNow = Calendar.getInstance();
        calNow.setFirstDayOfWeek(Calendar.SUNDAY);
        int dayId = calNow.get(Calendar.DAY_OF_WEEK);

        SimpleDateFormat sdf = new SimpleDateFormat("dd_MM_yyyy");
        String dateId = sdf.format(calNow.getTime());

        Intent snoozeIntent = new Intent( context, MainActivity.class);

        snoozeIntent.setAction("CLEAR NOTIFICATION");
        snoozeIntent.putExtra("TAB", dayId);

        PendingIntent snoozePendingIntent =
                PendingIntent.getActivity(context, 0, snoozeIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        // Create an explicit intent for an Activity in your app
        Intent intentCal = new Intent(context, LessonActivity.class);
        intentCal.putExtra("date_value", dateId);
        intentCal.putExtra("day_id", dayId);
        intentCal.putExtra("caller", "READ");
        intentCal.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intentCal, 0);

        text = text.replace("[", "");
        text = text.replace("]", "");

        text = text.replace("{'", "[");
        text = text.replace("'}", "]");

        text = text.replace("<i>", "");
        text = text.replace("</i>", "");

        text = text.replace("<b>", "");
        text = text.replace("</b>", "");

        text = text.replace("<p>", "");
        text = text.replace("</p>", "");

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, SSDiscuss.CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_icon)
                .setContentTitle(title)
                .setContentText(text)
                .setColor(context.getResources().getColor(R.color.colorPrimary))

                // Set the intent that will fire when the user taps the notification
                .setContentIntent(pendingIntent)

                .setPriority(Notification.PRIORITY_HIGH)
                .addAction(R.drawable.ic_read_notif, context.getString(R.string.read_comment), snoozePendingIntent)
                .setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);// notificationId is a unique int for each notification that you must define


        if (notify) {
            notificationManager.notify(100, builder.build());
        }

        stopSelf();

    }

    private Date convertDateId(String dateId) {
        try {
            return new SimpleDateFormat("dd_MM_yyyy").parse(dateId);
        } catch (ParseException e) {
            //Calendar cal = Calendar.getInstance();
            //Toast.makeText(this, cal.getTime().toString(), Toast.LENGTH_SHORT).show();
            return Calendar.getInstance().getTime();
        }
    }

    private Calendar convertedCalendar(String dateId) {
        Calendar cal = Calendar.getInstance();

        int year = Integer.parseInt(new SimpleDateFormat("yyyy").format(convertDateId(dateId)));
        int month = Integer.parseInt(new SimpleDateFormat("MM").format(convertDateId(dateId)));
        int t_date = Integer.parseInt(new SimpleDateFormat("dd").format(convertDateId(dateId)));

        cal.set(year, month - 1, t_date);

        return cal;
    }

}
