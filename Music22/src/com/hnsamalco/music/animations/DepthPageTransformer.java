package com.hnsamalco.music.animations;

import com.hnsamalco.music.R;

import android.view.View;
import android.widget.LinearLayout;
import android.support.v4.view.ViewPager;

public class DepthPageTransformer implements ViewPager.PageTransformer {
    private static final float MIN_SCALE = 0.75f;

    public void transformPage(View view, float position) {
        int pageWidth = view.getWidth();
        
        View dragView = (LinearLayout)view.findViewById(R.id.dragImage);

        if (position < -1) { // [-Infinity,-1)
            // This page is way off-screen to the left.
        	dragView.setAlpha(0);

        } else if (position <= 0) { // [-1,0]
            // Use the default slide transition when moving to the left page
        	dragView.setAlpha(1);
        	dragView.setTranslationX(0);
        	dragView.setScaleX(1);
        	dragView.setScaleY(1);

        } else if (position <= 1) { // (0,1]
            // Fade the page out.
        	dragView.setAlpha(1 - position);

            // Counteract the default slide transition
        	dragView.setTranslationX(pageWidth * -position);

            // Scale the page down (between MIN_SCALE and 1)
            float scaleFactor = MIN_SCALE
                    + (1 - MIN_SCALE) * (1 - Math.abs(position));
            dragView.setScaleX(scaleFactor);
            dragView.setScaleY(scaleFactor);

        } else { // (1,+Infinity]
            // This page is way off-screen to the right.
        	dragView.setAlpha(0);
        }
    }
}