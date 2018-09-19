package com.liuuuu.mediademo;

import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class TiltActivity extends AppCompatActivity implements SensorEventListener {

    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private TextView valueView;
    private View mTop, mBottom, mLeft, mRight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tilt);

        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        valueView = findViewById(R.id.values);
        mTop = findViewById(R.id.top);
        mBottom = findViewById(R.id.bottom);
        mLeft = findViewById(R.id.left);
        mRight = findViewById(R.id.right);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        float[] values = event.values;
        float x = values[0] / 10;
        float y = values[1] / 10;
        int scaleFactor;

        if (x > 0) {
            scaleFactor = (int) Math.min(x * 255, 255);
            mRight.setBackgroundColor(Color.TRANSPARENT);
            mLeft.setBackgroundColor(Color.argb(scaleFactor, 255, 0, 0));
        } else {
            scaleFactor = (int) Math.min(Math.abs(x) * 255, 255);
            mRight.setBackgroundColor(Color.argb(scaleFactor, 255, 0, 0));
            mLeft.setBackgroundColor(Color.TRANSPARENT);
        }

        if (y > 0) {
            scaleFactor = (int) Math.min(y * 255, 255);
            mTop.setBackgroundColor(Color.TRANSPARENT);
            mBottom.setBackgroundColor(Color.argb(scaleFactor, 255, 0, 0));
        } else {
            scaleFactor = (int) Math.min(Math.abs(y) * 255, 255);
            mTop.setBackgroundColor(Color.argb(scaleFactor, 255, 0, 0));
            mBottom.setBackgroundColor(Color.TRANSPARENT);
        }
        // 显示原始值
        valueView.setText(String.format("X: %1$1.2f, Y: %2$1.2f, Z: %3$1.2f", values[0], values[1], values[2]));
    }
}
