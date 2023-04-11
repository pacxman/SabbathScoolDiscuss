package com.ssdiscusskiny.preferences;

import android.os.Bundle;
import android.os.Build;
import android.content.SharedPreferences;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.preference.PreferenceManager;
import androidx.annotation.LayoutRes;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.annotation.Nullable;

import android.preference.PreferenceScreen;
import android.view.WindowManager;

import com.ssdiscusskiny.R;
import com.ssdiscusskiny.app.Variables;
import com.ssdiscusskiny.utils.PanelHandler;

public class Settings extends PreferenceActivity implements SharedPreferences.OnSharedPreferenceChangeListener
{
    private AppCompatDelegate mDelegate;

    private Preference fontPrefs;

    private PanelHandler panelHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        // TODO: Implement this method
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
		}
        getDelegate().installViewFactory();
        getDelegate().onCreate(savedInstanceState);

        setTheme(R.style.AppThemeSecond);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prefs);
        setSupportActionBar((Toolbar) findViewById(R.id.pref_toolbar));
        ((Toolbar) findViewById(R.id.pref_toolbar)).setTitle(getString(R.string.setts));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            getWindow().setNavigationBarColor(getResources().getColor(R.color.colorPrimary));
            getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimary));
        }


        
        addPreferencesFromResource(R.xml.prefs);
        PreferenceManager.getDefaultSharedPreferences(this).registerOnSharedPreferenceChangeListener(this);
        fontPrefs = findPreference("font_family");

        panelHandler = new PanelHandler(this);

    }
	
	@Override
	public void onSharedPreferenceChanged(SharedPreferences shp, String key)
	{
		// TODO: Implement this method
		if (key.equals("ntfHr")){
			Variables.ntfTimeChanged = true;
		}
        if (key.equals("night")){
            if (PreferenceManager.getDefaultSharedPreferences(this).getBoolean("night", false)){
                panelHandler.showNotificationDialog(getString(R.string.appearance_change), getString(R.string.night_mode_hint), 1);
            }
        }
        if (key.equals("show_mission")){
            Variables.mOptionChanged = true;
            if (!PreferenceManager.getDefaultSharedPreferences(this).getBoolean("show_mission", false)){
                panelHandler.showNotificationDialog(getString(R.string.mission_dis_title), getString(R.string.mission_dis_msg), 1);
            }
        }
		if (key.equals("defFont")){
            if (PreferenceManager.getDefaultSharedPreferences(this).getBoolean("defFont", false)){
                //ListPreference fontPrefs = (ListPreference)findPreference("font_family");
                PreferenceCategory categoryFont = (PreferenceCategory)findPreference("fonts");
                categoryFont.removePreference(fontPrefs);

            }else{

                PreferenceCategory categoryFont = (PreferenceCategory)findPreference("fonts");
                categoryFont.addPreference(fontPrefs);
            }
        }
	}
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        getDelegate().onPostCreate(savedInstanceState);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (PreferenceManager.getDefaultSharedPreferences(this).getBoolean("defFont", false)){
            PreferenceCategory categoryFont = (PreferenceCategory)findPreference("fonts");
            categoryFont.removePreference(fontPrefs);

        }else{
            PreferenceCategory categoryFont = (PreferenceCategory)findPreference("fonts");
            categoryFont.addPreference(fontPrefs);
        }
    }

    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        getDelegate().setContentView(layoutResID);
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        getDelegate().onPostResume();
    }

    @Override
    protected void onStop() {
        super.onStop();
        getDelegate().onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        getDelegate().onDestroy();
    }

    private void setSupportActionBar(@Nullable Toolbar toolbar) {
        getDelegate().setSupportActionBar(toolbar);
    }

    private AppCompatDelegate getDelegate() {
        if (mDelegate == null) {
            mDelegate = AppCompatDelegate.create(this, null);
        }
        return mDelegate;
    }

}
