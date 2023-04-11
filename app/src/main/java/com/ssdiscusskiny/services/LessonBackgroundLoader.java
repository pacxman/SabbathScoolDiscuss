package com.ssdiscusskiny.services;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ssdiscusskiny.app.Variables;
import com.ssdiscusskiny.generator.Grab;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class LessonBackgroundLoader extends Service {

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("SERVICE_LOADERS", this.getClass().getName()+" STARTED");
        new Thread(new Runnable() {
            @Override
            public void run() {
                final FirebaseDatabase mDb = FirebaseDatabase.getInstance();
                final Calendar calendar = Calendar.getInstance();

                final Calendar calendar1 = Calendar.getInstance();
                calendar1.add(Calendar.WEEK_OF_MONTH, 1);

                final Calendar calendar2 = Calendar.getInstance();
                calendar2.add(Calendar.WEEK_OF_MONTH, 2);

                final DatabaseReference mRef = mDb.getReference().child(Variables.content)
                        .child("lessons")
                        .child(Grab.year(calendar))
                        .child(Grab.quarter(calendar))
                        .child(Grab.lesson(calendar));

                final DatabaseReference mRef2 = mDb.getReference().child(Variables.content)
                        .child("lessons")
                        .child(Grab.year(calendar1))
                        .child(Grab.quarter(calendar1))
                        .child(Grab.lesson(calendar1));

                final DatabaseReference mRef3 = mDb.getReference().child(Variables.content)
                        .child("lessons")
                        .child(Grab.year(calendar2))
                        .child(Grab.quarter(calendar2))
                        .child(Grab.lesson(calendar2));

                mDb.getReference().child(Variables.content)
                        .child("lessons")
                        .child(Grab.year(calendar1))
                        .child(Grab.quarter(calendar1))
                        .child("image_url")
                        .keepSynced(true);

                mDb.getReference().child(Variables.content)
                        .child("data")
                        .child("pend_alert")
                        .child("content")
                        .keepSynced(true);

                mDb.getReference().child("data")
                        .child("pend_alert")
                        .child("title")
                        .keepSynced(true);

                mDb.getReference().child("data")
                        .child("skin_info")
                        .keepSynced(true);


                mRef.addValueEventListener(new ValueEventListener(){

                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        snapshot.getRef().removeEventListener(this);

                        for (DataSnapshot s : snapshot.getChildren()) {
                            mRef.child(s.getKey()).child("point").keepSynced(true);
                            mRef.child(s.getKey()).child("context").keepSynced(true);
                            mRef.child(s.getKey()).child("homeImgUrl").keepSynced(true);
                        }
                        mRef2.addValueEventListener(new ValueEventListener(){

                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                snapshot.getRef().removeEventListener(this);

                                for (DataSnapshot s : snapshot.getChildren()) {
                                    mRef2.child(s.getKey()).child("point").keepSynced(true);
                                    mRef2.child(s.getKey()).child("context").keepSynced(true);
                                }

                                mRef3.addValueEventListener(new ValueEventListener(){

                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        snapshot.getRef().removeEventListener(this);

                                        for (DataSnapshot s : snapshot.getChildren()) {
                                            mRef3.child(s.getKey()).child("point").keepSynced(true);
                                            mRef3.child(s.getKey()).child("context").keepSynced(true);
                                        }

                                        Log.d("SERVICE_LOADERS", "DONE LOADING");

                                        stopSelf();
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                        stopSelf();
                                    }
                                });

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                stopSelf();
                            }
                        });


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        stopSelf();
                    }
                });
            }
        }).start();

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        Log.d("SERVICE_LOADERS", this.getClass().getName()+" KILLED");
    }
    
}
