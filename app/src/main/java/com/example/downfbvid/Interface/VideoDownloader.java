package com.example.downfbvid.Interface;

public interface VideoDownloader {
    String createDirectory();

    void getVideoLink();

    void downloadVideo(final String vidUrl, String vidTitle);
}
