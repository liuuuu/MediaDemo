package com.liuuuu.mediademo;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
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
import android.widget.ImageView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TakePicture extends AppCompatActivity {
    private static final int REQUEST_IMAGE = 100;

    Button captureButton;
    ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_take_picture);

        captureButton = findViewById(R.id.capture);
        captureButton.setOnClickListener(listener);

        imageView = findViewById(R.id.image);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE && resultCode == Activity.RESULT_OK) {
            // 获得View的尺寸
            int targetW = imageView.getWidth();
            int targetH = imageView.getHeight();

            // 获得Bitmap的尺寸
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(mCurrentPhotoPath, options);
            int photoW = options.outWidth;
            int photoH = options.outHeight;

            // 确定缩小图像的比例
            int scaleFactor = Math.min(photoW / targetW, photoH / targetH);

            // 将图像文件解码到一个有View大小的位图
            options.inJustDecodeBounds = false;
            options.inSampleSize = scaleFactor;
            options.inPurgeable = true; // 表示系统内存不足时可以被回收

            Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, options);
            imageView.setImageBitmap(bitmap);
        }
    }

    String mCurrentPhotoPath;

    private File createImageFile() throws Exception {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName, // 前缀
                ".jpg", // 后缀
                storageDir // 路径
        );

        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private View.OnClickListener listener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            try {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                File photoFile = null;

                // 确保这个intent是一个 camera activity
                if (intent.resolveActivity(getPackageManager()) != null) {
                    // 创建图片文件
                    try {
                        photoFile = createImageFile();
                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.e("TAG", e.getMessage());
                    }
                    if (photoFile != null) { // 判断图片是否存在
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) { // 版本7.0以上拍照（获取URI的不同）
                            // 7.0以上用 getUriForFile(Context, String, File)
                            // authority 与 AndroidManifest.xml 中 provider.authorities 相同
                            Uri photoUri = FileProvider.getUriForFile(TakePicture.this,
                                    "com.liuuuu.mediademo.fileProvider", photoFile);
                            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                        } else {
                            // 添加附加信息来保存全尺寸的图片
                            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
                        }
                        startActivityForResult(intent, REQUEST_IMAGE);
                    }

                }
            } catch (ActivityNotFoundException e) {
                Log.e("TAG", e.getMessage());
            }
        }
    };

    /**
     * 将图片添加到图库
     */
    private void galleryAddPic() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(mCurrentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        TakePicture.this.sendBroadcast(mediaScanIntent);
    }
}
