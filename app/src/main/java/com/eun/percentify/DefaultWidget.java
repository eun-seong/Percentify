package com.eun.percentify;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;

public class DefaultWidget extends AppWidgetProvider {
    private static String TAG = "DefaultWidget";
    private RemoteViews views;
    private Context mContext;

    public void updateAppWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {
        Log.d(TAG, "----------------------updateAppWidget()---------------------- ");

        String type = PreferenceManager.getString(context, Integer.toString(appWidgetId));
        views = new RemoteViews(context.getPackageName(), R.layout.default_widget);

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

        int mPosition = PreferenceManager.getInt(context,appWidgetId+"theme");
        if(mPosition != -1) SetProgressManager.setTheme(context, views, mPosition);

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
            PreferenceManager.removeKey(context, Integer.toString(appWidgetId));
            PreferenceManager.removeKey(context, appWidgetId+"theme");
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

