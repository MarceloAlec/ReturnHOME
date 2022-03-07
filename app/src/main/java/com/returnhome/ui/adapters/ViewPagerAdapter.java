package com.returnhome.ui.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.returnhome.ui.fragments.MascotaDesaparecidaFragment;
import com.returnhome.ui.fragments.MiMascotaFragment;

import java.util.ArrayList;

public class ViewPagerAdapter extends FragmentStateAdapter {

    //ARREGLO DE FRAGMENTOS INCRUSTADOS EN CADA TAB
    private ArrayList<Fragment> fragments;

    public ViewPagerAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle, ArrayList<Fragment> fragments) {
        super(fragmentManager, lifecycle);
        this.fragments = fragments;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return fragments.get(position);
    }

    @Override
    public int getItemCount() {
        return fragments.size();
    }


}
