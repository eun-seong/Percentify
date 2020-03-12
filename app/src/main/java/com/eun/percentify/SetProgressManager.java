package com.eun.percentify;

import android.content.Context;
import android.util.Log;
import android.widget.RemoteViews;

import androidx.core.content.ContextCompat;

public class SetProgressManager {
    private final static String TAG = "@@@@SetProgressManager";

    public static void setProgress(RemoteViews views, int percent, String title, String range, String current){
        views.setProgressBar(R.id.progress,100,percent, false);
        views.setTextViewText(R.id.percent, percent+"%");
        views.setTextViewText(R.id.title, title);
        views.setTextViewText(R.id.range,range);
        views.setTextViewText(R.id.current, current);
    }

    public static void setCurrent(RemoteViews views, int percent, String current){
        views.setProgressBar(R.id.progress,100,percent, false);
        views.setTextViewText(R.id.percent, percent+"%");
        views.setTextViewText(R.id.current, current);
    }

    public static void setTheme(Context mContext, RemoteViews views, int position){
        Log.d(TAG, "setTheme: position = "+position);
        views.setInt(R.id.myWidget, "setBackgroundResource", ThemeData.themeDrawableId.get(position));
        views.setTextColor(R.id.range, ContextCompat.getColor(mContext, ThemeData.textColorId.get(position)));
        views.setTextColor(R.id.title, ContextCompat.getColor(mContext, ThemeData.textColorId.get(position)));
        views.setTextColor(R.id.current, ContextCompat.getColor(mContext, ThemeData.textColorId.get(position)));
        views.setTextColor(R.id.percent, ContextCompat.getColor(mContext, ThemeData.textColorId.get(position)));
//        views.setInt(R.id.progress,"setBackgroundResource", R.drawable.progressbar_theme);
    }
}
