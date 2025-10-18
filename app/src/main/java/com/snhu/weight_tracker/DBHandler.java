package com.snhu.weight_tracker;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Handles all DB functions for the Weight Tracker App
 */
public class DBHandler extends SQLiteOpenHelper {

    private static final int VERSION = 4;
    private static final String DATABASE_NAME = "app_test.db";

    private static DBHandler instance;

    DBHandler(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
        System.out.println("initiate");
    }

    private static DBHandler getInstance(Context context) {
        if (instance == null) {
            instance = new DBHandler(context);
        }

        return instance;
    }

    /**
     * Initialize table and column identifiers for DB methods
     */
    public static final class TableMeta {
        private static final String RECORDS_TABLE = "weight_records";
        private static final String COL_USER_ID = "user_id";
        private static final String COL_WEIGHT = "weight";
        private static final String COL_DATE = "date";
        private static final String USERS_TABLE = "users";
        private static final String COL_USERNAME = "username";
        private static final String COL_PASSWORD = "password";
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // User identifiers initialized above to create new DB tables

        System.out.println("--- CREATING TABLES ---");

        // build SQL queries to create tables
        String CREATE_RECORDS_TABLE_SQL =
                "CREATE TABLE IF NOT EXISTS " + TableMeta.RECORDS_TABLE + "( " +
                TableMeta.COL_USER_ID + " INTEGER," +
                TableMeta.COL_WEIGHT + " REAL," +
                TableMeta.COL_DATE + " TEXT)";

        String CREATE_USERS_TABLE_SQL =
                "CREATE TABLE IF NOT EXISTS " + TableMeta.USERS_TABLE + " (" +
                TableMeta.COL_USER_ID + " INTEGER PRIMARY KEY," +
                TableMeta.COL_USERNAME + " TEXT," +
                TableMeta.COL_PASSWORD + " TEXT)";

        // execute queries
        db.execSQL(CREATE_RECORDS_TABLE_SQL);
        db.execSQL(CREATE_USERS_TABLE_SQL);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TableMeta.RECORDS_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + TableMeta.USERS_TABLE);
        onCreate(db);
    }

    /**
     * Insert a new WeightRecord with the given params
     *
     * @param user_id       The ID of the user who entered the record
     * @param weightRecord  The WeightRecord object to be inserted
     * @return              whether operation was successful
     */
    public long addWeightRecord(int user_id, WeightRecord weightRecord) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(TableMeta.COL_USER_ID, user_id);
        values.put(TableMeta.COL_WEIGHT, weightRecord.getWeight());
        values.put(TableMeta.COL_DATE, weightRecord.getDate());

        return db.insert(TableMeta.RECORDS_TABLE, null, values);
    }

    /**
     * Method to fetch WeightRecord objects from DB
     *
     * @param user  the current logged in user ID
     * @return      A List of all the user's WeightRecords
     */
    public List<WeightRecord> getWeightRecords(@NonNull AuthenticatedUser user) {
        System.out.println("searching for records for: " + user.getUsername());
        List<WeightRecord> weightRecords = new ArrayList<>();

        SQLiteDatabase db = getReadableDatabase();

        String[] projection = {"rowid", TableMeta.COL_WEIGHT, TableMeta.COL_DATE};
        String selection = TableMeta.COL_USER_ID + " = ?";
        String[] selectArgs = {Integer.toString(user.get_id())};

        Cursor cursor = db.query(
            TableMeta.RECORDS_TABLE,
            projection,
            selection,
            selectArgs,
            null,
            null,
            TableMeta.COL_DATE
        );

        while (cursor.moveToNext()) {
            weightRecords.add(new WeightRecord(cursor.getInt(0), cursor.getFloat(1), cursor.getString(2)));
            System.out.println(cursor.getPosition());
        }
        cursor.close();
        return weightRecords;
    }

    /**
     * Fetch a specific WeightRecord by ID
     *
     * TODO: determine whether or not this method has a practical use in this app
     *
     * @param recordId  The WeightRecord ID
     * @return          A specific WeightRecord object
     */
    public WeightRecord getWeightRecordById(int recordId) {
        WeightRecord weightRecord = null;

        SQLiteDatabase db = getReadableDatabase();
        String sql = "SELECT * FROM " + TableMeta.RECORDS_TABLE + " WHERE " + TableMeta.COL_USER_ID + " = " + recordId;
        Cursor cursor = db.rawQuery(sql, new String[]{Long.toString(recordId)});

        if (cursor.moveToFirst()) {
            float weight = cursor.getFloat(1);
            String date = cursor.getString(2);

            weightRecord = new WeightRecord(recordId, weight, date);
        }
        cursor.close();
        return weightRecord;
    }

    /**
     * Edit existing weight record
     *
     * @param recordID  The WeightRecord to be modified
     * @return          Whether operation was successful
     */
    public boolean editWeightRecord(int recordID, WeightRecord weightRecord) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(TableMeta.COL_WEIGHT, weightRecord.getWeight());
        values.put(TableMeta.COL_DATE, weightRecord.getDate());

        String selection = "rowid = ?";
        String[] selectArgs = {Integer.toString(recordID)};

        return (db.update(TableMeta.RECORDS_TABLE, values, selection, selectArgs) > 0);
    }

    /**
     * Remove WeightRecord from DB
     *
     * @param recordID  The WeightRecord to be deleted
     * @return          Whether operation was successful
     */
    public boolean deleteWeightRecord(int recordID) {
        SQLiteDatabase db = getWritableDatabase();

        String selection = "rowid = ?";
        String[] selectArgs = {Integer.toString(recordID)};

        return (db.delete(TableMeta.RECORDS_TABLE, selection, selectArgs) > 0);
    }

    /**
     * Add a new user upon clicking "Register" button
     *
     * @param username  the new username to add
     * @param password  the new user's password
     * @return          whether operation was successful
     */
    public boolean addUser(String username, String password){
        SQLiteDatabase db = getWritableDatabase();

        String checkIfExistsSql = "SELECT * FROM " + TableMeta.USERS_TABLE +
                " WHERE " + TableMeta.COL_USERNAME + " = ?";

        Cursor cursor = db.rawQuery(checkIfExistsSql, new String[] {username});

        System.out.println(cursor.getCount());
        if (cursor.getCount() > 0) {
            return false;
        }
        cursor.close();

        ContentValues values = new ContentValues();
        values.put(TableMeta.COL_USERNAME, username);
        values.put(TableMeta.COL_PASSWORD, password);

        long newUserId = db.insert(TableMeta.USERS_TABLE, null, values);
        System.out.println("new user ID: " + newUserId);

        return true;
    }

    /**
     * Fetch User data from DB by login credentials
     *
     * @param username  the username to find
     * @param password  the user's password
     * @return          whether user with given credentials exists
     */
    public AuthenticatedUser getUser(String username,String password){
        SQLiteDatabase db = getReadableDatabase();

        String[] projection = {
                TableMeta.COL_USER_ID,
                TableMeta.COL_USERNAME,
                TableMeta.COL_PASSWORD
        };

        String selection = TableMeta.COL_USERNAME + " = ? AND " + TableMeta.COL_PASSWORD + " = ?";
        String[] selectArgs = {username, password};

        Cursor cursor = db.query(TableMeta.USERS_TABLE, projection, selection, selectArgs, null, null, null);

        AuthenticatedUser user = new AuthenticatedUser();
        if(cursor.moveToFirst()) {
//        System.out.println(cursor.getInt(0));
            user.set_id(cursor.getInt(0));
            user.setUsername(cursor.getString(1));
            user.setPassword(cursor.getString(2));
        }else{
            user = null;
        }
        cursor.close();
        return user;
    }

    /**
     * Strictly for testing purposes
     * Loads dummy data into DB for first user account
     * @return  newly added row ID if successful
     */
    public long loadInitialData() {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(TableMeta.COL_USER_ID, 1);
        values.put(TableMeta.COL_WEIGHT, 168.5);
        values.put(TableMeta.COL_DATE, "2025/07/25");

        db.insert(TableMeta.RECORDS_TABLE, null, values);

        values.put(TableMeta.COL_USER_ID, 1);
        values.put(TableMeta.COL_WEIGHT, 168.75);
        values.put(TableMeta.COL_DATE, "2025/07/26");

        db.insert(TableMeta.RECORDS_TABLE, null, values);

        values.put(TableMeta.COL_USER_ID, 1);
        values.put(TableMeta.COL_WEIGHT, 169.5);
        values.put(TableMeta.COL_DATE, "2025/07/27");

        long newRowId = db.insert(TableMeta.RECORDS_TABLE, null, values);

        return newRowId;
    }
}
