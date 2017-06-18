package com.example.android.popularmovies.adapter;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.android.popularmovies.R;
import com.example.android.popularmovies.model.Video;

import java.util.ArrayList;


public class HorizontalVideoAdapter extends RecyclerView.Adapter<HorizontalVideoAdapter.HorizontalVideoHolder> {

    private ArrayList<Video> video;
    private Context context;

    public HorizontalVideoAdapter(ArrayList<Video> vidoes, Context context){
        this.video = vidoes;
        this.context = context;

    }
    @Override
    public HorizontalVideoHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_grid_horizontal, parent, false);
        return  new HorizontalVideoHolder(view);

    }

    @Override
    public void onBindViewHolder(HorizontalVideoHolder holder, int position) {
        Video currentVideo = video.get(position);
        String youtubeId = currentVideo.getSource();
        String  youtubeUrl = "http://img.youtube.com/vi/" + youtubeId + "/mqdefault.jpg";
        Glide.with(context).load(youtubeUrl).crossFade().centerCrop().into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return video.size();
    }
    public class HorizontalVideoHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        ImageView imageView;
       // TextView textView;
        public HorizontalVideoHolder(View itemView) {
            super(itemView);
            imageView = (ImageView)itemView.findViewById(R.id.trailer_image);
           /// textView = (TextView)itemView.findViewById(R.id.trailer_name);
            itemView.setOnClickListener(this);         }

        @Override
        public void onClick(View v) {
            String videoId = video.get(getLayoutPosition()).getSource();
            watchYoutubeVideo(videoId);
           /* Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.video:" + videoId));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("VIDEO_ID", videoId);
            context.startActivity(intent);*/
        }
    }
    private void watchYoutubeVideo(String id){
        try{
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + id));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        }catch (ActivityNotFoundException ex){
            Intent intent=new Intent(Intent.ACTION_VIEW,
                    Uri.parse("http://www.youtube.com/watch?v="+id));
            context.startActivity(intent);
        }
    }
}
