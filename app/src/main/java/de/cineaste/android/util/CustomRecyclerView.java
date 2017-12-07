package de.cineaste.android.util;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.MotionEvent;


public class CustomRecyclerView extends RecyclerView {

    private boolean scrollingEnabled = true;

    public void enableScrolling(boolean enabled) {
        this.scrollingEnabled = enabled;
    }

    public boolean isScrollingEnabled() {
        return scrollingEnabled;
    }

    public CustomRecyclerView(Context context) {
        super(context);
    }

    public CustomRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public int computeVerticalScrollRange() {
        if (scrollingEnabled)
            return super.computeVerticalScrollRange();

        return 0;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent e) {
        return scrollingEnabled && super.onInterceptTouchEvent(e);
    }
}
