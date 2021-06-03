package com.kheileang.downfbvid.Simple;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.LoadAdError;

public class AdsManager {

    private static InterstitialAd interstitialAd;

    private static final String AD_UNIT_ID = "ca-app-pub-3940256099942544/1033173712";
    public Context context;
    private static final String TAG = AdsManager.class.getSimpleName();

    public AdsManager(Context context) {
        this.context = context;
    }

    public AdRequest getAdRequest(){
        return new AdRequest.Builder().build();
    }

    public InterstitialAd getInterstitialAd(){
        this.interstitialAd = new InterstitialAd(context);
        interstitialAd.setAdUnitId(AD_UNIT_ID);
        interstitialAd.loadAd(getAdRequest());
        return interstitialAd;
    }

    public void createBannerAds(AdView adView){
        AdRequest adRequest = new AdRequest.Builder().build();

        adView.setAdListener(new AdListener() {
            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                super.onAdFailedToLoad(loadAdError);
//                Toasty.info(context, ""+loadAdError.getCode(), Toasty.LENGTH_LONG).show();
                Log.w(TAG, "onAdFailedToLoad: "+"Banner Ads fails to load" );
            }

            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
//                Toasty.info(context, "Ads is loaded.", Toasty.LENGTH_LONG).show();
                Log.d(TAG, "onAdLoaded: "+"Banner Ads is loaded" );
            }
        });

        adView.loadAd(adRequest );
    }
}
