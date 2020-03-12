package com.eun.percentify;

import android.app.Activity;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RemoteViews;
import android.widget.TextView;
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

/**
 * The configuration screen for the {@link CountWidget CountWidget} AppWidget.
 */
public class CountWidgetConfigureActivity extends Activity implements RecyclerAdapter.OnListItemSelectedInterface {
    private static String TAG = "@@@@CountWidgetConfigureActivity";
    private int appWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
    private static final String ACTION_BUTTON_UP = "com.example.dday.ACTION_BUTTON_UP";
    private static final String ACTION_BUTTON_DOWN = "com.example.dday.ACTION_BUTTON_DOWN";
    private Context mContext;
    private RemoteViews views;
    private AppWidgetManager appWidgetManager;
    private AdView mAdView;

    private EditText title, start_input, unit, current_input, finish_input;
    private int start = -1, finish= -1, current =-1;
    private int percent= -1;

    // RecyclerView
    private int mPosition;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;

    private SQLiteDatabase sqliteDB = null ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.count_widget_configure);
        setResult(RESULT_CANCELED);
        mContext = this;

        title = findViewById(R.id.title_input);
        start_input = findViewById(R.id.start_input);
        current_input = findViewById(R.id.current_input);
        finish_input = findViewById(R.id.finish_input);
        unit = findViewById(R.id.unit);

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
        views = new RemoteViews(this.getPackageName(), R.layout.count_widget);

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
        if(cursor.moveToNext()) {
            String type = cursor.getString(1);
            mPosition = cursor.getInt(2);
            String Unit = cursor.getString(3);
            start = cursor.getInt(4);
            current = cursor.getInt(5);
            finish = cursor.getInt(6);
            title.setText(type);
            SetWidgetPreview.setWidgetTheme(this, mPosition);
            unit.setText(Unit);
            start_input.setText(Integer.toString(start));
            current_input.setText(Integer.toString(current));
            finish_input.setText(Integer.toString(finish));
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
        /************ 버튼 ************/
        unit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                TextView unit1 = findViewById(R.id.unit1);
                TextView unit2 = findViewById(R.id.unit2);
                unit1.setText(unit.getText());
                unit2.setText(unit.getText());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        Button submit = findViewById(R.id.submit);
        submit.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                try {
                    String Title = title.getText().toString();
                    String Unit = unit.getText().toString();
                    start = Integer.parseInt(start_input.getText().toString());
                    current = Integer.parseInt(current_input.getText().toString());
                    finish = Integer.parseInt(finish_input.getText().toString());

                    if(Title.equals("") || Unit.equals("") ){
                        Toast.makeText(mContext,"입력 값을 다시 확인해 주세요", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if(start > current) {
                        Toast.makeText(mContext,"현재 값은 시작 값보다 크거나 같아야 합니다", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if(current > finish) {
                        Toast.makeText(mContext,"현재 값은 끝 값보다 작거나 같아야 합니다", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    float startInput = (float)start;
                    float currentInput = (float)current;
                    float finishInput = (float)finish;

                    percent = Math.round((currentInput-startInput)/(finishInput-startInput)*100);
                    String range = ""+start+" "+Unit+" - "+finish+" "+Unit;

                    String sqlUpdate = "INSERT OR REPLACE INTO "+ getString(R.string.TABLE_NAME) + "("+
                            getString(R.string._ID)+", "+
                            getString(R.string.COLUMN_NAME_TITLE) +", "+
                            getString(R.string.COLUMN_NAME_THEME) +", "+
                            getString(R.string.COLUMN_NAME_UNIT) +", "+
                            getString(R.string.COLUMN_NAME_START) +", "+
                            getString(R.string.COLUMN_NAME_CURRENT) +", "+
                            getString(R.string.COLUMN_NAME_FINISH) +") VALUES (" +
                            appWidgetId +", '"+
                            Title +"', "+
                            mPosition +", '"+
                            Unit +"', "+
                            start +", "+
                            current +", "+
                            finish +")";
                    sqliteDB.execSQL(sqlUpdate) ;

                    SetProgressManager.setProgress(views, percent, Title, range, current+ " "+Unit);
                    SetProgressManager.setTheme(mContext, views, mPosition);

                    finishConfigure();
                } catch (Exception e){
                    Toast.makeText(mContext,"입력 값을 다시 확인해 주세요", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }

            }
        });

        setRecyclerView();
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
        Intent intentUp     = new Intent(mContext, CountWidget.class);
        Intent intentDown   = new Intent(mContext, CountWidget.class);

        intentUp.setAction(ACTION_BUTTON_UP);
        intentDown.setAction(ACTION_BUTTON_DOWN);

        intentUp.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        intentDown.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);

        PendingIntent pendingIntentUp       = PendingIntent.getBroadcast(mContext,appWidgetId,intentUp,0);
        PendingIntent pendingIntentDown     = PendingIntent.getBroadcast(mContext,appWidgetId,intentDown,0);

        views.setOnClickPendingIntent(R.id.buttonUP       ,pendingIntentUp);
        views.setOnClickPendingIntent(R.id.buttonDOWN     ,pendingIntentDown);

        // send data widget
        Intent resultValue = new Intent();
        resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);

        setResult(RESULT_OK, resultValue);
        setResult(RESULT_OK, intentUp);
        setResult(RESULT_OK, intentDown);

        appWidgetManager.updateAppWidget(appWidgetId, views);
        finish();
    }

}

