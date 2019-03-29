package diakonidze.marketprices.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

public class DBHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "marketDB";
    private static final int DB_VERSION = 1;
    private static final String TAG = "DBHelper";

    public DBHelper(@Nullable Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d(TAG, "DB on Create");

        db.execSQL(DBKeys.CREATE_TABLE_VERSION);
        db.execSQL(DBKeys.INITIALIZE_VERSIONS_TABLE);

        db.execSQL(DBKeys.CREATE_TABLE_MYLIST);

        db.execSQL(DBKeys.CREATE_TABLE_BRANDS);
        db.execSQL(DBKeys.CREATE_TABLE_MARKETS);
        db.execSQL(DBKeys.CREATE_TABLE_PACKS);
        db.execSQL(DBKeys.CREATE_TABLE_PARAMITERS);
        db.execSQL(DBKeys.CREATE_TABLE_PARAMVALUE);
        db.execSQL(DBKeys.CREATE_TABLE_PRODUCTS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d(TAG, "DB on Update");
    }
}
