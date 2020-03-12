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
    private Date currentTime;
    private ArrayList widgetList;
    private WidgetDatabaseManager databaseManager;
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

        if(!getWidgetData()){
            Intent intent = new Intent(getApplicationContext(),EmptyActivity.class);
            startActivity(intent);//액티비티 띄우기
        }
        setRecyclerView();
    }

    private void setRecyclerView() {
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        recyclerView.setHasFixedSize(true);

        // use a linear layout manager
        layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);

        // specify an adapter (see also next example)
        mAdapter = new RecyclerMainAdapter(widgetList, this, this);
        recyclerView.setAdapter(mAdapter);

        mAdapter.notifyDataSetChanged();
    }

    public boolean getWidgetData() {
        widgetList = new ArrayList<>();
        boolean isEmpty = false;

        try {
            String sqlSelect = "SELECT * FROM " + getString(R.string.TABLE_NAME);

            cursor = sqliteDB.rawQuery(sqlSelect, null);

            if(cursor != null)
            {
                while (cursor.moveToNext())
                {
                    isEmpty = true;
                    WidgetItem currentData = new WidgetItem();

                    currentData.setId(cursor.getInt(0));
                    currentData.setType(cursor.getString(1));
                    currentData.setTheme(cursor.getInt(2));
                    currentData.setUnit(cursor.getString(3));
                    currentData.setStart(cursor.getInt(4));
                    currentData.setCurrent(cursor.getInt(5));
                    currentData.setFinish(cursor.getInt(6));

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
//        RecyclerMainAdapter.WidgetViewHolder viewHolder = (RecyclerMainAdapter.WidgetViewHolder)recyclerView.findViewHolderForAdapterPosition(position);
        Log.d(TAG, "onItemSelected: "+appWidgetId);

        this.appWidgetId=appWidgetId;


        String sqlSelect = "SELECT * FROM " + getString(R.string.TABLE_NAME) +
                " WHERE _ID=" + appWidgetId;
        cursor = sqliteDB.rawQuery(sqlSelect, null);
        cursor.moveToNext();

        String type = cursor.getString(1);

        if(type.equals("today")||type.equals("week")||type.equals("month")||type.equals("year")) {
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
