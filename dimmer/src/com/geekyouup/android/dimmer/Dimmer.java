package com.geekyouup.android.dimmer;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

public class Dimmer extends Activity {
	
	private static final String KEY_STORED_BRIGHTNESS = "original_brightness";
	private static final String KEY_STORED_AUTOBRIGHT = "autobright_set";
	private static final String PREFS_NAME = "dim_sets";
	private static final String SCREEN_BRIGHTNESS_MODE = "screen_brightness_mode";
	private static final int SCREEN_MODE_MANUAL = 0;
	private static final int SCREEN_MODE_AUTO = 1;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        toggleBrightness();
    }
    
    private static final int MINIMUM_BACKLIGHT = 10;
    private void toggleBrightness() {

        try {
            ContentResolver cr = getContentResolver();
            int brightness = Settings.System.getInt(cr,Settings.System.SCREEN_BRIGHTNESS);
            
            if(brightness > 10) //store current brightness
            {
            	boolean autoBrightOn = (Settings.System.getInt(cr,SCREEN_BRIGHTNESS_MODE,-1)==SCREEN_MODE_AUTO);
            	
            	SharedPreferences settings = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
				SharedPreferences.Editor editor = settings.edit();
				editor.putInt(KEY_STORED_BRIGHTNESS, brightness);
				editor.putBoolean(KEY_STORED_AUTOBRIGHT, autoBrightOn);
				editor.commit();

				String toastText = "Setting brightness from " + brightness + " to 10";
				if(autoBrightOn)
				{
					Settings.System.putInt(cr, SCREEN_BRIGHTNESS_MODE, SCREEN_MODE_MANUAL);
					toastText = "Disabling \'Automatic Brightness\' and setting Brightness to 10";
				}
				
				Toast.makeText(this,toastText, Toast.LENGTH_SHORT).show();
				brightness = MINIMUM_BACKLIGHT;
            }else if(brightness == 10) // restore previous brightness
            {
            	SharedPreferences settings = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
				brightness = settings.getInt(KEY_STORED_BRIGHTNESS, brightness);
				boolean autoBright = settings.getBoolean(KEY_STORED_AUTOBRIGHT, false);
				if(autoBright) Settings.System.putInt(cr, SCREEN_BRIGHTNESS_MODE, SCREEN_MODE_AUTO);
				
				String toastText = "Restoring brightness from 10 to " + brightness;
				if(autoBright) toastText = "Enabling 'Automatic Brightness'";
				
				Toast.makeText(this, toastText, Toast.LENGTH_SHORT).show();
            }
            
            Settings.System.putInt(cr, Settings.System.SCREEN_BRIGHTNESS, brightness);
            WindowManager.LayoutParams lp = getWindow().getAttributes();
            lp.screenBrightness = 10 / 100.0f;
            getWindow().setAttributes(lp);
        } catch (Exception e) {
            Log.d("Bright", "toggleBrightness: " + e);
        }
        
        final Activity activity = this;
        Thread t = new Thread(){
        	public void run()
        	{
        		try {
					sleep(500);
				} catch (InterruptedException e) {}
        		activity.finish();
        	}
        };
        t.start();
    }
}