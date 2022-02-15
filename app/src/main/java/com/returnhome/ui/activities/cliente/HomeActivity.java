package com.returnhome.ui.activities.cliente;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.returnhome.R;

import com.returnhome.ui.activities.nfc.ReadTagActivity;

import com.returnhome.ui.activities.mascota.MapPetHomeActivity;
import com.returnhome.ui.adapters.ViewPagerAdapter;
import com.google.android.material.navigation.NavigationView;
import com.returnhome.utils.AppConfig;

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout mDrawerLayout;
    private NavigationView mNavigationView;
    private Toolbar mToolbar;
    private TextView mTextViewUserName;
    private TextView mTextViewUserPhoneNumber;
    private View mHeaderView;

    private ActionBarDrawerToggle mToggle;
    private AppConfig mAppConfig;

    private ViewPagerAdapter mViewPagerAdapter;
    private ViewPager2 mViewPager2;
    private TabLayout mTabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        initializeComponents();

        //CONFIGURACION DEL TOOLBAR
        setSupportActionBar(mToolbar);

        //CONFIGURACION DEL ICONO DE LA HAMBURGUESA EN EL TOOLBAR
        mToggle = new ActionBarDrawerToggle(this,mDrawerLayout,mToolbar,R.string.open_drawer, R.string.close_drawer);

        mNavigationView.setNavigationItemSelectedListener(this);


        mViewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(), getLifecycle());
        mViewPager2.setAdapter(mViewPagerAdapter);

        setTitle(R.string.app_name);

        String[] headerTab = new String[]{"Mis Mascotas", "Mascotas desaparecidas"};
        new TabLayoutMediator(mTabLayout, mViewPager2, (tab, position) -> tab.setText(headerTab[position])).attach();

        mAppConfig = new AppConfig(this);

        mTextViewUserName.setText(mAppConfig.getUserName());
        mTextViewUserPhoneNumber.setText(mAppConfig.getPhoneNumber());

    }

    private void initializeComponents() {
        mDrawerLayout = findViewById(R.id.drawerLayout);
        mNavigationView = findViewById(R.id.nav_view);
        mToolbar = findViewById(R.id.toolbar);
        mHeaderView = mNavigationView.getHeaderView(0);
        mTextViewUserName = mHeaderView.findViewById(R.id.textView_userName);
        mTextViewUserPhoneNumber =mHeaderView.findViewById(R.id.textView_phoneNumber);
        mTabLayout = findViewById(R.id.tabLayout);
        mViewPager2 = findViewById(R.id.viewPager2);
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Intent intent;

        switch  (item.getItemId()){
            case R.id.nav_account:
                intent = new Intent(HomeActivity.this, SelectOptionProfileActivity.class);
                startActivity(intent);
                break;

            case R.id.nav_write_tag:
                intent = new Intent(HomeActivity.this, MapPetHomeActivity.class);
                startActivity(intent);
                break;

            case R.id.nav_read_tag:
                intent = new Intent(HomeActivity.this, ReadTagActivity.class);
                startActivity(intent);
                break;

            case R.id.nav_found_pet:
                intent = new Intent(HomeActivity.this, ReadTagActivity.class);
                intent.putExtra("foundPet",true);
                startActivity(intent);
                break;

        }
        //OCULTA EL NAVIGATION DRAWER
        mDrawerLayout.closeDrawers();

        //EL ITEM SELECCIONADO SE ENCUENTRA SELECCIONADO
        return false;




    }


    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {

        super.onPostCreate(savedInstanceState);
        //PERMITE SINCRONIZAR EL ESTADO DEL ICONO HAMBURGUESA CON EL NAVIGATION VIEW
        //EN FUNCION DE SI EL MENU LATERAL ESTA ABIERTO O CERRADO
        mToggle.syncState();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        mTextViewUserName.setText(mAppConfig.getUserName());
        mTextViewUserPhoneNumber.setText(mAppConfig.getPhoneNumber());
    }


}