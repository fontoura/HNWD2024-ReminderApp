package com.thaysesolis.tasklist.persistence;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.thaysesolis.tasklist.model.Reminder;

import java.util.ArrayList;
import java.util.List;

public class ReminderPersistence extends SQLiteOpenHelper {

    public ReminderPersistence(Context context) {
        super(context, "reminder_database", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE reminder (id INTEGER PRIMARY KEY AUTOINCREMENT, title TEXT, text TEXT, timestamp INTEGER)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS reminder");
        onCreate(db);
    }

    public List<Reminder> getAllReminders() {
        try (SQLiteDatabase db = this.getReadableDatabase()) {
            List<Reminder> reminderList = new ArrayList<>();
            String selectQuery = "SELECT id, title, text, timestamp FROM reminder";
            try (Cursor cursor = db.rawQuery(selectQuery, null)) {
                if (cursor.moveToFirst()) {
                    do {
                        Reminder reminder = new Reminder();
                        reminder.setId(cursor.getInt(0));
                        reminder.setTitle(cursor.getString(1));
                        reminder.setText(cursor.getString(2));
                        reminder.setCreationTimestamp(cursor.getLong(3));

                        reminderList.add(reminder);
                    } while (cursor.moveToNext());
                }
            }
            return reminderList;
        }
    }

    public void addReminder(Reminder reminder) {
        try (SQLiteDatabase db = this.getWritableDatabase()) {
            ContentValues values = new ContentValues();
            values.put("title", reminder.getTitle());
            values.put("text", reminder.getText());
            values.put("timestamp", reminder.getCreationTimestamp());
            long id = db.insert("reminder", null, values);
            reminder.setId(id);
        }
    }

    public void deleteReminder(long id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("reminder", "id = ?", new String[]{String.valueOf(id)});
        db.close();
    }

    public void deleteReminder(Reminder reminder) {
    }
}
