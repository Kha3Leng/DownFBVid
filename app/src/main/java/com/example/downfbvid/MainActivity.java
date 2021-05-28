package com.example.downfbvid;

import android.Manifest;
import android.content.ClipData;
import android.content.ClipDescription;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.downfbvid.Activity.DownloadVideoActivity;
import com.example.downfbvid.Activity.NoInternetActivity;
import com.example.downfbvid.Activity.SettingActivity;
import com.example.downfbvid.Downloader.FBVideoDownloader;
import com.example.downfbvid.Service.ConnectivityService;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.BasePermissionListener;
import com.tapadoo.alerter.Alerter;

import es.dmoral.toasty.Toasty;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    public ImageView mFbLogo, mShare, mRate;
    private ClipboardManager clipboardManager;
    private LayoutInflater layoutInflater;
    private SharedPreferences sharedPreferences;
    private static final String TAG = MainActivity.class.getSimpleName();
    public String sharedPref = MainActivity.class.getCanonicalName();
    public static String fb_outside_link = null;
    Vibrator vibrator;

    BottomNavigationView bottomNavigationView;

    public FBVideoDownloader fbVideoDownloader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        clipboardManager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        layoutInflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        sharedPreferences = getSharedPreferences(sharedPref, MODE_PRIVATE);
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
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

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.title_bar_layout);


        setContentView(R.layout.activity_main);

        bottomNavigationView = findViewById(R.id.btmNavigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.home_menu:
                        Intent intent = new Intent( getApplicationContext(), MainActivity.class);
                        startActivity(intent);
                        break;
                    case R.id.video_menu:
                        startActivity(new Intent(getApplicationContext(), DownloadVideoActivity.class));
                        break;
                    case R.id.setting_menu:
                        startActivity(new Intent(getApplicationContext(), SettingActivity.class));
                        break;
                }
                return false;
            }
        });

        isFirstTime();

        askPermission();

        mFbLogo = findViewById(R.id.FBbtn);
        mRate = findViewById(R.id.rating);
        mShare = findViewById(R.id.share);

        mFbLogo.setOnClickListener(this);
        mRate.setOnClickListener(this);
        mShare.setOnClickListener(this);
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


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onClick(View v) {
        final int id = v.getId();

        switch(id) {
            case R.id.FBbtn:
                final boolean hasInternet = ConnectivityService.isConnected(this);

                if (!hasInternet) {
                    startActivity(new Intent(this, NoInternetActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                    finish();
                    return;
                }

                if (clipboardManager != null) {
                    final ClipDescription clipDescription = clipboardManager.getPrimaryClipDescription();

                    if (clipDescription != null && clipboardManager.hasPrimaryClip() && clipDescription.hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN)) {
                        final ClipData link = clipboardManager.getPrimaryClip();

                        if (link != null) {
                            final ClipData.Item item = link.getItemAt(0);
                            final String clipboardUrl = item.getText().toString();
                            fbVideoDownloader = new FBVideoDownloader(this, clipboardUrl);

                            // vibrator
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                vibrator.vibrate(VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE));
                            }
                            Toasty.info(this, "Loading...", Toasty.LENGTH_LONG).show();
                            fbVideoDownloader.downloadVideo(clipboardUrl, "hello");
                        }
                    }
                }
                break;
            case R.id.rating:
                rateMyApp();
                break;
            case R.id.share:
//                Toasty.info(this, "You Shared", Toasty.LENGTH_LONG).show();
                showShareCompat();
                break;

        }
    }

    private void rateMyApp() {
        final String appPackageName = "com.king.candycrushsaga";
        try{
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id="+appPackageName)));
        }catch (android.content.ActivityNotFoundException e){
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id="+appPackageName)));
        }
    }

    private void showShareCompat() {
        /*File imagePath = new File(getFilesDir(), "external_files");
        imagePath.mkdir();
        File imageFile = new File(imagePath.getPath(), "test.jpeg");

        Write data in your file

        Uri uri = FileProvider.getUriForFile(this, getPackageName(), imageFile);

        Intent intent = ShareCompat.IntentBuilder.from(this)
                .setStream(uri) // uri from FileProvider
                .setType("text/html")
                .getIntent()
                .setAction(Intent.ACTION_VIEW) //Change if needed
                .setDataAndType(uri, "*\/*")
                .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);*/

        String googlePlayAppLink = "https://play.google.com/store/apps/details?id=com.king.candycrushsaga";

        Intent i = new Intent(Intent.ACTION_SEND);
        i.setType("text/plain");
        i.putExtra(Intent.EXTRA_SUBJECT, "Sharing DownFBVid");
        i.putExtra(Intent.EXTRA_TEXT, googlePlayAppLink);
        startActivity(Intent.createChooser(i, "Share Via"));
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