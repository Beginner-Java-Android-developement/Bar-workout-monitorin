package com.golan.amit.barbacksitrolltrace;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.Toast;

public class CalendarActivity extends AppCompatActivity implements View.OnClickListener, CalendarView.OnDateChangeListener {

    CalendarView calendarView;
    Button btnFinishCalendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);

        calendarView = findViewById(R.id.simpleCalendarView);
        calendarView.setOnDateChangeListener(this);
        btnFinishCalendar = findViewById(R.id.btnFinishCalendarId);

        btnFinishCalendar.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if(v == btnFinishCalendar) {
            finish();   //  TODO return data
//            Intent i = new Intent(this, WorkMainActivity.class);
//            startActivity(i);
        }
    }

    @Override
    public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {
        Toast.makeText(getApplicationContext(), dayOfMonth + "/" + month + "/" + year, Toast.LENGTH_LONG).show();
    }
}
