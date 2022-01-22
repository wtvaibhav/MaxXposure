package com.maxxposure.app.view;

import androidx.annotation.ColorInt;
import androidx.annotation.ColorRes;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.maxxposure.app.R;
import com.maxxposure.app.databinding.ActivityScrNavigation2Binding;
import com.maxxposure.app.sharedpref.UserData;
import com.maxxposure.app.utils.CustomIntent;
import com.maxxposure.app.utils.CustomToast;
import com.maxxposure.app.view.frgament.FrgContactUs;
import com.maxxposure.app.view.frgament.FrgGetHelp;
import com.maxxposure.app.view.frgament.FrgHome;
import com.maxxposure.app.view.menu.DrawerAdapter;
import com.maxxposure.app.view.menu.DrawerItem;
import com.maxxposure.app.view.menu.SimpleItem;
import com.maxxposure.app.view.menu.SpaceItem;
import com.squareup.picasso.Picasso;
import com.yarolegovich.slidingrootnav.SlidingRootNav;
import com.yarolegovich.slidingrootnav.SlidingRootNavBuilder;

import java.util.Arrays;

public class ScrNavigation2 extends AppCompatActivity implements DrawerAdapter.OnItemSelectedListener, View.OnClickListener {
    private static final int POS_DASHBOARD = 0;
    private static final int POS_CONTACT_US = 1;
    private static final int POS_GET_HELP = 2;
    //private static final int POS_LOGOUT = 3;

    private String[] screenTitles;
    private Drawable[] screenIcons;

    private SlidingRootNav slidingRootNav;

    private ActivityScrNavigation2Binding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                getWindow().setStatusBarColor(getResources().getColor(R.color.white));
                getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            } else {
                getWindow().setStatusBarColor(Color.WHITE);
            }
        }
        binding = DataBindingUtil.setContentView(this, R.layout.activity_scr_navigation2);
        binding.ivMenu.setOnClickListener(this);


        slidingRootNav = new SlidingRootNavBuilder(this)
                .withMenuOpened(false)
                .withContentClickableWhenMenuOpened(false)
                .withSavedState(savedInstanceState)
                .withMenuLayout(R.layout.menu_left_drawer)
                .inject();

        screenIcons = loadScreenIcons();
        screenTitles = loadScreenTitles();

        DrawerAdapter adapter = new DrawerAdapter(Arrays.asList(
                createItemFor(POS_DASHBOARD).setChecked(true),
                createItemFor(POS_CONTACT_US),
                createItemFor(POS_GET_HELP),
                //  createItemFor(POS_LOGOUT),
                new SpaceItem(48)));
        adapter.setListener(this);

        TextView tv_user_name = findViewById(R.id.tv_user_name);
        String userFullname = UserData.getInstance().getFullName();
        tv_user_name.setText(userFullname.replace(" ", "\n"));

        ImageView iv_user_image = findViewById(R.id.iv_user_image);
        Picasso.with(this).load(UserData.getInstance().setImage())
                .placeholder(getResources().getDrawable(R.drawable.ic_dummy_user))
                .error(getResources().getDrawable(R.drawable.ic_dummy_user))
                .into(iv_user_image);

        Picasso.with(this).load(UserData.getInstance().setImage())
                .placeholder(getResources().getDrawable(R.drawable.ic_dummy_user))
                .error(getResources().getDrawable(R.drawable.ic_dummy_user))
                .into(binding.ivUserImage);

        RecyclerView list = findViewById(R.id.list);
        list.setNestedScrollingEnabled(false);
        list.setLayoutManager(new LinearLayoutManager(this));
        list.setAdapter(adapter);

        LinearLayout ll_logout = findViewById(R.id.ll_logout);
        ll_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialogForOpMenu();
            }
        });
        adapter.setSelected(POS_DASHBOARD);


    }

    @Override
    public void onItemSelected(int position) {
        Fragment selectedScreen = new FrgHome();
        if (position == POS_DASHBOARD) {
            selectedScreen = new FrgHome();
        } else if (position == POS_CONTACT_US) {
            selectedScreen = new FrgContactUs();
        } else if (position == POS_GET_HELP) {
            selectedScreen = new FrgGetHelp();
        } /*else {
            CustomToast.showToast(ScrNavigation2.this, "Logout");
            showDialogForOpMenu();
        }*/
        slidingRootNav.closeMenu();
        showFragment(selectedScreen);
    }

    private void showFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, fragment)
                .commit();
    }

    @SuppressWarnings("rawtypes")
    private DrawerItem createItemFor(int position) {
        return new SimpleItem(screenIcons[position], screenTitles[position])
                .withIconTint(color(R.color.text_black))
                .withTextTint(color(R.color.white))
                .withSelectedIconTint(color(R.color.colorAccent))
                .withSelectedTextTint(color(R.color.colorAccent));
    }

    private String[] loadScreenTitles() {
        return getResources().getStringArray(R.array.ld_activityScreenTitles);
    }

    private Drawable[] loadScreenIcons() {
        TypedArray ta = getResources().obtainTypedArray(R.array.ld_activityScreenIcons);
        Drawable[] icons = new Drawable[ta.length()];
        for (int i = 0; i < ta.length(); i++) {
            int id = ta.getResourceId(i, 0);
            if (id != 0) {
                icons[i] = ContextCompat.getDrawable(this, id);
            }
        }
        ta.recycle();
        return icons;
    }

    @ColorInt
    private int color(@ColorRes int res) {
        return ContextCompat.getColor(this, res);
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_menu:
                slidingRootNav.openMenu();
                break;
        }
    }

    private void showDialogForOpMenu() {

        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dlg_logout);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        Window window = dialog.getWindow();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        //   window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        CardView cv_proceed = dialog.findViewById(R.id.cv_proceed);
        TextView tv_cancel = dialog.findViewById(R.id.tv_cancel);


        tv_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        cv_proceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                UserData.getInstance().clearData();
                CustomIntent.startActivity(ScrNavigation2.this, ScrLogin.class);
            }
        });
        dialog.setCancelable(true);
        dialog.show();
    }
}