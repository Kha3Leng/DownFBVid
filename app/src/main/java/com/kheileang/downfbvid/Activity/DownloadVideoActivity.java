package com.kheileang.downfbvid.Activity;

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

import com.kheileang.downfbvid.Adapter.VideoAdapter;
import com.kheileang.downfbvid.Interface.OnItemClickListener;
import com.kheileang.downfbvid.R;
import com.kheileang.downfbvid.Simple.AdsManager;
import com.kheileang.downfbvid.Simple.Video;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.gson.Gson;

import java.util.ArrayList;

import es.dmoral.toasty.Toasty;


public class DownloadVideoActivity extends AppCompatActivity {

    RecyclerView mRecyclerView;
    public ArrayList<Video> videoArrayList;
    VideoAdapter videoAdapter;
    AdView adView1;
    InterstitialAd minterstitialAd;

    public AdsManager adsManager;

    private static final String TAG = DownloadVideoActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
//        getSupportActionBar().setCustomView(R.layout.download_title_action_bar)
//        getSupportActionBar().setTitle((Html.fromHtml("<font color=\"#FFFFFF\">" + "Downloaded Video" + "</font>")));
        setContentView(R.layout.activity_download_video);



        minterstitialAd = new InterstitialAd(this);
        adsManager = new AdsManager(getApplicationContext());
        adView1 = findViewById(R.id.adView1);
        adsManager.createBannerAds(adView1);

        /*MobileAds.initialize(DownloadVideoActivity.this,
                "ca-app-pub-3940256099942544/1033173712");*/
        minterstitialAd = adsManager.getInterstitialAd();

        // this is required to display Interstitial ads
        minterstitialAd.setAdListener(new AdListener(){
            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
                if(minterstitialAd != null){
                    if(minterstitialAd.isLoaded()){
                        minterstitialAd.show();
                    }
                }
            }
        });


        // Populating array with Video data
        videoArrayList = new ArrayList<>();
        loadingVideoData();

        mRecyclerView = findViewById(R.id.videoRecyclerView);
        videoAdapter = new VideoAdapter(this, videoArrayList, new OnItemClickListener() {
            @Override
            public void onItemClick(Video video) {
                Toasty.info(getApplicationContext(), "You choose " + video.getTitle(), Toasty.LENGTH_LONG).show();
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