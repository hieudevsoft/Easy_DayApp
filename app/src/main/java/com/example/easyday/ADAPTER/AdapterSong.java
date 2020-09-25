package com.example.easyday.ADAPTER;

import android.content.Context;
import android.media.MediaMetadataRetriever;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.easyday.CONTROL.SendPositionSong;
import com.example.easyday.CONTROL.TOOL;
import com.example.easyday.ENTITY.MusicsFile;
import com.example.easyday.FRAGMENT.music.HomeMusicFragment;
import com.example.easyday.R;

import java.util.List;

public class AdapterSong extends RecyclerView.Adapter<AdapterSong.ViewHolder> {
    List<MusicsFile> listMusics;
    Context context;
    public AdapterSong(List<MusicsFile> listMusics, Context context) {
        this.listMusics = listMusics;
        this.context = context;
    }

    public AdapterSong() {
    }

    @NonNull
    @Override
    public AdapterSong.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.layout_songs,null);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterSong.ViewHolder holder, final int position) {
        holder.tv_title.setText(listMusics.get(position).getTitle());
        holder.tv_artist.setText(listMusics.get(position).getArtist());
        byte [] iamge = null;
        try
        {
            iamge = TOOL.getAlbumArt(listMusics.get(position).getData());
        }catch (Exception e)
        {

        }

        if(iamge!=null){
            Glide.with(context).asBitmap().centerCrop().load(iamge).into(holder.image_songs);
        }
        else {
            Glide.with(context).asBitmap().centerCrop().load(R.drawable.music).into(holder.image_songs);
        }
        holder.mainLayoutSongs.startAnimation(AnimationUtils.loadAnimation(context, R.anim.anim_note));
        holder.mainLayoutSongs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SendPositionSong sendPositionSong = ViewModelProviders.of((FragmentActivity) context).get(SendPositionSong.class);
                sendPositionSong.getPosition().postValue(position);
                HomeMusicFragment.viewPager.setCurrentItem(1, true);
            }
        });
    }

    @Override
    public int getItemCount() {
        return listMusics.size();
    }
    static class ViewHolder extends RecyclerView.ViewHolder{
        ImageView image_songs;
        TextView tv_title,tv_artist;
        LinearLayout mainLayoutSongs;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            image_songs = itemView.findViewById(R.id.image_Song);
            tv_title = itemView.findViewById(R.id.title_songs);
            tv_artist = itemView.findViewById(R.id.artist_songs);
            mainLayoutSongs = itemView.findViewById(R.id.mainLayoutSongs);
        }
    }
}
