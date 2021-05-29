package com.example.downfbvid.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.downfbvid.R;
import com.example.downfbvid.Simple.Video;

import java.util.ArrayList;

public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.VideoViewHolder> {
    Context context;
    ArrayList<Video> videoArrayList;

    public VideoAdapter(Context context, ArrayList<Video> videoArrayList) {
        this.context = context;
        this.videoArrayList = videoArrayList;
    }

    @NonNull
    @Override
    public VideoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        VideoViewHolder videoViewHolder = new VideoViewHolder(LayoutInflater.from(context).inflate(R.layout.video_list_layout, parent, false));
        return videoViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull VideoViewHolder holder, int position) {
        Video video = videoArrayList.get(position);
        holder.bindTo(video);
    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public class VideoViewHolder extends RecyclerView.ViewHolder {
        private Video video;
        private TextView mTitle;

        public void bindTo(Video video){
            this.mTitle.setText(video.getTitle().toString());
        }

        public VideoViewHolder(@NonNull View itemView) {
            super(itemView);
            this.mTitle = itemView.findViewById(R.id.video_title);
        }
    }
}
