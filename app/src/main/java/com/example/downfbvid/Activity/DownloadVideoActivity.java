package com.example.downfbvid.Activity;

import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.downfbvid.Adapter.VideoAdapter;
import com.example.downfbvid.Interface.OnItemClickListener;
import com.example.downfbvid.R;
import com.example.downfbvid.Simple.Video;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.gson.Gson;

import java.util.ArrayList;

import es.dmoral.toasty.Toasty;


public class DownloadVideoActivity extends AppCompatActivity {

    RecyclerView mRecyclerView;
    public ArrayList<Video> videoArrayList;
    VideoAdapter videoAdapter;
    AdView adView1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
//        getSupportActionBar().setCustomView(R.layout.download_title_action_bar)
//        getSupportActionBar().setTitle((Html.fromHtml("<font color=\"#FFFFFF\">" + "Downloaded Video" + "</font>")));
        setContentView(R.layout.activity_download_video);
        adView1 = findViewById(R.id.adView1);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView1.loadAd(adRequest);
        videoArrayList = new ArrayList<>();

        loadingVideoData();

        mRecyclerView = findViewById(R.id.videoRecyclerView);
        videoAdapter = new VideoAdapter(this, videoArrayList, new OnItemClickListener() {
            @Override
            public void onItemClick(Video video) {
                Toasty.info(getApplicationContext(), "You choose " + video.getTitle(), Toasty.LENGTH_LONG).show();
//                startActivity(new Intent(getApplicationContext(), VideoPlayerActivity.class));
                Intent intent = new Intent(getApplicationContext(), VideoPlayerActivity.class);
                Gson gson = new Gson();
                String videoJson = gson.toJson(video);
                intent.putExtra("video", videoJson);

                startActivity(intent);
            }
        });
        mRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        mRecyclerView.setAdapter(videoAdapter);

    }

    /*@Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.setHeaderTitle("Select Actions");
        menu.add(0, v.getId(), 0, "Share This Video");
        menu.add(0, v.getId(), 0, "Delete This Video");
        menu.add(0, v.getId(), 0, "Play This Video");
    }*/

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

            String contentUri = ContentUris.withAppendedId(collection, id).toString();

            Bitmap thumbnail = MediaStore.Video.Thumbnails.getThumbnail(getContentResolver(), id, MediaStore.Video.Thumbnails.MICRO_KIND, null);

            videoArrayList.add(new Video(id, title, contentUri, duration, size/*, thumbnail*/));
        }
    }


}