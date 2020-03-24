package com.golan.amit.barbacksitrolltrace;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class WorkOutDbHelper extends SQLiteOpenHelper {

    public static final String DATABASENAME = "workout.db";
    public static final String TABLE = "tblsession";
    public static final int DATABASEVERSION = 1;
    public static final String ID_COLUMN = "id";
    public static final String TOTAL_COLUMN = "total";
    public static final String GOOD_COLUMN = "good";
    public static final String DATETIME_COLUMN = "curr_datetime";

    SQLiteDatabase database;

    public static final String CREATE_TABLE_WORKOUT =
            "CREATE TABLE IF NOT EXISTS " + TABLE +
                    "(" + ID_COLUMN + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    TOTAL_COLUMN + " INTEGER," +
                    GOOD_COLUMN + " INTEGER," +
                    DATETIME_COLUMN + " DATE);";

    String[] allColumns = {
            ID_COLUMN, TOTAL_COLUMN, GOOD_COLUMN, DATETIME_COLUMN
    };

    public WorkOutDbHelper(Context context) {
        super(context, DATABASENAME, null, DATABASEVERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        if (MainActivity.DEBUG) {
            Log.i(MainActivity.DEBUGTAG, "create string: {" + CREATE_TABLE_WORKOUT + "}");
        }
        try {
            db.execSQL(CREATE_TABLE_WORKOUT);
            if (MainActivity.DEBUG) {
                Log.i(MainActivity.DEBUGTAG, "database created");
            }
        } catch (Exception edb) {
            Log.e(MainActivity.DEBUGTAG, "database creation exception: " + edb);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE);
        onCreate(db);
    }

    public void open() {
        database = this.getWritableDatabase();
        if(MainActivity.DEBUG) {
            Log.i(MainActivity.DEBUGTAG, "database connection open");
        }
    }

    public void close() {
        if (database != null) {
            try {
                database.close();
                if(MainActivity.DEBUG) {
                    Log.i(MainActivity.DEBUGTAG, "database connection closed");
                }
            } catch (Exception edbc) {
                Log.e(MainActivity.DEBUGTAG, "database connection close exception: " + edbc);
            }
        } else {
            Log.e(MainActivity.DEBUGTAG, "database is null");
        }
    }


    public void insert(String sTotal, String sGood) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(TOTAL_COLUMN, sTotal);
        contentValues.put(GOOD_COLUMN, sGood);
        contentValues.put(DATETIME_COLUMN, currentDate());
        long insertedId = -1;

        try {
            insertedId = database.insert(TABLE, null, contentValues);
            if(MainActivity.DEBUG) {
                Log.i(MainActivity.DEBUGTAG, "inserted total: " + sTotal + ", good: " + sGood + " to db, id: " + insertedId);
            }
        } catch (Exception eid) {
            Log.e(MainActivity.DEBUGTAG, "insert exception: " + eid);
        }

    }


    public void resetTableToScratch() {
        try {
            database.execSQL("DROP TABLE IF EXISTS " + TABLE);
            if(MainActivity.DEBUG) {
                Log.i(MainActivity.DEBUGTAG, "database dropped");
            }
        } catch (Exception edbd) {
            Log.e(MainActivity.DEBUGTAG, "database drop exception: " + edbd);
        }

        try {
            database.execSQL(CREATE_TABLE_WORKOUT);
            if(MainActivity.DEBUG) {
                Log.i(MainActivity.DEBUGTAG, "database re-created");
            }
        } catch (Exception edbc) {
            Log.e(MainActivity.DEBUGTAG, "database re-create exception: " + edbc);
        }
    }



    public void displayDatabaseContent() {
        String query = "SELECT * FROM " + TABLE;
        Cursor cursor = database.rawQuery(query, null);
        if(cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                long id = cursor.getLong(cursor.getColumnIndex(ID_COLUMN));
                int total = cursor.getInt(cursor.getColumnIndex(TOTAL_COLUMN));
                int good = cursor.getInt(cursor.getColumnIndex(GOOD_COLUMN));
                String currentdate = cursor.getString(cursor.getColumnIndex(DATETIME_COLUMN));

                String tmpInfoDisplay = String.format("id: %d, total: %d, good: %d, date; %s",
                        id, total, good, currentdate);
                Log.i(MainActivity.DEBUGTAG, tmpInfoDisplay);
            }
        } else {
            Log.e(MainActivity.DEBUGTAG, "database is empty, no activity in account");
        }
    }


    public ArrayList<WorkOutSession> getAllWorkoutSessions() {
        ArrayList<WorkOutSession> l = new ArrayList<WorkOutSession>();
        String query = "SELECT * FROM " + TABLE;
        Cursor cursor = database.rawQuery(query, null);
        if (cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                long id = cursor.getLong(cursor.getColumnIndex(ID_COLUMN));
                int total = cursor.getInt(cursor.getColumnIndex(TOTAL_COLUMN));
                int good = cursor.getInt(cursor.getColumnIndex(GOOD_COLUMN));
                String currentdate = cursor.getString(cursor.getColumnIndex(DATETIME_COLUMN));
                WorkOutSession wos = new WorkOutSession((int) id, total, good, currentdate);
                l.add(wos);
            }
        }
        return l;
    }


    public ArrayList<WorkOutSession> getAllWorkoutSessionsByFilter(String selection, String orderBy) {
        Cursor cursor = database.query(TABLE, allColumns, selection, null, null, null, orderBy);
        ArrayList<WorkOutSession> l = new ArrayList<WorkOutSession>();
        if (cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                long id = cursor.getLong(cursor.getColumnIndex(ID_COLUMN));
                int total = cursor.getInt(cursor.getColumnIndex(TOTAL_COLUMN));
                int good = cursor.getInt(cursor.getColumnIndex(GOOD_COLUMN));
                String currentdate = cursor.getString(cursor.getColumnIndex(DATETIME_COLUMN));
                WorkOutSession wos = new WorkOutSession((int) id, total, good, currentdate);
                l.add(wos);
            }
        }
        return l;
    }

    public int totalSum(String column) {
        int totalSum = -1;

        if(!column.equals(TOTAL_COLUMN) && !column.equals(GOOD_COLUMN)) {
            Log.e(MainActivity.DEBUGTAG, "column argument: " + column + " doesn't exist");
            return totalSum;
        }

        String query = "SELECT SUM(" + column + ") FROM " + TABLE;
        Cursor cursor = database.rawQuery(query, null);
        if (cursor.moveToNext()) {
            try {
                totalSum = cursor.getInt(0);
                if(MainActivity.DEBUG) {
                    Log.i(MainActivity.DEBUGTAG, "total deposit sum: " + totalSum);
                }
            } catch (Exception e) {
                Log.e(MainActivity.DEBUGTAG, "total deposit sum exception: " + e);
            }
        }

        return totalSum;
    }


    public String lastActivityDate() {
        String tmpDate = null;

        String query = "SELECT " + DATETIME_COLUMN + " FROM " + TABLE +
                " ORDER BY " + DATETIME_COLUMN + " DESC LIMIT 1";
        Cursor cursor = database.rawQuery(query, null);
        if (cursor.moveToNext()) {
            try {
                tmpDate = cursor.getString(0);
                Log.d(MainActivity.DEBUGTAG, "select last date :" + tmpDate);
            } catch (Exception e) {
                Log.e(MainActivity.DEBUGTAG, "select last date exception:" + e);
            }
        }
        return tmpDate;
    }


    public WorkOutSession lastRecord() {
        WorkOutSession wos = null;
        String query = "SELECT * FROM " + TABLE +
                " ORDER BY " + DATETIME_COLUMN + " DESC LIMIT 1";
        Cursor cursor = database.rawQuery(query, null);
        if (cursor.moveToNext()) {
            try {
                long id = cursor.getLong(cursor.getColumnIndex(ID_COLUMN));
                int total = cursor.getInt(cursor.getColumnIndex(TOTAL_COLUMN));
                int good = cursor.getInt(cursor.getColumnIndex(GOOD_COLUMN));
                String currentdate = cursor.getString(cursor.getColumnIndex(DATETIME_COLUMN));
                wos = new WorkOutSession((int) id, total, good, currentdate);
            } catch (Exception e) {
                Log.e(MainActivity.DEBUGTAG, "database last record query exception: " + e);
            }
        }
        return wos;
    }


    public long deleteRecordById(long rowId) {
        return database.delete(TABLE, ID_COLUMN + "=" + rowId, null);
    }





    private String currentDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        return dateFormat.format(date);
    }


}
