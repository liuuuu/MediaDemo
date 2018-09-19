package com.liuuuu.mediademo;

import android.net.Uri;
import android.os.AsyncTask;
import android.widget.VideoView;

import java.net.HttpURLConnection;
import java.net.URL;

/**
 * VideoView videoView = new VideoView(this);
 * RedirectTracerTask task = new RedirectTracerTask(videoView);
 * Uri location = Uri.parse("URI_TO_REMOTE_VIDEO);
 *
 * task.execute(location);
 * 通过执行，将URL重定向的最终URI传递给VideoView
 */
public class RedirectTracerTask extends AsyncTask<Uri, Void, Uri> {

    private VideoView mVideo;
    private Uri initialUri;

    public RedirectTracerTask(VideoView video) {
        super();
        this.mVideo = video;
    }

    @Override
    protected Uri doInBackground(Uri... params) {
        initialUri = params[0];
        String redirected = null;
        try {
            URL url = new URL(initialUri.toString());
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            // 连接后会追踪最终地址
            redirected = connection.getHeaderField("Location");
            return Uri.parse(redirected);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected void onPostExecute(Uri result) {
        if (result != null) {
            mVideo.setVideoURI(result);
        } else {
            mVideo.setVideoURI(initialUri);
        }
    }
}
