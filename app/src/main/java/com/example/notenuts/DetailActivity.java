package com.example.notenuts;

import androidx.appcompat.app.AppCompatActivity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.ImageView;
import android.view.View;
import android.net.Uri;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DetailActivity extends AppCompatActivity {

    DBConfig config;
    SQLiteDatabase db;
    Cursor cursor;
    ImageView imgDetail;
    TextView txtJudul, txtDeskripsi, txtLabels, txtTimestamp;

    String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        config = new DBConfig(this);

        id = getIntent().getExtras().getString("id");

        txtJudul = findViewById(R.id.txt_judul);
        txtDeskripsi = findViewById(R.id.txt_deskripsi);
        imgDetail = findViewById(R.id.img_detail);
        txtLabels = findViewById(R.id.txt_labels);
        txtTimestamp = findViewById(R.id.txt_timestamp);

        showData();
    }

    private String formatTimestamp(long timeInMillis) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault());
        return sdf.format(new Date(timeInMillis));
    }

    private void showData() {
        db = config.getReadableDatabase();
        cursor = db.rawQuery("SELECT * FROM tb_tugas WHERE id = ?", new String[]{id});

        if (cursor.moveToFirst()) {
            txtJudul.setText(cursor.getString(1));
            txtDeskripsi.setText(cursor.getString(2));

            // Get image, labels, and timestamp data
            String imageUriString = cursor.getString(3);
            String labels = cursor.getString(4);
            long timestamp = cursor.getLong(5);

            // Display Image
            if (imageUriString != null && !imageUriString.isEmpty()) {
                imgDetail.setImageURI(Uri.parse(imageUriString));
                imgDetail.setVisibility(View.VISIBLE);
            }

            // Display Labels
            if (labels != null && !labels.isEmpty()) {
                txtLabels.setText("Labels: " + labels);
                txtLabels.setVisibility(View.VISIBLE);
            } else {
                txtLabels.setVisibility(View.GONE);
            }

            // Display formatted Timestamp
            txtTimestamp.setText(formatTimestamp(timestamp));
        }

        if (cursor != null) {
            cursor.close();
        }
    }
}