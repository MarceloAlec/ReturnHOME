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

import com.returnhome.ui.activities.nfc.LecturaEtiquetaActivity;

import com.returnhome.ui.activities.mascota.MapaSeleccionHogarMascotaActivity;
import com.returnhome.ui.adapters.PaginacionFragmentoAdapter;
import com.google.android.material.navigation.NavigationView;
import com.returnhome.utils.AppSharedPreferences;

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout mDrawerLayout;
    private NavigationView mNavigationView;
    private Toolbar mToolbar;
    private TextView mTextViewNombreCliente;
    private TextView mTextViewNumeroCelular;
    private View mHeaderView;

    private ActionBarDrawerToggle mToggle;
    private AppSharedPreferences mAppSharedPreferences;

    private PaginacionFragmentoAdapter mPaginacionFragmentoAdapter;
    private ViewPager2 mViewPager2;
    private TabLayout mTabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        inicializarComponentes();

        //CONFIGURACION DEL TOOLBAR
        setSupportActionBar(mToolbar);

        //CONFIGURACION DEL ICONO DE LA HAMBURGUESA EN EL TOOLBAR
        mToggle = new ActionBarDrawerToggle(this,mDrawerLayout,mToolbar,R.string.abrir_drawer, R.string.cerrar_drawer);

        mNavigationView.setNavigationItemSelectedListener(this);

        mPaginacionFragmentoAdapter = new PaginacionFragmentoAdapter(getSupportFragmentManager(), getLifecycle());
        mViewPager2.setAdapter(mPaginacionFragmentoAdapter);

        setTitle(R.string.app_name);

        String[] tituloTab = new String[]{"Mis Mascotas", "Mascotas desaparecidas"};
        new TabLayoutMediator(mTabLayout, mViewPager2, (tab, position) -> tab.setText(tituloTab[position])).attach();

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