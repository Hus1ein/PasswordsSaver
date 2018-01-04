package com.hussainabdelilah.passwordsaver.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.hussainabdelilah.passwordsaver.database.Database.*;

public class DatabaseOpenHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "passwords_saver";
    private static final int DB_VERSION = 1;
    public DatabaseOpenHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //Create UsernameTable
        db.execSQL("CREATE TABLE " + UsernamesTable.TABLE_NAME + "("
                + UsernamesTable.Columns.ID +" INTEGER PRIMARY KEY AUTOINCREMENT, "
                + UsernamesTable.Columns.USERNAME +" TEXT, "
                + UsernamesTable.Columns.PASSWORD +" TEXT);");
        //Create CategoryTable
        db.execSQL("CREATE TABLE " + CategoryTable.TABLE_NAME + "("
                + CategoryTable.Columns.ID +" INTEGER PRIMARY KEY AUTOINCREMENT, "
                + CategoryTable.Columns.ID_USERNAME + " INTEGER, "
                + CategoryTable.Columns.CATEGORY +" TEXT);");
        //Create DataTable
        db.execSQL("CREATE TABLE " + DataTable.TABLE_NAME + "("
                + DataTable.Columns.ID +" INTEGER PRIMARY KEY AUTOINCREMENT, "
                + DataTable.Columns.ID_USERNAME + " INTEGER, "
                + DataTable.Columns.ID_CATEGORY + " INTEGER, "
                +DataTable.Columns.ACCOUNT +" TEXT, "
                + DataTable.Columns.USERNAME +" TEXT, "
                + DataTable.Columns.PASSWORD +" TEXT);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
