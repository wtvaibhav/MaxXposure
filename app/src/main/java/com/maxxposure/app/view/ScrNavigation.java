package com.maxxposure.app.view;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.maxxposure.app.R;
import com.maxxposure.app.view.drawer.MenuItem;
import com.maxxposure.app.view.drawer.SNavigationDrawer;
import com.maxxposure.app.view.frgament.FrgContactUs;
import com.maxxposure.app.view.frgament.FrgGetHelp;
import com.maxxposure.app.view.frgament.FrgHome;

import java.util.ArrayList;
import java.util.List;

public class ScrNavigation extends AppCompatActivity {


    SNavigationDrawer sNavigationDrawer;
    int color1 = 0;
    Class fragmentClass;
    public static Fragment fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                getWindow().setStatusBarColor(getResources().getColor(R.color.white));
                getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            } else {
                getWindow().setStatusBarColor(Color.WHITE);
            }
        }
        setContentView(R.layout.activity_scr_navigation);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        sNavigationDrawer = findViewById(R.id.drawerView);
        List<MenuItem> menuItems = new ArrayList<>();
        menuItems.add(new MenuItem("Home", 0));
        menuItems.add(new MenuItem("Contact Us", 0));
        menuItems.add(new MenuItem("Get Help", 0));
        sNavigationDrawer.setMenuItemList(menuItems);
        fragmentClass = FrgHome.class;
        try {
            fragment = (Fragment) fragmentClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (fragment != null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out).replace(R.id.frameLayout, fragment).commit();
        }


        sNavigationDrawer.setOnMenuItemClickListener(new SNavigationDrawer.OnMenuItemClickListener() {
            @Override
            public void onMenuItemClicked(int position) {
                System.out.println("Position " + position);

                switch (position) {
                    case 0: {
                        color1 = R.color.red;
                        fragmentClass = FrgHome.class;
                        break;
                    }
                    case 1: {
                        color1 = R.color.colorPrimary;
                        fragmentClass = FrgContactUs.class;
                        break;
                    }
                    case 2: {
                        color1 = R.color.colorAccent;
                        fragmentClass = FrgGetHelp.class;
                        break;
                    }

                }
                sNavigationDrawer.setDrawerListener(new SNavigationDrawer.DrawerListener() {

                    @Override
                    public void onDrawerOpened() {
                    }

                    @Override
                    public void onDrawerOpening() {
                    }

                    @Override
                    public void onDrawerClosing() {
                        System.out.println("Drawer closed");
                        try {
                            fragment = (Fragment) fragmentClass.newInstance();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        if (fragment != null) {
                            FragmentManager fragmentManager = getSupportFragmentManager();
                            fragmentManager.beginTransaction().setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out).replace(R.id.frameLayout, fragment).commit();
                        }

                    }

                    @Override
                    public void onDrawerClosed() {
                        
                    }

                    @Override
                    public void onDrawerStateChanged(int newState) {
                        System.out.println("State " + newState);
                    }
                });
            }
        });
    }
}