package com.example.downfbvid;

import android.Manifest;
import android.content.ClipData;
import android.content.ClipDescription;
import android.content.ClipboardManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.downfbvid.Activity.NoInternetActivity;
import com.example.downfbvid.Downloader.FBVideoDownloader;
import com.example.downfbvid.Service.ConnectivityService;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.BasePermissionListener;
import com.tapadoo.alerter.Alerter;

import es.dmoral.toasty.Toasty;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    public ImageView mFbLogo;
    private ClipboardManager clipboardManager;
    private LayoutInflater layoutInflater;
    private SharedPreferences sharedPreferences;
    private static final String TAG = MainActivity.class.getSimpleName();
    public String sharedPref = MainActivity.class.getCanonicalName();
    public static String fb_outside_link = null;

    public FBVideoDownloader fbVideoDownloader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        clipboardManager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        layoutInflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        sharedPreferences = getSharedPreferences(sharedPref, MODE_PRIVATE);
//        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());

        /*final Window window = getWindow();
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            window.setStatusBarColor(0xFF_212121);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                final View decorView = window.getDecorView();
                decorView.setSystemUiVisibility(decorView.getSystemUiVisibility() & ~View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            }
        }*/
        ClipData clipData;
        if (getIntent() != null && (clipData = getIntent().getClipData()) != null) {
            clipboardManager.setPrimaryClip(getIntent().getClipData());
            showAlert("Facebook Link", "Link Copied", R.color.btnPrimary);
        }


        setContentView(R.layout.activity_main);

        isFirstTime();

        askPermission();

        mFbLogo = findViewById(R.id.FBbtn);
        mFbLogo.setOnClickListener(this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        final Intent fb_intent = getIntent();
        final String fb_share;

        if(fb_intent != null && (fb_share = fb_intent.getDataString()) != null){
            fb_outside_link = fb_share;
        }

        ClipData clipData = ClipData.newPlainText("text label", fb_outside_link);
        clipboardManager.setPrimaryClip(clipData);
    }

    public void showAlert(final String title, final String message, final int color) {
        Alerter.create(this)
                .setTitle(title)
                .setText(message)
                .setBackgroundColorRes(color)
                .setDuration(5000)
                .show();
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
                        Toasty.info(this, "Loading...", Toasty.LENGTH_LONG).show();
                        fbVideoDownloader.downloadVideo(clipboardUrl, "hello");
                    }
                }
            }
        }
    }

    /**
     * DO NOT DISABLE THIS SNIPPET OF CODE!!!
     * If you disable or ignore this snippet of code the app wont work,it will crash or will not Download
     */
    private void askPermission() {


        /* 1.  Using BasePermissionListener and Toasty library*/
        Dexter.withContext(this).withPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE).withListener(new BasePermissionListener() {
            @Override
            public void onPermissionDenied(final PermissionDeniedResponse permissionDeniedResponse) {
//                    Toasty.warning(MainActivity.this, "You have denied storage permissions and this app can't download video," +
//                                    ", the app force close try granting permission from Settings > Apps.",Toasty.LENGTH_LONG, true).show();

                Toasty.custom(MainActivity.this,
                                getString(R.string.storage_permission_denied),
                                ContextCompat.getDrawable(MainActivity.this, R.drawable.ic_baseline_warning_24),
                                10000,
                        true).show();
            }

            @Override
            public void onPermissionRationaleShouldBeShown(final PermissionRequest request, final PermissionToken token) {
                token.continuePermissionRequest();
            }
        }).onSameThread().check();

        /*PermissionListener dialogPermissionListener = DialogOnDeniedPermissionListener.Builder
                .withContext(this)
                .withTitle("Storage permission")
                .withMessage("Storage permission is needed to save video from facebook.")
                .withButtonText(android.R.string.ok)
                .withIcon(R.drawable.ic_baseline_warning_24)
                .build();*/
        /* 2. Using Dialong */
        /*Dexter.withContext(this).withPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE).withListener(dialogPermissionListener).check();*/
    }

    // Check if it's the first time the app opens, then do something..
    public void isFirstTime() {
        Handler handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                // firsttime preference set as true default..
                final boolean isFirstStart = sharedPreferences.getBoolean("isFirstTime", true);


                // check if it's first time, then change isFirstTime to false
                if(isFirstStart){
                    showAlert("Hello", "hello", R.color.btnPrimary);
                    sharedPreferences.edit().putBoolean("isFirstTime", false).apply();
                }

                handler.removeCallbacks(this);
            }
        });
    }

}