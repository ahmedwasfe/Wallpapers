package com.ahmet.iphonewallpaper.Fragments;


import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
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

import com.ahmet.iphonewallpaper.Adapter.RecentsAdapter;
import com.ahmet.iphonewallpaper.Config.SaveSettings;
import com.ahmet.iphonewallpaper.Database.DataSource.Recents.RecentRepository;
import com.ahmet.iphonewallpaper.Database.DataSource.LocalDatabase.LocalDataBase;
import com.ahmet.iphonewallpaper.Database.DataSource.Recents.RecentsDataSource;
import com.ahmet.iphonewallpaper.Database.DataSource.Model.Recents;
import com.ahmet.iphonewallpaper.R;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * A simple {@link Fragment} subclass.
 */
@SuppressLint("ValidFragment")
public class RecentsFragment extends Fragment {

    private RecyclerView mRecyclerRecent;
    private LinearLayout mLinearRecentEmpty;

    private List<Recents> listRecents;
    private RecentsAdapter recentsAdapter;

    Context context;

    // Room Database
    CompositeDisposable compositeDisposable;
    RecentRepository recentRepository;

    private SaveSettings saveSettings;

   // public RecentsFragment(){}

    public RecentsFragment() {
        // Required empty public constructor
        //this.context = context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        compositeDisposable = new CompositeDisposable();
        LocalDataBase database = (LocalDataBase) LocalDataBase.getInstance(getActivity());
        recentRepository = RecentRepository.getInstance(RecentsDataSource.getInstance(database.recentsDAO()));


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
        View itemView = inflater.inflate(R.layout.fragment_recents, container, false);

        mRecyclerRecent = itemView.findViewById(R.id.recycler_recent);
        mRecyclerRecent.setHasFixedSize(true);
        mRecyclerRecent.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));

        mLinearRecentEmpty = itemView.findViewById(R.id.linear_recent_empty);

        listRecents = new ArrayList<>();

//        recentsAdapter = new RecentsAdapter(getActivity(), listRecents);
//        mRecyclerRecent.setAdapter(recentsAdapter);
        runAnimations(mRecyclerRecent);

        return itemView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        loadRecents();

    }

    public void runAnimations(RecyclerView mRecyclerView){

        Context mContext = mRecyclerView.getContext();
        LayoutAnimationController controller = null;
        controller = AnimationUtils.loadLayoutAnimation(mContext, R.anim.layout_slide_from_bottom);

        recentsAdapter = new RecentsAdapter(getActivity(), listRecents);
        mRecyclerView.setAdapter(recentsAdapter);

        mRecyclerView.setLayoutAnimation(controller);
        mRecyclerView.getAdapter().notifyDataSetChanged();
        mRecyclerView.scheduleLayoutAnimation();

    }

    private void loadRecents() {

        Disposable disposable = recentRepository.getAllRecents()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Consumer<List<Recents>>() {
                    @Override
                    public void accept(List<Recents> recents) throws Exception {
                        onGetAllRecentsSuccess(recents);
                        runAnimations(mRecyclerRecent);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Log.e("ERROR", throwable.getMessage());
                    }
                });
        compositeDisposable.add(disposable);
    }

    private void onGetAllRecentsSuccess(List<Recents> recents) {

        listRecents.clear();
        listRecents.addAll(recents);

        Log.e("Recents_Size", listRecents.size()+"");

        if (listRecents.size() == 0){
            mLinearRecentEmpty.setVisibility(View.VISIBLE);
            mRecyclerRecent.setVisibility(View.GONE);
        }else {
            mLinearRecentEmpty.setVisibility(View.GONE);
            mRecyclerRecent.setVisibility(View.VISIBLE);
        }

        recentsAdapter.notifyDataSetChanged();
    }

    @Override
    public void onDestroy() {
        compositeDisposable.clear();
        super.onDestroy();
    }
}
