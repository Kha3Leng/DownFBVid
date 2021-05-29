package com.example.downfbvid.Activity;

import android.content.ContentUris;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;

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

        videoArrayList = new ArrayList<>();

        loadingVideoData();

        mRecyclerView = findViewById(R.id.videoRecyclerView);
        videoAdapter = new VideoAdapter(this, videoArrayList);
        mRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        mRecyclerView.setAdapter(videoAdapter);

    }

    private void loadingVideoData() {
        System.gc();

        Uri collection = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
        String[] projections = {MediaStore.Video.Media._ID,
                MediaStore.Video.Media.DATA,
                MediaStore.Video.Media.DISPLAY_NAME,
                MediaStore.Video.Media.SIZE,
                MediaStore.Video.Media.DURATION};
        String selections = MediaStore.Video.Media.DATA + " like ?";
        String[] selectionArgs = new String[]{"%FBVid%"};
        String sortOrder = MediaStore.Video.Media.DATE_TAKEN + " DESC";

        Cursor videoCursor = getApplicationContext().getContentResolver().query(
                collection,
                projections,
                selections, selectionArgs,
                sortOrder);

        int idCol = videoCursor.getColumnIndexOrThrow(MediaStore.Video.Media._ID);
        int titleCol = videoCursor.getColumnIndexOrThrow(MediaStore.Video.Media.DISPLAY_NAME);
        int durationCol = videoCursor.getColumnIndexOrThrow(MediaStore.Video.Media.DURATION);
        int sizeCol = videoCursor.getColumnIndexOrThrow(MediaStore.Video.Media.SIZE);

        while(videoCursor.moveToNext()){
            long id = videoCursor.getLong(idCol);
            String title = videoCursor.getString(titleCol);
            int duration = videoCursor.getInt(durationCol);
            int size = videoCursor.getInt(sizeCol);

            Uri contentUri = ContentUris.withAppendedId(collection, id);

            Bitmap thumbnail = MediaStore.Video.Thumbnails.getThumbnail(getContentResolver(), id, MediaStore.Video.Thumbnails.MICRO_KIND, null);

            videoArrayList.add(new Video(title, contentUri, duration, size/*, thumbnail*/));
        }
    }


}