package com.liuuuu.mediademo;

import android.content.res.AssetManager;
import android.media.AudioManager;
import android.media.SoundPool;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.SparseIntArray;
import android.view.View;
import android.widget.Toast;

import java.io.IOException;

public class SoundPoolActivity extends AppCompatActivity implements View.OnClickListener{

    private AudioManager mAudioManager;
    private SoundPool mSoundPool;
    private SparseIntArray mSoundMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sound_pool);
        // 得到 AudioManager 系统服务
        mAudioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        // 设置声音池，通过标准的扬声器输出每次只播放一个音频
        mSoundPool = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);

        findViewById(R.id.button_beep1).setOnClickListener(this);
        findViewById(R.id.button_beep2).setOnClickListener(this);
        findViewById(R.id.button_beep3).setOnClickListener(this);

        // 加载每个音频并把他们的 streamId 保存到一个 Map 中
        mSoundMap = new SparseIntArray();
        AssetManager manager = getAssets();
        try {
            int streamId;
            streamId = mSoundPool.load(manager.openFd("Beep1.ogg"), 1);
            mSoundMap.put(R.id.button_beep1, streamId);

            streamId = mSoundPool.load(manager.openFd("Beep2.ogg"), 1);
            mSoundMap.put(R.id.button_beep2, streamId);

            streamId = mSoundPool.load(manager.openFd("Beep3.ogg"), 1);
            mSoundMap.put(R.id.button_beep3, streamId);

        } catch (IOException e) {
            Toast.makeText(this, "Error Loading Sound Effects", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mSoundPool.release();
        mSoundPool = null;
    }

    @Override
    public void onClick(View v) {
        // 查找适合的音频ID
        int streamId = mSoundMap.get(v.getId());
        if (streamId > 0) {
            float streamVolumeCurrent = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
            float streamVolumeMax = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
            float volume = streamVolumeCurrent / streamVolumeMax;
            // 使用指定的音量播放音频，不循环播放并且使用标准的播放速度
            mSoundPool.play(streamId, volume, volume, 1, 0, 1.0f);
        }
    }
}
