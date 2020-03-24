package com.golan.amit.barbacksitrolltrace;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    public static final String DEBUGTAG = "AMGO";
    public static final boolean DEBUG = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        for dev & debug phase -> to comment
//        demoInsert();

        redirect();
    }

    private void demoInsert() {
        WorkOutDbHelper w = new WorkOutDbHelper(this);
        w.open();
        w.insert("5", "2");
        w.insert("5", "0");
        w.insert("5", "1");
        w.insert("5", "0");
        w.insert("5", "3");
        w.close();
    }

    private void redirect() {
        Intent i = new Intent(this, WorkMainActivity.class);
        startActivity(i);
    }
}
