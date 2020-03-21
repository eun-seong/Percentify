package com.eun.percentify;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.widget.RemoteViews;

import java.io.File;

/**
 * Implementation of App Widget functionality.
 * App Widget Configuration implemented in {@link CountWidgetConfigureActivity CountWidgetConfigureActivity}
 */
public class CountWidget extends AppWidgetProvider {
    private static final String TAG = "@@@@DefaultWidget";
    private static final String ACTION_BUTTON_UP = "com.example.dday.ACTION_BUTTON_UP";
    private static final String ACTION_BUTTON_DOWN = "com.example.dday.ACTION_BUTTON_DOWN";
    String filename = "Widgets.db";
    private int start, current, finish, mPosition;
    private static RemoteViews views;
    private String mTitle, mType, unit;
    private Cursor cursor;

    public void updateAppWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {
        Log.d(TAG, "----------------------updateAppWidget()---------------------- ");

        /********************** UI Update **********************/
        views = new RemoteViews(context.getPackageName(), R.layout.count_widget);

        SQLiteDatabase sqliteDB = null;
        try {
            File databseFile = context.getDatabasePath(filename);
            sqliteDB = SQLiteDatabase.openOrCreateDatabase(databseFile, null);

            String sqlSelect = "SELECT * FROM " + context.getString(R.string.TABLE_NAME) +
                    " WHERE "+context.getString(R.string._ID)+"=" + appWidgetId;

            cursor = sqliteDB.rawQuery(sqlSelect, null);
            cursor.moveToNext();

            mType = cursor.getString(1);
            mTitle = cursor.getString(2);
            mPosition = cursor.getInt(3);
            unit = cursor.getString(4);
            start = cursor.getInt(5);
            current = cursor.getInt(6);
            finish = cursor.getInt(7);

            if(mPosition != -1) SetProgressManager.setTheme(context, views, mPosition);
            SetProgressManager.setProgress(views,mType, mTitle, " "+unit, start, current, finish);

        } catch (Exception e){
            e.printStackTrace();
            String databasePath = context.getFilesDir().getPath()+"/"+filename;
            File databaseFile = new File(databasePath);
            sqliteDB = SQLiteDatabase.openOrCreateDatabase(databaseFile, null);
        }


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
        views = new RemoteViews(context.getPackageName(), R.layout.count_widget);
        Log.d(TAG, "buttonClicked: appWidgetId " + appWidgetId);

        /************* SQLite *************/
        File databseFile = context.getDatabasePath(filename);
        SQLiteDatabase sqliteDB = SQLiteDatabase.openOrCreateDatabase(databseFile, null);

        String sqlSelect = "SELECT * FROM "+ context.getString(R.string.TABLE_NAME) +
                " WHERE "+context.getString(R.string._ID)+"="+ appWidgetId;
        Cursor cursor = sqliteDB.rawQuery(sqlSelect, null) ;
        cursor.moveToNext();

        int position = cursor.getInt(3);
        unit = cursor.getString(4);
        start = cursor.getInt(5);
        current = cursor.getInt(6);
        finish = cursor.getInt(7);
        /************* SQLite *************/

        int changeCurrent = current + val;
        if((current==start && val== -1) || (current==finish && val == 1)) return;

        SetProgressManager.setCurrent(views, mType," "+unit, start, changeCurrent, finish);
        SetProgressManager.setTheme(context, views, position);

        String sqlUpdate = "UPDATE "+ context.getString(R.string.TABLE_NAME) +
                " SET " + context.getString(R.string.COLUMN_NAME_CURRENT)+"="+changeCurrent+
                " WHERE "+context.getString(R.string._ID)+"="+appWidgetId ;

        sqliteDB.execSQL(sqlUpdate) ;

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
        File databseFile = context.getDatabasePath(filename);
        SQLiteDatabase sqliteDB = SQLiteDatabase.openOrCreateDatabase(databseFile, null);

        for (int appWidgetId : appWidgetIds) {
            Log.d(TAG, "-----------------------------------onDeleted()----------------------------------- ");
            String sqlDelete = "DELETE FROM "+ context.getString(R.string.TABLE_NAME) +
                    " WHERE "+context.getString(R.string._ID)+"="+ appWidgetId;

            sqliteDB.execSQL(sqlDelete) ;
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

