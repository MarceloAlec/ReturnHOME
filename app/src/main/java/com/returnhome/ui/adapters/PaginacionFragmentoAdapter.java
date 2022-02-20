package com.returnhome.ui.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.returnhome.ui.fragments.MascotaDesaparecidaFragment;
import com.returnhome.ui.fragments.MiMascotaFragment;

public class PaginacionFragmentoAdapter extends FragmentStateAdapter {

    public PaginacionFragmentoAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {

        switch (position){
            case 1:
                return new MascotaDesaparecidaFragment();

        }

        return new MiMascotaFragment();
    }

    @Override
    public int getItemCount() {
        return 2;
    }


}
