
package me.vinachiong.sample.datepopuppager.newpager.transformer;


import android.view.View;

import androidx.viewpager.widget.ViewPager;

/**
 * Created by mikeafc on 15/11/26.
 */
public class UltraVerticalTransformer implements ViewPager.PageTransformer {
    private float yPosition;

    @Override
    public void transformPage(View view, float position) {
        view.setTranslationX(view.getWidth() * -position);
        yPosition = position * view.getHeight();
        view.setTranslationY(yPosition);
    }

    public float getPosition() {
        return yPosition;
    }
}
