package com.example.downfbvid.Interface;

public interface VideoDownloader {
    String createDirectory();

    String getVideoLink();

    void downloadVideo();
}
