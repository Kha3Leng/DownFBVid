package com.example.downfbvid.Downloader;

import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import com.example.downfbvid.Interface.VideoDownloader;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FBVideoDownloader implements VideoDownloader {
    private Context context;
    private String vidUrl;
    private long downloadID;
    private String vidTitle;
    ArrayList<String> trueLink;

    public FBVideoDownloader(Context context, String vidUrl) {
        this.context = context;
        this.vidUrl = vidUrl;
    }

    @Override
    public String createDirectory() {
        File folder = new File(Environment.getExternalStorageDirectory() + File.separator + "FBVid" );
        boolean success = true;

        if(!folder.exists()){
            success = folder.mkdirs();
        }
        assert  success != false;
        return folder.getPath();
    }

    @Override
    public void getVideoLink() {
        String clipboardUrl = this.vidUrl;
        if (clipboardUrl.contains("fb") || clipboardUrl.contains("facebook")){
            new Thread(new Runnable() {
                @Override
                public void run() {
                    HttpURLConnection connection;
                    BufferedReader reader;
                    String title = "fb_"+System.currentTimeMillis();
                    String cleanUrl = "No Video URL";
                    try {
                        URL url = new URL(clipboardUrl);
                        connection = (HttpURLConnection) url.openConnection();
                        connection.connect();

                        InputStream inputStream = connection.getInputStream();
                        reader = new BufferedReader(new InputStreamReader(inputStream));
                        final StringBuilder stringBuilder = new StringBuilder();
                        String readline = null;

                        while((readline = reader.readLine()) != null){
                            stringBuilder.append(readline);

                            if(stringBuilder.toString().contains("og:video:url")){
                                final String substr1 = stringBuilder.substring(stringBuilder.indexOf("og:video:url"));

                                if (substr1.contains("og:title")){
                                    title = substr1.substring(substr1.indexOf("og:title"));
                                    title = title.substring(ordinalIndexOf(title,"\"",1)+1,ordinalIndexOf(title,"\"",2));
                                }
                                cleanUrl = substr1.substring(ordinalIndexOf(substr1,"\"",1)+1,ordinalIndexOf(substr1,"\"",2));

                                if(cleanUrl.contains("amp;")){
                                    cleanUrl = cleanUrl.replace("amps;", "");
                                }

                                if(!cleanUrl.contains("https")){
                                    cleanUrl = cleanUrl.replace("http", "https");
                                }
                                break;
                            }
                        }
                        downloadVideo(cleanUrl, title);
                    } catch (IOException e) {
                        e.printStackTrace();
                        downloadVideo("No url", "no title");
                    }
                }
            }).start();
        }else{
            Toast.makeText(context, "Try copying FB Vid Url..", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void downloadVideo(final String vidUrl, String vidTitle) {
        new Handler(Looper.getMainLooper()).post(() -> Toast.makeText(this.context, "Downloading in progess", Toast.LENGTH_LONG).show());
        if (!vidUrl.contains("No url")){
            String path = createDirectory();
            if (vidTitle != null){
                vidTitle += ".mp4";
            }

            File newFile = new File(path, vidTitle);
            DownloadManager.Request request = new DownloadManager.Request(Uri.parse(vidUrl));
            request.allowScanningByMediaScanner();
            request.setDescription(vidTitle)
                    .setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE | DownloadManager.Request.NETWORK_WIFI)
                    .setDestinationUri(Uri.fromFile(newFile))
                    .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                    .setVisibleInDownloadsUi(true)
                    .setTitle(vidTitle);
            DownloadManager downloadManager = (DownloadManager)context.getSystemService(Context.DOWNLOAD_SERVICE);
            assert downloadManager != null;
            downloadID = downloadManager.enqueue(request);
        }else {
            new Handler(Looper.getMainLooper()).post(() -> Toast.makeText(this.context, "No URL fed", Toast.LENGTH_SHORT).show());
        }
    }

    private ArrayList<String> getURL(String clipboardUrl) {
        final ArrayList<String> urlList = new ArrayList<>();
        final Matcher matcher = Pattern.compile("(https?://|www.)[-A-Za-z0-9+&@#/%?=~_()|!:,.;]+[-A-Za-z0-9+&@#/%?=~_()|!:,.;]*").matcher(clipboardUrl);
        while (matcher.find()){
            String gp = matcher.group();
            if(gp.startsWith("(") && gp.endsWith(")")){
                gp.substring(1, gp.length()-1);
            }
            urlList.add(gp);
        }
        return urlList;
    }

    private static int ordinalIndexOf(String str, String substr, int n) {
        int pos = -1;
        do {
            pos = str.indexOf(substr, pos + 1);
        } while (n-- > 0 && pos != -1);
        return pos;
    }
}
