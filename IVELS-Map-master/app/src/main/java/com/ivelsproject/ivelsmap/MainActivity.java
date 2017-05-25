package com.ivelsproject.ivelsmap;

import android.content.res.Configuration;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.heinrichreimersoftware.materialdrawer.DrawerView;
import com.heinrichreimersoftware.materialdrawer.structure.DrawerItem;
import com.heinrichreimersoftware.materialdrawer.structure.DrawerProfile;
import com.heinrichreimersoftware.materialdrawer.theme.DrawerTheme;
import com.ivelsproject.ivelsmap.fragment.MapFragment;
import com.ivelsproject.ivelsmap.fragment.LecturerPosFragment;

public class MainActivity extends AppCompatActivity {

    private DrawerView drawer;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;
    private int fragmentMenu = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        if (savedInstanceState != null) {
            fragmentMenu = savedInstanceState.getInt("fragmentMenu");
        }

        drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        drawer = (DrawerView) findViewById(R.id.drawer);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment fragment = fragmentManager.findFragmentById(R.id.fragment_container);
        if (fragment == null) {
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.add(R.id.fragment_container, new MapFragment());
            fragmentTransaction.commit();
        }

        final Handler handler = new Handler();

        drawerToggle = new ActionBarDrawerToggle(
                this,
                drawerLayout,
                toolbar,
                R.string.drawer_open,
                R.string.drawer_close
        ) {

            public void onDrawerClosed(View view) {
                invalidateOptionsMenu();
            }

            public void onDrawerOpened(View drawerView) {
                invalidateOptionsMenu();
            }
        };

        drawerLayout.setDrawerListener(drawerToggle);
        drawerLayout.closeDrawer(drawer);

        drawer.addItem(new DrawerItem()
                        .setImage(ContextCompat.getDrawable(MainActivity.this, R.drawable.ic_explore))
                        .setTextPrimary(getString(R.string.menu_map))
                        .setOnItemClickListener(new DrawerItem.OnItemClickListener() {
                            @Override
                            public void onClick(DrawerItem item, long id, int position) {
                                fragmentMenu = position;
                                drawer.selectItem(position);
                                Fragment fr = new MapFragment();
                                selectMenu(fr);
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        drawerLayout.closeDrawer(GravityCompat.START);
                                    }
                                }, 300);
                            }
                        })
        );

        drawer.addItem(new DrawerItem()
                        .setImage(ContextCompat.getDrawable(MainActivity.this, R.drawable.ic_person_pin))
                        .setTextPrimary(getString(R.string.menu_lecturer_position))
                        .setOnItemClickListener(new DrawerItem.OnItemClickListener() {
                            @Override
                            public void onClick(DrawerItem item, long id, int position) {
                                fragmentMenu = position;
                                drawer.selectItem(position);
                                Fragment fr = new LecturerPosFragment();
                                selectMenu(fr);
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        drawerLayout.closeDrawer(GravityCompat.START);
                                    }
                                }, 300);
                            }
                        })
        );

        drawer.addDivider();

        drawer.addItem(new DrawerItem()
                        .setImage(ContextCompat.getDrawable(MainActivity.this, R.drawable.ic_help))
                        .setTextPrimary(getString(R.string.menu_help))
        );

        drawer.addItem(new DrawerItem()
                        .setImage(ContextCompat.getDrawable(MainActivity.this, R.drawable.ic_setting_light))
                        .setTextPrimary(getString(R.string.menu_setting))
        );

        drawer.selectItem(0);

        drawer.addProfile(new DrawerProfile()
                        .setId(1)
                        .setRoundedAvatar((BitmapDrawable) ContextCompat.getDrawable(MainActivity.this, R.drawable.profile_image))
                        .setBackground(ContextCompat.getDrawable(MainActivity.this, R.drawable.menu_background))
                        .setName(getString(R.string.dummy_name))
                        .setDescription(getString(R.string.dummy_nim))
        );

        drawer.setDrawerTheme(
                new DrawerTheme(this)
                        .setHighlightColorRes(R.color.colorPrimary)
        );
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putInt("fragmentMenu", fragmentMenu);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }


    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        drawerToggle.syncState();
    }

    public void selectMenu(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, fragment);
        fragmentTransaction.commit();
    }
}
