package com.ssdiscusskiny.monetize;

import android.content.Context;

import java.util.Calendar;
import java.util.Date;


public class Monetize {
    private Context context;

    public Monetize(Context context) {
        this.context = context;
    }

    public static boolean monetize() {
        Calendar cal = Calendar.getInstance();
        Date date = new Date();
        cal.setTime(date);

        if (cal.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY && cal.get(Calendar.HOUR_OF_DAY) < 14) {
            return false;
        } else {
            return true;
        }
    }

		/*private void unmuteSound(Context context)
		{
				AudioManager aManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
				aManager.setStreamMute(AudioManager.STREAM_MUSIC, false);
		}
		private void muteSound(Context context)
		{
				AudioManager aManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
				aManager.setStreamMute(AudioManager.STREAM_MUSIC, true);
		}*/

}
