package com.thaysesolis.tasklist;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.thaysesolis.tasklist.adapter.ReminderArrayAdapter;
import com.thaysesolis.tasklist.model.Reminder;
import com.thaysesolis.tasklist.persistence.ReminderPersistence;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.List;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_NEW_REMINDER = 1;

    private static final int LIMIT_REMINDERS = 4;

    private ReminderPersistence reminderPersistence;

    private ReminderArrayAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        reminderPersistence = new ReminderPersistence(this);

        List<Reminder> reminders = reminderPersistence.getAllReminders();
        adapter = new ReminderArrayAdapter(this, reminders, this::deleteReminder);

        ListView listView = findViewById(R.id.listView);
        listView.setAdapter(adapter);

        Button buttonActivate = findViewById(R.id.buttonActivate);
        if (isActivated()) {
            ViewGroup parentView = (ViewGroup) buttonActivate.getParent();
            if (parentView != null) {
                parentView.removeView(buttonActivate);
            }
        } else {
            buttonActivate.setOnClickListener(v -> openActivateScreen());
        }

        Button buttonNewReminder = findViewById(R.id.buttonNewReminder);
        buttonNewReminder.setOnClickListener(v -> openNewReminderActivity());
    }

    private void openActivateScreen() {
        LayoutInflater layoutInflater = LayoutInflater.from(this);
        View promptView = layoutInflater.inflate(R.layout.input_dialog, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setView(promptView);

        final EditText editText = promptView.findViewById(R.id.inputEditText);

        alertDialogBuilder.setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        String input = editText.getText().toString();
                        dialog.dismiss();
                        verifyActivation(input);
                    }
                })
                .setNegativeButton("Cancelar",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

        AlertDialog alert = alertDialogBuilder.create();
        alert.show();
    }

    private void verifyActivation(String input) {
        boolean activationOK = false;
        try {
            activationOK = checkSignature(input);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (activationOK) {
            String filePath = ".reminder-activation";

            try {
                writeFile(filePath, input);
            } catch (Exception e) {
                e.printStackTrace();
                activationOK = false;
            }
        }
        if (activationOK) {
            Button buttonActivate = findViewById(R.id.buttonActivate);
            if (isActivated()) {
                ViewGroup parentView = (ViewGroup) buttonActivate.getParent();
                if (parentView != null) {
                    parentView.removeView(buttonActivate);
                }
            }

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Ativado com sucesso!")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // User clicked OK button
                            dialog.dismiss(); // Dismiss the dialog
                        }
                    });
            AlertDialog dialog = builder.create();
            dialog.show();
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Não foi possível ativar!")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // User clicked OK button
                            dialog.dismiss(); // Dismiss the dialog
                        }
                    });
            AlertDialog dialog = builder.create();
            dialog.show();
        }
    }

    private void writeFile(String fileName, String input) throws Exception {
        try (FileOutputStream fos = new FileOutputStream(new File(this.getFilesDir(), fileName))) {
            fos.write(input.getBytes());
        }
    }

    private void deleteReminder(Reminder reminder) {
        adapter.remove(reminder);
        reminderPersistence.deleteReminder(reminder.getId());
        adapter.notifyDataSetChanged();

    }

    private void openNewReminderActivity() {
        if (adapter.getCount() >= LIMIT_REMINDERS && !isActivated()) {
            showNotActivatedWarning();
            return;
        }

        Intent intent = new Intent(this, NewReminderActivity.class);
        startActivityForResult(intent, REQUEST_CODE_NEW_REMINDER);
    }

    private void showNotActivatedWarning() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Você atingiu o limite de " + LIMIT_REMINDERS + ". Para criar mais lembretes, ative seu app!")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User clicked OK button
                        dialog.dismiss(); // Dismiss the dialog
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private boolean isActivated() {
        try {
            String filePath = ".reminder-activation";

            String content = readFile(filePath);

            return checkSignature(content);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private String readFile(String fileName) throws Exception {
        StringBuilder stringBuilder = new StringBuilder();
        try (FileInputStream fis =new FileInputStream(new File(this.getFilesDir(), fileName)) ){
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader bufferedReader = new BufferedReader(isr);
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line).append("\n");
            }
        }
        return stringBuilder.toString();
    }


    public static boolean checkSignature(String input) {
        String[] parts = input.trim().split(":");

        if (parts.length != 2) {
            return false;
        }

        String a = parts[0];
        String b = parts[1];

        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest((a + "_ReminderAppSecret").getBytes());
            String expectedB = Base64.getEncoder().encodeToString(hashBytes);
            return b.equals(expectedB);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_NEW_REMINDER && resultCode == RESULT_OK && data != null) {
            String title = data.getStringExtra("title");
            String text = data.getStringExtra("text");

            Reminder reminder = new Reminder();
            reminder.setTitle(title);
            reminder.setText(text);
            reminderPersistence.addReminder(reminder);

            adapter.add(reminder);
            adapter.notifyDataSetChanged();
        }
    }

}
