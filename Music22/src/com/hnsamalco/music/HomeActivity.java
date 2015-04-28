package com.hnsamalco.music;

import java.util.ArrayList;
import java.util.List;

import android.app.ActionBar;
import android.app.Activity;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.hnsamalco.music.adapter.DrawerMenuAdapter;
import com.hnsamalco.music.adapter.ViewPagerAdapter;
import com.hnsamalco.music.animations.DepthPageTransformer;
import com.hnsamalco.music.communication.SongsListner;
import com.hnsamalco.music.customview.BottomBarDrawerLayout;
import com.hnsamalco.music.customview.BottomBarDrawerLayout.DrawerListener;
import com.hnsamalco.music.data.AlbumDetails;
import com.hnsamalco.music.drawable.DrawerArrowDrawable;

public class HomeActivity extends FragmentActivity implements SongsListner{

    private List<String> menulist = new ArrayList<String>();
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private TextView actionBartitle;
    
    private final int MUSICLISTPAGE=0;
    private final int MYLIBRARYPAGE=1;
    private final int PLAYLISTPAGE=2;
    
    private DrwaerMenuToggle menuToggle;
    String TAG="Music";
    public static final String SAVED_STATE_ACTION_BAR_HIDDEN = "saved_state_action_bar_hidden";
    private ImageView appImage;
    private DrawerArrowDrawable drawerArrowDrawable;
    private float offset;
    private boolean flipped,bottomUpdrawer;
    private static int menuPosotion;
    private  BottomBarDrawerLayout slideInLayout;
    private ViewPager playPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        //drawer menu labels
        menulist.add("Listen Now");
        menulist.add("My Library");
        menulist.add("PlayLists");

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);
        appImage = (ImageView)findViewById(android.R.id.home);
        drawerArrowDrawable = new DrawerArrowDrawable(getResources());
        drawerArrowDrawable.setStrokeColor(getResources().getColor(R.color.light_gray));
        appImage.setImageDrawable(drawerArrowDrawable);

        // Set the adapter for the drawer list view
        mDrawerList.setAdapter(new DrawerMenuAdapter(this, R.layout.drawer_menu_list,menulist));
        mDrawerList.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterview, View view, int position,
                   long arg3) {
                // TODO Auto-generated method stub
                menuListItemCheck(position);
            }
        });

        //DrawerMenu toggle for open and close listner
        menuToggle = new DrwaerMenuToggle(this,                  /* host Activity */
                mDrawerLayout,         /* DrawerLayout object */
                R.drawable.ic_drawer,  /* nav drawer icon to replace 'Up' caret */
                R.string.drawer_open,  /* "open drawer" description */
                R.string.drawer_close);

        mDrawerLayout.setDrawerListener(menuToggle);
        getActionBar().setDisplayShowHomeEnabled(true);
        getActionBar().setHomeButtonEnabled(true);
        actionBartitle = (TextView) findViewById(android.R.id.text1);

        //set first item as selected in drawer menu
        menuListItemCheck(0);

        boolean actionBarHidden = savedInstanceState != null && savedInstanceState.getBoolean(SAVED_STATE_ACTION_BAR_HIDDEN, false);
        if (actionBarHidden) {
            int actionBarHeight = getActionBarHeight();
            setActionBarTranslation(-actionBarHeight);//will "hide" an ActionBar
        }

        final ActionBar actionBar = getActionBar();

        slideInLayout = (BottomBarDrawerLayout) findViewById(R.id.drawerLayout);
        slideInLayout.setDrawerShadow(R.drawable.bottom_top_slide);
        slideInLayout.setDrawerListener(new DrawerListener() {

            @Override
            public void onDrawerStateChanged(int newState) {

            }

            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                 if (actionBar != null) {
                    if (slideOffset > 0.8f) {
                        actionBar.hide();
                    } else {
                        actionBar.show();
                    }
                }
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED, mDrawerList);
                bottomUpdrawer=true;
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED, mDrawerList);
                bottomUpdrawer=false;
            }
        });

    }

	public void setActionBarTranslation(float y) {
        // Figure out the actionbar height
        int actionBarHeight = getActionBarHeight();
        // A hack to add the translation to the action bar
        ViewGroup content = ((ViewGroup)findViewById(android.R.id.content).getParent());
        int children = content.getChildCount();
        for (int i = 0; i < children; i++) {
            View child = content.getChildAt(i);
            if (child.getId() != android.R.id.content) {
            	child.setVisibility(View.GONE);
                if (y <= -actionBarHeight) {
//                    child.setVisibility(View.GONE);
                    Log.i("Music", "inside view gone :::"+y);
                } else {
                    child.setVisibility(View.VISIBLE);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                        child.setTranslationY(y);
                        Log.i("Music", "inside view VISIBLE :::"+y);
                    } else {
//                        AnimatorProxy.wrap(child).setTranslationY(y);
                    }
                }
            }
        }
    }

    private int getActionBarHeight(){
        int actionBarHeight = 0;
        TypedValue tv = new TypedValue();
        if (getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
            actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data,getResources().getDisplayMetrics());
        }
        return actionBarHeight;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public void setTitle(CharSequence title) {
         getActionBar().setTitle(title);
    }

    private void menuListItemCheck(int position){
        menuPosotion=position;
        mDrawerList.setItemChecked(position, true);
        mDrawerList.setSelection(position);
        mDrawerLayout.closeDrawer(mDrawerList);
        actionBartitle.setText(menulist.get(position));
        //setTitle(menulist.get(position));
        menuItemClick(position);
    }

    private void menuItemClick(int position){	
        Fragment fragment=null;

        switch(position){

            case MUSICLISTPAGE:
                fragment = new MusicListFragment();
                break;

            case MYLIBRARYPAGE:
                fragment = new MyLibraryFragment();
                break;

            case PLAYLISTPAGE:
                fragment = new PlayListFragment();
                break;

        }

        setPage(fragment);
	}
	
	public void setPage(Fragment fragment){
        if (fragment != null) {
            mDrawerLayout.closeDrawer(Gravity.START);
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.content_frame, fragment).commit();
        } else {
            // error in creating fragment
            Log.e("HomeActivity", "Error in creating fragment");
        }
    }

    private class DrwaerMenuToggle extends ActionBarDrawerToggle{

        public DrwaerMenuToggle(Activity activity, DrawerLayout drawerLayout,
                int drawerImageRes, int openDrawerContentDescRes,
                int closeDrawerContentDescRes) {
                super(activity, drawerLayout, drawerImageRes, openDrawerContentDescRes,
                       closeDrawerContentDescRes);
        }

        @Override
        public void onDrawerSlide(View drawerView, float slideOffset) {
            offset = slideOffset;
	        // Sometimes slideOffset ends up so close to but not quite 1 or 0.
	        if (slideOffset >= .995) {
	          flipped = true;
	          drawerArrowDrawable.setFlip(flipped);
	        } else if (slideOffset <= .005) {
	          flipped = false;
	          drawerArrowDrawable.setFlip(flipped);
	        }

	        drawerArrowDrawable.setParameter(offset);
		}
		
		 /** Called when a drawer has settled in a completely closed state. */
        public void onDrawerClosed(View view) {
            super.onDrawerClosed(view);
            actionBartitle.setText(menulist.get(menuPosotion));
        }

        /** Called when a drawer has settled in a completely open state. */
        public void onDrawerOpened(View drawerView) {
            super.onDrawerOpened(drawerView);
            
            actionBartitle.setText(R.string.drawer_menu_open);
        }

    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        menuToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        menuToggle.onConfigurationChanged(newConfig);
    }
    
    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(Gravity.START)){
            mDrawerLayout.closeDrawer(Gravity.START);
        }else if(bottomUpdrawer){
            slideInLayout.closeDrawerView(slideInLayout.findVisibleDrawer());
        }
        else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Pass the event to ActionBarDrawerToggle, if it returns
        // true, then it has handled the app icon touch event
        if (menuToggle.onOptionsItemSelected(item)) {
          return true;
        }
        // Handle your other action bar items...

        return super.onOptionsItemSelected(item);
    }

	@Override
	public void onSongsSelected(AlbumDetails details, int position) {
        ViewPagerAdapter playAdapter = new ViewPagerAdapter(this,details);
        playPager = (ViewPager)findViewById(R.id.myViewPager);
        playPager.setPageTransformer(true, new DepthPageTransformer());
        playPager.setAdapter(playAdapter);
        System.out.println("print the songs list size::"+details.getSongs().size());
    }

}
