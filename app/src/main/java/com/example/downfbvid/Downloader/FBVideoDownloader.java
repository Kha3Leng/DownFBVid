package com.example.downfbvid.Downloader;

import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import com.example.downfbvid.Interface.VideoDownloader;
import com.tapadoo.alerter.Alerter;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static android.content.Context.DOWNLOAD_SERVICE;

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
    public void downloadVideo(final String vidUrl, String vidTitle) {
        new Data().execute(vidUrl);
    }

    private class Data extends AsyncTask<String, String,String> {

        @Override
        protected String doInBackground(String... strings) {
            HttpURLConnection connection;
            BufferedReader reader;
            String title = "fb_"+System.currentTimeMillis();
            String cleanUrl = "No Url";
            try {
                URL url = new URL(strings[0]);
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
                            vidTitle = title.substring(ordinalIndexOf(title,"\"",1)+1,ordinalIndexOf(title,"\"",2));
                        }
                        cleanUrl = substr1.substring(ordinalIndexOf(substr1,"\"",1)+1,ordinalIndexOf(substr1,"\"",2));

                        if(cleanUrl.contains("amp;")){
                            cleanUrl = cleanUrl.replace("amp;", "");
                        }

                        if(!cleanUrl.contains("https")){
                            cleanUrl = cleanUrl.replace("http", "https");
                        }
                        break;
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return cleanUrl;
        }

        @Override
        protected void onPostExecute(String s) {
            if (!s.contains("No url")){
                String path = createDirectory();
                if (vidTitle != null){
                    vidTitle += ".mp4";
                }else{
                    vidTitle = "fb_"+System.currentTimeMillis()+".mp4";
                }
                try {
                    File newFile = new File(path, vidTitle);
                    DownloadManager.Request request = new DownloadManager.Request(Uri.parse(s));
                    request.allowScanningByMediaScanner();
                    request.setDescription(vidTitle)
                            .setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE | DownloadManager.Request.NETWORK_WIFI)
                            .setDestinationUri(Uri.fromFile(newFile))
                            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                            .setVisibleInDownloadsUi(true)
                            .setTitle(vidTitle);
                    DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
                    assert downloadManager != null;
                    downloadID = downloadManager.enqueue(request);
                }catch (Exception e) {
                    if (Looper.myLooper()==null)
                        Looper.prepare();

                    if (e.getMessage().contains("WRITE_EXTERNAL_STORAGE")) {
                        String err = e.getMessage();
                        String str = err.substring(0, err.indexOf(".mp4"));
                        String initial = str.substring(0, err.lastIndexOf("/"));
                        String msg = initial + err.substring(err.indexOf(".mp4") + 4);
                        Toast.makeText(context, "You have denied storage permissions and this app can't download video, the app force close try granting permission from Settings > Apps.", Toast.LENGTH_SHORT).show();
                    }
                    Looper.loop();
                }
            }
            else {
                if (Looper.myLooper()==null)
                    Looper.prepare();
                Toast.makeText(context, "Make sure you have copied facebook video url..", Toast.LENGTH_LONG).show();
                Looper.loop();
            }
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            Looper.prepare();
            Toast.makeText(context, "Video Can't be downloaded! Try Again", Toast.LENGTH_SHORT).show();
            Looper.loop();
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
