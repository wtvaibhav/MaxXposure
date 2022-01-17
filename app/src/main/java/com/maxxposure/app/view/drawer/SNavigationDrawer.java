package com.maxxposure.app.view.drawer;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.IntDef;
import androidx.cardview.widget.CardView;

import com.maxxposure.app.R;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.List;

public class SNavigationDrawer extends RelativeLayout {


    //Context
    protected Context mContext;
    protected LayoutInflater mLayoutInflater;

    //Layouts
    protected List<MenuItem> menuItemList;
    protected RelativeLayout rootLayout;
    protected CardView containerCV;
    protected ImageView iv_menu;
    protected ScrollView menuSV;
    protected LinearLayout menuLL;
    protected LinearLayout containerLL;

    //Customization Variables
    private int appbarColor = R.color.white;
    private int appbarTitleTextColor = R.color.black;
    private int miSemiTraColor = R.color.black;
    private int navigationDrawerBackgroundColor = R.color.black;
    private int primaryMenuItemTextColor = R.color.colorPrimary;
    private int secondaryMenuItemTextColor = R.color.white;
    private int menuIconTintColor = R.color.black;
    private float menuIconSize = 30;
    private float appbarTitleTextSize = 20;
    private float primaryMenuItemTextSize = 20;
    private float secondaryMenuItemTextSize = 20;

    //Other stuff
    private boolean navOpen = false;
    private int currentPos = 0;
    float centerX, centerY;

    @IntDef({STATE_OPEN, STATE_CLOSED, STATE_OPENING, STATE_CLOSING})
    @Retention(RetentionPolicy.SOURCE)
    private @interface State {
    }

    //Indicates that any drawer is open. No animation is in progress.
    public static final int STATE_OPEN = 0;

    //Indicates that any drawer is closed. No animation is in progress.
    public static final int STATE_CLOSED = 1;

    //Indicates that a drawer is in the process of opening.
    public static final int STATE_OPENING = 2;

    //Indicates that a drawer is in the process of closing.
    public static final int STATE_CLOSING = 3;

    //Listeners
    private OnHamMenuClickListener onHamMenuClickListener;
    private OnMenuItemClickListener onMenuItemClickListener;
    private DrawerListener drawerListener;


    public SNavigationDrawer(Context context) {
        super(context);
    }

    public SNavigationDrawer(Context context, AttributeSet attrs) {
        super(context, attrs);

        init(context);
        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.SNavigationDrawer,
                0, 0);
        setAttributes(a);
        a.recycle();

    }

    //Adding the child views inside CardView LinearLayout
    @Override
    public void addView(View child, int index, ViewGroup.LayoutParams params) {
        if (containerLL == null) {
            super.addView(child, index, params);
        } else {
            //Forward these calls to the content view
            containerLL.addView(child, index, params);
        }
    }

    //Initialization
    public void init(Context context) {
        mContext = context;
        mLayoutInflater = LayoutInflater.from(context);
        //Load RootView from xml
        View rootView = mLayoutInflater.inflate(R.layout.widget_navigation_drawer, this, true);
        rootLayout = rootView.findViewById(R.id.rootLayout);
        containerCV = rootView.findViewById(R.id.containerCV);
        iv_menu = rootView.findViewById(R.id.iv_menu);
        menuSV = rootView.findViewById(R.id.menuSV);
        menuLL = rootView.findViewById(R.id.menuLL);
        containerLL = rootView.findViewById(R.id.containerLL);

        menuItemList = new ArrayList<>();


        iv_menu.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                hamMenuClicked();
                if (navOpen) {
                    closeDrawer();
                } else {
                    openDrawer();
                }
            }
        });

    }

    @SuppressLint("ResourceAsColor")
    protected void initMenu() {
        for (int i = 0; i < menuItemList.size(); i++) {
            View view = LayoutInflater.from(getContext()).inflate(R.layout.menu_row_item, null);

            TextView titleTV = view.findViewById(R.id.titleTV);
            TextView titleTV1 = view.findViewById(R.id.titleTV1);
            ImageView backgroundIV = view.findViewById(R.id.backgroundIV);
            CardView backgroundCV = view.findViewById(R.id.backgroundCV);
            View tintView = (View) view.findViewById(R.id.tintView);
            tintView.setBackgroundColor(miSemiTraColor);
            titleTV.setTextColor(secondaryMenuItemTextColor);
            titleTV1.setTextColor(primaryMenuItemTextColor);
            titleTV.setTextSize(secondaryMenuItemTextSize);
            titleTV1.setTextSize(primaryMenuItemTextSize);
            final RelativeLayout rootRL = view.findViewById(R.id.rootRL);
            backgroundCV.setTag("cv" + i);
            System.out.println("Testing " + backgroundCV.getTag());
            titleTV.setTag("tv" + i);
            if (i >= 1) {
                backgroundCV.setVisibility(View.GONE);
                backgroundCV.animate().translationX(rootRL.getX() - backgroundCV.getWidth()).setDuration(1).start();
                titleTV.setVisibility(View.VISIBLE);
            }
            rootRL.setTag(i);
            rootRL.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (currentPos != Integer.valueOf(view.getTag().toString())) {

                        final CardView backCV1 = (CardView) menuLL.findViewWithTag("cv" + currentPos);
                        final TextView title1 = (TextView) menuLL.findViewWithTag("tv" + currentPos);

                        backCV1.animate().translationX(rootRL.getX() - backCV1.getWidth()).setDuration(300).start();

                        currentPos = Integer.valueOf(view.getTag().toString());
                        menuItemClicked(currentPos);


                        final CardView backCV = (CardView) menuLL.findViewWithTag("cv" + currentPos);
                        final TextView title = (TextView) menuLL.findViewWithTag("tv" + currentPos);
                        backCV.setVisibility(View.INVISIBLE);
                        System.out.println("Drawer Testing " + backCV.getTag());
                        backCV.animate().translationX(rootRL.getX() - backCV.getWidth()).setDuration(1).start();
                        backCV.animate().translationX(rootRL.getX()).setDuration(300).start();
                        backCV.setVisibility(View.VISIBLE);
                        title.setVisibility(View.GONE);

                        final Handler handler1 = new Handler();
                        handler1.postDelayed(new Runnable() {
                            @Override
                            public void run() {

                                backCV1.setVisibility(View.GONE);
                                title1.setVisibility(View.VISIBLE);
                            }
                        }, 300);
                        //Close Navigation Drawer
                        closeDrawer();
                    } else {
                        menuItemClicked(currentPos);
                        closeDrawer();
                    }
                }
            });
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                // backgroundIV.setImageDrawable(getContext().getDrawable(menuItemList.get(i).getImageId()));
            }
            titleTV.setText(menuItemList.get(i).getTitle());
            titleTV1.setText(menuItemList.get(i).getTitle());
            menuLL.addView(view);
        }
    }

    //Hamburger button Click Listener
    public interface OnHamMenuClickListener {

        public void onHamMenuClicked();

    }

    //Listener for menu item click
    public interface OnMenuItemClickListener {

        public void onMenuItemClicked(int position);

    }

    //Listener for monitoring events about drawer.
    public interface DrawerListener {

        //Called when a drawer is opening.
        void onDrawerOpening();

        //Called when a drawer is closing.
        void onDrawerClosing();

        //Called when a drawer has settled in a completely open state.
        void onDrawerOpened();

        //Called when a drawer has settled in a completely closed state.
        void onDrawerClosed();

        //Called when the drawer motion state changes. The new state will
        void onDrawerStateChanged(@State int newState);

    }

    public OnHamMenuClickListener getOnHamMenuClickListener() {
        return onHamMenuClickListener;
    }

    public void setOnHamMenuClickListener(OnHamMenuClickListener onHamMenuClickListener) {
        this.onHamMenuClickListener = onHamMenuClickListener;
    }

    public OnMenuItemClickListener getOnMenuItemClickListener() {
        return onMenuItemClickListener;
    }

    public void setOnMenuItemClickListener(OnMenuItemClickListener onMenuItemClickListener) {
        this.onMenuItemClickListener = onMenuItemClickListener;
    }

    public DrawerListener getDrawerListener() {
        return drawerListener;
    }

    public void setDrawerListener(DrawerListener drawerListener) {
        this.drawerListener = drawerListener;
    }

    protected void hamMenuClicked() {
        if (onHamMenuClickListener != null) {
            onHamMenuClickListener.onHamMenuClicked();
        }
    }

    protected void menuItemClicked(int position) {
        if (onMenuItemClickListener != null) {
            onMenuItemClickListener.onMenuItemClicked(position);

        }
    }

    protected void drawerOpened() {
        if (drawerListener != null) {
            drawerListener.onDrawerOpened();
            drawerListener.onDrawerStateChanged(STATE_OPEN);
        }
    }

    protected void drawerClosed() {
        System.out.println("Drawer Closing");
        if (drawerListener != null) {
            drawerListener.onDrawerClosed();
            drawerListener.onDrawerStateChanged(STATE_CLOSED);
        }
    }

    protected void drawerOpening() {
        if (drawerListener != null) {
            drawerListener.onDrawerOpening();
            drawerListener.onDrawerStateChanged(STATE_OPENING);
        }
    }

    protected void drawerClosing() {
        if (drawerListener != null) {
            drawerListener.onDrawerClosing();
            drawerListener.onDrawerStateChanged(STATE_CLOSING);
        }
    }

    //Closes drawer
    public void closeDrawer() {
        drawerClosing();
        navOpen = false;
        final int[] stateSet = {android.R.attr.state_checked * (navOpen ? 1 : -1)};
        containerCV.animate().translationX(rootLayout.getX()).translationY(rootLayout.getY()).setDuration(500).start();
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                drawerClosed();
                containerCV.setCardElevation((float) 0);
                containerCV.setRadius((float) 0);
            }
        }, 500);
    }

    //Opens Drawer
    public void openDrawer() {

        drawerOpening();
        navOpen = true;
        final int[] stateSet = {android.R.attr.state_checked * (navOpen ? 1 : -1)};
        //menuIV.setImageState(stateSet, true);
        containerCV.setCardElevation((float) 100.0);
        containerCV.setRadius((float) 60.0);
        containerCV.animate().translationX(rootLayout.getX() + (rootLayout.getWidth() / 8) + (rootLayout.getWidth() / 2)).translationY(250).setDuration(500).start();
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                drawerOpened();
            }
        }, 250);
    }

    //set Attributes from xml
    protected void setAttributes(TypedArray attrs) {

        setAppbarColor(attrs.getColor(R.styleable.SNavigationDrawer_appbarColor, getResources().getColor(appbarColor)));
        setAppbarTitleTextColor(attrs.getColor(R.styleable.SNavigationDrawer_appbarTitleTextColor, getResources().getColor(appbarTitleTextColor)));
        setMenuiconTintColor(attrs.getColor(R.styleable.SNavigationDrawer_HamMenuIconTintColor, getResources().getColor(menuIconTintColor)));
        setMiSemiTraColor(attrs.getColor(R.styleable.SNavigationDrawer_HamMenuItemSemiTransparentColor, getResources().getColor(miSemiTraColor)));
        setNavigationDrawerBackgroundColor(attrs.getColor(R.styleable.SNavigationDrawer_navigationDrawerBackgroundColor, getResources().getColor(navigationDrawerBackgroundColor)));
        setPrimaryMenuItemTextColor(attrs.getColor(R.styleable.SNavigationDrawer_navigationDrawerBackgroundColor, getResources().getColor(primaryMenuItemTextColor)));
        setSecondaryMenuItemTextColor(attrs.getColor(R.styleable.SNavigationDrawer_secondaryMenuItemTextColor, getResources().getColor(secondaryMenuItemTextColor)));
        setAppbarTitleTextSize(attrs.getDimension(R.styleable.SNavigationDrawer_appbarTitleTextSize, 20));
        setPrimaryMenuItemTextSize(attrs.getDimension(R.styleable.SNavigationDrawer_primaryMenuItemTextSize, 20));
        setSecondaryMenuItemTextSize(attrs.getDimension(R.styleable.SNavigationDrawer_secondaryMenuItemTextSize, 20));
        setMenuIconSize(attrs.getDimension(R.styleable.SNavigationDrawer_HamMenuIconSize, 20));

    }

    //To change the AppBar Title

    //To check if drawer is open or not
    public boolean isDrawerOpen() {
        return navOpen;
    }

    //Adding menu to drawer
    public void addMenuItem(MenuItem menuItem) {
        if (menuItemList != null) {
            menuItemList.add(menuItem);
        }
    }

    //Getting the list of Menu Items
    public List<MenuItem> getMenuItemList() {
        return menuItemList;
    }

    //Setting the list of Menu Items
    public void setMenuItemList(List<MenuItem> menuItemList) {
        this.menuItemList = menuItemList;
        initMenu();
    }

    /*
     *
     * Customization :)
     *
     */
    public int getAppbarColor() {
        return appbarColor;
    }

    public void setAppbarColor(int appbarColor) {
        this.appbarColor = appbarColor;
    }

    public int getAppbarTitleTextColor() {
        return appbarTitleTextColor;
    }

    public void setAppbarTitleTextColor(int appbarTitleTextColor) {
        this.appbarTitleTextColor = appbarTitleTextColor;
    }

    public float getAppbarTitleTextSize() {
        return appbarTitleTextSize;
    }

    public void setAppbarTitleTextSize(float appbarTitleTextSize) {
        this.appbarTitleTextSize = appbarTitleTextSize;
    }

    public int getMenuiconTintColor() {
        return menuIconTintColor;
    }

    public void setMenuiconTintColor(int menuIconTintColor) {
        this.menuIconTintColor = menuIconTintColor;
    }

    public float getMenuIconSize() {
        return menuIconSize;
    }

    public void setMenuIconSize(float menuIconSize) {
        //Todo Change Icon Size
        this.menuIconSize = menuIconSize;
    }

    public int getMiSemiTraColor() {
        return miSemiTraColor;
    }

    public void setMiSemiTraColor(int miSemiTraColor) {
        this.miSemiTraColor = miSemiTraColor;
        invalidate();
    }

    public int getNavigationDrawerBackgroundColor() {
        return navigationDrawerBackgroundColor;
    }

    public void setNavigationDrawerBackgroundColor(int navigationDrawerBackgroundColor) {
        rootLayout.setBackgroundColor(navigationDrawerBackgroundColor);
        this.navigationDrawerBackgroundColor = navigationDrawerBackgroundColor;
    }

    public int getPrimaryMenuItemTextColor() {
        return primaryMenuItemTextColor;
    }

    public void setPrimaryMenuItemTextColor(int primaryMenuItemTextColor) {
        this.primaryMenuItemTextColor = primaryMenuItemTextColor;
        invalidate();
    }

    public int getSecondaryMenuItemTextColor() {
        return secondaryMenuItemTextColor;
    }

    public void setSecondaryMenuItemTextColor(int secondaryMenuItemTextColor) {
        this.secondaryMenuItemTextColor = secondaryMenuItemTextColor;
        invalidate();
    }

    public float getPrimaryMenuItemTextSize() {
        return primaryMenuItemTextSize;

    }

    public void setPrimaryMenuItemTextSize(float primaryMenuItemTextSize) {
        this.primaryMenuItemTextSize = primaryMenuItemTextSize;
        invalidate();
    }

    public float getSecondaryMenuItemTextSize() {
        return secondaryMenuItemTextSize;
    }

    public void setSecondaryMenuItemTextSize(float secondaryMenuItemTextSize) {
        this.secondaryMenuItemTextSize = secondaryMenuItemTextSize;
        invalidate();
    }

    //to change the typeface of appbar title
    public void setAppbarTitleTypeface(Typeface titleTypeface) {
    }
}
