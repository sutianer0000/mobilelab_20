package com.example.lab06.lesson3;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class EventDbHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "events.db";
    private static final int DB_VERSION = 1;
    private static final String TABLE = "events";

    public EventDbHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE + " (id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "name TEXT, place TEXT, date TEXT, time TEXT, enabled INTEGER)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE);
        onCreate(db);
    }

    public List<Event> getEvents(boolean showAll) {
        List<Event> events = new ArrayList<>();
        String selection = showAll ? null : "enabled = 1";
        try (Cursor cursor = getReadableDatabase().query(TABLE, null, selection, null, null, null, "id")) {
            while (cursor.moveToNext()) {
                events.add(readEvent(cursor));
            }
        }
        return events;
    }

    public Event getEvent(long id) {
        String[] args = {String.valueOf(id)};
        try (Cursor cursor = getReadableDatabase().query(TABLE, null, "id = ?", args, null, null, null)) {
            return cursor.moveToFirst() ? readEvent(cursor) : null;
        }
    }

    public void insert(Event event) {
        getWritableDatabase().insert(TABLE, null, toValues(event));
    }

    public void update(Event event) {
        String[] args = {String.valueOf(event.id)};
        getWritableDatabase().update(TABLE, toValues(event), "id = ?", args);
    }

    public void setEnabled(long id, boolean enabled) {
        ContentValues values = new ContentValues();
        values.put("enabled", enabled ? 1 : 0);
        getWritableDatabase().update(TABLE, values, "id = ?", new String[]{String.valueOf(id)});
    }

    public void delete(long id) {
        getWritableDatabase().delete(TABLE, "id = ?", new String[]{String.valueOf(id)});
    }

    public void deleteAll() {
        getWritableDatabase().delete(TABLE, null, null);
    }

    private Event readEvent(Cursor cursor) {
        return new Event(
                cursor.getLong(cursor.getColumnIndexOrThrow("id")),
                cursor.getString(cursor.getColumnIndexOrThrow("name")),
                cursor.getString(cursor.getColumnIndexOrThrow("place")),
                cursor.getString(cursor.getColumnIndexOrThrow("date")),
                cursor.getString(cursor.getColumnIndexOrThrow("time")),
                cursor.getInt(cursor.getColumnIndexOrThrow("enabled")) == 1);
    }

    private ContentValues toValues(Event event) {
        ContentValues values = new ContentValues();
        values.put("name", event.name);
        values.put("place", event.place);
        values.put("date", event.date);
        values.put("time", event.time);
        values.put("enabled", event.enabled ? 1 : 0);
        return values;
    }
}
