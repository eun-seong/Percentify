package com.eun.percentify;

import android.content.Context;
import android.util.Log;
import android.widget.RemoteViews;

import androidx.core.content.ContextCompat;

import java.util.Calendar;

public class SetProgressManager {
    private final static String TAG = "@@@@SetProgressManager";

    public static void setProgress(RemoteViews views, String type, String title, String unit, long start, long current, long finish){
        int percent = (int)Math.round((double)(current-start)/(double)(finish-start)*100);
        Log.d(TAG, "setProgress: "+String.format("title: %s\tstart: %d\tcurrent: %d\tfinish: %d\tpercent: %d", title, start, current, finish, percent));
        String range = start+unit+"-"+finish+unit;
        String cur = current+unit;

        if(type.equals("week")) {
            range = "월요일 - 일요일";
            cur = CalculatorManager.getCurrentDayName(Calendar.getInstance().get(Calendar.DAY_OF_WEEK))+unit;
        } else if(type.equals("year")){
            range = "1월 - 12월";
            cur = (Calendar.getInstance().get(Calendar.MONTH)+1)+unit;
        } else if(type.equals("dday")){
            Calendar cs = Calendar.getInstance();
            cs.setTimeInMillis(start);
            Calendar cc = Calendar.getInstance();
            cc.setTimeInMillis(current);
            Calendar cf = Calendar.getInstance();
            cf.setTimeInMillis(finish);

            if(cs.get(Calendar.YEAR) == cf.get(Calendar.YEAR)){
                if(cs.get(Calendar.MONTH) == cf.get(Calendar.MONTH)) {
                    range = String.format("%d일 - %d일", cs.get(Calendar.DATE), cf.get(Calendar.DATE));
                    cur = cc.get(Calendar.DATE)+unit;
                }
                else {
                    range = String.format("%d월 %d일 - %d월 %d일", cs.get(Calendar.MONTH), cs.get(Calendar.DATE), cf.get(Calendar.MONTH), cf.get(Calendar.DATE));
                    cur = String.format("%d월 %d일", cc.get(Calendar.MONTH), cc.get(Calendar.DATE));
                }
            }
        }

        views.setProgressBar(R.id.progress,100, percent, false);
        views.setTextViewText(R.id.percent, percent+"%");
        views.setTextViewText(R.id.title, title);
        views.setTextViewText(R.id.range,range);
        views.setTextViewText(R.id.current, cur);
    }

    public static void setCurrent(RemoteViews views,String type, String unit, int start, int current, int finish){
        int percent = (int)Math.round((double)(current)/(double)(finish-start)*100);
        Log.d(TAG, "setCurrent: "+String.format("title: %s\tstart: %d\tcurrent: %d\tfinish: %d\tpercent: %d", type, start, current, finish, percent));
        String cur = current+unit;

        if(type.equals("week")) {
            cur = CalculatorManager.getCurrentDayName(Calendar.getInstance().get(Calendar.DAY_OF_WEEK))+unit;
        } else if(type.equals("year")){
            cur = (Calendar.getInstance().get(Calendar.MONTH)+1)+unit;
        } else if(type.equals("dday")){
            Calendar cs = Calendar.getInstance();
            cs.setTimeInMillis(start);
            Calendar cc = Calendar.getInstance();
            cc.setTimeInMillis(current);
            Calendar cf = Calendar.getInstance();
            cf.setTimeInMillis(finish);

            if(cs.get(Calendar.YEAR) == cf.get(Calendar.YEAR)){
                if(cs.get(Calendar.MONTH) == cf.get(Calendar.MONTH)) {
                    cur = cc.get(Calendar.DATE)+unit;
                }
                else {
                    cur = String.format("%d월 %d일", cc.get(Calendar.MONTH), cc.get(Calendar.DATE));
                }
            }
        }

        views.setProgressBar(R.id.progress,100,percent, false);
        views.setTextViewText(R.id.percent, percent+"%");
        views.setTextViewText(R.id.current, cur);
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
