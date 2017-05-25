package com.ivelsproject.ivelsid;

import android.content.DialogInterface;
import android.content.res.Configuration;
import android.graphics.Bitmap;
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
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.heinrichreimersoftware.materialdrawer.DrawerView;
import com.heinrichreimersoftware.materialdrawer.structure.DrawerItem;
import com.heinrichreimersoftware.materialdrawer.structure.DrawerProfile;
import com.heinrichreimersoftware.materialdrawer.theme.DrawerTheme;
import com.ivelsproject.ivelsid.fragment.AppListFragment;
import com.ivelsproject.ivelsid.fragment.IdHistoryFragment;
import com.ivelsproject.ivelsid.fragment.ProfileFragment;
import com.ivelsproject.ivelsid.lib.zxing.Contents;
import com.ivelsproject.ivelsid.lib.zxing.QRCodeEncoder;

public class MainActivity extends AppCompatActivity {

    private DrawerView drawer;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;
    private int fragmentMenu = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        getWindow().setFlags(
//                WindowManager.LayoutParams.FLAG_SECURE,
//                WindowManager.LayoutParams.FLAG_SECURE
//        );

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
            fragmentTransaction.add(R.id.fragment_container, new IdHistoryFragment());
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
                        .setImage(ContextCompat.getDrawable(MainActivity.this, R.drawable.ic_history))
                        .setTextPrimary(getString(R.string.menu_history))
                        .setOnItemClickListener(new DrawerItem.OnItemClickListener() {
                            @Override
                            public void onClick(DrawerItem item, long id, int position) {
                                fragmentMenu = position;
                                drawer.selectItem(position);
                                Fragment fr = new IdHistoryFragment();
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
                        .setImage(ContextCompat.getDrawable(MainActivity.this, R.drawable.ic_list))
                        .setTextPrimary(getString(R.string.menu_app_list))
                        .setOnItemClickListener(new DrawerItem.OnItemClickListener() {
                            @Override
                            public void onClick(DrawerItem item, long id, int position) {
                                fragmentMenu = position;
                                drawer.selectItem(position);
                                Fragment fr = new AppListFragment();
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
                        .setImage(ContextCompat.getDrawable(MainActivity.this, R.drawable.ic_account_box))
                        .setTextPrimary(getString(R.string.menu_account))
                        .setOnItemClickListener(new DrawerItem.OnItemClickListener() {
                            @Override
                            public void onClick(DrawerItem item, long id, int position) {
                                fragmentMenu = position;
                                drawer.selectItem(position);
                                Fragment fr = new ProfileFragment();
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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        switch (item.getItemId()) {
            case R.id.action_qrcode:

                LayoutInflater inflater = LayoutInflater.from(MainActivity.this);
                View view = inflater.inflate(R.layout.fragment_id, null);

                ImageView qrCode = (ImageView) view.findViewById(R.id.qrcode);

                String qrData = "11520241027";
                int qrCodeDimensions = 500;

                QRCodeEncoder qrCodeEncoder = new QRCodeEncoder(qrData, null,
                        Contents.Type.TEXT, BarcodeFormat.QR_CODE.toString(), qrCodeDimensions);

                try {
                    Bitmap bitmap = qrCodeEncoder.encodeAsBitmap();
                    qrCode.setImageBitmap(bitmap);
                } catch (WriterException e) {
                    e.printStackTrace();
                }

                AlertDialog.Builder alertBuilder = new AlertDialog.Builder(MainActivity.this);
                alertBuilder.setView(view);
                alertBuilder.setPositiveButton(R.string.button_hide, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });
                final AlertDialog dialog = alertBuilder.create();
                dialog.show();
                break;
        }

        return super.onOptionsItemSelected(item);
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
