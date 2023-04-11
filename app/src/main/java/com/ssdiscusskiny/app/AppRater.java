package com.ssdiscusskiny.app;

import android.app.Dialog;
import android.content.Intent;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.util.Log;
import android.view.Gravity;
import android.view.Window;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.TextView;
import android.widget.LinearLayout;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;

import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKey;

import com.ssdiscusskiny.R;
import com.ssdiscusskiny.SplashActivity;

public class AppRater
 {

    private static int DAYS_UNTIL_PROMPT = 6;//Min number of days
    private final static int LAUNCHES_UNTIL_PROMPT = 6;//Min number of launches

    public static void app_launched(Context mContext) {
        SharedPreferences prefs = null;

		try{
			MasterKey masterKey = new MasterKey.Builder(mContext)
					.setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
					.build();

			prefs = EncryptedSharedPreferences.create(
					mContext,
					"account_prefs",
					masterKey,
					EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
					EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM);

		}catch(Exception ex){
			ex.printStackTrace();
		}


        if (prefs.getBoolean("dontshowagain", false)) { return ; }

        SharedPreferences.Editor editor = prefs.edit();

        // Increment launch counter
        long launch_count = prefs.getLong("launch_count", 0) + 1;
		DAYS_UNTIL_PROMPT = prefs.getInt("day_until_prompt", 6);
        

        // Get date of first launch
        Long date_firstLaunch = prefs.getLong("date_firstlaunch", 0);
        if (date_firstLaunch == 0) {
            date_firstLaunch = System.currentTimeMillis();
            editor.putLong("date_firstlaunch", date_firstLaunch);
        }

        // Wait at least n days before opening
        if (launch_count >= LAUNCHES_UNTIL_PROMPT) {
            if (System.currentTimeMillis() >= date_firstLaunch + 
				(DAYS_UNTIL_PROMPT * 24 * 60 * 60 * 1000)) {
                showRateDialog(mContext, editor);
				launch_count=0;
				date_firstLaunch=0l;
            }
        }

		editor.putLong("launch_count", launch_count);
        editor.commit();
    }   

    public static void showRateDialog(final Context mContext, final SharedPreferences.Editor editor) {
		final View dialogView = View.inflate(mContext, R.layout.rater_dlg, null);
        final Dialog dialog = new Dialog(mContext, R.style.MyDialogStyle);

		dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(dialogView);
		dialog.setCancelable(false);
		dialog.setCanceledOnTouchOutside(true);

		Window window = dialog.getWindow();
		window.setLayout(GridLayout.LayoutParams.MATCH_PARENT, GridLayout.LayoutParams.MATCH_PARENT);
		window.setGravity(Gravity.CENTER);

        TextView tv = dialogView.findViewById(R.id.rater_msg);
        tv.setText(R.string.rater_msg);
        tv.setTextAppearance(mContext,android.R.style.TextAppearance_Large);

        Button b1 = dialogView.findViewById(R.id.rate);
        b1.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					mContext.startActivity(new Intent(getOpenFacebookIntent(mContext)) /*new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + APP_PNAME))*/);
					dialog.dismiss();
				}
			});

        Button b2 = dialogView.findViewById(R.id.rateLater);
        b2.setOnClickListener(v -> {
			if (editor != null) {
				DAYS_UNTIL_PROMPT = 12;
				editor.putBoolean("dontshowagain", false);
				editor.putInt("day_until_prompt", DAYS_UNTIL_PROMPT);
				editor.commit();
			}
			dialog.dismiss();
		});
        
        /*Button b3 = dialogView.findViewById(R.id.rateClose);
        b3.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					if (editor != null) {
						DAYS_UNTIL_PROMPT = 12;
						editor.putBoolean("dontshowagain", false);
						editor.putInt("day_until_prompt", DAYS_UNTIL_PROMPT);
						editor.commit();
					}
					dialog.dismiss();
				}
			});*/
		
        dialog.show();        
    }
	
	public static Intent getOpenFacebookIntent(Context context) {

		try {
			context.getPackageManager().getPackageInfo("com.facebook.katana", 0);
			return new Intent(Intent.ACTION_VIEW, Uri.parse("fb://page/459642507927948"));
		} catch (Exception e) {
			return new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/459642507927948"));
		}
	}
}
