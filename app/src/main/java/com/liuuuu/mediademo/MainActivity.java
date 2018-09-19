package com.liuuuu.mediademo;

import android.app.ListActivity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.List;

public class MainActivity extends ListActivity {

    Class[] classes = {TakePicture.class, TakeVideoActivity.class, PreviewActivity.class, RecordActivity.class,
            VideoCaptureActivity.class, PlayActivity.class, VideoActivity.class,
            SoundPoolActivity.class, TiltActivity.class, CompassActivity.class,
            MetadataActivity.class};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String[] activities = {"拍摄照片", "拍摄视频", "自定义摄像头覆盖层", "录制音频", "自定义视频采集",
                "音频播放", "视频播放器", "播放音效", "倾斜监控器", "监控罗盘的方向", "从媒体内容中获取元数据"};
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_list_item_1, android.R.id.text1, activities);
        setListAdapter(arrayAdapter);

    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        Intent intent = new Intent(this, classes[position]);
        startActivity(intent);
    }
}
