package com.eun.percentify;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

public class WidgetDatabaseManager {
    static final String DB_WIDGET = "Widget.db";   //DB이름
    static final String TABLE_WIDGETS = "Widgets"; //Table 이름
    static final int DB_VERSION = 1;			//DB 버전

    Context myContext = null;

    private static WidgetDatabaseManager myDBManager = null;
    private SQLiteDatabase mydatabase = null;

    public static class WidgetEntry implements BaseColumns {
        public static final String TABLE_NAME = "Widgets";
        public static final String _ID = "_id";
        public static final String COLUMN_NAME_TYPE = "type";
        public static final String COLUMN_NAME_THEME = "theme";
        public static final String COLUMN_NAME_UNIT = "unit";
        public static final String COLUMN_NAME_START = "start";
        public static final String COLUMN_NAME_CURRENT = "current";
        public static final String COLUMN_NAME_FINISH = "finish";
    }

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
        mydatabase.execSQL("CREATE TABLE IF NOT EXISTS " + WidgetEntry.TABLE_NAME +
                "(" + WidgetEntry._ID+" INTEGER PRIMARY KEY," +
                WidgetEntry.COLUMN_NAME_TYPE +"TEXT," +
                WidgetEntry.COLUMN_NAME_THEME +"INTEGER," +
                WidgetEntry.COLUMN_NAME_UNIT +"TEXT," +
                WidgetEntry.COLUMN_NAME_START +"INTEGER," +
                WidgetEntry.COLUMN_NAME_CURRENT +"INTEGER," +
                WidgetEntry.COLUMN_NAME_FINISH +"INTEGER);");
    }

    public long insert(ContentValues addRowValue)
    {
        return mydatabase.insert(TABLE_WIDGETS, null, addRowValue);
    }

    public Cursor query(String[] colums,
                        String selection,
                        String[] selectionArgs,
                        String groupBy,
                        String having,
                        String orderby)
    {
        return mydatabase.query(TABLE_WIDGETS,
                colums,
                selection,
                selectionArgs,
                groupBy,
                having,
                orderby);
    }

    public int delete(String whereClause,
                      String[] whereArgs)
    {
        return mydatabase.delete(TABLE_WIDGETS,
                whereClause,
                whereArgs);
    }
}
