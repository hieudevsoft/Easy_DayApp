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
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.easyday.ENTITY.MusicsFile;
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
    public void onBindViewHolder(@NonNull AdapterSong.ViewHolder holder, int position) {
        holder.tv_title.setText(listMusics.get(position).getTitle());
        holder.tv_artist.setText(listMusics.get(position).getArtist());
        byte[] iamge = getAlbumArt(listMusics.get(position).getData());
        if(iamge!=null){
            Glide.with(context).asBitmap().centerCrop().load(iamge).into(holder.image_songs);
        }
        else {
            Glide.with(context).asBitmap().centerCrop().load(R.drawable.music).into(holder.image_songs);
        }
        holder.mainLayoutSongs.startAnimation(AnimationUtils.loadAnimation(context, R.anim.anim_note));
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
    private byte[] getAlbumArt(String uri)
    {
        MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
        mediaMetadataRetriever.setDataSource(uri);
        byte[] result = mediaMetadataRetriever.getEmbeddedPicture();
        mediaMetadataRetriever.release();
        return result;

    }
}
