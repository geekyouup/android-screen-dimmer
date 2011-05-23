package com.geekyouup.android.dimmer;

import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;

/**
 * Define a simple widget that shows a battery meter. To build
 * an update we spawn a background {@link Service} to perform the API queries.
 */
public class DimmerWidget extends AppWidgetProvider {
	
	@Override
	public void onEnabled(Context context) {
		super.onEnabled(context);
	}

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager,int[] appWidgetIds) {
        
    	// Build an update that holds the updated widget contents
        RemoteViews updateViews = new RemoteViews(context.getPackageName(), R.layout.widget);
		try
		{
            Intent defineIntent2 = new Intent(context,Dimmer.class);
            PendingIntent pendingIntent2 = PendingIntent.getActivity(context,0 /* no requestCode */, defineIntent2, 0 /* no flags */);
            updateViews.setOnClickPendingIntent(R.id.widget, pendingIntent2);
		}catch(Exception e)
		{
			Log.e("AutoBright Widget","Error Settings Intents",e);
		}

    	// Build the widget update for today
        if(updateViews != null)
        {
            try
            {
	            // Push update for this widget to the home screen
	            ComponentName thisWidget = new ComponentName(context, DimmerWidget.class);
	            if(thisWidget != null)
	            {
		            AppWidgetManager manager = AppWidgetManager.getInstance(context);
		            if(manager != null && updateViews != null)
		            {
		            	manager.updateAppWidget(thisWidget, updateViews);
		            }
	            }
            }catch(Exception e)
            {
            	Log.e("Widget", "Update Service Failed to Start", e);
            }
        }
    
    }
}
