package com.eun.percentify;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

public class WidgetDatabaseManager {
    static final String DB_WIDGET = "Widget.db";   //DB이름
    static final String TABLE_WIDGETS = "Widgets"; //Table 이름
    static final int DB_VERSION = 1;			//DB 버전

    Context myContext = null;

    private static WidgetDatabaseManager myDBManager = null;
    private SQLiteDatabase mydatabase = null;

    //MovieDatabaseManager 싱글톤 패턴으로 구현
    public static WidgetDatabaseManager getInstance(Context context)
    {
        if(myDBManager == null)
        {
            myDBManager = new WidgetDatabaseManager(context);
        }

        return myDBManager;
    }

    private WidgetDatabaseManager(Context context)
    {
        myContext = context;

        //DB Open
        mydatabase = context.openOrCreateDatabase(DB_WIDGET, context.MODE_PRIVATE,null);

        //Table 생성
        mydatabase.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_WIDGETS +
                "(" + "_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "theme INT," +
                "category TEXT," +
                "grade TEXT);");
    }

    public long insert(ContentValues addRowValue)
    {
        return mydatabase.insert(TABLE_WIDGETS, null, addRowValue);
    }
}
