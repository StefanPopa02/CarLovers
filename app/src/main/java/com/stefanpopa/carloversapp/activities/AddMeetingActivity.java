package com.stefanpopa.carloversapp.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.stefanpopa.carloversapp.R;
import com.stefanpopa.carloversapp.model.LatLng;
import com.stefanpopa.carloversapp.model.Meeting;
import com.stefanpopa.carloversapp.model.MyCalendar;
import com.stefanpopa.carloversapp.util.UserApi;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class AddMeetingActivity extends AppCompatActivity {

    private MaterialEditText datetime;
    private MaterialEditText description;
    private TextView address;
    private ImageView close;
    private TextView add;
    private FirebaseFirestore db;
    private Calendar calendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_meeting);
        db = FirebaseFirestore.getInstance();
        close = findViewById(R.id.meeting_close);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        add = findViewById(R.id.add_meeting);
        address = findViewById(R.id.address);
        description = findViewById(R.id.description);
        datetime = findViewById(R.id.edit_text_date);
        datetime.setInputType(InputType.TYPE_NULL);
        datetime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDateTimeDialog(datetime);
            }
        });
        MarkerOptions marker = UserApi.getInstance().getMarker();
        address.setText(marker.getTitle());
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addNewMeeting(marker);
            }
        });
    }

    private void addNewMeeting(MarkerOptions marker) {
        if (TextUtils.isEmpty(datetime.getText().toString()) || TextUtils.isEmpty(description.getText().toString())) {
            Toast.makeText(this, "No empty fields allowed!", Toast.LENGTH_SHORT).show();
        } else {
            Meeting newMeeting = new Meeting();
            newMeeting.setDateAndTime(datetime.getText().toString());
            newMeeting.setDescription(description.getText().toString());
            newMeeting.setUserId(FirebaseAuth.getInstance().getUid());
            newMeeting.setTimestamp(null);
            newMeeting.setLatLng(new LatLng(marker.getPosition().latitude, marker.getPosition().longitude));
            newMeeting.setAddress(marker.getTitle());
            MyCalendar myCalendar = new MyCalendar();
            myCalendar.setTimeInMillis(calendar.getTimeInMillis());
            newMeeting.setCalendar(myCalendar);

            db.collection("Meetings").add(newMeeting);
            Toast.makeText(this, "Meeting added!", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void showDateTimeDialog(final EditText date_time_in) {
        calendar = Calendar.getInstance();
        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, month);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                TimePickerDialog.OnTimeSetListener timeSetListener = new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        calendar.set(Calendar.MINUTE, minute);

                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yy-MM-dd HH:mm");

                        date_time_in.setText(simpleDateFormat.format(calendar.getTime()));
                    }
                };

                new TimePickerDialog(AddMeetingActivity.this, timeSetListener, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), false).show();
            }
        };
        new DatePickerDialog(AddMeetingActivity.this, dateSetListener, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
    }

}