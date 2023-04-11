package com.ssdiscusskiny.app;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.SharedPreferences;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.multidex.MultiDexApplication;

/*import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;*/
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.security.Key;
import java.util.HashMap;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;


public class SSDiscuss extends MultiDexApplication
{

    private String TAG = getClass().getSimpleName();
    public static String CHANNEL_ID = "SSD_READ";
    public static String CHANNEL_NAME = "Daily Update";
    public static String CHANNEL_DESCRIPTION = "Daily reminder for lesson";
	
    @SuppressLint("MissingPermission")
    @Override
    public void onCreate()
    {
        // TODO: Implement this method
        super.onCreate();

        //Initialize ads
        //MobileAds.initialize(this,this.getString(R.string.admobAppId));

        createNotificationChannel();

        /*OnInitializationCompleteListener onInListener = initializationStatus -> {

        };
        MobileAds.initialize(this, onInListener);*/
        //Enable offline capability
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
		

        FirebaseDatabase.getInstance().getReference("contents").child("comments").keepSynced(true);
		FirebaseDatabase.getInstance().getReference().child("admin").child("state").keepSynced(true);
        FirebaseDatabase.getInstance().getReference().child("data").child("urls").child("plugin").keepSynced(true);
		
    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = CHANNEL_NAME;
            String description = CHANNEL_DESCRIPTION;
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            channel.enableVibration(true);
            channel.enableLights(true);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

}
