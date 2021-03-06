package com.kheileang.downfbvid.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.kheileang.downfbvid.R;

import es.dmoral.toasty.Toasty;

public class NoInternetActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_no_internet);
    }

    public void onClick(View view) {
        final int id = view.getId();

        switch(id){
            case R.id.setting:
                startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                finish();
                break;
            case R.id.downloaded_video:
                startActivity(new Intent(this, DownloadVideoActivity.class));
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                break;
            /*case R.id.close:
                finish();
                break;*/
            default:
                Toasty.info(view.getContext(), "What...");
        }
    }
}