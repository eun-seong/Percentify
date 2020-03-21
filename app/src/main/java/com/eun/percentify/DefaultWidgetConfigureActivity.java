package com.eun.percentify;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.ReceiverCallNotAllowedException;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
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
import java.util.Calendar;
import java.util.Date;

public class DefaultWidgetConfigureActivity extends Activity implements RecyclerAdapter.OnListItemSelectedInterface {
    private static String TAG = "@@@@DefaultWidgetConfigureActivity";
    private int appWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
    private String mType, mTitle, unit;
    private Context mContext;
    private long sDate = -1,fDate = -1;
    private long start, current, finish;
    private RemoteViews views;
    private AppWidgetManager appWidgetManager;
    private AdView mAdView;
    private Calendar cal;
    private LinearLayout date_layout;
    private  Button bt_dateStart, bt_dateFinish;

    // RecyclerView
    private int mPosition;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;

    //    private WidgetDatabaseManager databaseManager;
    private SQLiteDatabase sqliteDB = null ;

    private RadioGroup radioGroup0, radioGroup1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: ----------------------------------------------");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.default_widget_configure);
        setResult(RESULT_CANCELED);
        mContext = this;
        cal = Calendar.getInstance();
        date_layout = findViewById(R.id.date_layout);

        setAdView();
        setSelectButton();
        setRadioButton();

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
        views = new RemoteViews(this.getPackageName(), R.layout.default_widget);

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
                getString(R.string.COLUMN_NAME_TYPE) +" TEXT, " +
                getString(R.string.COLUMN_NAME_TITLE) +" TEXT, " +
                getString(R.string.COLUMN_NAME_THEME) +" INTEGER, " +
                getString(R.string.COLUMN_NAME_UNIT) +" TEXT, " +
                getString(R.string.COLUMN_NAME_START) +" INTEGER, " +
                getString(R.string.COLUMN_NAME_CURRENT) +" INTEGER, " +
                getString(R.string.COLUMN_NAME_FINISH) +" INTEGER);" ;

        sqliteDB.execSQL(sqlCreateTbl) ;

        String sqlSelect = "SELECT * FROM " + getString(R.string.TABLE_NAME) +
                " WHERE "+getString(R.string._ID)+"=" + appWidgetId;

        Cursor cursor = sqliteDB.rawQuery(sqlSelect, null);
        if(cursor.moveToNext()){
            mType = cursor.getString(1);
            mTitle = cursor.getString(2);
            mPosition = cursor.getInt(3);
            unit = cursor.getString(4);
            start = cursor.getInt(5);
            current = cursor.getInt(6);
            finish = cursor.getInt(7);

            switch (mType){
                case "today":
                    radioGroup0.check(R.id.rg_btn1);
                    break;
                case "week":
                    radioGroup0.check(R.id.rg_btn2);
                    break;
                case "month":
                    radioGroup0.check(R.id.rg_btn3);
                    break;
                case "year":
                    radioGroup0.check(R.id.rg_btn4);
                    break;
                case "dday":
                    radioGroup1.check(R.id.rg_btn5);
                    EditText editText = findViewById(R.id.title_input);
                    editText.setText(mType);

                    Calendar c = Calendar.getInstance();
                    c.setTimeInMillis(start);
                    String str = String.format("%d. %d. %d", c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DATE));
                    bt_dateStart.setText(str);

                    c.setTimeInMillis(finish);
                    bt_dateFinish.setText(str);

                    sDate = start;
                    fDate = finish;
                    break;
            }
            SetWidgetPreview.setWidgetTheme(this, mPosition);
        }

        setRecyclerView();
    }

    void setRadioButton(){
        radioGroup0 = findViewById(R.id.radioGroup0);
        radioGroup1 = findViewById(R.id.radioGroup1);

        radioGroup0.setOnCheckedChangeListener(listener0);
        radioGroup1.setOnCheckedChangeListener(listener1);
    }

    private RadioGroup.OnCheckedChangeListener listener0 = new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            radioGroup1.setOnCheckedChangeListener(null);
            radioGroup1.clearCheck();
            radioGroup1.setOnCheckedChangeListener(listener1);

            date_layout.setVisibility(View.GONE);
            switch (checkedId){
                case R.id.rg_btn1:
                    Log.d(TAG, "onCheckedChanged: rb_btn1");
                    radioGroup0.check(R.id.rg_btn1);
                    mType = "today";
                    mTitle = "오늘";
                    break;
                case R.id.rg_btn2:
                    radioGroup0.check(R.id.rg_btn2);
                    mType = "week";
                    mTitle = "이번 주";
                    break;
                case R.id.rg_btn3:
                    radioGroup0.check(R.id.rg_btn3);
                    mType = "month";
                    mTitle = "이번 달";
                    break;
                case R.id.rg_btn4:
                    radioGroup0.check(R.id.rg_btn4);
                    mType = "year";
                    mTitle = "올 해";
                    break;
            }
        }
    };

    private RadioGroup.OnCheckedChangeListener listener1 = new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            radioGroup0.setOnCheckedChangeListener(null);
            radioGroup0.clearCheck();
            radioGroup0.setOnCheckedChangeListener(listener0);

            date_layout.setVisibility(View.VISIBLE);
            radioGroup1.check(R.id.rg_btn5);
            mType = "dday";
        }
    };

    void setSelectButton(){
        bt_dateStart = findViewById(R.id.date_start);
        bt_dateStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog dialog = new DatePickerDialog(mContext, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int date) {
                        Calendar c = Calendar.getInstance();
                        c.set(year, month, date);
                        sDate = c.getTimeInMillis();

                        String str = year+". "+(month+1)+". "+date+".";
                        bt_dateStart.setText(str);
                    }
                }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DATE));

                if(fDate != -1) dialog.getDatePicker().setMaxDate(fDate);    //입력한 날짜 이후로 클릭 안되게 옵션
                if(sDate != -1 ) {
                    Calendar c = Calendar.getInstance();
                    c.setTimeInMillis(sDate);
                    dialog.updateDate(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DATE));
                }
                dialog.show();
            }
        });

        bt_dateFinish = findViewById(R.id.date_finish);
        bt_dateFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog dialog = new DatePickerDialog(mContext, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int date) {
                        Calendar c = Calendar.getInstance();
                        c.set(year, month, date);
                        fDate = c.getTimeInMillis();

                        String str = year+". "+(month+1)+". "+date+".";
                        bt_dateFinish.setText(str);
                    }
                }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DATE));

                if(sDate != -1) dialog.getDatePicker().setMinDate(sDate);    //입력한 날짜 이후로 클릭 안되게 옵션
                if(fDate != -1 ) {
                    Calendar c = Calendar.getInstance();
                    c.setTimeInMillis(fDate);
                    dialog.updateDate(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DATE));
                }
                dialog.show();
            }
        });

        Button bt_submit = findViewById(R.id.submit);
        bt_submit.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                switch (mType){
                    case "today":
                        unit = "시";
                        mTitle = "오늘";
                        start=0;
                        current=Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
                        finish=24;
                        break;
                    case "week":
                        unit = "요일";
                        mTitle = "이번 주";
                        int curDay = Calendar.getInstance().get(Calendar.DAY_OF_WEEK) == 1 ? 6 : Calendar.getInstance().get(Calendar.DAY_OF_WEEK)-2;
                        start=0;
                        current = curDay*24 + Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
                        finish=168;
                        break;
                    case "month":
                        unit = "일";
                        mTitle = "이번 달";
                        start=1;
                        current = Calendar.getInstance().get(Calendar.DATE);
                        finish = CalculatorManager.getMonthEnd();
                        break;
                    case "year":
                        unit = "월";
                        mTitle = "올 해";
                        start=1;
                        current=Calendar.getInstance().get(Calendar.DAY_OF_YEAR);
                        finish=CalculatorManager.getYearEnd();
                        break;
                    case "dday":
                        EditText editText = findViewById(R.id.title_input);
                        mTitle = editText.getText().toString();
                        unit = "일";
                        start = sDate;
                        current = Calendar.getInstance().getTimeInMillis();
                        finish = fDate;
                        break;
                    default:
                        Toast.makeText(mContext,"범위를 선택해 주세요",Toast.LENGTH_SHORT).show();
                        return;
                }

                String sqlUpdate = "INSERT OR REPLACE INTO "+ getString(R.string.TABLE_NAME) + "("+
                        getString(R.string._ID)+", "+
                        getString(R.string.COLUMN_NAME_TITLE) +", "+
                        getString(R.string.COLUMN_NAME_TYPE) +", "+
                        getString(R.string.COLUMN_NAME_THEME) +", "+
                        getString(R.string.COLUMN_NAME_UNIT) +", "+
                        getString(R.string.COLUMN_NAME_START) +", "+
                        getString(R.string.COLUMN_NAME_CURRENT) +", "+
                        getString(R.string.COLUMN_NAME_FINISH) +") VALUES (" +
                        appWidgetId +", '"+
                        mTitle +"', '"+
                        mType +"', "+
                        mPosition +", '"+
                        unit +"', "+
                        start +", "+
                        current +", "+
                        finish +")";

                sqliteDB.execSQL(sqlUpdate) ;

                Log.d(TAG, String.format("onClick: start: %d\tcurrent: %d\tfinish: %d", start, current, finish));
                SetProgressManager.setProgress(views, mType, mTitle, unit, start, current, finish);
                SetProgressManager.setTheme(mContext, views, mPosition);

                finishConfigure();
            }
        });
    }

    void setAdView(){
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

