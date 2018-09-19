package com.liuuuu.mediademo;

import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.MediaController;
import android.widget.VideoView;

public class VideoActivity extends AppCompatActivity {

    VideoView videoView;
    MediaController controller;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        videoView = new VideoView(this);

        // remote: 添加 INTERNET 权限
//        videoView.setVideoURI(Uri.parse(""));
        videoView.setVideoPath("/storage/sdcard/recorded_video.mp4");
        controller = new MediaController(this);
        videoView.setMediaController(controller);
        videoView.start();

        setContentView(videoView);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        videoView.stopPlayback();
    }
}
