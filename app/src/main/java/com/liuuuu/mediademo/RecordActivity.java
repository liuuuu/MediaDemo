package com.liuuuu.mediademo;

import android.Manifest;
import android.content.pm.PackageManager;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.io.File;
import java.io.IOException;

public class RecordActivity extends AppCompatActivity {

    private MediaRecorder recorder;
    private Button start, stop;
    File path;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);

        start = findViewById(R.id.startButton);
        start.setOnClickListener(startListener);

        stop = findViewById(R.id.stopButton);
        stop.setOnClickListener(stopListener);

        recorder = new MediaRecorder();
        path = new File(getExternalFilesDir(Environment.DIRECTORY_DCIM), "myRecording.3gp");
        Log.e("TAG", "FILE路径：" + path.getAbsolutePath());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.RECORD_AUDIO}, 1);
            } else {
                resetRecorder();
            }
        } else {
            resetRecorder();
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    resetRecorder();
                }
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        recorder.release();
    }

    private void resetRecorder() {
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP); // 输出格式 3GP
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
        recorder.setOutputFile(path.getAbsolutePath());
        try {
            recorder.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private View.OnClickListener startListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            try {
                recorder.start();

                start.setEnabled(false);
                stop.setEnabled(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    private View.OnClickListener stopListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            try {

                recorder.stop();
                start.setEnabled(true);
                stop.setEnabled(false);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };
}
