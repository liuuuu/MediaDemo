package com.liuuuu.mediademo;

import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;

import java.io.File;
import java.io.IOException;

public class VideoCaptureActivity extends AppCompatActivity implements SurfaceHolder.Callback {

    private Camera mCamera;
    private MediaRecorder mRecorder;

    private SurfaceView mPreview;
    private Button mRecordButton;

    private boolean mRecording = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_capture);

        mRecordButton = findViewById(R.id.button_record);
        mRecordButton.setText("开始录制");

        mPreview = findViewById(R.id.surface_video);
        mPreview.getHolder().addCallback(this);
        mPreview.getHolder().setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        mCamera = Camera.open();

        // 旋转预览画面为竖屏
        mCamera.setDisplayOrientation(90);
        mRecorder = new MediaRecorder();
    }

    @Override
    protected void onDestroy() {
        mCamera.release();
        mCamera = null;
        super.onDestroy();
    }

    public void onRecordClick(View view) {
        updateRecordingState();
    }

    /**
     * 初始化摄像头和摄像机
     * 这些方法的顺序很重要，因为 MediaRecorder 的状态严格依赖于每一个调用方法
     *
     * @throws IllegalStateException
     * @throws IOException
     */
    private void initializeRecorder() throws IllegalStateException, IOException {
        // 解锁摄像头，允许MediaRecorder 使用它
        mCamera.unlock();
        mRecorder.setCamera(mCamera);

        // 设置MediaRecorder 的数据源
        mRecorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
        mRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);

        // 更新输出设置
        File recordOutput = new File(Environment.getExternalStorageDirectory(), "recorded_video.mp4");
        if (recordOutput.exists()) {
            recordOutput.delete();
        }
        Log.i("***TAG***", "file:" + recordOutput.getAbsolutePath());

        CamcorderProfile cpHigh = CamcorderProfile.get(CamcorderProfile.QUALITY_HIGH);
        mRecorder.setProfile(cpHigh);
        mRecorder.setOutputFile(recordOutput.getAbsolutePath());
        // 为摄像机关联一个 Surface，从而实现在录制时同时预览
        mRecorder.setPreviewDisplay(mPreview.getHolder().getSurface());

        // 设置录制的一些限制值，这些值是可以选的
        mRecorder.setMaxDuration(50000); // 50秒
        mRecorder.setMaxFileSize(5000000); // 5MB
        mRecorder.setOrientationHint(90); // 输出格式方向

        mRecorder.prepare();
    }

    private void updateRecordingState() {
        if (mRecording) {
            mRecording = false;
            // 重置摄像机的状态以便进行下次录制
            mRecorder.stop();
            mRecorder.reset();
            // 返回摄像机继续预览
            mCamera.lock();
            mRecordButton.setText("开始录制");
        } else {
            try {
                // 重置摄像机以便下次会话
                initializeRecorder();
                // 开始录制
                mRecording = true;
                mRecorder.start();
                mRecordButton.setText("停止录制");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        // 得到一个 Surface 后，立刻启动摄像头预览
        try {
            mCamera.setPreviewDisplay(holder);
            mCamera.startPreview();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }
}
