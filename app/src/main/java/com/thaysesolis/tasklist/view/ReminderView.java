package com.thaysesolis.tasklist.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.thaysesolis.tasklist.R;
import com.thaysesolis.tasklist.model.Reminder;

import java.time.Instant;
import java.time.ZoneId;

public class ReminderView extends View {
    private View rootView;
    private TextView textViewTitle;
    private TextView textViewText;
    private TextView textViewTimestamp;

    private Runnable deleteAction = null;

    public ReminderView(Context context) {
        this(context, null);
    }

    public ReminderView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        rootView = LayoutInflater.from(context).inflate(R.layout.layout_reminder, null);
        textViewTitle = rootView.findViewById(R.id.textViewTitle);
        textViewText = rootView.findViewById(R.id.textViewText);
        textViewTimestamp = rootView.findViewById(R.id.textViewTimestamp);

        Button button = (Button) rootView.findViewById(R.id.buttonDelete);
        button.setOnClickListener(v -> handleDelete());
    }

    public Runnable getDeleteAction() {
        return deleteAction;
    }

    public void setDeleteAction(Runnable deleteAction) {
        this.deleteAction = deleteAction;
    }

    public void setReminder(Reminder reminder) {
        textViewTitle.setText(reminder.getTitle());
        textViewText.setText(reminder.getText());
        textViewTimestamp.setText(Instant.ofEpochMilli(reminder.getCreationTimestamp()).atZone(ZoneId.systemDefault()).toLocalDateTime().toString());
    }

    public View getView() {
        return rootView;
    }

    private void handleDelete() {
        if (deleteAction != null) {
            deleteAction.run();
        }
    }
}
