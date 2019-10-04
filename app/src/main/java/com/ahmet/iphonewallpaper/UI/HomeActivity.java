package com.ahmet.iphonewallpaper.UI;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.graphics.Color;
import android.net.Uri;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;

import com.ahmet.iphonewallpaper.Adapter.FragmentsAdapter;
import com.ahmet.iphonewallpaper.Config.CheckInternetConnection;
import com.ahmet.iphonewallpaper.Config.Common;
import com.ahmet.iphonewallpaper.Config.SaveSettings;
import com.ahmet.iphonewallpaper.Fragments.CategoryFragment;
import com.ahmet.iphonewallpaper.Fragments.RecentsFragment;
import com.ahmet.iphonewallpaper.Fragments.SettingsFragment;
import com.ahmet.iphonewallpaper.Fragments.TrendingFragment;
import com.ahmet.iphonewallpaper.Fragments.FavoriteFragment;
import com.ahmet.iphonewallpaper.R;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.doubleclick.PublisherAdRequest;
import com.google.android.gms.ads.doubleclick.PublisherAdView;
import com.google.firebase.auth.FirebaseAuth;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ogaclejapan.smarttablayout.SmartTabLayout;


public class HomeActivity extends AppCompatActivity {

    // View
    private SmartTabLayout mTabLayout;
    private ViewPager mViewPager;
    private Toolbar mToolbar;
    private NavigationView mNavigationView;


    private CheckInternetConnection connection;

    private AlertDialog alertDialog;

    private SaveSettings saveSettings;

    private static final int ID_NOTIFICATION = 888;
    private static final String CHANNEL_ID = "channel wallpapers";

    long countWallpapers = 0;

    private PublisherAdView mPublisherAdView;

    //@RequiresApi(api = Build.VERSION_CODES.M)
    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        saveSettings = new SaveSettings(this);
        if (saveSettings.getNightModeState() == true) {
            setTheme(R.style.DarkTheme);
        } else {
            setTheme(R.style.AppTheme);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        MobileAds.initialize(this, "ca-app-pub-4765070079723849~5007430629");

        PublisherAdView adView = new PublisherAdView(this);
        adView.setAdSizes(AdSize.BANNER);

        adView.setAdUnitId("ca-app-pub-4765070079723849/1665386996");

        mPublisherAdView = findViewById(R.id.publisherAdView);
        PublisherAdRequest adRequest = new PublisherAdRequest.Builder()
                //.addTestDevice("33BE2250B43518CCDA7DE426D04EE231")
                .build();
        mPublisherAdView.loadAd(adRequest);
       // mPublisherAdView.setVisibility(View.GONE);

        mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle(R.string.app_name);


        mTabLayout = findViewById(R.id.tab_layout);
        mViewPager = findViewById(R.id.view_pager);
        mNavigationView = findViewById(R.id.navigation_view);



        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        Common.PERMISSION_REQUEST_CODE);
            }
        }


        FragmentsAdapter fragmentsAdapter = new FragmentsAdapter(getSupportFragmentManager());

        fragmentsAdapter.addFragment(new CategoryFragment(), getString(R.string.categores));
        fragmentsAdapter.addFragment(new RecentsFragment(), getString(R.string.recents));
        fragmentsAdapter.addFragment(new TrendingFragment(), getString(R.string.trending));
        fragmentsAdapter.addFragment(new FavoriteFragment(), getString(R.string.favorite));
        fragmentsAdapter.addFragment(new SettingsFragment(), getString(R.string.action_settings));


        mViewPager.setAdapter(fragmentsAdapter);
//        mTabLayout.setupWithViewPager(mViewPager);

        mTabLayout.setViewPager(mViewPager);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mTabLayout.getTabAt(0).setTooltipText(getString(R.string.categores));
            mTabLayout.getTabAt(1).setTooltipText(getString(R.string.recents));
            mTabLayout.getTabAt(2).setTooltipText(getString(R.string.trending));
            mTabLayout.getTabAt(3).setTooltipText(getString(R.string.favorite));
            mTabLayout.getTabAt(4).setTooltipText(getString(R.string.action_settings));
        }


        createChannelNotification();

        connection = new CheckInternetConnection(this);
        if (connection.isConnectInternet()) {

            Snackbar mSnackBar = Snackbar.make(mViewPager, R.string.internet_connection, Snackbar.LENGTH_SHORT);
            View snackBarView = mSnackBar.getView();
            snackBarView.setBackgroundColor(Color.parseColor("#16A463"));
            mSnackBar.show();
            mPublisherAdView.setVisibility(View.VISIBLE);
            // Toast.makeText(this, getString(R.string.internet_connection), Toast.LENGTH_SHORT).show();

            // alertDialog.show();
            // alertDialog.dismiss();
        } else {
            mPublisherAdView.setVisibility(View.GONE);
//            Snackbar mSnackBar = Snackbar.make(mViewPager,R.string.no_internet_connection, Snackbar.LENGTH_INDEFINITE);
//            View snackBarView = mSnackBar.getView();
//            snackBarView.setBackgroundColor(Color.parseColor("#FF143D"));
//            mSnackBar.show();
//            fragmentsAdapter.addFragment(new RecentsFragment(this), "");
            // Toast.makeText(this, getString(R.string.no_internet_connection), Toast.LENGTH_SHORT).show();

            // alertDialog.show();
        }


        FirebaseDatabase.getInstance()
                .getReference(Common.STR_WALLPAPER)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                        countWallpapers = dataSnapshot.getChildrenCount();

                        long newWallpapers = countWallpapers + 1;


//                Toast.makeText(HomeActivity.this, "ChildrenCount / "+countWallpapers,
//                        Toast.LENGTH_LONG).show();
                        Common.setBadge(HomeActivity.this, (int) countWallpapers);

                        if (countWallpapers == newWallpapers) {
//                    Toast.makeText(HomeActivity.this, "not new wallapers",
//                            Toast.LENGTH_LONG).show();

                        } else {
//                    Toast.makeText(HomeActivity.this, "new Wallpapers", Toast.LENGTH_LONG).show();
                            createNotification();
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

        // check if not sgin in then navigat sign-in page
//        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
//            startActivityForResult(AuthUI.getInstance().createSignInIntentBuilder().build(),
//                    Common.SIGN_IN_REQUEST_CODE);
//        } else {
//            Snackbar.make(mViewPager, new StringBuilder(getString(R.string.welcome_user) + " ")
//                            .append(FirebaseAuth.getInstance().getCurrentUser().getEmail()).toString(),
//                    Snackbar.LENGTH_SHORT);
//                   // .show();
//        }


    }


    //@RequiresApi(api = Build.VERSION_CODES.M)
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//
//        if (requestCode == Common.SIGN_IN_REQUEST_CODE) {
//            if (resultCode == RESULT_OK) {
////
//                Snackbar.make(mViewPager, new StringBuilder(getString(R.string.welcome_user) + " ")
//                                .append(FirebaseAuth.getInstance().getCurrentUser().getEmail()).toString(),
//                        Snackbar.LENGTH_SHORT);
//                      //  .show();
//
//                if (ActivityCompat.checkSelfPermission(this,
//                        Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
//                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                        requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
//                                Common.PERMISSION_REQUEST_CODE);
//                    }
//                }
//
//            }
//        }
//
//    }

    void signOut() {
        FirebaseAuth.getInstance().signOut();
        finish();
    }

    private void createChannelNotification() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID,
                    "name", NotificationManager.IMPORTANCE_DEFAULT);
            notificationChannel.setDescription("Description");
            notificationChannel.setShowBadge(true);
            notificationChannel.setVibrationPattern(new long[]{300, 500, 700, 900});

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(notificationChannel);
        }
    }

    private void createNotification() {

        FirebaseDatabase.getInstance().getReference()
                .child(Common.STR_WALLPAPER)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        long countWallpapers = dataSnapshot.getChildrenCount();

                        if (countWallpapers > dataSnapshot.getChildrenCount()) {

                            Uri soundUri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.alarm_rooster);

                            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(HomeActivity.this, CHANNEL_ID)
                                    .setContentTitle("Wallpapers")
                                    .setContentText("new Wallpapers")
                                    .setSmallIcon(R.mipmap.ic_launcher_round)
                                    .setSound(soundUri)
                                    .setPriority(NotificationCompat.PRIORITY_HIGH);

                            Notification notification = mBuilder.build();
                            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(HomeActivity.this);
                            notificationManager.notify(ID_NOTIFICATION, notification);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

    }

}