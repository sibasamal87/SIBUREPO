package com.hnsamalco.music;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hnsamalco.music.customview.PagerSlidingTabStrip;

public class MyLibraryFragment extends Fragment {

    private PagerSlidingTabStrip tabs;
    private ViewPager pager;
    private MyPagerAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.my_library_fragment, container,false);
        tabs = (PagerSlidingTabStrip) view.findViewById(R.id.tabs);
        pager = (ViewPager) view.findViewById(R.id.pager);
        pager.setOffscreenPageLimit(3);

        System.out.println("insideeee fragment");
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        adapter = new MyPagerAdapter(getFragmentManager());
        pager.setAdapter(adapter);
        final int pageMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 30, getResources()
             .getDisplayMetrics());
        pager.setPageMargin(pageMargin);
        tabs.setViewPager(pager);
    }

    public class MyPagerAdapter extends FragmentStatePagerAdapter{

        private final String[] TITLES = { "GENERS","ARTISTS","ALBUMS","SONGS"};
        private FragmentManager mFragmentManager;

        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
            mFragmentManager = fm;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return TITLES[position];
        }

        @Override
        public int getCount() {
            return TITLES.length;
        }

        @Override
        public Fragment getItem(int position) {

            String name = makeFragmentName(R.id.pager, position);
            Fragment f = mFragmentManager.findFragmentByTag(name);
            if(f == null){
            	f=MyLibraryTabFragment.newInstance(position);
            	System.out.println("going insideee nullllllll");
            }else{
            	System.out.println("going insideee nullllllll else::");
            }
            return f;
        }

        private String makeFragmentName(int viewId, long id) {
        	return "android:switcher:" + viewId + ":" + id;
        }

    }
}
