package com.liuuuu.mediademo;

import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.widget.ImageView;
import android.widget.MediaController;

public class PlayActivity extends AppCompatActivity implements MediaController.MediaPlayerControl, MediaPlayer.OnBufferingUpdateListener {


    MediaController mController;
    MediaPlayer mPlayer;
    ImageView coverImage;

    int bufferPercent = 0 ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);

        coverImage = findViewById(R.id.coverImage);

        mController = new MediaController(this);
        mController.setAnchorView(findViewById(R.id.root));
    }

    @Override
    protected void onResume() {
        super.onResume();
        mPlayer = new MediaPlayer();
        // 设置音频数据源
        try {
            mPlayer.setDataSource("/sdcard/Android/data/com.liuuuu.mediademo/files/DCIM/myRecording.3gp");
            mPlayer.prepare();
        } catch (Exception e) {
            e.printStackTrace();
        }
        // 设置专辑封面图片
        coverImage.setImageResource(R.drawable.ic_launcher_background);

        mController.setMediaPlayer(this);
        mController.setEnabled(true);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mPlayer.release();
        mPlayer = null;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mController.show();
        return super.onTouchEvent(event);
    }

    // MediaPlayerControl 方法
    @Override
    public int getBufferPercentage() {
        return bufferPercent;
    }

    @Override
    public int getCurrentPosition() {
        return mPlayer.getCurrentPosition();
    }

    @Override
    public int getDuration() {
        return mPlayer.getDuration();
    }

    @Override
    public boolean isPlaying() {
        return mPlayer.isPlaying();
    }

    @Override
    public void pause() {
        mPlayer.pause();
    }

    @Override
    public void seekTo(int pos) {
        mPlayer.seekTo(pos);
    }

    @Override
    public void start() {
        mPlayer.start();
    }

    // BufferUpdateListener 方法
    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {
        bufferPercent = percent;
    }

    // Android 2.0+ 目标回调
    public boolean canPause(){
        return true;
    }

    @Override
    public boolean canSeekBackward() {
        return true;
    }

    @Override
    public boolean canSeekForward() {
        return true;
    }

    // Android 4.3+目标回调
    @Override
    public int getAudioSessionId() {
        return mPlayer.getAudioSessionId();
    }

}
