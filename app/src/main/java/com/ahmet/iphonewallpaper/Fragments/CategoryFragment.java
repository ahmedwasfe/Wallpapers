package com.ahmet.iphonewallpaper.Fragments;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.core.view.MenuItemCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.ahmet.iphonewallpaper.Adapter.TrendingAdapter;
import com.ahmet.iphonewallpaper.Config.CheckInternetConnection;
import com.ahmet.iphonewallpaper.Config.Common;
import com.ahmet.iphonewallpaper.Config.SaveSettings;
import com.ahmet.iphonewallpaper.Interface.ITrendingLoadDone;
import com.ahmet.iphonewallpaper.Interface.ItemClickListener;
import com.ahmet.iphonewallpaper.Model.WallpaperItem;
import com.ahmet.iphonewallpaper.Service.PicassoLoadingService;
import com.ahmet.iphonewallpaper.UI.ListWallpaper;
import com.ahmet.iphonewallpaper.Model.Category;
import com.ahmet.iphonewallpaper.R;
import com.ahmet.iphonewallpaper.UI.ViewWallpaper;
import com.ahmet.iphonewallpaper.ViewHolder.CategoryHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import ss.com.bannerslider.Slider;
import ss.com.bannerslider.event.OnSlideClickListener;

/**
 * A simple {@link Fragment} subclass.
 */
public class CategoryFragment extends Fragment implements ITrendingLoadDone {

    // Firebase
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference mReferenceCategory;

    // Firebase UI Adaptr
    FirebaseRecyclerOptions<Category> firebaseOptions;
    FirebaseRecyclerAdapter<Category, CategoryHolder> firebaseAdapter;

    // View
    private RecyclerView mRecyclerCategory;
    private Slider mSlider;
    private LinearLayout mLinearDisconnection;

    private SaveSettings saveSettings;

    private ITrendingLoadDone mLisener;

    private FirebaseDatabase database;
    private DatabaseReference mRefReferenceTrending;


    private CheckInternetConnection connection;

    View convertView;

    public CategoryFragment() {

        // Required empty public constructor
        firebaseDatabase = FirebaseDatabase.getInstance();
        //firebaseDatabase.goOffline();
        mReferenceCategory = firebaseDatabase.getReference(Common.STR_CATEGORY_BG);
        mReferenceCategory.keepSynced(true);

        database = FirebaseDatabase.getInstance();
        mRefReferenceTrending = database.getReference(Common.STR_WALLPAPER);
        mRefReferenceTrending.keepSynced(true);


        mLisener = this;


        firebaseOptions = new FirebaseRecyclerOptions.Builder<Category>()
                .setQuery(mReferenceCategory, Category.class) // Select all
                .build();


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
        convertView = inflater.inflate(R.layout.fragment_category, container, false);

        mRecyclerCategory = convertView.findViewById(R.id.recycler_category);
        mRecyclerCategory.setHasFixedSize(true);
        mRecyclerCategory.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));


        mSlider = convertView.findViewById(R.id.slider);
        Slider.init(new PicassoLoadingService(getActivity()));

        mLinearDisconnection = convertView.findViewById(R.id.linear_category_disconecction);

        if (connection.isConnectInternet()) {
            getCategory();
            mLinearDisconnection.setVisibility(View.GONE);
        }else {
            mLinearDisconnection.setVisibility(View.VISIBLE);
            mRecyclerCategory.setVisibility(View.GONE);
            mSlider.setVisibility(View.GONE);
        }

        return convertView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

            getCategory();
            setCategory();
            getTrending();
          //  loadCountWallpaperList();
            runAnimations(mRecyclerCategory);
    }

    private void getCategory(){

        firebaseAdapter = new FirebaseRecyclerAdapter<Category, CategoryHolder>(firebaseOptions) {
            @Override
            protected void onBindViewHolder(@NonNull final CategoryHolder categoryHolder,
                                            int position, @NonNull final Category category) {

                Picasso.with(getActivity())
                        .load(category.getImageLink())
                        .placeholder(R.drawable.ic_terrain_black_24dp)
                        .networkPolicy(NetworkPolicy.OFFLINE)
                        .into(categoryHolder.categoryImage, new Callback() {
                            @Override
                            public void onSuccess() {

                            }

                            @Override
                            public void onError() {
                                // try again online if cache failed

                                Picasso.with(getActivity())
                                        .load(category.getImageLink())
                                        .error(R.drawable.ic_terrain_black_24dp)
                                        .into(categoryHolder.categoryImage, new Callback() {
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


                categoryHolder.categoryName.setText(category.getName());

                categoryHolder.setItemClickListener(getActivity(),new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position) {
                        // Code late for detalis Category

                        Common.STR_CATEGORY_ID_SELECTED = firebaseAdapter.getRef(position).getKey();
                        Common.CATEGORY_SELECTED = category.getName();
                        Intent intent = new Intent(getContext(), ListWallpaper.class);
                        startActivity(intent);
                    }
                });

            }

            @NonNull
            @Override
            public CategoryHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                View convertView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.raw_category, parent, false);

                return new CategoryHolder(convertView);
            }
        };
    }

    private void setCategory() {
        firebaseAdapter.startListening();
        mRecyclerCategory.setAdapter(firebaseAdapter);
    }

    @Override
    public void onStart() {
        super.onStart();

        if (firebaseAdapter != null){
            firebaseAdapter.startListening();
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        if (firebaseAdapter != null){
            firebaseAdapter.startListening();
        }
    }

    @Override
    public void onStop() {

        if (firebaseAdapter != null){
            firebaseAdapter.stopListening();
        }

        super.onStop();
    }

    private void getTrending() {

        Query query = mRefReferenceTrending.orderByChild("countView")
                .limitToLast(15); // get 10 item have biggest view count

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<WallpaperItem> mList = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    WallpaperItem image = snapshot.getValue(WallpaperItem.class);
                    mList.add(image);
                }
                mLisener.onTrendingLoadDoneLisener(mList);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getActivity(), "" + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public void onTrendingLoadDoneLisener(final List<WallpaperItem> mListTrending) {

        mSlider.setAdapter(new TrendingAdapter(getActivity(), mListTrending));
        mSlider.setInterval(5000);
        mSlider.setLoopSlides(true);
        mSlider.setAnimateIndicators(true);

        mSlider.setOnSlideClickListener(new OnSlideClickListener() {
            @Override
            public void onSlideClick(int position) {

                WallpaperItem wallpaperItem = mListTrending.get(position);
                Intent intent = new Intent(getContext(), ViewWallpaper.class);
                Common.wallpaperSelected = wallpaperItem;
                Common.select_background_key = firebaseAdapter.getRef(position).getKey();
                startActivity(intent);
            }
        });
    }

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