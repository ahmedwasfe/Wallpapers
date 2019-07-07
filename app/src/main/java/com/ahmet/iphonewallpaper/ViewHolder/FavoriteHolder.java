package com.ahmet.iphonewallpaper.ViewHolder;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.ahmet.iphonewallpaper.R;

public class FavoriteHolder extends RecyclerView.ViewHolder {

    public ImageView mImageShowWallpaper;
    public ImageButton mDeleteFavorite, mShare, mSetWallpaper;

    public FavoriteHolder(@NonNull View itemView) {
        super(itemView);

        mImageShowWallpaper = itemView.findViewById(R.id.image_wallpaper_favorite);
        mDeleteFavorite = itemView.findViewById(R.id.image_delete_favorite);
        mShare = itemView.findViewById(R.id.image_share);
        mSetWallpaper = itemView.findViewById(R.id.image_set_wallpaper);
    }
}
