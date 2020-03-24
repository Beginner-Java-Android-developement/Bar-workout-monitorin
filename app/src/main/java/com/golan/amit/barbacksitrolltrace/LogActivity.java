package com.golan.amit.barbacksitrolltrace;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class LogActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemLongClickListener {

    Button btnBackToMainPage, btnResetDb;
    TextView tvPercentage;
    WorkOutDbHelper wodh;
    ArrayList<WorkOutSession> listOfWorkOutSessions;
    ListView lv;
    WorkOutSessionAdapter workOutSessionAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log);

        init();

        setListeners();

        displayInfo();
    }

    private void displayInfo() {
        wodh.open();
        int totalSum = wodh.totalSum(WorkOutDbHelper.TOTAL_COLUMN);
        int goodSum = wodh.totalSum(WorkOutDbHelper.GOOD_COLUMN);
        double percent = (double) goodSum / totalSum;
        percent *= 100;
        DecimalFormat formatter = new DecimalFormat("#0.00");
        wodh.close();
        String info = String.format("Total: %d, Good: %d, Ratio: %s%%",
                totalSum, goodSum, formatter.format(percent));
        tvPercentage.setText(info);
        if (percent > 55) {
            tvPercentage.setTextColor(Color.BLUE);
        } else {
            tvPercentage.setTextColor(Color.RED);
        }
    }

    private void init() {
        btnBackToMainPage = findViewById(R.id.btnBackToMainPageId);
        btnResetDb = findViewById(R.id.btnResetDbId);
        tvPercentage = findViewById(R.id.tvPercentageId);
        lv = findViewById(R.id.lv);
        wodh = new WorkOutDbHelper(this);
        listOfWorkOutSessions = new ArrayList<WorkOutSession>();

        wodh.open();
        listOfWorkOutSessions = wodh.getAllWorkoutSessions();
        wodh.close();

        workOutSessionAdapter = new WorkOutSessionAdapter(this, 0, listOfWorkOutSessions);
        lv.setAdapter(workOutSessionAdapter);
    }

    private void setListeners() {
        btnBackToMainPage.setOnClickListener(this);
        btnResetDb.setOnClickListener(this);
        lv.setOnItemLongClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if(v == btnBackToMainPage) {
            Intent i = new Intent(this, WorkMainActivity.class);
            startActivity(i);
        } else if(v == btnResetDb) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setPositiveButton("כן", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    wodh.open();
                    wodh.resetTableToScratch();
                    listOfWorkOutSessions = wodh.getAllWorkoutSessions();
                    refreshMyAdapter();
                    wodh.close();
                }
            });
            builder.setNegativeButton("לא", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                }
            });
            builder.setTitle("מחיקת הרשומות");
            builder.setMessage("האם למחוק את הרשומות?");
            AlertDialog dlg = builder.create();
            dlg.show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
//        return super.onOptionsItemSelected(item);
        wodh.open();
        switch (item.getItemId()) {
            case R.id.menu_date_asc:
                listOfWorkOutSessions = wodh.getAllWorkoutSessionsByFilter(null, "curr_datetime ASC");
                refreshMyAdapter();
                break;
            case R.id.menu_date_desc:
                listOfWorkOutSessions = wodh.getAllWorkoutSessionsByFilter(null, "curr_datetime DESC");
                refreshMyAdapter();
                break;
            case R.id.menu_total_attaempts:
                listOfWorkOutSessions = wodh.getAllWorkoutSessionsByFilter(null, "total ASC");
                refreshMyAdapter();
                break;
            case R.id.menu_good_attempts:
                listOfWorkOutSessions = wodh.getAllWorkoutSessionsByFilter("good > 0", "good ASC");
                refreshMyAdapter();
                break;
        }
        wodh.close();
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

        if (MainActivity.DEBUG) {
            Log.v(MainActivity.DEBUGTAG, "raw details, position: " + position + ", id: " + id);
        }

        final WorkOutSession wos = workOutSessionAdapter.getItem(position);
        if (wos == null) {
            Log.e(MainActivity.DEBUGTAG, "picked a null object");
            return true;
        }
        final int realId = wos.getId();
        if (MainActivity.DEBUG) {
            Log.i(MainActivity.DEBUGTAG, "details: " + wos.toString());
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setPositiveButton("כן", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                wodh.open();
                wodh.deleteRecordById(realId);
                listOfWorkOutSessions = wodh.getAllWorkoutSessions();
                wodh.close();
                refreshMyAdapter();
                displayInfo();
            }
        });
        builder.setNegativeButton("לא", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        builder.setTitle("מחיקת רשומה");
        builder.setMessage("האם למחוק את הרשומה?");
        AlertDialog dlg = builder.create();
        dlg.show();
        return true;
    }

    public void refreshMyAdapter() {
        workOutSessionAdapter = new WorkOutSessionAdapter(this, 0, listOfWorkOutSessions);
        lv.setAdapter(workOutSessionAdapter);
    }


}
