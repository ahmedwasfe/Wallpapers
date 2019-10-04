package com.ahmet.iphonewallpaper.UI;

import android.Manifest;
import android.app.AlertDialog;
import android.app.WallpaperManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.snackbar.Snackbar;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.ahmet.iphonewallpaper.Config.CheckInternetConnection;
import com.ahmet.iphonewallpaper.Config.Common;
import com.ahmet.iphonewallpaper.Config.SaveSettings;
import com.ahmet.iphonewallpaper.Database.DataSource.Recents.RecentRepository;
import com.ahmet.iphonewallpaper.Database.DataSource.LocalDatabase.LocalDataBase;
import com.ahmet.iphonewallpaper.Database.DataSource.Recents.RecentsDataSource;
import com.ahmet.iphonewallpaper.Database.DataSource.Model.Recents;
import com.ahmet.iphonewallpaper.Database.SqliteDatabase.FavoriteDatabaseSQLite;
import com.ahmet.iphonewallpaper.Helper.SaveImageHelper;
import com.ahmet.iphonewallpaper.Model.WallpaperItem;
import com.ahmet.iphonewallpaper.R;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.share.Sharer;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;
import com.facebook.share.widget.ShareDialog;
import com.github.clans.fab.FloatingActionMenu;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import dmax.dialog.SpotsDialog;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class ViewWallpaper extends AppCompatActivity {

    // View
    private CollapsingToolbarLayout collapsingToolbarLayout;
   // private FloatingActionButton fabSetWallpaper,fabDownload,fabAddRecents;
    private ImageView imageSetWallpaper;
    private RelativeLayout relativeLayout;

    private com.github.clans.fab.FloatingActionButton
            fabShareFacebook,fabSetWallpaper,
            fabDownload,fabShare,fabFavorite;

    private FloatingActionMenu fabMenuShare;

    // Room Database
    CompositeDisposable compositeDisposable;
    RecentRepository recentRepository;

    private InterstitialAd mInterstitialAd;

    private DatabaseReference mFirebaseDatabase;

    // Facebook Share
    private CallbackManager callbackManager;
    private ShareDialog shareDialog;

    // Check Internet Connection
    private CheckInternetConnection connection;

    private SaveSettings saveSettings;

    private int checkWallpaper;
    private String imageLink;
    private FavoriteDatabaseSQLite databaseFavoritr;
    private WallpaperItem wallpaperItem;

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

        MobileAds.initialize(this, "ca-app-pub-4765070079723849~5007430629");
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId("ca-app-pub-4765070079723849/6020361134");
        mInterstitialAd.loadAd(new AdRequest.Builder().build());
        if (mInterstitialAd.isLoaded())
            mInterstitialAd.show();
        else
            Log.d("InterstitialAd", "The interstitial wasn't loaded yet.");

        // Init Check Internet
        connection = new CheckInternetConnection(this);

        databaseFavoritr = new FavoriteDatabaseSQLite(ViewWallpaper.this);
        imageLink = Common.wallpaperSelected.getImageUrl();
        wallpaperItem = new WallpaperItem(imageLink);
        checkWallpaper = databaseFavoritr.checkData(imageLink);


        compositeDisposable = new CompositeDisposable();
        final LocalDataBase database = (LocalDataBase) LocalDataBase.getInstance(this);
        recentRepository = RecentRepository.getInstance(RecentsDataSource.getInstance(database.recentsDAO()));


        relativeLayout = findViewById(R.id.relative_layout);
        fabSetWallpaper = findViewById(R.id.fab_set_wallpaper);
        fabDownload = findViewById(R.id.fab_download);
        fabShareFacebook = findViewById(R.id.fab_share_facebook);
        fabShare = findViewById(R.id.fab_share);
        fabMenuShare = findViewById(R.id.menu_fab_share);
        fabFavorite = findViewById(R.id.fab_favorite);
        imageSetWallpaper = findViewById(R.id.image_thumb_wallpaper);

        if (checkWallpaper > 0){
            fabFavorite.setImageResource(R.drawable.ic_favorite);
        }else {
//            databaseFavoritr.deleteData(wallpaperItem);
            fabFavorite.setImageResource(R.drawable.ic_favorite_border);
        }

//        if (!connection.isConnectInternet()){
//            Picasso.with(this)
//                    .load(R.drawable.ic_terrain_black_24dp)
//                    .into(imageSetWallpaper);
//        }

        // Room Database
//        collapsingToolbarLayout.setCollapsedTitleTextAppearance(R.style.CollapsedAppBar);
//        collapsingToolbarLayout.setExpandedTitleTextAppearance(R.style.ExpandedAppBar);
//        collapsingToolbarLayout.setTitle(Common.CATEGORY_SELECTED);

        mFirebaseDatabase = FirebaseDatabase.getInstance().getReference();

//        mFirebaseDatabase
//                .child(Common.STR_WALLPAPER).child(Common.select_background_key)
//                .addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                String image = dataSnapshot.child("imageUrl").getValue().toString();
//                Toast.makeText(ViewWallpaper.this, " " + image, Toast.LENGTH_SHORT).show();
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });

        //Facebook
        callbackManager = CallbackManager.Factory.create();
        shareDialog = new ShareDialog(this);

        Picasso.with(getApplicationContext())
                .load(Common.wallpaperSelected.getImageUrl())
                .into(imageSetWallpaper);

        // View Count
        increaseViewCount();

        // add To Recents
        addToRecents();


        fabSetWallpaper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Picasso.with(getApplicationContext())
                        .load(Common.wallpaperSelected.getImageUrl())
                        .into(target);

                if (mInterstitialAd.isLoaded())
                    mInterstitialAd.show();
                else
                    Log.d("InterstitialAd", "The interstitial wasn't loaded yet.");
            }
        });

        fabShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri imageUrl = Uri.parse(Common.wallpaperSelected.getImageUrl());
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("image/*");
                shareIntent.putExtra(Intent.EXTRA_STREAM,imageUrl);
                startActivity(shareIntent);

                //shareImage(Common.wallpaperSelected.getImageUrl());

                if (mInterstitialAd.isLoaded())
                    mInterstitialAd.show();
                else
                    Log.d("InterstitialAd", "The interstitial wasn't loaded yet.");
            }
        });


        fabDownload.setOnClickListener(new View.OnClickListener() {

            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {

                AlertDialog dialog = new SpotsDialog(ViewWallpaper.this);
                dialog.show();
                dialog.setMessage(getString(R.string.please_wait));

//                if (ActivityCompat.checkSelfPermission(ViewWallpaper.this,
//                        Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
//                    requestPermissions(new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE},
//                            Common.PERMISSION_REQUEST_CODE);
//                }else{

                    String fileName = UUID.randomUUID().toString() + ".png";
                    Picasso.with(ViewWallpaper.this)
                            .load(Common.wallpaperSelected.getImageUrl())
                            .into(new SaveImageHelper(ViewWallpaper.this,
                                    dialog,
                                    getApplicationContext().getContentResolver(),
                                    fileName, "AHMET ViewWallpapers Image"));
               // }

                if (mInterstitialAd.isLoaded())
                    mInterstitialAd.show();
                else
                    Log.d("InterstitialAd", "The interstitial wasn't loaded yet.");
            }
        });

        fabShareFacebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create Callback
                shareDialog.registerCallback(callbackManager, new FacebookCallback<Sharer.Result>() {
                    @Override
                    public void onSuccess(Sharer.Result result) {
                        Toast.makeText(ViewWallpaper.this, R.string.share_successfull+"", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCancel() {
                        Toast.makeText(ViewWallpaper.this, R.string.share_cancelled+"", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(FacebookException error) {
                        Toast.makeText(ViewWallpaper.this, error.getMessage()+"", Toast.LENGTH_SHORT).show();
                    }
                });


                // We Will fetch phone from link and convert to bitmab
                Picasso.with(getApplicationContext())
                        .load(Common.wallpaperSelected.getImageUrl())
                        .into(fbConvertBitmab);

                if (mInterstitialAd.isLoaded())
                    mInterstitialAd.show();
                else
                    Log.d("InterstitialAd", "The interstitial wasn't loaded yet.");

            }
        });


        fabFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (checkWallpaper > 0){
                    databaseFavoritr.deleteData(wallpaperItem.getImageID());
                    fabFavorite.setImageResource(R.drawable.ic_favorite_border);
                   // Toast.makeText(ViewWallpaper.this, "deleted Successfull", Toast.LENGTH_SHORT).show();
                }else {
                    databaseFavoritr.addData(wallpaperItem);
                    fabFavorite.setImageResource(R.drawable.ic_favorite);
                   // Toast.makeText(ViewWallpaper.this, "done add to database", Toast.LENGTH_SHORT).show();
                }
            }
        });

        if (mInterstitialAd.isLoaded())
            mInterstitialAd.show();
        else
            Log.d("InterstitialAd", "The interstitial wasn't loaded yet.");

    }

    public void shareImage(String imageUrl){

        Picasso.with(this)
                .load(imageUrl)
                .into(new Target() {
                    @Override
                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                        Intent intent = new Intent(Intent.ACTION_SEND);
                        intent.setType("image/*");
                        intent.putExtra(Intent.EXTRA_STREAM, getLocalBitmapUri(bitmap));
                        startActivity(intent);

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
                    getExternalFilesDir(Environment.DIRECTORY_PICTURES),
                    UUID.randomUUID().toString() + ".png");
            FileOutputStream outputStream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 90, outputStream);
            outputStream.close();
            bmapUri = Uri.fromFile(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bmapUri;
    }

    private void increaseViewCount() {

        FirebaseDatabase.getInstance()
                .getReference(Common.STR_WALLPAPER)
                .child(Common.select_background_key)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.hasChild("countView")){
                            WallpaperItem wallpaperItem = dataSnapshot.getValue(WallpaperItem.class);
                            long count = wallpaperItem.getcountView() + 1;

                            //Update
                            Map updateViewCount = new HashMap<>();
                            updateViewCount.put("countView", count);
                            FirebaseDatabase.getInstance()
                                    .getReference(Common.STR_WALLPAPER)
                                    .child(Common.select_background_key)
                                    .updateChildren(updateViewCount)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
//                                            Toast.makeText(ViewWallpaper.this,
//                                                    "Update Count View Success",
//                                                        Toast.LENGTH_SHORT).show();
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
//                                    Toast.makeText(ViewWallpaper.this,
//                                            "Can not update Count View",
//                                                Toast.LENGTH_SHORT).show();
                                }
                            });
                        }else{ // if view count is not set (default 1)

                            Map updateViewCount = new HashMap<>();
                            updateViewCount.put("countView", Long.valueOf(1));
                            FirebaseDatabase.getInstance()
                                    .getReference(Common.STR_WALLPAPER)
                                    .child(Common.select_background_key)
                                    .updateChildren(updateViewCount)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
//                                            Toast.makeText(ViewWallpaper.this,
//                                                    "make Default Success",
//                                                        Toast.LENGTH_SHORT).show();
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
//                                    Toast.makeText(ViewWallpaper.this,
//                                            "Can not default Count View",
//                                            Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

    }

    private void addToRecents() {

        Disposable disposable = Observable.create(new ObservableOnSubscribe<Object>() {

            @Override
            public void subscribe(ObservableEmitter<Object> e) {
                Recents recents = new Recents(Common.wallpaperSelected.getImageUrl(),
                        Common.wallpaperSelected.getCategoryId(),
                        String.valueOf(System.currentTimeMillis()),
                        Common.select_background_key);
                recentRepository.insertRecents(recents);
                e.onComplete();
            }
        }).observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Consumer<Object>() {
                    @Override
                    public void accept(Object o) {

                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Log.e("Error", throwable.getMessage());
                    }
                }, new Action() {
                    @Override
                    public void run() throws Exception {

                    }
                });
        compositeDisposable.add(disposable);
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case Common.PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    AlertDialog dialog = new SpotsDialog(ViewWallpaper.this);
                    dialog.show();
                    dialog.setMessage(getString(R.string.please_wait));

                    String fileName = UUID.randomUUID().toString() + ".png";
                    Picasso.with(getBaseContext())
                            .load(Common.wallpaperSelected.getImageUrl())
                            .into(new SaveImageHelper(getBaseContext(),
                                    dialog,
                                    getApplicationContext().getContentResolver(),
                                    fileName, "AHMET ViewWallpapersFavorite Image"));
                   // dialog.dismiss();

                }else {
                    //Toast.makeText(this, "You need accept this permission to download image", Toast.LENGTH_SHORT).show();
                    Snackbar.make(relativeLayout,
                            R.string.accept_permission,
                                  Snackbar.LENGTH_SHORT).show();
                }
                break;
        }
    }



    @Override
    protected void onDestroy() {
        Picasso.with(this).cancelRequest(target);
        compositeDisposable.clear();
        super.onDestroy();
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home){
            finish(); // Close activity when click back button
        }
        return super.onOptionsItemSelected(item);
    }

    void saveImage(){
        Dexter.withActivity(this)
                .withPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        if (report.areAllPermissionsGranted()){
                            AlertDialog dialog = new SpotsDialog(ViewWallpaper.this);
                            dialog.show();
                            dialog.setMessage(getString(R.string.please_wait));

                            String fileName = UUID.randomUUID().toString() + ".png";
                            Picasso.with(getBaseContext())
                                    .load(Common.wallpaperSelected.getImageUrl())
                                    .into(new SaveImageHelper(getBaseContext(),
                                            dialog,
                                            getApplicationContext().getContentResolver(),
                                            fileName, "AHMET ViewWallpapersFavorite Image"));
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                });
    }
}
