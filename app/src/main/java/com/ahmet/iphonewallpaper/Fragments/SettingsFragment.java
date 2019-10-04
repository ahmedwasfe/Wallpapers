package com.ahmet.iphonewallpaper.Fragments;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import androidx.annotation.Nullable;
import com.google.android.material.snackbar.Snackbar;
import androidx.fragment.app.Fragment;
import androidx.cardview.widget.CardView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.ahmet.iphonewallpaper.Adapter.FavoriteAdapter;
import com.ahmet.iphonewallpaper.Config.SaveSettings;
import com.ahmet.iphonewallpaper.Database.DataSource.LocalDatabase.LocalDataBase;
import com.ahmet.iphonewallpaper.Database.DataSource.Recents.RecentRepository;
import com.ahmet.iphonewallpaper.Database.DataSource.Recents.RecentsDataSource;
import com.ahmet.iphonewallpaper.Database.SqliteDatabase.FavoriteDatabaseSQLite;
import com.ahmet.iphonewallpaper.R;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class SettingsFragment extends Fragment {

    private Switch mNightMode;
    private CardView mCardStyleApp, mCardHelpApp,
                    mCardShareApp, mCardAboutApp,
                    mCardClearRecents, mCardClearFavorite;

    private LinearLayout mLinearAboutApp, mLinearHelpApp;
    private TextView mAppVersion, mSendSuggestion;

    private SaveSettings saveSettings;
    private RecentRepository mRecentRepository;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LocalDataBase mLocalDataBase = (LocalDataBase) LocalDataBase.getInstance(getActivity());
        mRecentRepository = RecentRepository.getInstance(RecentsDataSource.getInstance(mLocalDataBase.recentsDAO()));
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {


        saveSettings = new SaveSettings(getContext());
        if (saveSettings.getNightModeState() == true){
            getContext().setTheme(R.style.DarkTheme);
        }else {
            getContext().setTheme(R.style.AppTheme);
        }

        View itemView = inflater.inflate(R.layout.fragment_settings, container, false);

        saveSettings = new SaveSettings(getContext());
        if (saveSettings.getNightModeState() == true){
            getContext().setTheme(R.style.DarkTheme);
        }else {
            getContext().setTheme(R.style.AppTheme);
        }

        mNightMode = itemView.findViewById(R.id.switch_style_app);

        mCardStyleApp = itemView.findViewById(R.id.card_app_style);
        mCardHelpApp = itemView.findViewById(R.id.card_app_help);
        mCardShareApp = itemView.findViewById(R.id.card_app_share);
        mCardAboutApp = itemView.findViewById(R.id.card_app_about);
        mCardClearRecents = itemView.findViewById(R.id.card_clear_recents);
        mCardClearFavorite = itemView.findViewById(R.id.card_clear_favorite);

        mLinearAboutApp = itemView.findViewById(R.id.linear_about_app);
        mLinearHelpApp = itemView.findViewById(R.id.linear_help_app);

        mAppVersion = itemView.findViewById(R.id.text_app_version);
        mSendSuggestion = itemView.findViewById(R.id.text_send_suggestion);

        return itemView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (saveSettings.getNightModeState() == true){
            mNightMode.setChecked(true);
        }

        mNightMode.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    saveSettings.setNightModeState(true);
                    restartApp();
                    // Toast.makeText(getActivity(), "Dark Style", Toast.LENGTH_SHORT).show();
                }else{
                    saveSettings.setNightModeState(false);
                    restartApp();
                    // Toast.makeText(getActivity(), "White Style", Toast.LENGTH_SHORT).show();
                }
            }
        });

        getAppVersionName();

        mCardStyleApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!saveSettings.getNightModeState() == true){
                    mNightMode.setChecked(true);
                }else {
                    mNightMode.setChecked(false);
                }
            }
        });

        mCardShareApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareApp();
            }
        });

        mCardAboutApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                runAnimations(mLinearAboutApp);

                if (mLinearAboutApp.getVisibility() == View.GONE) {
                    mLinearAboutApp.setVisibility(View.VISIBLE);
                    runAnimations(mLinearAboutApp);
                }else {
                    mLinearAboutApp.setVisibility(View.GONE);
                    runAnimations(mLinearAboutApp);
                }
            }
        });

        mCardHelpApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Snackbar.make(mCardHelpApp, "Help",
//                        Snackbar.LENGTH_SHORT).show();
                if (mLinearHelpApp.getVisibility() == View.GONE)
                    mLinearHelpApp.setVisibility(View.VISIBLE);
                else
                    mLinearHelpApp.setVisibility(View.GONE);
            }
        });

        mSendSuggestion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendSuggestion();
            }
        });

        mCardClearRecents.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearRecents();
            }
        });

        mCardClearFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearFavorite();
            }
        });
    }

    private void restartApp() {
        startActivity(new Intent(getActivity(), getActivity().getClass()));
        getActivity().finish();
    }

    private void clearRecents(){
        Disposable disposable = Observable.create(new ObservableOnSubscribe<Object>() {
            @Override
            public void subscribe(ObservableEmitter<Object> e) throws Exception {
                mRecentRepository.deleteAllRecents();
                e.onComplete();
                Snackbar.make(mCardClearRecents, "Clear Successfully",
                        Snackbar.LENGTH_SHORT).show();
            }
        }).observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Consumer<Object>() {
                    @Override
                    public void accept(Object o) throws Exception {

                    }
                });

        new CompositeDisposable(disposable);
    }

    private void clearFavorite(){
        FavoriteDatabaseSQLite favoriteDB = new FavoriteDatabaseSQLite(getActivity());
        favoriteDB.deleteAll();
        FavoriteAdapter favoriteAdapter = new FavoriteAdapter();
        favoriteAdapter.notifyDataSetChanged();

        Snackbar.make(mCardClearFavorite, "Clear Successfully",
                Snackbar.LENGTH_SHORT).show();
    }

    private void shareApp(){
        try {
            String textShare = "Wallpapers";
            String linkShare = "https://play.google.com/store/apps/details?id=com.ahmet.iphonewallpaper";

            Intent intentShare = new Intent(Intent.ACTION_SEND)
                    .setType("text/plain")
                    .putExtra(Intent.EXTRA_TEXT, textShare + "\n\n" + linkShare);
            startActivity(intentShare);
        }catch (Exception ex){
            //Toast.makeText(this, R.string.error_to_invite_friends, Toast.LENGTH_SHORT).show();
        }
    }

    private void sendSuggestion(){

        String email = "ahmedwasfe84@gmail.com";
        String[] sendTo = {"ahmedwasfe84@gmail.com"};

        String messageWlecome = getString(R.string.messageWlecome);
        String messageHelp = getString(R.string.messageHelp);

        Intent sendEmailIntent = new Intent(Intent.ACTION_SEND);
        sendEmailIntent.setType("message/rfc822");
        sendEmailIntent.putExtra(Intent.EXTRA_EMAIL, sendTo);
       // sendEmailIntent.putExtra(Intent.EXTRA_CC, email);
        sendEmailIntent.putExtra(Intent.EXTRA_SUBJECT, messageWlecome);
        sendEmailIntent.putExtra(Intent.EXTRA_TEXT, messageHelp);
        startActivity(sendEmailIntent);
    }

    private void getAppVersionName(){
        try {
            PackageInfo packageInfo = getActivity().getPackageManager()
                    .getPackageInfo(getActivity().getPackageName(), 0);
            String appVersion = packageInfo.versionName;
            mAppVersion.setText("App Version : " +  appVersion);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void runAnimations(LinearLayout linearLayout){

        Context mContext = linearLayout.getContext();
        LayoutAnimationController controller = null;
        controller = AnimationUtils.loadLayoutAnimation(mContext, R.anim.layout_slide_from_bottom);


        linearLayout.scheduleLayoutAnimation();

    }

}
