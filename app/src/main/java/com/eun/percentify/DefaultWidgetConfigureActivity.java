package com.eun.percentify;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RemoteViews;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

public class DefaultWidgetConfigureActivity extends Activity implements RecyclerAdapter.OnListItemSelectedInterface {
    private static String TAG = "@@@@DefaultWidgetConfigureActivity";
    private int appWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
    private int percent;
    private String unit;
    private String mType;
    private Context mContext;
    private RemoteViews views;
    private AppWidgetManager appWidgetManager;
    private AdView mAdView;

    // RecyclerView
    private int mPosition;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;

    //    private WidgetDatabaseManager databaseManager;
    private SQLiteDatabase sqliteDB = null ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: ----------------------------------------------");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.default_widget_configure);
        setResult(RESULT_CANCELED);
        mContext = this;

        /************ 위젯 설정 ************/
        // get a widget ID
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            appWidgetId = extras.getInt(
                    AppWidgetManager.EXTRA_APPWIDGET_ID,
                    AppWidgetManager.INVALID_APPWIDGET_ID);
        }

        if (appWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) finish();

        appWidgetManager = AppWidgetManager.getInstance(this);

        /************ DB 설정 ************/
        String filename = "Widgets.db";
        try {
            File databseFile = getDatabasePath(filename);
            sqliteDB = SQLiteDatabase.openOrCreateDatabase(databseFile, null);
//            sqliteDB = SQLiteDatabase.openOrCreateDatabase("Widgets.db", null) ;
        } catch (SQLiteException e) {
            String databasePath = getFilesDir().getPath()+"/"+filename;
            File databaseFile = new File(databasePath);
            sqliteDB = SQLiteDatabase.openOrCreateDatabase(databaseFile, null);

            e.printStackTrace() ;
        }

        String sqlCreateTbl = "CREATE TABLE IF NOT EXISTS " + getString(R.string.TABLE_NAME) +
                " (" + getString(R.string._ID)+" INTEGER PRIMARY KEY," +
                getString(R.string.COLUMN_NAME_TITLE) +" TEXT, " +
                getString(R.string.COLUMN_NAME_THEME) +" INTEGER, " +
                getString(R.string.COLUMN_NAME_UNIT) +" TEXT, " +
                getString(R.string.COLUMN_NAME_START) +" INTEGER, " +
                getString(R.string.COLUMN_NAME_CURRENT) +" INTEGER, " +
                getString(R.string.COLUMN_NAME_FINISH) +" INTEGER);" ;

        sqliteDB.execSQL(sqlCreateTbl) ;

        String sqlSelect = "SELECT * FROM " + getString(R.string.TABLE_NAME) +
                " WHERE _ID=" + appWidgetId;

        Cursor cursor = sqliteDB.rawQuery(sqlSelect, null);
        if(cursor.moveToNext()){
            mType = cursor.getString(1);
            mPosition = cursor.getInt(2);
            switch (mType){
                case "today":
                    setButtonColor(R.id.rg_btn1);
                    break;
                case "week":
                    setButtonColor(R.id.rg_btn2);
                    break;
                case "month":
                    setButtonColor(R.id.rg_btn3);
                    break;
                case "year":
                    setButtonColor(R.id.rg_btn4);
                    break;
            }
            SetWidgetPreview.setWidgetTheme(this, mPosition);
        }


        /************ 광고 설정 ************/
        MobileAds.initialize(this, getString(R.string.admob_app_id));

        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        // 광고가 제대로 로드 되는지 테스트 하기 위한 코드입니다.
        mAdView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                // Code to be executed when an ad finishes loading.
                // 광고가 문제 없이 로드시 출력됩니다.
                Log.d(TAG, "onAdLoaded");
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
                // Code to be executed when an ad request fails.
                // 광고 로드에 문제가 있을시 출력됩니다.
                Log.d(TAG, "onAdFailedToLoad " + errorCode);
            }

            @Override
            public void onAdOpened() {
                // Code to be executed when an ad opens an overlay that
                // covers the screen.
            }

            @Override
            public void onAdClicked() {
                // Code to be executed when the user clicks on an ad.
            }

            @Override
            public void onAdLeftApplication() {
                // Code to be executed when the user has left the app.
            }

            @Override
            public void onAdClosed() {
                // Code to be executed when the user is about to return
                // to the app after tapping on an ad.
            }
        });



        /************ 액티비티 설정 ************/
        views = new RemoteViews(this.getPackageName(), R.layout.default_widget);

        RadioGroup radioGroup = findViewById(R.id.radioGroup);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId){
                    case R.id.rg_btn1:
                        setButtonColor(R.id.rg_btn1);
                        mType = "today";
                        percent = CalculatorManager.getToday();
                        break;
                    case R.id.rg_btn2:
                        setButtonColor(R.id.rg_btn2);
                        mType = "week";
                        percent = CalculatorManager.getWeek();
                        break;
                    case R.id.rg_btn3:
                        setButtonColor(R.id.rg_btn3);
                        mType = "month";
                        percent = CalculatorManager.getMonth();
                        break;
                    case R.id.rg_btn4:
                        setButtonColor(R.id.rg_btn4);
                        mType = "year";
                        percent = CalculatorManager.getYear();
                        break;
                }
            }
        });

        Button bt_submit = findViewById(R.id.submit);
        bt_submit.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                switch (mType){
                    case "today":
                        unit = "시";
                        SetProgressManager.setProgress(views, percent, "오늘", "0시 - 24시", CalculatorManager.getCurrentHour()+"시");
                        break;
                    case "week":
                        unit = "요일";
                        SetProgressManager.setProgress(views, percent, "이번 주", "월요일 - 일요일", CalculatorManager.getCurrentDaynum()+ "요일");
                        break;
                    case "month":
                        unit = "일";
                        SetProgressManager.setProgress(views, percent, "이번 달", "1일 - "+ CalculatorManager.getMonthEnd()+"일", CalculatorManager.getCurrentDay()+ "일");
                        break;
                    case "year":
                        unit = "월";
                        SetProgressManager.setProgress(views, percent, "올 해", "1월 - 12월", CalculatorManager.getCurrentMonth()+ "월");
                        break;
                    default:
                        Toast.makeText(mContext,"범위를 선택해 주세요",Toast.LENGTH_SHORT).show();
                        return;
                }

                String sqlUpdate = "INSERT OR REPLACE INTO "+ getString(R.string.TABLE_NAME) + " ("+
                        getString(R.string._ID)+", "+
                        getString(R.string.COLUMN_NAME_TITLE) +", "+
                        getString(R.string.COLUMN_NAME_THEME) +", "+
                        getString(R.string.COLUMN_NAME_UNIT) +") VALUES (" +
                        appWidgetId +", '"+
                        mType +"', "+
                        mPosition +", '"+
                        unit +"')";
                sqliteDB.execSQL(sqlUpdate) ;

                SetProgressManager.setTheme(mContext, views, mPosition);
                finishConfigure();
            }
        });

        setRecyclerView();
    }

    void setButtonColor(int buttonId){
        findViewById(R.id.rg_btn1).setBackgroundResource(R.drawable.preview_bt_round);
        findViewById(R.id.rg_btn2).setBackgroundResource(R.drawable.preview_bt_round);
        findViewById(R.id.rg_btn3).setBackgroundResource(R.drawable.preview_bt_round);
        findViewById(R.id.rg_btn4).setBackgroundResource(R.drawable.preview_bt_round);

        findViewById(buttonId).setBackgroundResource(R.drawable.submit_bt_pressed);
    }

    /******************** RecyclerView ************************/
    private void setRecyclerView() {
        ArrayList<Integer> listResId = new ArrayList<>(Arrays.asList(
                R.drawable._widgettheme_0,
                R.drawable._widgettheme_1,
                R.drawable._widgettheme_2,
                R.drawable._widgettheme_3,
                R.drawable._widgettheme_4,
                R.drawable._widgettheme_5,
                R.drawable._widgettheme_6,
                R.drawable._widgettheme_7,
                R.drawable._widgettheme_8
        ));

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        recyclerView.setHasFixedSize(true);

        // use a linear layout manager
        layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(layoutManager);

        // specify an adapter (see also next example)
        mAdapter = new RecyclerAdapter(listResId, this, this);
        recyclerView.setAdapter(mAdapter);

        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onItemSelected(View v, int position) {
        RecyclerAdapter.ItemViewHolder viewHolder = (RecyclerAdapter.ItemViewHolder)recyclerView.findViewHolderForAdapterPosition(position);
        Log.d(TAG, "onItemSelected: "+position);
        SetWidgetPreview.setWidgetTheme(this, position);

        mPosition=position;
    }

    public void finishConfigure(){
        // 구성 완료 시
        appWidgetManager.updateAppWidget(appWidgetId, views);

        // send data widget
        Intent resultValue = new Intent();
        resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        setResult(RESULT_OK, resultValue);
        finish();
    }

}

