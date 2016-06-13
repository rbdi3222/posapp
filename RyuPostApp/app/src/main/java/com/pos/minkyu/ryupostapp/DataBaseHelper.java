package com.pos.minkyu.ryupostapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by 0106h on 2016-05-09.
 */
public class DataBaseHelper {
    public static final String KEY_TABLENUM = "tablenum";
    public static final String KEY_MENUENAME = "menuename";
    public static final String KEY_FOODNAME = "foodname";
    public static final String KEY_FOODPRICE = "foodprice";
    public static final String KEY_FOODCOUNT = "foodcount";

    private static final String TAG = "DataBaseHelper";
//sdfadsfasdf
    private DatabaseHelper mDbHelper;
    private SQLiteDatabase mDb;

    private static final String POSDB = "CREATE TABLE POSDB (_id integer primary key autoincrement, "
            + "tablenum integer, menuename text, foodname text, foodprice integer, foodcount integer);";

    private static final String DATABASE_NAME = "POS";
    private static final String DATABASE_TABLE = "POSDB";
    private static final int DATABASE_VERSION = 1;
    private final Context mCtx;

    private static class DatabaseHelper extends SQLiteOpenHelper {
        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(POSDB);

            ContentValues values = new ContentValues();
            values.put(KEY_TABLENUM, 1);
            values.put(KEY_MENUENAME, "메뉴");
            values.put(KEY_FOODNAME, "음식");
            values.put(KEY_FOODPRICE, 1000);
            values.put(KEY_FOODCOUNT, 1);

            db.insert(DATABASE_TABLE, null, values);
            values.clear();
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w(TAG, "Upgrading database from version " + oldVersion + " to " + newVersion
                    + ", which will destroy all old data");
            db.execSQL("DROP TABLE IF EXISTS POSDB");
            onCreate(db);
        }
    }

    public DataBaseHelper(Context ctx) {
        this.mCtx = ctx;
    }

    public DataBaseHelper open() throws SQLException {
        mDbHelper = new DatabaseHelper(mCtx);
        mDb = mDbHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        mDbHelper.close();
    }

    public void BeginTransaction(){
        mDb.beginTransaction();
    }

    public void SetTransactionSuccessful(){
        mDb.setTransactionSuccessful();
    }

    public void EndTransaction(){
        mDb.endTransaction();
    }

    public long insertIntoPosDB(int tablenum, String menuename, String foodname, int foodprice, int foodcount) {
        ContentValues values = new ContentValues();
        values.put(KEY_TABLENUM, tablenum);
        values.put(KEY_MENUENAME, menuename);
        values.put(KEY_FOODNAME, foodname);
        values.put(KEY_FOODPRICE, foodprice);
        values.put(KEY_FOODCOUNT, foodcount);

        return mDb.insert(DATABASE_TABLE, null, values);
    }

    public Cursor seletAllPosDB() {
        String query = "SELECT * FROM POSDB;";
        Cursor cursor = mDb.rawQuery(query, null);

        return cursor;
    }

    public Cursor selectTableInfo() {
        String query = "SELECT DISTINCT tablenum FROM POSDB;";
        Cursor cursor = mDb.rawQuery(query, null);

        return cursor;
    }

    public Cursor selectMenueInfo() {
        String query = "SELECT DISTINCT menuename FROM POSDB WHERE menuename is not null;";
        Cursor cursor = mDb.rawQuery(query, null);

        return cursor;
    }

    public Cursor selectFoodInfo(String menuename) {
        String query = "SELECT DISTINCT foodname, foodprice FROM POSDB WHERE menuename = '" + menuename+ "' and foodname is not null; ";
        Cursor cursor = mDb.rawQuery(query, null);

        return cursor;
    }

    public Cursor selectCountInfo(int tablenum, String menuename, String foodname) {
        String query = "SELECT foodcount FROM POSDB WHERE tablenum = " + tablenum + " and menuename = '" + menuename + "' and foodname = '" + foodname + "';";
        Cursor cursor = mDb.rawQuery(query, null);
        if (cursor != null) {
            cursor.moveToFirst();
        }
        return cursor;
    }
    public Cursor selectAllSumpriceInfo() {
        String query = "SELECT tablenum, sum(foodcount * foodprice) FROM POSDB group by tablenum; ";
        Cursor cursor = mDb.rawQuery(query, null);

        return cursor;
    }

    public Cursor selectSumpriceInfo(int tablenum) {
        String query = "SELECT foodcount * foodprice FROM POSDB WHERE tablenum = " + tablenum + " and foodname is not null and foodcount != 0; ";
        Cursor cursor = mDb.rawQuery(query, null);

        return cursor;
    }
    public Cursor selectPreviewpriceInfo(int tablenum) {
        String query = "SELECT foodname, foodcount FROM POSDB WHERE foodcount >= 1 and tablenum = " + tablenum + ";";
        Cursor cursor = mDb.rawQuery(query, null);

        return cursor;
    }
    public boolean updateCountInfo(int tablenum, String menuename, String foodname, int foodcount) {
        ContentValues args = new ContentValues();
        args.put("foodcount", foodcount);

        return mDb.update("POSDB", args, "tablenum = " + tablenum + " and menuename = '" + menuename + "' and foodname = '" + foodname + "'", null) > 0;
    }

    public boolean deleteMenue(String menuename){
        String query = "DELETE FROM POSDB WHERE menuename = '" + menuename + "' and menuename is not null;";
        mDb.execSQL(query);
        return true;
    }
    public boolean deleteFoodInfo(String menuename, String foodname) {
        String query = "DELETE FROM POSDB WHERE menuename = '" + menuename + "' and foodname = '" + foodname + "' and foodname is not null;";
        mDb.execSQL(query);

        return true;
    }
    public boolean deleteCountInfo(int tablenum, int menuenum, int foodnum) {
        String query = "delete from countdb where tablenum = " + tablenum + " and menuenum = " + menuenum + " and foodnum = " + foodnum + ";";
        mDb.execSQL(query);
        query = "delete from menuedb where menuenum = " + menuenum + ";";
        mDb.execSQL(query);

        return true;
    }
    public boolean deleteTable(int tablenum) {
        String query = "DELETE FROM POSDB WHERE TABLENUM = " + tablenum + ";";
        mDb.execSQL(query);
        return true;
    }

    public boolean deleteCountClear(int tablenum) {
        String query = "UPDATE POSDB SET foodcount = 0 WHERE TABLENUM = " + tablenum + " AND menuename IS NOT NULL;";
        mDb.execSQL(query);
        return true;
    }

}
