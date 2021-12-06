package com.example.semestr;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    Button alarm_button;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm dd:MM:yyyy", Locale.getDefault());
        alarm_button = findViewById(R.id.alarm_button);
        alarm_button.setOnClickListener(v -> {
            MaterialTimePicker materialTimePicker = new MaterialTimePicker.Builder()
                    .setTimeFormat(TimeFormat.CLOCK_24H)
                    .setHour(12)
                    .setMinute(0)
                    .setTitleText("Выберите время")
                    .build();

            materialTimePicker.addOnPositiveButtonClickListener(view -> {
                MaterialDatePicker.Builder<Long> builder = MaterialDatePicker.Builder.datePicker();
                builder.setTitleText("Choose date");
                MaterialDatePicker<Long> picker = builder.build();
                picker.show(getSupportFragmentManager(), picker.toString());
                picker.addOnPositiveButtonClickListener(selection -> {
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTimeInMillis(selection);
                    calendar.set(Calendar.SECOND, 0);
                    calendar.set(Calendar.MILLISECOND, 0);
                    calendar.set(Calendar.MINUTE, materialTimePicker.getMinute());
                    calendar.set(Calendar.HOUR_OF_DAY, materialTimePicker.getHour());
                    LinearLayout layout = findViewById(R.id.layout);
                    int childcount = layout.getChildCount();
                    boolean finish = false;
                    for (int i = 0; i < childcount && !finish; i++){
                        View text = layout.getChildAt(i);
                        if( text instanceof TextView ) {
                            TextView textView = (TextView) text;
                            if (textView.getText().length() == 0){
                                textView.setText(sdf.format(calendar.getTime()));
                                finish = true;
                            }
                        }
                    }
                    if (finish){
                        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

                        AlarmManager.AlarmClockInfo alarmClockInfo = new AlarmManager.AlarmClockInfo(calendar.getTimeInMillis(),
                                getAlarmInfoPendingIntent());

                        alarmManager.setAlarmClock(alarmClockInfo, getAlarmActionPendingIntent());
                        Toast.makeText(this, "Будильник установлен на " + sdf.format(calendar.getTime()), Toast.LENGTH_SHORT).show();
                    }
                    else
                        Toast.makeText(this, "Лимит превышен!", Toast.LENGTH_SHORT).show();
                });
            });
            materialTimePicker.show(getSupportFragmentManager(), "tag_picker");
        });
    }

    private PendingIntent getAlarmInfoPendingIntent() {
        Intent alarmInfoIntent = new Intent(this, MainActivity.class);
        alarmInfoIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        return PendingIntent.getActivity(this, 0, alarmInfoIntent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private PendingIntent getAlarmActionPendingIntent() {
        Intent intent = new Intent(this, AlarmActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        return PendingIntent.getActivity(this, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }
}