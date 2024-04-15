package com.thaysesolis.tasklist;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

public class NewReminderActivity extends AppCompatActivity {

    private EditText editTextTitle;
    private EditText editTextText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_reminder);

        editTextTitle = findViewById(R.id.editTextTitle);
        editTextText = findViewById(R.id.editTextText);

        Button buttonSave = findViewById(R.id.buttonSave);
        Button buttonCancel = findViewById(R.id.buttonCancel);

        buttonSave.setOnClickListener(v -> saveReminder());

        buttonCancel.setOnClickListener(v -> cancel());
    }

    private void saveReminder() {
        String title = editTextTitle.getText().toString().trim();
        String text = editTextText.getText().toString().trim();

        if (!title.isEmpty() && !text.isEmpty()) {
            Intent intent = new Intent();
            intent.putExtra("title", title);
            intent.putExtra("text", text);
            setResult(RESULT_OK, intent);
            finish();
        }
    }

    private void cancel() {
        finish();
    }
}