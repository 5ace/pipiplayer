package cn.pipi.mobile.pipiplayer.local.vlc;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import cn.pipi.mobile.pipiplayer.local.vlc.BitmapCache;
import cn.pipi.mobile.pipiplayer.local.vlc.MediaDatabase;
import cn.pipi.mobile.pipiplayer.util.CatchHandler;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.preference.PreferenceManager;
import android.util.Log;
import android.app.Activity;
import android.app.Application;

public class VLCApplication extends Application {
    public final static String TAG = "PipiPlayer/VLCApplication";
    private static VLCApplication instance;

    public final static String SLEEP_INTENT = "cn.pipi.mobile.pipiplayer.local.vlc.SleepIntent";

    @Override
    public void onCreate() {
        super.onCreate();
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        String p = pref.getString("set_locale", "");
        if (p != null && !p.equals("")) {
            Locale locale;
            // workaround due to region code
            if(p.equals("zh-TW")) {
                locale = Locale.TRADITIONAL_CHINESE;
            } else if(p.startsWith("zh")) {
                locale = Locale.CHINA;
            } else if(p.equals("pt-BR")) {
                locale = new Locale("pt", "BR");
            } else if(p.equals("bn-IN") || p.startsWith("bn")) {
                locale = new Locale("bn", "IN");
            } else {
                /**
                 * Avoid a crash of
                 * java.lang.AssertionError: couldn't initialize LocaleData for locale
                 * if the user enters nonsensical region codes.
                 */
                if(p.contains("-"))
                    p = p.substring(0, p.indexOf('-'));
                locale = new Locale(p);
            }
            Locale.setDefault(locale);
            Configuration config = new Configuration();
            config.locale = locale;
            getBaseContext().getResources().updateConfiguration(config,
                    getBaseContext().getResources().getDisplayMetrics());
            
            CatchHandler.getInstance().init(getApplicationContext());//异常处理
        }

        instance = this;

        // Initialize the database soon enough to avoid any race condition and crash
        MediaDatabase.getInstance();
    }

    /**
     * Called when the overall system is running low on memory
     */
    @Override
    public void onLowMemory() {
        super.onLowMemory();
        Log.w(TAG, "System is running low on memory");

        BitmapCache.getInstance().clear();
    }

    /**
     * @return the main context of the Application
     */
    public static Context getAppContext()
    {
    	if(instance==null){
    		Log.i("VLCApplication", "instance ==null ");
    	}
        return instance;
    }

    /**
     * @return the main resources from the Application
     */
    public static Resources getAppResources()
    {
        if(instance == null) return null;
    	
        return instance.getResources();
    }
    
    public boolean isServiceShowing=false;

	public boolean isServiceShowing() {
		return isServiceShowing;
	}

	public void setServiceShowing(boolean isServiceShowing) {
		this.isServiceShowing = isServiceShowing;
	}
   
}
