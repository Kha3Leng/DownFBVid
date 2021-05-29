package com.example.downfbvid.Activity;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.downfbvid.Adapter.VideoAdapter;
import com.example.downfbvid.R;
import com.example.downfbvid.Simple.Video;

import java.util.ArrayList;


public class DownloadVideoActivity extends AppCompatActivity {

    RecyclerView mRecyclerView;
    ArrayList<Video> videoArrayList;
    VideoAdapter videoAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download_video);

        mRecyclerView = findViewById(R.id.videoRecyclerView);
        videoAdapter = new VideoAdapter(this, videoArrayList);
        mRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        mRecyclerView.setAdapter(videoAdapter);

    }
}