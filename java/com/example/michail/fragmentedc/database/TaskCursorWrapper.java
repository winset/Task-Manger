package com.example.michail.fragmentedc.database;

import android.database.Cursor;
import android.database.CursorWrapper;

import com.example.michail.fragmentedc.Time;
import com.example.michail.fragmentedc.database.TaskDbScheme.TaskTable;

import java.util.Date;
import java.util.UUID;

public class TaskCursorWrapper extends CursorWrapper {

    public TaskCursorWrapper(Cursor cursor) {
        super(cursor);
    }

    public Time getTime() {
        String uuidString = getString(getColumnIndex(TaskTable.Cols.UUID));
        String title = getString(getColumnIndex(TaskTable.Cols.TITLE));
        long date = getLong(getColumnIndex(TaskTable.Cols.DATE));
        long clockTime = getLong(getColumnIndex(TaskTable.Cols.TIME));
        int isSolved = getInt(getColumnIndex(TaskTable.Cols.SOLVED));
        String contact = getString(getColumnIndex(TaskTable.Cols.CONTACT));

        Time time = new Time(UUID.fromString(uuidString));
        time.setTitle(title);
        time.setClockTime(new Date(clockTime));
        time.setDate(new Date(date));
        time.setSolved(isSolved != 0);
        time.setContact(contact);
        return time;
    }

}
