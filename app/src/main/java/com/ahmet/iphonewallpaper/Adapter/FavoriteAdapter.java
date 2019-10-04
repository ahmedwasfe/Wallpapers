package com.ahmet.iphonewallpaper.Adapter;

import android.app.AlertDialog;
import android.app.WallpaperManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.ahmet.iphonewallpaper.Database.SqliteDatabase.FavoriteDatabaseSQLite;
import com.ahmet.iphonewallpaper.Model.WallpaperItem;
import com.ahmet.iphonewallpaper.R;
import com.ahmet.iphonewallpaper.UI.ViewWallpapersFavorite;
import com.ahmet.iphonewallpaper.ViewHolder.FavoriteHolder;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import dmax.dialog.SpotsDialog;

public class FavoriteAdapter extends RecyclerView.Adapter<FavoriteHolder> {

    private Context mContext;
    private List<WallpaperItem> mListFavorite;
    private LayoutInflater layoutInflater;

    private AlertDialog alertDialog;

    private InterstitialAd mInterstitialAd;

    public FavoriteAdapter() {
    }

    public FavoriteAdapter(Context mContext, List<WallpaperItem> mListFavorite) {
        this.mContext = mContext;
        this.mListFavorite = mListFavorite;
        layoutInflater = LayoutInflater.from(mContext);
    }

    @NonNull
    @Override
    public FavoriteHolder onCreateViewHolder( ViewGroup parent, int viewType) {

        View convertView = layoutInflater.inflate(R.layout.raw_favorite, parent, false);

        return new FavoriteHolder(convertView);

    }

    @Override
    public void onBindViewHolder(@NonNull final FavoriteHolder favoriteHolder, final int position) {


        MobileAds.initialize(mContext, "ca-app-pub-4765070079723849~5007430629");
        mInterstitialAd = new InterstitialAd(mContext);
        mInterstitialAd.setAdUnitId("ca-app-pub-4765070079723849/1271431483");
        mInterstitialAd.loadAd(new AdRequest.Builder().build());


        final WallpaperItem wallpaperItem = mListFavorite.get(position);
        final FavoriteDatabaseSQLite databaseFavoritr = new FavoriteDatabaseSQLite(mContext);
        alertDialog = new SpotsDialog(mContext);

        Picasso.with(mContext)
                .load(wallpaperItem.getImageUrl())
                .placeholder(R.drawable.ic_terrain_black_24dp)
                .into(favoriteHolder.mImageShowWallpaper);

        favoriteHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(mContext, ViewWallpapersFavorite.class);
                intent.putExtra("image", wallpaperItem.getImageUrl());
                mContext.startActivity(intent);

                if (mInterstitialAd.isLoaded())
                    mInterstitialAd.show();
                else
                    Log.d("InterstitialAd", "The interstitial wasn't loaded yet.");
            }
        });



        favoriteHolder.mDeleteFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

               // if (checkWallpaper > 0){
                    databaseFavoritr.deleteData(wallpaperItem.getImageID());
                    mListFavorite.remove(position);
                    notifyDataSetChanged();
                   // Toast.makeText(mContext, "deleted success", Toast.LENGTH_SHORT).show();
              //  }

            }
        });

        favoriteHolder.mShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Uri imageUri = Uri.parse(wallpaperItem.getImageUrl());
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("image/*");
                intent.putExtra(Intent.EXTRA_STREAM, imageUri);
                mContext.startActivity(intent);

                //shareImage(wallpaperItem.getImageUrl());

                if (mInterstitialAd.isLoaded())
                    mInterstitialAd.show();
                else
                    Log.d("InterstitialAd", "The interstitial wasn't loaded yet.");

            }
        });


        favoriteHolder.mSetWallpaper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Picasso.with(mContext)
                        .load(wallpaperItem.getImageUrl())
                        .into(target);

                if (mInterstitialAd.isLoaded())
                    mInterstitialAd.show();
                else
                    Log.d("InterstitialAd", "The interstitial wasn't loaded yet.");

            }
        });


    }

    @Override
    public int getItemCount() {
        return mListFavorite.size();
    }

    private Target target = new Target() {
        @Override
        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
            alertDialog.show();
            WallpaperManager wallpaperManager = WallpaperManager.getInstance(mContext);
            try {
                wallpaperManager.setBitmap(bitmap);
//                Snackbar mSnackbar = null;
//                View mView = mSnackbar.getView();
//                mSnackbar = Snackbar.make(mView,
//                        R.string.set_wallpaper,
//                        Snackbar.LENGTH_SHORT);
//                mView.setBackgroundColor(Color.parseColor("#f789ff"));
//                mSnackbar.show();

                alertDialog.dismiss();
                Toast.makeText(mContext, mContext.getString(R.string.set_wallpaper), Toast.LENGTH_SHORT).show();


            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onBitmapFailed(Drawable errorDrawable) {

        }

        @Override
        public void onPrepareLoad(Drawable placeHolderDrawable) {

        }
    };


    public void shareImage(String imageUrl){

        Picasso.with(mContext)
                .load(imageUrl)
                .into(new Target() {
                    @Override
                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                        Intent intent = new Intent(Intent.ACTION_SEND);
                        intent.setType("image/*");
                        intent.putExtra(Intent.EXTRA_STREAM, getLocalBitmapUri(bitmap));
                        mContext.startActivity(intent);

                    }

                    @Override
                    public void onBitmapFailed(Drawable errorDrawable) {

                    }

                    @Override
                    public void onPrepareLoad(Drawable placeHolderDrawable) {

                    }
                });

    }

    private Uri getLocalBitmapUri(Bitmap bitmap){
        Uri bmapUri = null;
        try {
        File file = new File(
                mContext.getExternalFilesDir(Environment.DIRECTORY_PICTURES),
                "image" + System.currentTimeMillis() + ".jpg");
            FileOutputStream outputStream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, outputStream);
            outputStream.close();
            bmapUri = Uri.fromFile(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bmapUri;
    }

}
