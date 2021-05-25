package com.example.downfbvid;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ClipData;
import android.content.ClipDescription;
import android.content.ClipboardManager;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import com.example.downfbvid.Activity.NoInternetActivity;
import com.example.downfbvid.Downloader.FBVideoDownloader;
import com.example.downfbvid.Interface.VideoDownloader;
import com.example.downfbvid.Service.ConnectivityService;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    public ImageView mFbLogo;
    private ClipboardManager clipboardManager;
    private LayoutInflater layoutInflater;
    private static final String TAG = MainActivity.class.getSimpleName();

    public FBVideoDownloader fbVideoDownloader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        clipboardManager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        layoutInflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);

        setContentView(R.layout.activity_main);

        mFbLogo = findViewById(R.id.FBbtn);
        mFbLogo.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        final int id = v.getId();

        if (id == R.id.FBbtn){
            final boolean hasInternet = ConnectivityService.isConnected(this);

            if(!hasInternet){
                startActivity(new Intent(this, NoInternetActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                finish();
                return;
            }


            if(clipboardManager != null){
                final ClipDescription clipDescription = clipboardManager.getPrimaryClipDescription();

                if (clipDescription != null && clipboardManager.hasPrimaryClip() && clipDescription.hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN)){
                    final ClipData link = clipboardManager.getPrimaryClip();

                    if(link != null){
                        final ClipData.Item item = link.getItemAt(0);
                        final String clipboardUrl = item.getText().toString();
                        fbVideoDownloader = new FBVideoDownloader(this, clipboardUrl);
                        fbVideoDownloader.downloadVideo(clipboardUrl, "hello");
                    }
                }
            }


        }
    }


}