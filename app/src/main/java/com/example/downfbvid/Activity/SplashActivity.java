package com.example.downfbvid.Activity;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;

import com.example.downfbvid.MainActivity;
import com.example.downfbvid.R;
import com.example.downfbvid.Service.ConnectivityService;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;

public class SplashActivity extends AppCompatActivity {
    public boolean hasInternet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Check if internet is available
        hasInternet = ConnectivityService.isConnected(this);
        
        // if KitKat do something..
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
            }
        });

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
        }, 1500);
    }

    private void startIntent(Class<? extends Activity> activityClass) {
        startActivity(new Intent(this, activityClass).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
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