package com.ahmet.iphonewallpaper.Fragments;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.LinearLayout;

import com.ahmet.iphonewallpaper.Adapter.FavoriteAdapter;
import com.ahmet.iphonewallpaper.Database.SqliteDatabase.FavoriteDatabaseSQLite;
import com.ahmet.iphonewallpaper.Model.WallpaperItem;
import com.ahmet.iphonewallpaper.R;

import java.util.ArrayList;
import java.util.List;

public class FavoriteFragment extends Fragment {

    private RecyclerView mRecyclerFavorite;
    private LinearLayout mLinearFavoriteEmpty;

    private FavoriteDatabaseSQLite mFavoriteDatabaseSQLite;
    private List<WallpaperItem> mListFavorite;
    private FavoriteAdapter favoriteAdapter;



    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mFavoriteDatabaseSQLite = new FavoriteDatabaseSQLite(getActivity());
        mListFavorite = new ArrayList<>();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View itemView = inflater.inflate(R.layout.fragment_favorite, container, false);

        mRecyclerFavorite = itemView.findViewById(R.id.recycler_favorite);
        mRecyclerFavorite.setLayoutManager(new StaggeredGridLayoutManager(2, LinearLayout.VERTICAL));

        mLinearFavoriteEmpty = itemView.findViewById(R.id.linear_favorite_empty);

        return itemView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mListFavorite = mFavoriteDatabaseSQLite.getAllData();

        runAnimations(mRecyclerFavorite);

        if (mListFavorite.size() == 0){
            mRecyclerFavorite.setVisibility(View.GONE);
            mLinearFavoriteEmpty.setVisibility(View.VISIBLE);
        }else {
            mRecyclerFavorite.setVisibility(View.VISIBLE);
            mLinearFavoriteEmpty.setVisibility(View.GONE);
        }


        //loadFavorite();
    }


    public void runAnimations(RecyclerView mRecyclerView){

        Context mContext = mRecyclerView.getContext();
        LayoutAnimationController controller = null;
        controller = AnimationUtils.loadLayoutAnimation(mContext, R.anim.layout_slide_from_bottom);

        favoriteAdapter = new FavoriteAdapter(getActivity(), mListFavorite);
        mRecyclerView.setAdapter(favoriteAdapter);

        mRecyclerView.setLayoutAnimation(controller);
        mRecyclerView.getAdapter().notifyDataSetChanged();
        mRecyclerView.scheduleLayoutAnimation();

    }

}
