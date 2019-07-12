package com.example.myapplication;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity implements
        NavigationView.OnNavigationItemSelectedListener {

    private int permissionRequestCode = 123;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setToolbar();
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new MainFragment())
                    .commit();
        }
        int smspermission1 = ActivityCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS);
        int smssendpermission = ActivityCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS);
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                ((smspermission1 != PackageManager.PERMISSION_GRANTED) ||
                        (smssendpermission != PackageManager.PERMISSION_GRANTED)
                )) {
            final String[] permissions = new String[]{Manifest.permission.SEND_SMS, Manifest.permission.RECEIVE_SMS};
            ActivityCompat.requestPermissions(this, permissions, permissionRequestCode);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == permissionRequestCode) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(getApplicationContext(), "Спасибо!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getApplicationContext(),
                        "Извините, без данного разрещения связь с разработчиком невозможна",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void setToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_add_city) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.container, new MenuFragment())
                    .addToBackStack(MainFragment.class.getName())
                    .commit();
            return true;
        } else if (id == R.id.action_info) {
            Toast.makeText(this, R.string.Information_in_development, Toast.LENGTH_SHORT).show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        int id = menuItem.getItemId();
        if (id == R.id.nav_home) {
            Toast.makeText(this, getString(R.string.home), Toast.LENGTH_SHORT).show();
        } else if (id == R.id.nav_tools) {
            Toast.makeText(this, getString(R.string.settings), Toast.LENGTH_SHORT).show();
        } else if (id == R.id.nav_about_developer) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.container, new AboutDeveloperFragment())
                    .addToBackStack(MainFragment.class.getName())
                    .commit();
            Toast.makeText(this, getString(R.string.about_the_developer), Toast.LENGTH_SHORT).show();
        } else if (id == R.id.nav_send_developer) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.container, new SendDeveloperFragment())
                    .addToBackStack(MainFragment.class.getName())
                    .commit();
            Toast.makeText(this, getString(R.string.Message_to_developer), Toast.LENGTH_SHORT).show();
        }
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
