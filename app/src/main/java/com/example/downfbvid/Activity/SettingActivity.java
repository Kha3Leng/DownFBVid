package com.example.downfbvid.Activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.downfbvid.R;
import com.example.downfbvid.Simple.AdsManager;
import com.google.android.gms.ads.AdView;

public class SettingActivity extends AppCompatActivity {

    private TextView mRate, mShare, mApps, mPolicy, mHowTo, mDemo, mNotWorking, mFeedback;
    AdView adView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        AdsManager adsManager = new AdsManager(this);
        adView = findViewById(R.id.adView2);
        adView.loadAd(adsManager.getAdRequest());
    }

    public void onClick(View view) {
        int id = view.getId();

        switch (id){
            case R.id.rate_setting:
                rateMyApp();
                break;
            case R.id.share_setting:
                shareMyApp();
                break;
            case R.id.other_app_setting:
                viewOtherApps();
                break;
            case R.id.policy_setting:
                showPolicy();
                break;
            case R.id.howto_setting:
                break;
            case R.id.demo_setting:
                break;
            case R.id.notworking_setting:
                break;
            case R.id.feedback_setting:
                break;
        }
    }

    private void showPolicy() {
        final String privacyUrl = "https://sites.google.com/view/alpa-privacy-policy";

        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(privacyUrl)));
    }

    private void viewOtherApps() {
        final String comName = "King";

        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?pub="+comName)));
    }

    private void shareMyApp() {
        final String appPackageName = "https://play.google.com/store/apps/details?id=com.king.candycrushsaga";

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_SUBJECT, "MY APP");
        intent.putExtra(Intent.EXTRA_TEXT, appPackageName);
        startActivity(Intent.createChooser(intent, "Share Via"));
    }

    private void rateMyApp() {
        final String appPackageName = "com.king.candycrushsaga";
        try{
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id="+appPackageName)));
        }catch (android.content.ActivityNotFoundException e){
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://detail?id="+appPackageName)));
        }
    }


}