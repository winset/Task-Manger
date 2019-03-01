package com.example.michail.fragmentedc;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.michail.fragmentedc.database.TaskBaseHelper;
import com.example.michail.fragmentedc.database.TaskCursorWrapper;
import com.example.michail.fragmentedc.database.TaskDbScheme.TaskTable;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class TimeLab {
    private static TimeLab sTimeLab;
    private Context mContext;
    private SQLiteDatabase mDatabase;
    // private List<Time> mTimes;

    public static TimeLab get(Context context) {
        if (sTimeLab == null) {
            sTimeLab = new TimeLab(context);
        }
        return sTimeLab;
    }

    private TimeLab(Context context) {
        mContext = context.getApplicationContext();
        mDatabase = new TaskBaseHelper(mContext).getWritableDatabase();
        //  mTimes = new ArrayList<>();
    }

    public void addTask(Time c) {
        //   mTimes.add(c);
        ContentValues values = getContentValues(c);
        mDatabase.insert(TaskTable.NAME, null, values);
    }

    public void deleteTask(String id) {
        mDatabase.delete(TaskTable.NAME, TaskTable.Cols.UUID + "=?", new String[]{id});
    }

    public List<Time> getTimes() {
        //  return mTimes;
        List<Time> times = new ArrayList<>();
        TaskCursorWrapper cursor = queryTime(null, null);
        try {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                times.add(cursor.getTime());
                cursor.moveToNext();
            }
        } finally {
            cursor.close();
        }
        return times;
    }

    public Time getTime(UUID id) {
        TaskCursorWrapper cursor = queryTime(TaskTable.Cols.UUID + "=?",
                new String[]{id.toString()});
        try {
            if (cursor.getCount() == 0) {
                return null;
            }
            cursor.moveToFirst();
            return cursor.getTime();
        } finally {
            cursor.close();
        }
    }

    public File getPhotoFile(Time time) {
        File filesDir = mContext.getFilesDir();
        return new File(filesDir, time.getPhotoFileName());
    }

    private static ContentValues getContentValues(Time time) {
        ContentValues values = new ContentValues();
        values.put(TaskTable.Cols.UUID, time.getId().toString());
        values.put(TaskTable.Cols.TITLE, time.getTitle());
        values.put(TaskTable.Cols.DATE, time.getDate().getTime());
        values.put(TaskTable.Cols.TIME, time.getClockTime().getTime());
        values.put(TaskTable.Cols.SOLVED, time.isSolved() ? 1 : 0);
        values.put(TaskTable.Cols.CONTACT, time.getContact());
        return values;
    }

    public void updateTime(Time time) {
        String uuidString = time.getId().toString();
        ContentValues values = getContentValues(time);
        mDatabase.update(TaskTable.NAME, values, TaskTable.Cols.UUID + " = ?", new String[]{uuidString});
    }

    private TaskCursorWrapper queryTime(String whereClause, String[] whereArgs) {
        Cursor cursor = mDatabase.query(
                TaskTable.NAME,
                null,
                whereClause,
                whereArgs,
                null,
                null,
                null
        );
        return new TaskCursorWrapper(cursor);
    }


}

