package com.liuuuu.mediademo;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.media.ExifInterface;
import android.os.Build;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public class PreviewActivity extends AppCompatActivity implements SurfaceHolder.Callback, Camera.ShutterCallback, Camera.PictureCallback {

    Camera mCamera;
    SurfaceView mPreview;
    private int mDegrees;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview);

        mPreview = findViewById(R.id.preview);
        mPreview.getHolder().addCallback(this);
        // 需要支持Android 3.0 之前版本
        mPreview.getHolder().setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.CAMERA}, 1);
            }
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        mCamera = Camera.open();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mCamera.stopPreview();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mCamera.release();
    }

    public void onCancelClick(View view) {
        finish();
    }

    public void onSnapClick(View view) {
        // 拍摄照片
        mCamera.takePicture(this, null, null, this);
    }

    // Camera回调方法
    @Override
    public void onShutter() {
        Toast.makeText(this, "Click", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPictureTaken(byte[] data, Camera camera) {

        // 将照片保存到某个位置，这里保存到外部存储器中
        String filePath = "";
        try {
            // 获得原始位图
            Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
            // 根据角度旋转摆正
            bitmap = getZeroDegreeBitmap(bitmap, mDegrees);

            // 输出文件
            File photoFile = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), "picture.jpg");
            filePath = photoFile.getAbsolutePath();

            Log.i("TAG", "file: " + photoFile.getAbsolutePath());

            // 输出文件流
            FileOutputStream out = new FileOutputStream(photoFile);
            // 设置响应的输出质量 quality 100%质量输出
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // 必须重启预览
        camera.startPreview();


        readPictureDegree(filePath);
    }

    /**
     * 使用矩阵方程旋转位图
     *
     * @param srcBitmap
     * @param degree
     * @return
     */
    private Bitmap getZeroDegreeBitmap(Bitmap srcBitmap, int degree) {
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);
        Bitmap bitmap = null;
        bitmap = Bitmap.createBitmap(srcBitmap, 0, 0, srcBitmap.getWidth(), srcBitmap.getHeight(), matrix, true);
        return bitmap;
    }

    /**
     * 获取图片旋转角度
     *
     * @param path
     * @return
     */
    private int readPictureDegree(String path) {
        int degree = 0;
        try {
            ExifInterface exif = new ExifInterface(path);
//            exif.setAttribute(ExifInterface.TAG_ORIENTATION, String.valueOf(ExifInterface.ORIENTATION_ROTATE_90));
            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
                default:
                    degree = 0;
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.e("TAG", "图片角度：" + degree + "°");
        return degree;
    }


    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        Camera.Parameters params = mCamera.getParameters();
        // 得到设备支持的尺寸，并选择第一个尺寸（最大）
        List<Camera.Size> sizes = params.getSupportedPreviewSizes();
        Camera.Size selected = sizes.get(0);
        params.setPreviewSize(selected.width, selected.height);
        mCamera.setParameters(params);

        // 修正预览图片角度
        mDegrees = setCameraDisplayOrientation(this, 0, mCamera);
        // 修正输出图片角度
        params.setRotation(mDegrees);
        mCamera.setParameters(params);
        mCamera.startPreview();
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        try {
            mCamera.setPreviewDisplay(mPreview.getHolder());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
    }

    /**
     * 修正相机预览方向
     *
     * @param activity
     * @param cameraId 0 到 Camera.getNumberOfCameras()-1 之间
     * @param camera
     */
    public static int setCameraDisplayOrientation(Activity activity, int cameraId, Camera camera) {

        Camera.CameraInfo info = new Camera.CameraInfo();
        Camera.getCameraInfo(cameraId, info);
        int rotation = activity.getWindowManager().getDefaultDisplay().getRotation();
        Log.i("TAG", "rotation = " + rotation);
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0:
                degrees = 0;
                break;
            case Surface.ROTATION_90:
                degrees = 90;
                break;
            case Surface.ROTATION_180:
                degrees = 180;
                break;
            case Surface.ROTATION_270:
                degrees = 270;
                break;
        }

        int result;
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % 360;
            result = (360 - result) % 360;// 补偿镜
        } else {
            result = (info.orientation - degrees + 360) % 360;
        }
        camera.setDisplayOrientation(result);

        Log.i("TAG", "rotation:result = " + result);
        return result;
    }
}
