package com.liuuuu.mediademo;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.VideoView;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TakeVideoActivity extends AppCompatActivity {

    private static final int REQUEST_VIDEO = 100;

    Button captureButton;
    TextView text;
    File destination;
    VideoView mVideoView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_take_video);

        captureButton = findViewById(R.id.capture);
        captureButton.setOnClickListener(listener);

        text = findViewById(R.id.file);

        mVideoView = findViewById(R.id.mVideoView);

        try {
            destination = createVideoFile();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_VIDEO && resultCode == Activity.RESULT_OK) {
            String location = data.getData().toString();
            mVideoView.setVideoURI(data.getData());
            mVideoView.start();
            text.setText(location);
        }
    }

    /**
     * 创建视频文件
     * file:///storage/emulated/0/Android/data/com.liuuuu.mediademo/files/Movies/filename.mp3
     * @return
     * @throws Exception
     */
    private File createVideoFile() throws Exception {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String videoFileName = "VIDEO_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_MOVIES);
        File video = File.createTempFile(
                videoFileName, // 前缀
                ".mp3", // 后缀
                storageDir // 路径
        );
        return video;
    }

    private View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            try {
                Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    Uri videoUri = FileProvider.getUriForFile(TakeVideoActivity.this,
                            "com.liuuuu.mediademo.fileProvider", destination);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, videoUri);
                } else {
                    // 添加(可选)附加信息已将视频保存到指定文件
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(destination)); // 7.0版本后出错
                }
                // 可选的附加信息用来设置视频质量
                intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 0);
                TakeVideoActivity.this.startActivityForResult(intent, REQUEST_VIDEO);
            } catch (ActivityNotFoundException e) {
                Log.e("TAG", e.getMessage());
            }
        }
    };
}
