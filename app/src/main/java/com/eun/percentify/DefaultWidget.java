package com.eun.percentify;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.widget.EditText;
import android.widget.RemoteViews;

import java.io.File;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

public class DefaultWidget extends AppWidgetProvider {
    private static String TAG = "@@@@DefaultWidget";
    String filename = "Widgets.db";
    private String mType, mTitle, unit;
    private long start, current, finish;
    private int mPosition;
    private RemoteViews views;
    private Cursor cursor;
    private Timer timer;

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        Log.d(TAG, "onUpdate: ");
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds){
        Log.d(TAG, "onDeleted: ");
        for (int appWidgetId : appWidgetIds) {
            Log.d(TAG, "-----------------------------------onDeleted()----------------------------------- ");
            Log.d(TAG, "onDeleted: "+appWidgetId);
            timer.cancel();
            File databseFile = context.getDatabasePath(filename);
            SQLiteDatabase sqliteDB = SQLiteDatabase.openOrCreateDatabase(databseFile, null);

            String sqlDelete = "DELETE FROM "+ context.getString(R.string.TABLE_NAME) +
                    " WHERE _ID="+ appWidgetId;

            sqliteDB.execSQL(sqlDelete) ;
        }
    }

    @Override
    public void onEnabled(Context context) {
        Log.d(TAG, "onEnabled: ");
    }

    @Override
    public void onDisabled(Context context) {
        Log.d(TAG, "onDisabled: ");
    }


    public void updateAppWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {
        Log.d(TAG, "-----------------------------------updateAppWidget()----------------------------------- ");
        views = new RemoteViews(context.getPackageName(), R.layout.default_widget);

        timer = new Timer();
        timer.scheduleAtFixedRate(new updateTimer(context, appWidgetManager, appWidgetId), 1000, 1000);

        setUpdate(context, appWidgetId);

        Log.d(TAG, "updateAppWidget: SetProgress");

        Intent intent=new Intent(context, MainActivity.class);
        intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);

        PendingIntent pe=PendingIntent.getActivity(context, 0, intent, 0);
        views.setOnClickPendingIntent(R.id.myWidget, pe);

        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    void setUpdate(Context context, int appWidgetId){
        SQLiteDatabase sqliteDB = null ;
        try {
            File databseFile = context.getDatabasePath(filename);
            sqliteDB = SQLiteDatabase.openOrCreateDatabase(databseFile, null);

            String sqlSelect = "SELECT * FROM " + context.getString(R.string.TABLE_NAME) +
                    " WHERE _ID=" + appWidgetId;
            cursor = sqliteDB.rawQuery(sqlSelect, null);
            cursor.moveToNext();

            mType = cursor.getString(1);
            mTitle = cursor.getString(2);
            mPosition = cursor.getInt(3);
            unit = cursor.getString(4);
            start = cursor.getLong(5);
            finish = cursor.getLong(7);

            switch (mType){
                case "today":
                    current=Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
                    break;
                case "week":
                    int curDay = Calendar.getInstance().get(Calendar.DAY_OF_WEEK) == 1 ? 6 : Calendar.getInstance().get(Calendar.DAY_OF_WEEK)-2;
                    current = curDay*24 + Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
                    break;
                case "month":
                    current = Calendar.getInstance().get(Calendar.DATE);
                    break;
                case "year":
                    current=Calendar.getInstance().get(Calendar.DAY_OF_YEAR);
                    break;
                case "dday":
                    current = Calendar.getInstance().getTimeInMillis();
                    break;
            }

            Log.d(TAG, "setUpdate: mPosition = "+mPosition);
            if(mPosition != -1) SetProgressManager.setTheme(context, views, mPosition);
            SetProgressManager.setProgress(views, mType, mTitle, unit, start, current, finish);

        } catch (Exception e) {
            e.printStackTrace();

            String databasePath = context.getFilesDir().getPath()+"/"+filename;
            File databaseFile = new File(databasePath);
            sqliteDB = SQLiteDatabase.openOrCreateDatabase(databaseFile, null);
        }
    }


    private class updateTimer extends TimerTask{
        RemoteViews views;
        AppWidgetManager appWidgetManager;
        ComponentName componentName;
        Context mContext;
        int appWidgetId;

        public updateTimer(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {
            this.appWidgetManager = appWidgetManager;
            views = new RemoteViews(context.getPackageName(), R.layout.default_widget);
            componentName = new ComponentName(context, updateTimer.class);
            mContext = context;
            this.appWidgetId = appWidgetId;
        }

        @Override
        public void run() {
            setUpdate(mContext, appWidgetId);
            appWidgetManager.updateAppWidget(componentName, views);

            Log.d(TAG, "run: timer "+appWidgetId);
        }

    }
}

