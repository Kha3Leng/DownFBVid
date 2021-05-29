package com.example.downfbvid.Adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.downfbvid.Interface.OnItemClickListener;
import com.example.downfbvid.R;
import com.example.downfbvid.Simple.Video;

import java.util.ArrayList;

public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.VideoViewHolder> {
    Context context;
    ArrayList<Video> videoArrayList;
    private OnItemClickListener listener;

    public VideoAdapter(Context context, ArrayList<Video> videoArrayList, OnItemClickListener listener) {
        this.context = context;
        this.videoArrayList = videoArrayList;
        this.listener = listener;
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
        holder.bindTo(video, listener);
    }

    @Override
    public int getItemCount() {
        return videoArrayList.size();
    }

    public class VideoViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ImageView mImageView;
        private TextView mTitle;

        public void bindTo(Video video, OnItemClickListener listener){
            this.mTitle.setText(video.getTitle());

            // load thumbnail of the video
            Glide.with(context).asBitmap().load(Uri.parse(video.getPath())).into(mImageView);

            // set onclick listener
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(video);
                }
            });
        }

        public VideoViewHolder(@NonNull View itemView) {
            super(itemView);
            this.mTitle = itemView.findViewById(R.id.video_title);
            this.mImageView = itemView.findViewById(R.id.thumbnail_view);
        }

        @Override
        public void onClick(View v) {
            int id = v.getId();
        }
    }
}
