package com.example.notenuts;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import androidx.annotation.Nullable;
import android.content.Context;

public class DBConfig extends SQLiteOpenHelper {
    private static final String DB_NAME = "db_catatan.db";
    // VITAL: Increment the database version from 1 to 2
    private static final int DB_VERSION = 2;

    public DBConfig(@Nullable Context context){
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Add new columns for image_uri, labels, and timestamp
        String queryCreateTable = "CREATE TABLE tb_tugas (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "title VARCHAR(255) NOT NULL, " +
                "description TEXT NOT NULL, " +
                "image_uri TEXT, " + // To store the path/URI of the image
                "labels TEXT, " +    // To store comma-separated labels
                "timestamp INTEGER)";// To store the creation/modification time
        db.execSQL(queryCreateTable);

        // Optional: Update your sample data
        String querySampleData = "INSERT INTO tb_tugas (title, description, labels, timestamp) VALUES " +
                "('UTS', 'Membuat program sederhana', 'Penting,Kuliah', " + System.currentTimeMillis() + "), " +
                "('Tugas', 'Praktik SQLite', 'Kuliah', " + System.currentTimeMillis() + ")";
        db.execSQL(querySampleData);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This method is called when DB_VERSION is increased.
        // It prevents users from losing their data on app update.
        if (oldVersion < 2) {
            db.execSQL("ALTER TABLE tb_tugas ADD COLUMN image_uri TEXT");
            db.execSQL("ALTER TABLE tb_tugas ADD COLUMN labels TEXT");
            db.execSQL("ALTER TABLE tb_tugas ADD COLUMN timestamp INTEGER");
        }
    }
}