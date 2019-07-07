package com.ahmet.iphonewallpaper.UI;

import android.content.Context;
import android.content.Intent;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.LinearLayout;

import com.ahmet.iphonewallpaper.Config.CheckInternetConnection;
import com.ahmet.iphonewallpaper.Config.Common;
import com.ahmet.iphonewallpaper.Config.SaveSettings;
import com.ahmet.iphonewallpaper.Interface.ItemClickListener;
import com.ahmet.iphonewallpaper.Model.WallpaperItem;
import com.ahmet.iphonewallpaper.R;
import com.ahmet.iphonewallpaper.ViewHolder.ListWallpaperHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.doubleclick.PublisherAdRequest;
import com.google.android.gms.ads.doubleclick.PublisherAdView;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ListWallpaper extends AppCompatActivity{

    private Query query;
    private FirebaseRecyclerOptions<WallpaperItem> firebaseoptions;
    private FirebaseRecyclerAdapter<WallpaperItem, ListWallpaperHolder> firebaseAdapter;

    private RecyclerView mRecyclerWallpaper;
    private LinearLayout mLinearDisconnection;

    private SaveSettings saveSettings;
    private CheckInternetConnection connection;

    private final String RECYCLER_POSITION_KEY = "recycler_position";
    private static String LIST_STATE = "list_state";
    private Parcelable mSaveRecyclerState;
    private ArrayList<WallpaperItem> mLiatWallpaper = new ArrayList<>();
    private RecyclerView.LayoutManager mLayoutManager;

    private PublisherAdView mPublisherAdView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        saveSettings = new SaveSettings(this);
        if (saveSettings.getNightModeState() == true){
            setTheme(R.style.DarkTheme);
        }else {
            setTheme(R.style.AppTheme);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_wallpaper);

        MobileAds.initialize(this, "ca-app-pub-4765070079723849~5007430629");

        PublisherAdView adView = new PublisherAdView(this);
        adView.setAdSizes(AdSize.BANNER);

        adView.setAdUnitId("ca-app-pub-4765070079723849/6128940602");

        mPublisherAdView = findViewById(R.id.publisherAdView);
        PublisherAdRequest adRequest = new PublisherAdRequest.Builder()
                //.addTestDevice("33BE2250B43518CCDA7DE426D04EE231")
                .build();
        mPublisherAdView.loadAd(adRequest);

        connection = new CheckInternetConnection(this);
        if (connection.isConnectInternet()){
            mPublisherAdView.setVisibility(View.VISIBLE);
        }else {
            mPublisherAdView.setVisibility(View.GONE);
        }


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(Common.CATEGORY_SELECTED);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //getSupportActionBar().setDisplayShowHomeEnabled(true);

        connection = new CheckInternetConnection(this);

        mLayoutManager = new GridLayoutManager(this, 2,
                        LinearLayoutManager.VERTICAL, false);

        mRecyclerWallpaper = findViewById(R.id.recycler_list_wallpaper);
        mRecyclerWallpaper.setHasFixedSize(true);
        mRecyclerWallpaper.setLayoutManager(mLayoutManager);

        mLinearDisconnection = findViewById(R.id.linear_list_wallpaper_disconecction);

        if (connection.isConnectInternet()){
            mLinearDisconnection.setVisibility(View.GONE);
        }else {
            mLinearDisconnection.setVisibility(View.VISIBLE);
            mRecyclerWallpaper.setVisibility(View.GONE);
        }

        loadWallpaperList();


    }

    private void loadWallpaperList() {

        query = FirebaseDatabase.getInstance().getReference().child(Common.STR_WALLPAPER)
                .orderByChild("categoryId").equalTo(Common.STR_CATEGORY_ID_SELECTED);

        firebaseoptions = new FirebaseRecyclerOptions.Builder<WallpaperItem>()
                .setQuery(query, WallpaperItem.class)
                .build();

        firebaseAdapter = new FirebaseRecyclerAdapter<WallpaperItem, ListWallpaperHolder>(firebaseoptions) {
            @Override
            protected void onBindViewHolder(@NonNull final ListWallpaperHolder wallpaperHolder,
                                            int position, @NonNull final WallpaperItem wallpaperItem) {

                getSupportActionBar()
                        .setTitle(Common.CATEGORY_SELECTED
                                + " " + firebaseoptions.getSnapshots().size());

                Picasso.with(getBaseContext())
                        .load(wallpaperItem.getImageUrl())
                        .networkPolicy(NetworkPolicy.OFFLINE)
                        .into(wallpaperHolder.imageWallpaper, new Callback() {
                            @Override
                            public void onSuccess() {
//

                            }

                            @Override
                            public void onError() {
                                Picasso.with(ListWallpaper.this)
                                        .load(wallpaperItem.getImageUrl())
                                        .error(R.drawable.ic_terrain_black_24dp)
                                        //.networkPolicy(NetworkPolicy.OFFLINE)
                                        .into(wallpaperHolder.imageWallpaper, new Callback() {
                                            @Override
                                            public void onSuccess() {

                                            }

                                            @Override
                                            public void onError() {
                                                Log.e("ERROR AHMET", "Couldn’t fetch Image");
                                            }
                                        });
                            }
                        });

                wallpaperHolder.setItemClickListener(ListWallpaper.this, new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position) {
                        // Code late for detalis ViewWallpapersFavorite

                        Snackbar.make(mRecyclerWallpaper,
                                Common.select_background_key +
                                        " / " + wallpaperItem.getImageUrl(),
                                Snackbar.LENGTH_INDEFINITE);
                                //.show();

                        Intent intent = new Intent(ListWallpaper.this, ViewWallpaper.class);
                        Common.wallpaperSelected = wallpaperItem;
                        Common.select_background_key = firebaseAdapter.getRef(position).getKey();
                        startActivity(intent);
                    }
                });

            }

            @NonNull
            @Override
            public ListWallpaperHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                View convertView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.raw_wallpaper_list, parent, false);

                int height = parent.getMeasuredHeight() / 2;
                convertView.setMinimumHeight(height);

                return new ListWallpaperHolder(convertView);
            }
        };

        firebaseAdapter.startListening();
      //  mRecyclerWallpaper.setAdapter(firebaseAdapter);
        runAnimations(mRecyclerWallpaper);

    }

    @Override
    protected void onStart() {
        super.onStart();

        if (firebaseAdapter != null){
            firebaseAdapter.startListening();
        }
    }


    @Override
    protected void onResume() {
        super.onResume();

        if (firebaseAdapter != null){
            firebaseAdapter.startListening();
        }
    }

    @Override
    protected void onStop() {

        if (firebaseAdapter != null){
            firebaseAdapter.stopListening();
        }

        super.onStop();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home){
            finish(); // Close activity when click back button
        }
        return super.onOptionsItemSelected(item);
    }


    public int mScrollPosition;




    public void runAnimations(RecyclerView mRecyclerView){

        Context mContext = mRecyclerView.getContext();
        LayoutAnimationController controller = null;
        controller = AnimationUtils.loadLayoutAnimation(mContext, R.anim.layout_slide_from_bottom);

        mRecyclerView.setAdapter(firebaseAdapter);

        mRecyclerView.setLayoutAnimation(controller);
        mRecyclerView.getAdapter().notifyDataSetChanged();
        mRecyclerView.scheduleLayoutAnimation();

    }

}
