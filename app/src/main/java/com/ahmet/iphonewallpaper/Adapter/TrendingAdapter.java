package com.ahmet.iphonewallpaper.Adapter;

import android.content.Context;
import com.ahmet.iphonewallpaper.Model.WallpaperItem;
import java.util.List;
import ss.com.bannerslider.adapters.SliderAdapter;
import ss.com.bannerslider.viewholder.ImageSlideViewHolder;

public class TrendingAdapter extends SliderAdapter {

    private List<WallpaperItem> mList;
    private Context mContext;

    public TrendingAdapter(Context mContext, List<WallpaperItem> mList){
        this.mContext = mContext;
        this.mList = mList;
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    @Override
    public void onBindImageSlide(final int position, ImageSlideViewHolder imageSlideViewHolder) {

        final WallpaperItem wallpaperItem = mList.get(position);

        imageSlideViewHolder.bindImageSlide(wallpaperItem.getImageUrl());

    }
}
