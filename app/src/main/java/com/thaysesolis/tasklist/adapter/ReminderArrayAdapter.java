package com.thaysesolis.tasklist.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.thaysesolis.tasklist.model.Reminder;
import com.thaysesolis.tasklist.view.ReminderView;

import java.util.List;
import java.util.function.Consumer;

public class ReminderArrayAdapter extends ArrayAdapter<Reminder> {

    private final Consumer<Reminder> deleter;

    public ReminderArrayAdapter(Context context, List<Reminder> reminders, Consumer<Reminder> deleter) {
        super(context, 0, reminders);
        this.deleter = deleter;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Reminder reminder = getItem(position);

        ReminderView reminderView;
        if (convertView instanceof ReminderView){
            reminderView = (ReminderView) convertView;
        } else {
            reminderView = new ReminderView(getContext());
        }

        reminderView.setReminder(reminder);
        reminderView.setDeleteAction(() -> deleter.accept(reminder));

        return reminderView.getView();
    }
}
