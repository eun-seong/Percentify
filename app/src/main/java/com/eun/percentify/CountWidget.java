package com.eun.percentify;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;

/**
 * Implementation of App Widget functionality.
 * App Widget Configuration implemented in {@link CountWidgetConfigureActivity CountWidgetConfigureActivity}
 */
public class CountWidget extends AppWidgetProvider {
    private static final String TAG = "DefaultWidget";
    private static final String ACTION_BUTTON_UP = "com.example.dday.ACTION_BUTTON_UP";
    private static final String ACTION_BUTTON_DOWN = "com.example.dday.ACTION_BUTTON_DOWN";
    private static RemoteViews views;

    public void updateAppWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {
        Log.d(TAG, "----------------------updateAppWidget()---------------------- ");

        /********************** UI Update **********************/
        views = new RemoteViews(context.getPackageName(), R.layout.count_widget);

        String Title    = PreferenceManager.getString(context, appWidgetId+"title");
        String unit     = PreferenceManager.getString(context, appWidgetId+"unit");
        int start       = PreferenceManager.getInt(context, appWidgetId+"start");
        int current     = PreferenceManager.getInt(context, appWidgetId+"current");
        int finish      = PreferenceManager.getInt(context, appWidgetId+"finish");
        int mPosition = PreferenceManager.getInt(context,appWidgetId+"theme");

        int percent = Math.round(((float)(current-start))/(float)(finish-start)*100);
        String range = start+" "+unit+" - "+finish+" "+unit;

        if(mPosition != -1) SetProgressManager.setTheme(context, views, mPosition);
        SetProgressManager.setProgress(views, percent, Title, range, current+ " "+unit);

        /********************** Intent **********************/
        Intent intentUp     = new Intent(context, CountWidget.class);
        Intent intentDown   = new Intent(context, CountWidget.class);
        Intent intent       = new Intent(context, MainActivity.class);

        intentUp.setAction(ACTION_BUTTON_UP);
        intentDown.setAction(ACTION_BUTTON_DOWN);
        intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);

        intentUp.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        intentDown.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);

        PendingIntent pendingIntentUp       = PendingIntent.getBroadcast(context,appWidgetId,intentUp,0);
        PendingIntent pendingIntentDown     = PendingIntent.getBroadcast(context,appWidgetId,intentDown,0);
        PendingIntent pe                    = PendingIntent.getActivity(context, 0, intent, 0);

        views.setOnClickPendingIntent(R.id.buttonUP       ,pendingIntentUp);
        views.setOnClickPendingIntent(R.id.buttonDOWN     ,pendingIntentDown);
        views.setOnClickPendingIntent(R.id.myWidget ,pe);

        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    public void buttonClicked(Context context, AppWidgetManager appWidgetManager, Intent intent, int val) {
        int appWidgetId =intent.getExtras().getInt(AppWidgetManager.EXTRA_APPWIDGET_ID);

        String unit = PreferenceManager.getString(context, appWidgetId + "unit");
        int current   = PreferenceManager.getInt(context, appWidgetId + "current");
        int start     = PreferenceManager.getInt(context, appWidgetId + "start");
        int finish    = PreferenceManager.getInt(context, appWidgetId + "finish");
        int changeCurrent = current + val;
        if((current==start && val== -1) || (current==finish && val == 1)) return;

        views = new RemoteViews(context.getPackageName(), R.layout.count_widget);

        int percent = Math.round((float)(changeCurrent - start) / (float)(finish - start) * 100);
        SetProgressManager.setCurrent(views, percent, changeCurrent+ " "+unit);

        PreferenceManager.setInt(context, appWidgetId + "current", changeCurrent);

        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context,intent);

        final String action = intent.getAction();
        Log.d(TAG, "onReceive: action = "+action);

        if(ACTION_BUTTON_UP.equals(action)){
            Log.d(TAG, "onReceive: "+"UP");

            AppWidgetManager manager = AppWidgetManager.getInstance(context);
            buttonClicked(context, manager, intent, 1);
        }
        else if(ACTION_BUTTON_DOWN.equals(action)){
            Log.d(TAG, "onReceive: "+"DOWN");

            AppWidgetManager manager = AppWidgetManager.getInstance(context);
            buttonClicked(context, manager, intent, -1);
        }
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        super.onUpdate(context,appWidgetManager,appWidgetIds);

        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds){
        super.onDeleted(context,appWidgetIds);
        for (int appWidgetId : appWidgetIds) {
            PreferenceManager.removeKey(context, appWidgetId+"title");
            PreferenceManager.removeKey(context, appWidgetId+"unit");
            PreferenceManager.removeKey(context, appWidgetId+"start");
            PreferenceManager.removeKey(context, appWidgetId+"finish");
            PreferenceManager.removeKey(context, appWidgetId+"unit");
            PreferenceManager.removeKey(context, appWidgetId+"theme");
        }
    }

    @Override
    public void onEnabled(Context context) {
        super.onEnabled(context);
    }

    @Override
    public void onDisabled(Context context) {
        super.onDisabled(context);
    }
}

