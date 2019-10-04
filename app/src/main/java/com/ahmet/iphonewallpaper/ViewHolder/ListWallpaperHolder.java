package com.ahmet.iphonewallpaper.ViewHolder;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.ahmet.iphonewallpaper.Interface.ItemClickListener;
import com.ahmet.iphonewallpaper.R;

public class ListWallpaperHolder extends RecyclerView.ViewHolder implements View.OnClickListener {


    private Context context;

    public ImageView imageWallpaper;
    public ImageButton mBtnFavorite;

    ItemClickListener itemClickListener;



    public void setItemClickListener(Context context, ItemClickListener itemClickListener){
        this.itemClickListener = itemClickListener;
        this.context = context;
    }

    public ListWallpaperHolder(View itemView) {
        super(itemView);

        imageWallpaper = itemView.findViewById(R.id.image_wallpaper);
        mBtnFavorite = itemView.findViewById(R.id.btn_add_favorite);

        itemView.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        itemClickListener.onClick(view, getAdapterPosition());
    }

}
