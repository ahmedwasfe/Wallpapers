package com.ahmet.iphonewallpaper.Helper;

import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.provider.MediaStore;

import android.widget.Toast;

import com.ahmet.iphonewallpaper.R;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.lang.ref.WeakReference;

public class SaveImageHelper implements Target {

  private Context context;
  private WeakReference<AlertDialog> dialogWeakReference;
  private WeakReference<ContentResolver> resolverWeakReference;
  private String name, desc;

  public SaveImageHelper(Context context, AlertDialog alertDialog, ContentResolver contentResolver, String name, String desc) {
    this.context = context;
    this.dialogWeakReference = new WeakReference<AlertDialog>(alertDialog);
    this.resolverWeakReference = new WeakReference<ContentResolver>(contentResolver);
    this.name = name;
    this.desc = desc;
  }

  @Override
  public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {

    ContentResolver resolver = resolverWeakReference.get();
    AlertDialog alertDialog = dialogWeakReference.get();

    if (resolver != null){
      MediaStore.Images.Media.insertImage(resolver, bitmap, name, desc);
      Toast.makeText(context, R.string.download_success, Toast.LENGTH_SHORT).show();
      //Snackbar.make(alertDialog.getListView(),"Download Success", Snackbar.LENGTH_SHORT).show();
      alertDialog.dismiss();
    }

  }

  @Override
  public void onBitmapFailed(Drawable errorDrawable) {

  }

  @Override
  public void onPrepareLoad(Drawable placeHolderDrawable) {

  }
}
