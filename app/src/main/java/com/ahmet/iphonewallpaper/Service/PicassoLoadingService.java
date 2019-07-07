package com.ahmet.iphonewallpaper.Service;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import ss.com.bannerslider.ImageLoadingService;

public class PicassoLoadingService implements ImageLoadingService {

    private Context mContext;

    public PicassoLoadingService(Context mContext){
        this.mContext = mContext;
    }

    @Override
    public void loadImage(String url, ImageView imageView) {
        Picasso.with(mContext).load(url).into(imageView);
    }

    @Override
    public void loadImage(int resource, ImageView imageView) {
        Picasso.with(mContext).load(resource).into(imageView);
    }

    @Override
    public void loadImage(String url, int placeHolder, int errorDrawable, ImageView imageView) {
        Picasso.with(mContext).load(url).placeholder(placeHolder).error(errorDrawable).into(imageView);
    }
}
