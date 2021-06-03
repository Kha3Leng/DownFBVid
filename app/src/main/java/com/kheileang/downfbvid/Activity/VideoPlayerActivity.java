package com.kheileang.downfbvid.Activity;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.widget.MediaController;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;

import com.kheileang.downfbvid.R;
import com.kheileang.downfbvid.Simple.Video;
import com.google.gson.Gson;

import es.dmoral.toasty.Toasty;

public class VideoPlayerActivity extends AppCompatActivity {

    private VideoView videoView;
    private MediaController mediaController;
    private Video video;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        System.gc();
        super.onCreate(savedInstanceState);
        // From JSON to Object
        Gson gson = new Gson();
        video = gson.fromJson(getIntent().getStringExtra("video"), Video.class);

        setContentView(R.layout.activity_video_player);
        setTitle(video.getTitle());;

        videoView = findViewById(R.id.video_player);
        mediaController = new MediaController(this);

        mediaController.setAnchorView(videoView);
        videoView.setMediaController(mediaController);
        videoView.setVideoURI(Uri.parse(video.getPath()));
        videoView.requestFocus();
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mediaController.setAnchorView(videoView);
                videoView.start();
            }
        });
        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                Toasty.info(getApplicationContext(), video.getTitle()+ " finished.", Toasty.LENGTH_SHORT).show();
            }
        });


    }
}