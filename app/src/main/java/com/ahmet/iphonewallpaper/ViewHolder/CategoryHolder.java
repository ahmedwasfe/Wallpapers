package com.ahmet.iphonewallpaper.ViewHolder;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.ahmet.iphonewallpaper.Config.CheckInternetConnection;
import com.ahmet.iphonewallpaper.Interface.ItemClickListener;
import com.ahmet.iphonewallpaper.R;
import com.squareup.picasso.Picasso;

public class CategoryHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    private Context context;

    public TextView categoryName;
    public ImageView categoryImage;

    ItemClickListener itemClickListener;

    private CheckInternetConnection connection;

    public void setItemClickListener(Context context, ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
        this.context = context;
        connection = new CheckInternetConnection(context);
    }

    public CategoryHolder(View itemView) {
        super(itemView);
        categoryName = itemView.findViewById(R.id.category_name);
        categoryImage = itemView.findViewById(R.id.category_image);


//        if (!connection.isConnectInternet()) {
//            Picasso.with(context)
//                    .load(R.drawable.ic_terrain_black_24dp)
//                    .into(categoryImage);
//        }

        itemView.setOnClickListener(this);

    }


    @Override
    public void onClick(View view) {
        itemClickListener.onClick(view, getAdapterPosition());
    }
}
