package com.alec.returnhome.ui.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.MenuItem;

import com.alec.returnhome.R;
import com.alec.returnhome.ui.fragments.PetsFragment;
import com.alec.returnhome.ui.fragments.ProfileFragment;
import com.google.android.material.navigation.NavigationView;

public class NavigationActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    DrawerLayout mDrawerLayout;
    NavigationView mNavigationView;
    Toolbar mToolbar;

    ActionBarDrawerToggle mToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);

        mDrawerLayout = findViewById(R.id.drawerLayout);
        mNavigationView = findViewById(R.id.nav_view);
        mToolbar = findViewById(R.id.toolbar);

        //CONFIGURACION DEL TOOLBAR
        setSupportActionBar(mToolbar);

        //CONFIGURACION DEL ICONO DE LA HAMBURGUESA EN EL TOOLBAR
        mToggle = new ActionBarDrawerToggle(this,mDrawerLayout,mToolbar,R.string.open_drawer, R.string.close_drawer);

        mNavigationView.setNavigationItemSelectedListener(this);

        //REEMPLAZA EL FRAGMENT LAYOUT CON ID CONTENT POR EL FRAGMENT PETS
        getSupportFragmentManager().beginTransaction().add(R.id.content, new PetsFragment()).commit();
        setTitle(getString(R.string.nav_pet));

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();

        switch  (item.getItemId()){
            case R.id.nav_pet:

                ft.replace(R.id.content, new PetsFragment()).commit();

                break;
            case R.id.nav_profile:
                ft.replace(R.id.content, new ProfileFragment()).commit();
                break;
        }

        setTitle(item.getTitle());
        //OCULTA EL NAVIGATION DRAWER
        mDrawerLayout.closeDrawers();
        //EL ITEM SELECCIONADO SE ENCUENTRA SELECCIONADO
        return true;
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {

        super.onPostCreate(savedInstanceState);
        //PERMITE SINCRONIZAR EL ESTADO DEL ICONO HAMBURGUESA CON EL NAVIGATION VIEW
        //EN FUNCION DE SI EL MENU LATERAL ESTA ABIERTO O CERRADO
        mToggle.syncState();
    }
}