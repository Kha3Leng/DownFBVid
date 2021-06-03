package com.kheileang.downfbvid.Adapter;

import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.kheileang.downfbvid.Activity.VideoPlayerActivity;
import com.kheileang.downfbvid.Interface.OnItemClickListener;
import com.kheileang.downfbvid.R;
import com.kheileang.downfbvid.Simple.Video;
import com.google.gson.Gson;

import java.util.ArrayList;

import es.dmoral.toasty.Toasty;

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

    public class VideoViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener {
        private ImageView mImageView;
        private Video video;
        private TextView mTitle;

        public void bindTo(Video video, OnItemClickListener listener){
            this.video = video;
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

            itemView.setOnLongClickListener(this);
        }

        public VideoViewHolder(@NonNull View itemView) {
            super(itemView);
            this.mTitle = itemView.findViewById(R.id.video_title);
            this.mImageView = itemView.findViewById(R.id.thumbnail_view);
        }

        @Override
        public boolean onLongClick(View v) {
            final CharSequence[] items = {"Share This Video",
                                        "Delete This Video",
                                        "Play This Video"};
            AlertDialog.Builder menuAlertBuilder = new AlertDialog.Builder(context);
            menuAlertBuilder.setTitle("Choose Actions");
            menuAlertBuilder.setItems(items, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch(which){
                        case 0:
                            Toasty.info(context, "You Shared "+ video.getTitle(), Toasty.LENGTH_SHORT).show();
                            Intent intent = new Intent(Intent.ACTION_SEND);
                            intent.setType("video/mp4");
                            intent.putExtra(Intent.EXTRA_SUBJECT, video.getTitle());
                            intent.putExtra(Intent.EXTRA_TITLE, video.getTitle());
                            intent.putExtra(Intent.EXTRA_STREAM, Uri.parse(video.getPath()));
                            context.startActivity(Intent.createChooser(intent, "Share Via"));
                            break;
                        case 1:
                            Toasty.info(context, "You Deleted  "+ video.getTitle(), Toasty.LENGTH_SHORT).show();
                            Uri vidUrl = ContentUris.withAppendedId(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, video.getVidId());
                            context.getContentResolver().delete(vidUrl, null, null);

                            // Send intent to refresh media store after deletion of video
                            // context.startActivity(new Intent(context, DownloadVideoActivity.class));

                            break;
                        case 2:
                            Toasty.info(context, "You Played "+ video.getTitle(), Toasty.LENGTH_SHORT).show();
                            Intent playintent = new Intent(context, VideoPlayerActivity.class);
                            Gson gson = new Gson();
                            String videoJson = gson.toJson(video);
                            playintent.putExtra("video", videoJson);

                            context.startActivity(playintent);
                            break;
                    }
                }
            });
            menuAlertBuilder.show();
            return true;
        }
    }
}
