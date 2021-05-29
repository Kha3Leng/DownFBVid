package com.example.downfbvid.Simple;

public class Video {
    private String title;
    private String uri;
    private int duration;
    private int size;
//    private Bitmap thumbnail;

    public String getTitle() {
        return title;
    }

    public String getPath() {
        return uri;
    }

    /*public Bitmap getThumbnail(){ return thumbnail; }*/

    public int getDuration() {
        return duration;
    }

    public int getSize() {
        return size;
    }

    public Video(String title, String path, int duration, int size/*, Bitmap thumbnail*/) {
        this.title = title;
        this.uri = path;
        this.duration = duration;
        this.size = size;
        /*this.thumbnail = thumbnail;*/
    }
}
