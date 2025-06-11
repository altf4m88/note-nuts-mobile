// In MainActivity.java
package com.example.notenuts;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.ImageView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    DBConfig config;
    SQLiteDatabase db;
    Cursor cursor;

    RecyclerView rcvData;
    RecyclerView.Adapter adapter;
    RecyclerView.LayoutManager layout;

    // Add a list for timestamps
    ArrayList idList, titleList, timestampList;

    ImageView linkAdd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        config = new DBConfig(this);

        linkAdd = findViewById(R.id.link_add);

        linkAdd.setOnClickListener( view -> {
            startActivity(new Intent( MainActivity.this, AddNoteActivity.class ));
        });
    }

    @Override
    protected void onResume() {
        showData();
        super.onResume();
    }

    void showData() {
        idList = new ArrayList<>();
        titleList = new ArrayList<>();
        timestampList = new ArrayList<>(); // Initialize the new list

        // Pass the new list to the adapter
        adapter = new DataAdapter(this, idList, titleList, timestampList);
        rcvData = findViewById(R.id.rcv_data);
        layout = new LinearLayoutManager(this);

        rcvData.setLayoutManager(layout);
        rcvData.setHasFixedSize(true);
        rcvData.setAdapter(adapter);

        db = config.getReadableDatabase();
        cursor = db.rawQuery("SELECT id, title, timestamp FROM tb_tugas ORDER BY timestamp DESC", null);
        cursor.moveToFirst();

        for( int count = 0; count < cursor.getCount(); count++ ) {
            cursor.moveToPosition(count);
            idList.add(cursor.getString(0));
            titleList.add(cursor.getString(1));
            timestampList.add(cursor.getLong(2)); // Add timestamp to the list
        }

        if (cursor != null) {
            cursor.close();
        }
    }
}