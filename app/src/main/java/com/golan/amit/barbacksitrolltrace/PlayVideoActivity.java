package com.golan.amit.barbacksitrolltrace;

import android.os.strictmode.WebViewMethodCalledOnWrongThreadViolation;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.MediaController;
import android.widget.VideoView;

public class PlayVideoActivity extends AppCompatActivity implements View.OnClickListener {

    VideoView video;
    MediaController mediaController;
    Button btnGoBackFromVideo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_video);

        init();

        setListeners();

        play();
    }

    private void setListeners() {
        btnGoBackFromVideo.setOnClickListener(this);
    }

    private void play() {
        video.start();
        video.requestFocus();
    }

    private void init() {
        video = (VideoView) findViewById(R.id.vvBarVideoId);
        mediaController = new MediaController(this);
        mediaController.setAnchorView(video);
        video.setMediaController(mediaController);
        video.setKeepScreenOn(true);
        video.setVideoPath("android.resource://" + getPackageName() + "/" + R.raw.bar_back_rollover);

        btnGoBackFromVideo = findViewById(R.id.btnVideoGoBackId);
    }

    @Override
    public void onClick(View v) {
        if (v == btnGoBackFromVideo) {
            finish();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        video.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (video != null) {
            try {
                video.start();
            } catch (Exception e) {
            }
        }
    }
}
