package com.ahmet.iphonewallpaper.Fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.ahmet.iphonewallpaper.Config.CheckInternetConnection;
import com.ahmet.iphonewallpaper.Config.Common;
import com.ahmet.iphonewallpaper.Config.SaveSettings;
import com.ahmet.iphonewallpaper.Interface.ItemClickListener;
import com.ahmet.iphonewallpaper.Model.WallpaperItem;
import com.ahmet.iphonewallpaper.R;
import com.ahmet.iphonewallpaper.UI.ViewWallpaper;
import com.ahmet.iphonewallpaper.ViewHolder.ListWallpaperHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

/**
 * A simple {@link Fragment} subclass.
 */
public class TrendingFragment extends Fragment {


    private RecyclerView mRecyclerTrending;
    private LinearLayout mLinearDisconnection;

    FirebaseDatabase database;
    DatabaseReference mRefReferenceTrending;

    FirebaseRecyclerOptions<WallpaperItem> firebaseOptions;
    FirebaseRecyclerAdapter<WallpaperItem, ListWallpaperHolder> firebaseAdapter;

    private SaveSettings saveSettings;
    private CheckInternetConnection connection;

    public TrendingFragment() {
        // Required empty public constructor

        database = FirebaseDatabase.getInstance();
        mRefReferenceTrending = database.getReference(Common.STR_WALLPAPER);
        mRefReferenceTrending.keepSynced(true);

        Query query = mRefReferenceTrending.orderByChild("countView")
                .limitToLast(10); // get 10 item have biggest view count
        firebaseOptions = new FirebaseRecyclerOptions.Builder<WallpaperItem>()
                .setQuery(query, WallpaperItem.class)
                .build();

        firebaseAdapter = new FirebaseRecyclerAdapter<WallpaperItem, ListWallpaperHolder>(firebaseOptions) {
            @Override
            protected void onBindViewHolder(@NonNull final ListWallpaperHolder wallpaperHolder,
                                            int position, @NonNull final WallpaperItem wallpaperItem) {


                Picasso.with(getContext())
                        .load(wallpaperItem.getImageUrl())
                        .networkPolicy(NetworkPolicy.OFFLINE)
                        .into(wallpaperHolder.imageWallpaper, new Callback() {
                            @Override
                            public void onSuccess() {

                            }

                            @Override
                            public void onError() {
                                Picasso.with(getActivity())
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

                wallpaperHolder.setItemClickListener(getActivity(),new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position) {
                        // Code late for detalis ViewWallpapersFavorite

                        Intent intent = new Intent(getContext(), ViewWallpaper.class);
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
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        connection = new CheckInternetConnection(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        saveSettings = new SaveSettings(getContext());
        if (saveSettings.getNightModeState() == true){
            getContext().setTheme(R.style.DarkTheme);
        }else {
            getContext().setTheme(R.style.AppTheme);
        }

        // Inflate the layout for this fragment
        View convertView = inflater.inflate(R.layout.fragment_trending, container, false);

        mRecyclerTrending = convertView.findViewById(R.id.recycler_trending);
        mRecyclerTrending.setHasFixedSize(true);
        mRecyclerTrending.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));

        mLinearDisconnection = convertView.findViewById(R.id.linear_trending_disconecction);

        if (connection.isConnectInternet()){
            mLinearDisconnection.setVisibility(View.GONE);
        }else {
            mLinearDisconnection.setVisibility(View.VISIBLE);
            mRecyclerTrending.setVisibility(View.GONE);
        }

        setTrending();

        return convertView;
    }

    private void setTrending() {
        firebaseAdapter.startListening();
        mRecyclerTrending.setAdapter(firebaseAdapter);
    }

    @Override
    public void onStart() {
        super.onStart();
        firebaseAdapter.startListening();
    }

    @Override
    public void onResume() {
        super.onResume();
        firebaseAdapter.startListening();
    }

    @Override
    public void onStop() {
        firebaseAdapter.stopListening();
        super.onStop();
    }
}
