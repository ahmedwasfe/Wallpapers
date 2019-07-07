package com.ahmet.iphonewallpaper.Adapter;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

public class FragmentsAdapter extends FragmentPagerAdapter {

  List<Fragment> listFragments = new ArrayList<>();
  List<String> listTitls = new ArrayList<>();

  public FragmentsAdapter(FragmentManager fragmentManager) {
    super(fragmentManager);
  }

  @Override
  public Fragment getItem(int position) {
    return listFragments.get(position);
  }

  @Override
  public int getCount() {
    return listFragments.size();
  }

  @Nullable
  @Override
  public CharSequence getPageTitle(int position) {
    return listTitls.get(position);
  }

  public void addFragment(Fragment fragment, String title){
    listFragments.add(fragment);
    listTitls.add(title);
  }
}
