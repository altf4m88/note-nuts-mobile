package com.example.notenuts;

// In AddNoteActivity.java (apply similar logic to EditActivity.java)
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

public class AddNoteActivity extends AppCompatActivity {

    Button btnSubmit, btnSelectImage;
    EditText edtTitle, edtDescription, edtLabels; // Add edtLabels
    ImageView imgPreview; // Add ImageView

    DBConfig config;
    SQLiteDatabase db;

    private Uri selectedImageUri; // To hold the selected image URI

    // Modern way to handle activity results for picking media
    private final ActivityResultLauncher<Intent> imagePickerLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    selectedImageUri = result.getData().getData();

                    // Persist permission to access the URI across device reboots
                    final int takeFlags = Intent.FLAG_GRANT_READ_URI_PERMISSION;
                    getContentResolver().takePersistableUriPermission(selectedImageUri, takeFlags);

                    imgPreview.setImageURI(selectedImageUri);
                    imgPreview.setVisibility(View.VISIBLE);
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_note);

        config = new DBConfig(this);
        edtTitle = findViewById(R.id.edt_title);
        edtDescription = findViewById(R.id.edt_description);
        edtLabels = findViewById(R.id.edt_labels); // Initialize
        btnSelectImage = findViewById(R.id.btn_select_image); // Initialize
        imgPreview = findViewById(R.id.img_preview); // Initialize
        btnSubmit = findViewById(R.id.btn_submit);

        btnSubmit.setOnClickListener(view -> addNote());

        btnSelectImage.setOnClickListener(view -> {
            // Launch the image picker
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("image/*");
            imagePickerLauncher.launch(intent);
        });
    }

    private void addNote() {
        String title = edtTitle.getText().toString();
        String description = edtDescription.getText().toString();
        String labels = edtLabels.getText().toString(); // Get labels
        long timestamp = System.currentTimeMillis(); // Get current time

        // Convert URI to string to store in DB. If no image, store null.
        String imageUriString = selectedImageUri != null ? selectedImageUri.toString() : null;

        if (title.isEmpty() || description.isEmpty()) {
            Toast.makeText(this, "Judul dan Deskripsi harus diisi!", Toast.LENGTH_SHORT).show();
        } else {
            // BEST PRACTICE: Use ContentValues and db.insert() to prevent SQL injection
            android.content.ContentValues values = new android.content.ContentValues();
            values.put("title", title);
            values.put("description", description);
            values.put("labels", labels);
            values.put("timestamp", timestamp);
            values.put("image_uri", imageUriString);

            db = config.getWritableDatabase();
            long newRowId = db.insert("tb_tugas", null, values);

            if (newRowId != -1) {
                Toast.makeText(this, "Catatan berhasil dibuat.", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Gagal membuat catatan.", Toast.LENGTH_SHORT).show();
            }
        }
    }
}