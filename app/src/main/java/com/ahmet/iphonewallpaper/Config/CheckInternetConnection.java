package com.ahmet.iphonewallpaper.Config;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by Ahmet on 26/2/2017.
 */

public class CheckInternetConnection {

    private Context context;

    public CheckInternetConnection(Context context){
        this.context = context;
    }
   public boolean isConnectInternet(){
       ConnectivityManager Connectmanager = (ConnectivityManager) context.getSystemService(context.CONNECTIVITY_SERVICE);
       if (Connectmanager != null){
           NetworkInfo info = Connectmanager.getActiveNetworkInfo();
           if (info != null && info.isConnected()){
               return true;
           }
       }
       return false;
   }
}
