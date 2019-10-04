package com.ahmet.iphonewallpaper.Adapter;

import android.content.Context;
import android.content.Intent;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ahmet.iphonewallpaper.Config.Common;
import com.ahmet.iphonewallpaper.Database.DataSource.Model.Recents;
import com.ahmet.iphonewallpaper.Interface.ItemClickListener;
import com.ahmet.iphonewallpaper.Model.WallpaperItem;
import com.ahmet.iphonewallpaper.R;
import com.ahmet.iphonewallpaper.UI.ViewWallpaper;
import com.ahmet.iphonewallpaper.ViewHolder.ListWallpaperHolder;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.List;

public class RecentsAdapter extends RecyclerView.Adapter<ListWallpaperHolder> {

    private Context context;
    private List<Recents> listRecents;

    public RecentsAdapter(Context context, List<Recents> listRecents) {
        this.context = context;
        this.listRecents = listRecents;
    }

    public RecentsAdapter(Context context){
        this.context = context;
    }

    @Override
    public ListWallpaperHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View convertView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.raw_wallpaper_list, parent, false);

        int height = parent.getMeasuredHeight() / 2;
        convertView.setMinimumHeight(height);

        return new ListWallpaperHolder(convertView);
    }

    @Override
    public void onBindViewHolder(final ListWallpaperHolder holder, final int position) {

        Picasso.with(context)
                .load(listRecents.get(position).getImageLink())
                .networkPolicy(NetworkPolicy.OFFLINE)
                .into(holder.imageWallpaper, new Callback() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError() {
                        Picasso.with(context)
                                .load(listRecents.get(position).getImageLink())
                                .error(R.drawable.ic_terrain_black_24dp)
                                //.networkPolicy(NetworkPolicy.OFFLINE)
                                .into(holder.imageWallpaper, new Callback() {
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

        holder.setItemClickListener(context, new ItemClickListener() {
            @Override
            public void onClick(View view, int position) {
                // Code late for detalis ViewWallpapersFavorite

                Intent intent = new Intent(context, ViewWallpaper.class);
                WallpaperItem wallpaperItem = new WallpaperItem();
                wallpaperItem.setImageUrl(listRecents.get(position).getImageLink());
                wallpaperItem.setCategoryId(listRecents.get(position).getCategoryId());
                Common.wallpaperSelected = wallpaperItem;
                Common.select_background_key = listRecents.get(position).getKey();
                context.startActivity(intent);
            }
        });

//        holder.mBtnFavorite.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                FavoriteDatabaseSQLite database = new FavoriteDatabaseSQLite(context);
//                String imageLink = listRecents.get(position).getImageLink();
//                Recents recents = new Recents(imageLink);
//                int checkWallpaper = database.checkData(imageLink);
//                if (checkWallpaper > 0){
//                    Toast.makeText(context, "this wallpaper found in database", Toast.LENGTH_SHORT).show();
//                }else {
//                    database.addData(recents);
//                    Toast.makeText(context, "done add to database", Toast.LENGTH_SHORT).show();
//                }
//            }
//        });

    }

    @Override
    public int getItemCount() {
        return listRecents.size();
    }

}
