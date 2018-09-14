package com.liuuuu.mediademo;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.Image;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

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
            // 处理并显示照片
            Bitmap userImage = (Bitmap) data.getExtras().get("data");
            imageView.setImageBitmap(userImage);
        }
    }

    private View.OnClickListener listener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            try {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, REQUEST_IMAGE);
            } catch (ActivityNotFoundException e) {
                Log.e("TAG", e.getMessage());
            }
        }
    };
}
