package com.kheileang.downfbvid.Activity;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;

import com.kheileang.downfbvid.MainActivity;
import com.kheileang.downfbvid.R;
import com.kheileang.downfbvid.Service.ConnectivityService;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;

public class SplashActivity extends AppCompatActivity {
    public boolean hasInternet;
    private static final String TAG = SplashActivity.class.getSimpleName();
    private ClipboardManager clipboardManager;
    private Intent shareIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Check if internet is available
        hasInternet = ConnectivityService.isConnected(this);
        
        //Kitkat translucent statusbar
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
            setTranslucentStatus();

        // Ignore Error
        // Make screen Portrait to disable Landscape orientation.
        //The Editor Probably will say that this is an error just ignore it, doesn't make any harm.
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        // Bind Activity with xml
        setContentView(R.layout.activity_splash);

        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
//                Toasty.info(getApplicationContext(), "Ads Loading..", Toasty.LENGTH_LONG).show();
                Log.i(TAG, "onInitializationComplete: initialization of ads finished");
            }
        });

        clipboardManager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);

        ClipData clipData;
        shareIntent = getIntent();
        if(shareIntent != null && (clipData = shareIntent.getClipData()) != null) {
            clipboardManager.setPrimaryClip(clipData);
        }
        showScreen();
    }

    private void showScreen() {
        new Handler().postDelayed(() -> {
            final Class<? extends Activity> activityClass;

            if (!hasInternet) {
                // Jumps to No Connection Activity.
                activityClass = NoInternetActivity.class;
            } else {
                // Jumps to Main Activty.
                activityClass = MainActivity.class;
            }

            startIntent(activityClass);
        }, 1000);
    }

    private void startIntent(Class<? extends Activity> activityClass) {
        Intent carryIntent = new Intent(this, activityClass);
        /*if (clipboardManager.hasPrimaryClip() && shareIntent != null
        && shareIntent.getClipData() != null){
            carryIntent.putExtra("fromLink", "yes");
            carryIntent.putExtra("vidLink", clipboardManager
            .getPrimaryClip()
            .getItemAt(0)
            .coerceToText(this).toString());
        }else{
            carryIntent.putExtra("fromLink", "no");
        }*/
        startActivity(carryIntent);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        finish();
    }

    private void setTranslucentStatus() {
        final int bits = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT ? WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS : 0;

        final Window win = getWindow();
        final WindowManager.LayoutParams winParams = win.getAttributes();
        winParams.flags |= bits;
        win.setAttributes(winParams);
    }
}