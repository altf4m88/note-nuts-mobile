// In EditActivity.java
package com.example.notenuts;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

public class EditActivity extends AppCompatActivity {

    DBConfig config;
    SQLiteDatabase db;
    Cursor cursor;

    EditText edtTitle, edtDescription, edtLabels;
    Button btnSubmit, btnSelectImage;
    ImageView imgPreview;
    String id;

    private Uri selectedImageUri;

    private final ActivityResultLauncher<Intent> imagePickerLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    selectedImageUri = result.getData().getData();
                    final int takeFlags = Intent.FLAG_GRANT_READ_URI_PERMISSION;
                    getContentResolver().takePersistableUriPermission(selectedImageUri, takeFlags);
                    imgPreview.setImageURI(selectedImageUri);
                    imgPreview.setVisibility(View.VISIBLE);
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        config = new DBConfig(this);
        id = getIntent().getStringExtra("id");

        edtTitle = findViewById(R.id.edt_title);
        edtDescription = findViewById(R.id.edt_description);
        edtLabels = findViewById(R.id.edt_labels);
        btnSubmit = findViewById(R.id.btn_submit);
        btnSelectImage = findViewById(R.id.btn_select_image);
        imgPreview = findViewById(R.id.img_preview);

        btnSubmit.setOnClickListener(view -> editData());
        btnSelectImage.setOnClickListener(view -> {
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("image/*");
            imagePickerLauncher.launch(intent);
        });

        showData();
    }

    private void showData() {
        db = config.getReadableDatabase();
        cursor = db.rawQuery("SELECT * FROM tb_tugas WHERE id = ?", new String[]{id});
        if (cursor.moveToFirst()) {
            edtTitle.setText(cursor.getString(1));
            edtDescription.setText(cursor.getString(2));

            String imageUriString = cursor.getString(3);
            if (imageUriString != null && !imageUriString.isEmpty()) {
                selectedImageUri = Uri.parse(imageUriString);
                imgPreview.setImageURI(selectedImageUri);
                imgPreview.setVisibility(View.VISIBLE);
            }

            String labels = cursor.getString(4);
            edtLabels.setText(labels);
        }
        if (cursor != null) {
            cursor.close();
        }
    }

    private void editData() {
        String title = edtTitle.getText().toString();
        String description = edtDescription.getText().toString();
        String labels = edtLabels.getText().toString();
        long timestamp = System.currentTimeMillis(); // Update timestamp on edit
        String imageUriString = selectedImageUri != null ? selectedImageUri.toString() : null;

        if (title.isEmpty() || description.isEmpty()) {
            Toast.makeText(this, "Judul dan Deskripsi harus diisi!", Toast.LENGTH_SHORT).show();
        } else {
            // BEST PRACTICE: Use ContentValues and db.update() for safe, parameterized queries
            ContentValues values = new ContentValues();
            values.put("title", title);
            values.put("description", description);
            values.put("labels", labels);
            values.put("timestamp", timestamp);
            values.put("image_uri", imageUriString);

            db = config.getWritableDatabase();
            int rowsAffected = db.update("tb_tugas", values, "id = ?", new String[]{id});

            if (rowsAffected > 0) {
                Toast.makeText(this, "Catatan berhasil diubah.", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Gagal mengubah catatan.", Toast.LENGTH_SHORT).show();
            }
        }
    }
}