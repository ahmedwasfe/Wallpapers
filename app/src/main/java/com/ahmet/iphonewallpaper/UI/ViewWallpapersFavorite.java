package com.ahmet.iphonewallpaper.UI;

import android.app.WallpaperManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.ahmet.iphonewallpaper.Config.Common;
import com.ahmet.iphonewallpaper.Config.SaveSettings;
import com.ahmet.iphonewallpaper.Database.SqliteDatabase.FavoriteDatabaseSQLite;
import com.ahmet.iphonewallpaper.Model.WallpaperItem;
import com.ahmet.iphonewallpaper.R;
import com.facebook.CallbackManager;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;
import com.facebook.share.widget.ShareDialog;
import com.github.clans.fab.FloatingActionMenu;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.IOException;

public class ViewWallpapersFavorite extends AppCompatActivity {

    private ImageView imageSetWallpaper;
    private RelativeLayout relativeLayout;

    private com.github.clans.fab.FloatingActionButton
            fabShareFacebook,fabSetWallpaper,
            fabDownload,fabShare,fabFavorite;

    private FloatingActionMenu fabMenuShare;

    private String image;
    private int checkWallpaper;


    // Facebook Share
    private CallbackManager callbackManager;
    private ShareDialog shareDialog;


    private SaveSettings saveSettings;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        saveSettings = new SaveSettings(this);
        if (saveSettings.getNightModeState() == true){
            setTheme(R.style.DarkTheme);
        }else {
            setTheme(R.style.AppTheme);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_wallpaper);

        image = getIntent().getStringExtra("image");


        relativeLayout = findViewById(R.id.relative_layout);
        fabSetWallpaper = findViewById(R.id.fab_set_wallpaper);
        fabDownload = findViewById(R.id.fab_download);
        fabShareFacebook = findViewById(R.id.fab_share_facebook);
        fabShare = findViewById(R.id.fab_share);
        fabMenuShare = findViewById(R.id.menu_fab_share);
        fabFavorite = findViewById(R.id.fab_favorite);
        imageSetWallpaper = findViewById(R.id.image_thumb_wallpaper);

        fabMenuShare.setVisibility(View.GONE);

        Picasso.with(this)
                .load(image)
                .into(imageSetWallpaper);

        fabSetWallpaper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Picasso.with(getApplicationContext())
                        .load(Common.wallpaperSelected.getImageUrl())
                        .into(target);
            }
        });

        fabShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("image/*");
                intent.putExtra(Intent.EXTRA_STREAM, image);
                startActivity(intent);

            }
        });


        final FavoriteDatabaseSQLite database = new FavoriteDatabaseSQLite(ViewWallpapersFavorite.this);
        final String imageLink = Common.wallpaperSelected.getImageUrl();
        final WallpaperItem wallpaperItem = new WallpaperItem(imageLink);
        checkWallpaper = database.checkData(imageLink);

        fabFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (checkWallpaper > 0){
                    Toast.makeText(ViewWallpapersFavorite.this, "this wallpaper found in database", Toast.LENGTH_SHORT).show();
                }else {
                    database.addData(wallpaperItem);
                    fabFavorite.setImageResource(R.drawable.ic_favorite);
                    Toast.makeText(ViewWallpapersFavorite.this, "done add to database", Toast.LENGTH_SHORT).show();
                }
            }
        });

        if (checkWallpaper > 0){
            fabFavorite.setImageResource(R.drawable.ic_favorite);
        }

    }


    private Target fbConvertBitmab = new Target() {
        @Override
        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
            SharePhoto sharePhoto = new SharePhoto.Builder()
                    .setBitmap(bitmap)
                    .build();
            if (ShareDialog.canShow(SharePhotoContent.class)){
                SharePhotoContent content = new SharePhotoContent.Builder()
                        .addPhoto(sharePhoto)
                        .build();
                shareDialog.show(content);
            }
        }

        @Override
        public void onBitmapFailed(Drawable errorDrawable) {

        }

        @Override
        public void onPrepareLoad(Drawable placeHolderDrawable) {

        }
    };

    private Target target = new Target() {
        @Override
        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
            WallpaperManager wallpaperManager = WallpaperManager.getInstance(getApplicationContext());
            try {
                wallpaperManager.setBitmap(bitmap);
                Snackbar.make(relativeLayout,
                        R.string.set_wallpaper,
                        Snackbar.LENGTH_SHORT).show();

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

}
