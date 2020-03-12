package com.eun.percentify;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;
import android.widget.RemoteViews;

import java.io.File;
import java.util.ArrayList;

public class DefaultWidget extends AppWidgetProvider {
    private static String TAG = "@@@@DefaultWidget";
    String filename = "Widgets.db";
    private RemoteViews views;
    private String type;
    private int mPosition;
    private String unit;
    private Cursor cursor;

    public void updateAppWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {
        Log.d(TAG, "----------------------updateAppWidget()---------------------- ");

        SQLiteDatabase sqliteDB = null ;
        try {
            File databseFile = context.getDatabasePath(filename);
            sqliteDB = SQLiteDatabase.openOrCreateDatabase(databseFile, null);

            String sqlSelect = "SELECT * FROM " + context.getString(R.string.TABLE_NAME) +
                    " WHERE _ID=" + appWidgetId;
            cursor = sqliteDB.rawQuery(sqlSelect, null);
            cursor.moveToNext();

            type = cursor.getString(1);
            mPosition = cursor.getInt(2);
            unit = cursor.getString(3);

            switch (type){
                case "today":
                    updateToday();
                    break;
                case "week":
                    updateWeek();
                    break;
                case "month":
                    updateMonth();
                    break;
                case "year":
                    updateYear();
                    break;
            }

            if(mPosition != -1) SetProgressManager.setTheme(context, views, mPosition);
            Log.d(TAG, "updateAppWidget: SetProgress");

        } catch (Exception e) {
            e.printStackTrace();

            String databasePath = context.getFilesDir().getPath()+"/"+filename;
            File databaseFile = new File(databasePath);
            sqliteDB = SQLiteDatabase.openOrCreateDatabase(databaseFile, null);
        }

        //        String type = PreferenceManager.getString(context, Integer.toString(appWidgetId));
        views = new RemoteViews(context.getPackageName(), R.layout.default_widget);

        Intent intent=new Intent(context, MainActivity.class);
        intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);

        PendingIntent pe=PendingIntent.getActivity(context, 0, intent, 0);
        views.setOnClickPendingIntent(R.id.myWidget, pe);

        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    public void updateToday(){
        int percent = CalculatorManager.getToday();
        SetProgressManager.setProgress(views, percent, "오늘", "0 - 24", CalculatorManager.getCurrentHour()+ "시");
    }

    public void updateWeek(){
        int percent = CalculatorManager.getWeek();
        SetProgressManager.setProgress(views, percent, "이번 주", "월요일 - 일요일", CalculatorManager.getCurrentDaynum()+ "요일");
    }

    public void updateMonth(){
        int percent = CalculatorManager.getMonth();
        SetProgressManager.setProgress(views, percent, "이번 달", "1일 - "+ CalculatorManager.getMonthEnd()+"일", CalculatorManager.getCurrentDay()+ "일");
    }
    public void updateYear(){
        int percent = CalculatorManager.getYear();
        SetProgressManager.setProgress(views, percent, "올 해", "1월 - 12월", CalculatorManager.getCurrentMonth()+ "월");
    }

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

}

