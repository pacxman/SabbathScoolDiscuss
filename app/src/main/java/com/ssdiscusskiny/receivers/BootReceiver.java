package com.ssdiscusskiny.receivers;

import android.app.PendingIntent;
import android.app.AlarmManager;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Intent;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import java.util.Calendar;
import java.text.SimpleDateFormat;

import android.preference.PreferenceManager;

import androidx.annotation.RequiresApi;
import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKey;

import com.ssdiscusskiny.LoginActivity;
import com.ssdiscusskiny.R;
import com.ssdiscusskiny.services.NetworkSchedulerService;

public class BootReceiver extends BroadcastReceiver {

    private String TAG = getClass().getSimpleName();
    //@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: Implement this method
        if (intent.getAction() == Intent.ACTION_BOOT_COMPLETED) {

            PreferenceManager.setDefaultValues(context, R.xml.prefs, false);
            final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);

            final Calendar cal = Calendar.getInstance();
            cal.set(Calendar.HOUR_OF_DAY, Integer.parseInt(prefs.getString("ntfHr", "8")));
            cal.set(Calendar.MINUTE, 00);
            cal.set(Calendar.SECOND, 00);

            Intent intent2 = new Intent(context, AlarmReceiver.class);
            final PendingIntent alarmIntent = PendingIntent.getBroadcast(context, 0, intent2, PendingIntent.FLAG_IMMUTABLE);

            final AlarmManager alarmMgr = (AlarmManager) context.getSystemService(context.ALARM_SERVICE);
            SharedPreferences appPrefs = null;

            try {
                MasterKey masterKey = new MasterKey.Builder(context)
                        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                        .build();

                appPrefs = EncryptedSharedPreferences.create(
                        context,
                        "account_prefs",
                        masterKey,
                        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM);

            } catch (Exception ex) {
                ex.printStackTrace();
            }

            //if (!appPrefs.getBoolean("firstRun", true))
            if (!appPrefs.getBoolean("firstRun", true)) alarmMgr.setInexactRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), AlarmManager.INTERVAL_DAY, alarmIntent);

            Log.d(TAG, "ALARM SET");
            //scheduleJob(context);

        }

    }

	/*@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
	private void scheduleJob(Context context) {
		JobInfo myJob = new JobInfo.Builder(0, new ComponentName(context, NetworkSchedulerService.class))
				.setRequiresCharging(true)
				.setMinimumLatency(1000)
				.setOverrideDeadline(2000)
				.setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
				.setPersisted(true)
				.build();

		JobScheduler jobScheduler = (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
		jobScheduler.schedule(myJob);
	}*/

}
