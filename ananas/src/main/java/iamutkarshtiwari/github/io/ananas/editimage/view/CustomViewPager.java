package iamutkarshtiwari.github.io.ananas.editimage.view;

import android.content.Context;
import android.util.AttributeSet;

import androidx.viewpager.widget.ViewPager;

import iamutkarshtiwari.github.io.ananas.editimage.utils.GestureNavigationBounds;

public class CustomViewPager extends ViewPager {
    private boolean isCanScroll = false;

    public CustomViewPager(Context context) {
        super(context);
    }

    public CustomViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        new GestureNavigationBounds(this);
    }

    @Override
    public void setCurrentItem(int item, boolean smoothScroll) {
        isCanScroll = true;
        super.setCurrentItem(item, smoothScroll);
        isCanScroll = false;
    }

    @Override
    public void setCurrentItem(int item) {
        setCurrentItem(item, false);
    }

    public void setScanScroll(boolean isCanScroll) {
        this.isCanScroll = isCanScroll;
    }

    @Override
    public void scrollTo(int x, int y) {
        if (isCanScroll) {
            super.scrollTo(x, y);
        }
    }
}
