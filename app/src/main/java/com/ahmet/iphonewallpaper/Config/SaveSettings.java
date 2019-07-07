package com.ahmet.iphonewallpaper.Config;

import android.content.Context;
import android.content.SharedPreferences;

public class SaveSettings {

  private SharedPreferences preferences;

  public SaveSettings(Context context){
    preferences = context.getSharedPreferences("saveNightMode",Context.MODE_PRIVATE);
  }

  // this Method Will Save the Night Mode State / true or false
  public void setNightModeState(Boolean state){

    SharedPreferences.Editor editor = preferences.edit();
    editor.putBoolean("NightMode", state);
    editor.commit();
  }

  // this Method Will Load the Night Mode State / true or false
  public Boolean getNightModeState(){
    Boolean state = preferences.getBoolean("NightMode", false);
    return state;
  }
}
