package com.example.downfbvid.Simple;

import android.net.Uri;

public class Video {
    private String title;
    private Uri uri;
    private int duration;
    private int size;
//    private Bitmap thumbnail;

    public String getTitle() {
        return title;
    }

    public Uri getPath() {
        return uri;
    }

    /*public Bitmap getThumbnail(){ return thumbnail; }*/

    public int getDuration() {
        return duration;
    }

    public int getSize() {
        return size;
    }

    public Video(String title, Uri path, int duration, int size/*, Bitmap thumbnail*/) {
        this.title = title;
        this.uri = path;
        this.duration = duration;
        this.size = size;
        /*this.thumbnail = thumbnail;*/
    }
}
