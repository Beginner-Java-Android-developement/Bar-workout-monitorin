package com.golan.amit.barbacksitrolltrace;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

public class WorkMainActivity extends AppCompatActivity implements View.OnClickListener, SeekBar.OnSeekBarChangeListener, View.OnLongClickListener {

    TextView tvDisplay;
    EditText etTotal, etGood;
    Button btnSubmit, btnLogDisplay, btnCalendar;
    ImageView ivBar;
    WorkOutSession wos;

    Animation animScaleInOut;

    MediaPlayer mp;
    SeekBar sb;
    AudioManager am;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_work_main);

        init();

        setListeners();

        play();
    }

    private void play() {
        WorkOutDbHelper wodh = new WorkOutDbHelper(this);
        wodh.open();
        wos = wodh.lastRecord();
        if(wos != null) {
            String infoStr = String.format("%d Good practices, out of %d total sets\non day: %s",
                    wos.getGood(), wos.getTotal(), wos.getCurr_datetime());

            StringBuilder sb = new StringBuilder();

            sb.append("אימון אחרון: ");
            sb.append(wos.getCurr_datetime());
            sb.append("\n");
            sb.append("נסיונות מוצלחים: ");
            sb.append(wos.getGood());
            sb.append("\n");
            sb.append("מתוך: ");
            sb.append(wos.getTotal());
            sb.append(" ");
            sb.append("סך הכל");
            tvDisplay.setText(sb.toString());
        }
        wodh.close();
        ivBar.setAnimation(animScaleInOut);
    }

    private void setListeners() {
        btnSubmit.setOnClickListener(this);
        btnLogDisplay.setOnClickListener(this);
        btnCalendar.setOnClickListener(this);
        ivBar.setOnLongClickListener(this);
    }

    private void init() {
        tvDisplay = findViewById(R.id.tvDisplayInfoId);
        etGood = findViewById(R.id.etGoodId);
        etTotal = findViewById(R.id.etTotalId);
        etTotal.requestFocus();
        btnSubmit = findViewById(R.id.btnSubmitId);
        btnLogDisplay = findViewById(R.id.btnLogDisplayId);
        btnCalendar = findViewById(R.id.btnCalendarId);
        ivBar = findViewById(R.id.ivBackPicId);
        animScaleInOut = AnimationUtils.loadAnimation(this, R.anim.anim_scale_inout);

        sb = findViewById(R.id.sb);
        mp = MediaPlayer.create(this, R.raw.austine);
        mp.start();

        am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        int max = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        sb.setMax(max);
        sb.setProgress(max / 4);
        am.setStreamVolume(AudioManager.STREAM_MUSIC, max / 4, 0);
        sb.setOnSeekBarChangeListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v == btnSubmit) {
            if (verifyUserInput()) {

                try {
                    WorkOutDbHelper wd = new WorkOutDbHelper(this);
                    wd.open();
                    wd.insert(etTotal.getText().toString(), etGood.getText().toString());
                    wd.close();
                    Toast.makeText(this, "input inserted to database", Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    Toast.makeText(this, "failed to insert input to database", Toast.LENGTH_SHORT).show();
                    Log.e(MainActivity.DEBUGTAG, "exception when inserting input to db " + e);
                }

                clearEditTexts();
            } else {
                clearEditTexts();
            }
        } else if (v == btnLogDisplay) {
            Toast.makeText(this, "Clicked", Toast.LENGTH_SHORT).show();
            Intent i = new Intent(this, LogActivity.class);
            startActivity(i);
        } else if(v == btnCalendar) {
            Intent i = new Intent(this, CalendarActivity.class);
            startActivity(i);
        }
    }

    private void clearEditTexts() {
        etGood.setText("");
        etTotal.setText("");
    }

    private boolean verifyUserInput() {
        if (etTotal.getText() == null || etTotal.getText().toString().equalsIgnoreCase("")) {
            Log.e(MainActivity.DEBUGTAG, "total is empty");
            Toast.makeText(this, "הוכנס ערך ריק", Toast.LENGTH_SHORT).show();
            return false;
        }
        int totalInt = -1;
        try {
            totalInt = Integer.parseInt(etTotal.getText().toString());
        } catch (Exception e) {
            Log.e(MainActivity.DEBUGTAG, "total is illegal number");
            Toast.makeText(this, "הוכנס ערך בלתי חוקי", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (totalInt < 1) {
            Log.e(MainActivity.DEBUGTAG, "total must be over 0");
            return false;
        }

        if (etGood.getText() == null || etGood.getText().toString().equalsIgnoreCase("")) {
            Log.e(MainActivity.DEBUGTAG, "good is empty");
            Toast.makeText(this, "הוכנס ערך ריק", Toast.LENGTH_SHORT).show();
            return false;
        }
        int goodInt = -1;
        try {
            goodInt = Integer.parseInt(etGood.getText().toString());
        } catch (Exception e) {
            Log.e(MainActivity.DEBUGTAG, "good is illegal number");
            Toast.makeText(this, "הוכנס ערך בלתי חוקי", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (goodInt > totalInt) {
            Log.e(MainActivity.DEBUGTAG, "good practices can't be more than total attempts");
            Toast.makeText(this, "מספר ההצלחות חייב להיות קטן או שווה למספר הנסיונות הכולל", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        am.setStreamVolume(AudioManager.STREAM_MUSIC, progress, 0);
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mp != null) {
            try {
                mp.start();
            } catch (Exception e) {
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        mp.pause();
    }

    @Override
    public boolean onLongClick(View v) {
        if(v == ivBar) {
            Intent i = new Intent(this, PlayVideoActivity.class);
            startActivity(i);
        }
        return true;
    }
}
