package com.eun.percentify;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;

public class MainActivity extends AppCompatActivity implements RecyclerMainAdapter.OnListItemSelectedInterface {
    private final static String TAG="@@@@MainActivity";
    private ArrayList<WidgetItem> widgetList;
    String filename = "Widgets.db";

    // RecyclerView
    private int mPosition;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;

    private Cursor cursor;
    private SQLiteDatabase sqliteDB;
    private int appWidgetId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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

        if(getWidgetData()){
            Intent intent = new Intent(getApplicationContext(),EmptyActivity.class);
            startActivity(intent);//액티비티 띄우기
        }
        setRecyclerView();
    }

    private void setRecyclerView() {
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        mAdapter = new RecyclerMainAdapter(widgetList, this);
        recyclerView.setAdapter(mAdapter);

        mAdapter.notifyDataSetChanged();
    }

    public boolean getWidgetData() {
        widgetList = new ArrayList<>();
        boolean isEmpty = true;

        try {
            String sqlSelect = "SELECT * FROM " + getString(R.string.TABLE_NAME);

            cursor = sqliteDB.rawQuery(sqlSelect, null);

            if(cursor != null)
            {
                isEmpty = false;
                while (cursor.moveToNext())
                {
                    WidgetItem currentData = new WidgetItem();

                    currentData.setId(cursor.getInt(0));
                    currentData.setType(cursor.getString(1));
                    currentData.setTitle(cursor.getString(2));
                    currentData.setTheme(cursor.getInt(3));
                    currentData.setUnit(cursor.getString(4));
                    currentData.setStart(cursor.getInt(5));
                    currentData.setCurrent(cursor.getInt(6));
                    currentData.setFinish(cursor.getInt(7));

                    Log.d(TAG, "getWidgetData: add "+currentData.getTitle());
                    widgetList.add(currentData);
                }
            }

        } catch (Exception e){
            e.printStackTrace();
        }
        return isEmpty;
    }

    @Override
    public void onItemSelected(View v, int appWidgetId) {
        Log.d(TAG, "onItemSelected: "+appWidgetId);

        this.appWidgetId=appWidgetId;


        String sqlSelect = "SELECT * FROM " + getString(R.string.TABLE_NAME) +
                " WHERE _ID=" + appWidgetId;
        cursor = sqliteDB.rawQuery(sqlSelect, null);
        cursor.moveToNext();

        String mType = cursor.getString(1);
        String mTitle = cursor.getString(2);

        if(mType.equals("today")||mType.equals("week")||mType.equals("month")||mType.equals("year")||mType.equals("dday")) {
            Intent intent = new Intent(getApplicationContext(), DefaultWidgetConfigureActivity.class);
            intent.putExtra("appWidgetId", appWidgetId);
            startActivity(intent);
        }else{
            Intent intent = new Intent(getApplicationContext(), CountWidgetConfigureActivity.class);
            intent.putExtra("appWidgetId", appWidgetId);
            startActivity(intent);
        }
    }
}
