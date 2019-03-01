package com.example.michail.fragmentedc.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.michail.fragmentedc.database.TaskDbScheme.TaskTable;

public class TaskBaseHelper extends SQLiteOpenHelper {
    public static final int VERSION = 1;
    public static final String DATABASE_NAME = "taskBase.db";


    public TaskBaseHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + TaskTable.NAME + "(" + " _id integer primary key autoincrement, " +
                TaskTable.Cols.UUID + "," +
                TaskTable.Cols.TITLE + "," +
                TaskTable.Cols.DATE + "," +
                TaskTable.Cols.TIME + "," +
                TaskTable.Cols.SOLVED + ","+
                TaskTable.Cols.CONTACT +
                ")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
