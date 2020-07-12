package com.r2f.rehab2fit.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.io.IOException;
import java.util.HashMap;

/*
    Provides access to local user repository
    Extends SQLiteOpenHelper, uses SQLite database to store users and retrieve
    user info for validation
 */
public class LoginRepository extends SQLiteOpenHelper {

    public enum LoginStatus{
        statusOk,
        statusDatabaseError,
        statusNoSuchUser,
        statusIncorrectPassword,
    }
    // public methods

//    public LoginRepository(@Nullable Context context) {
 //       super(context, DB_NAME, null, DB_VERSION);
 //   }
    // Add a new user given their details
    public void AddUser(UserInfo user){
        //Get the Data Repository in write mode
        SQLiteDatabase db = this.getWritableDatabase();
        //Create a new map of values, where column names are the keys
        ContentValues cValues = new ContentValues();
        cValues.put(KEY_EMAIL, user.getUserId());
        cValues.put(KEY_PASSWORD, user.getPassword());
        cValues.put(KEY_NAME, user.getDisplayName());
        cValues.put(KEY_ADDRESS, user.getAddress());
        cValues.put(KEY_PHONE, user.getPhone());
        // Insert the new row, returning the primary key value of the new row
        long newRowId = db.insert(TABLE_Users,null, cValues);
        db.close();
    }

    // Lookup a user given userId (email) and password
    public HashMap<String, String> GetUser(String userId, String password){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_Users + " WHERE " +  KEY_EMAIL + "=\'" + userId + "\'";
        Cursor cursor = db.rawQuery(query,null);
        HashMap<String,String> user = new HashMap<>();
        user.put("statusMsg", "User not found.");
        if (cursor.moveToNext()){       // Lookup with username succeeded
            // Check password
            String dbPass = cursor.getString(cursor.getColumnIndex(KEY_PASSWORD));
            if(!(password.equals(dbPass)))
                user.put("statusMsg", "Incorrect password");
            else {
                user.put(KEY_EMAIL, cursor.getString(cursor.getColumnIndex(KEY_EMAIL)));
                user.put(KEY_ADDRESS, cursor.getString(cursor.getColumnIndex(KEY_ADDRESS)));
                user.put(KEY_PASSWORD, cursor.getString(cursor.getColumnIndex(KEY_PASSWORD)));
                user.put(KEY_NAME, cursor.getString(cursor.getColumnIndex(KEY_NAME)));
                user.put(KEY_ADDRESS, cursor.getString(cursor.getColumnIndex(KEY_ADDRESS)));
                user.put(KEY_PHONE, cursor.getString(cursor.getColumnIndex(KEY_PHONE)));
                user.put("statusMsg", "");
            }
        }
        return user;
    }

    public LoginRepository(UserInfo dataSource, @Nullable Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        this.dataSource = dataSource;
    }

    public static LoginRepository getInstance(UserInfo dataSource) {
        if (instance == null) {
            instance = new LoginRepository(dataSource, null);
        }
        return instance;
    }

    public LoginStatus login(UserInfo user) {
        try {
            HashMap<String, String> userData = GetUser(user.getUserId(), user.getPassword());
            if (userData.get("statusMsg") == "")         // Success
            {
                user.setUserId(userData.get(KEY_EMAIL));
                user.setDisplayName(userData.get(KEY_NAME));
                user.setPhone(userData.get(KEY_PHONE));
                user.setAddress(userData.get(KEY_ADDRESS));
                user.setPassword(userData.get(KEY_PASSWORD));
                return LoginStatus.statusOk;
            }
            else{           // User not found
                return LoginStatus.statusNoSuchUser;
            }
        } catch (Exception e){
            return LoginStatus.statusDatabaseError;
        }
    }

    // private methods

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String CREATE_TABLE = "CREATE TABLE " + TABLE_Users + "("
                + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + KEY_EMAIL + " TEXT,"
                + KEY_PASSWORD + " TEXT,"
                + KEY_NAME + " TEXT,"
                + KEY_ADDRESS + " TEXT,"
                + KEY_PHONE + " TEXT"+ ")";
        sqLiteDatabase.execSQL(CREATE_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
    // private variables
    private static volatile LoginRepository instance;

    private UserInfo dataSource;

    // If user credentials will be cached in local storage, it is recommended it be encrypted
    // @see https://developer.android.com/training/articles/keystore

    private static final int DB_VERSION = 1;
    private static final String DB_NAME = "Rehab2Fit";
    private static final String TABLE_Users = "userInfo";
    private static final String KEY_ID = "id";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_PASSWORD = "password";
    private static final String KEY_NAME = "name";
    private static final String KEY_ADDRESS = "address";
    private static final String KEY_PHONE = "phone";
}