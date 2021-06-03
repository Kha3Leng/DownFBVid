package com.kheileang.downfbvid.Service;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;


public class ConnectivityService {
    @Nullable
    private static NetworkInfo getNetworkInfo(@NonNull final Context context){
        final ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return manager != null ? manager.getActiveNetworkInfo(): null;
    }

    public static boolean isConnected(final Context context){
        final NetworkInfo info = ConnectivityService.getNetworkInfo(context);
        return info != null && info.isConnected();
    }

}
