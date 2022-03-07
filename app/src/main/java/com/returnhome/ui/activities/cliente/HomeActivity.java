package com.returnhome.ui.activities.cliente;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.returnhome.R;

import com.returnhome.ui.activities.nfc.LecturaEtiquetaActivity;

import com.returnhome.ui.activities.mascota.MapaSeleccionHogarMascotaActivity;
import com.returnhome.ui.adapters.ViewPagerAdapter;
import com.google.android.material.navigation.NavigationView;
import com.returnhome.ui.fragments.MascotaDesaparecidaFragment;
import com.returnhome.ui.fragments.MiMascotaFragment;
import com.returnhome.utils.AppSharedPreferences;

import java.util.ArrayList;

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout mDrawerLayout;
    private NavigationView mNavigationView;
    private Toolbar mToolbar;
    private TextView mTextViewNombreCliente;
    private TextView mTextViewNumeroCelular;
    private View mHeaderView;

    private ActionBarDrawerToggle mToggle;
    private AppSharedPreferences mAppSharedPreferences;

    private ViewPagerAdapter mViewPagerAdapter;
    private ViewPager2 mViewPager2;
    private TabLayout mTabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        inicializarComponentes();

        //CONFIGURACION DEL TOOLBAR
        setSupportActionBar(mToolbar);
        setTitle(R.string.app_name);

        //CONFIGURACION DEL ICONO DE LA HAMBURGUESA EN EL TOOLBAR
        mToggle = new ActionBarDrawerToggle(this,mDrawerLayout,mToolbar,R.string.abrir_drawer, R.string.cerrar_drawer);

        mNavigationView.setNavigationItemSelectedListener(this);

        //CONFIGURACION VIEWPAGER
        mViewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(), getLifecycle(), agregarFragments());
        mViewPager2.setAdapter(mViewPagerAdapter);
        String[] tituloTab = new String[]{"Mis Mascotas", "Mascotas desaparecidas"};
        new TabLayoutMediator(mTabLayout, mViewPager2, (tab, position) -> tab.setText(tituloTab[position])).attach();
        //

        mAppSharedPreferences = new AppSharedPreferences(this);

        mTextViewNombreCliente.setText(mAppSharedPreferences.obtenerNombreCliente());
        mTextViewNumeroCelular.setText(mAppSharedPreferences.obtenerNumeroCelular());

    }

    private void inicializarComponentes() {
        mDrawerLayout = findViewById(R.id.drawerLayout);
        mNavigationView = findViewById(R.id.nav_view);
        mToolbar = findViewById(R.id.toolbar);
        mHeaderView = mNavigationView.getHeaderView(0);
        mTextViewNombreCliente = mHeaderView.findViewById(R.id.textViewNombreCliente);
        mTextViewNumeroCelular =mHeaderView.findViewById(R.id.textViewNumeroCelular);
        mTabLayout = findViewById(R.id.tabLayout);
        mViewPager2 = findViewById(R.id.viewPager2);
    }

    private ArrayList<Fragment> agregarFragments(){
        ArrayList<Fragment> fragments = new ArrayList<>();

        fragments.add(new MiMascotaFragment());
        fragments.add(new MascotaDesaparecidaFragment());
        return fragments;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Intent intent;

        switch  (item.getItemId()){
            case R.id.nav_ajustes:
                intent = new Intent(HomeActivity.this, SeleccionOpcionAjustesActivity.class);
                startActivity(intent);
                break;

            case R.id.nav_escribir_etiqueta:
                intent = new Intent(HomeActivity.this, MapaSeleccionHogarMascotaActivity.class);
                startActivity(intent);
                break;

            case R.id.nav_leer_etiqueta:
                intent = new Intent(HomeActivity.this, LecturaEtiquetaActivity.class);
                startActivity(intent);
                break;

            case R.id.nav_mascota_encontrada:
                intent = new Intent(HomeActivity.this, LecturaEtiquetaActivity.class);
                intent.putExtra("mascotaEncontrada",true);
                startActivity(intent);
                break;

        }

        mDrawerLayout.closeDrawers();

        //EL ITEM SELECCIONADO SE ENCUENTRA SELECCIONADO
        return false;

    }


    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {

        super.onPostCreate(savedInstanceState);
        // SINCRONIZA EL ESTADO DEL ICONO HAMBURGUESA CON EL NAVIGATION VIEW
        //EN FUNCION DE SI EL MENU LATERAL ESTA ABIERTO O CERRADO
        mToggle.syncState();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        mTextViewNombreCliente.setText(mAppSharedPreferences.obtenerNombreCliente());
        mTextViewNumeroCelular.setText(mAppSharedPreferences.obtenerNumeroCelular());
    }


}