package com.ahmet.iphonewallpaper.Fragments;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.ahmet.iphonewallpaper.Adapter.FavoriteAdapter;
import com.ahmet.iphonewallpaper.Database.SqliteDatabase.FavoriteDatabaseSQLite;
import com.ahmet.iphonewallpaper.Model.WallpaperItem;
import com.ahmet.iphonewallpaper.R;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

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
