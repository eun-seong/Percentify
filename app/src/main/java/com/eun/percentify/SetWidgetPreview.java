package com.eun.percentify;

import android.app.Activity;
import android.graphics.drawable.GradientDrawable;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;


public class SetWidgetPreview {
    private final static String TAG = "@@@@SetWidgetPreview";

    public static void setWidgetTheme(Activity activity, int position){
        Log.d(TAG, "setWidgetTheme: position = "+position);
        LinearLayout linearLayout = activity.findViewById(R.id.progress_layout);
        TextView[] tv = {
                activity.findViewById(R.id.range),
                activity.findViewById(R.id.title),
                activity.findViewById(R.id.current),
                activity.findViewById(R.id.percent)};

        for(int i=0;i<4;i++) tv[i].setTextColor(ContextCompat.getColor(activity,ThemeData.textColorId.get(position)));

        GradientDrawable linearLayoutBackground = (GradientDrawable) linearLayout.getBackground();
        linearLayoutBackground.setColor(ContextCompat.getColor(activity,ThemeData.backgroundColorId.get(position)));
    }
}
