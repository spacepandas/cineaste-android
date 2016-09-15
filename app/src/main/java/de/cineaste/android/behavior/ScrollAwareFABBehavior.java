package de.cineaste.android.behavior;

import android.content.Context;
import android.os.AsyncTask;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.View;

public class ScrollAwareFABBehavior extends FloatingActionButton.Behavior {

    public ScrollAwareFABBehavior( Context context, AttributeSet attrs ) {
        super();
    }

    @Override
    public boolean onStartNestedScroll(
            CoordinatorLayout coordinatorLayout,
            FloatingActionButton child,
            View directTargetChild,
            View target,
            int nestedScrollAxes ) {

        return nestedScrollAxes == ViewCompat.SCROLL_AXIS_VERTICAL ||
                super.onStartNestedScroll(
                        coordinatorLayout,
                        child,
                        directTargetChild,
                        target,
                        nestedScrollAxes );
    }

    @Override
    public void onNestedScroll(
            CoordinatorLayout coordinatorLayout,
            final FloatingActionButton child,
            View target,
            int dxConsumed,
            int dyConsumed,
            int dxUnconsumed,
            int dyUnconsumed ) {
        super.onNestedScroll(
                coordinatorLayout,
                child,
                target,
                dxConsumed,
                dyConsumed,
                dxUnconsumed,
                dyUnconsumed );

        if( dyConsumed > 0 && child.getVisibility() == View.VISIBLE ) {
            child.hide();
            new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... voids) {
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException ex) {
                        //do nothing
                    }
                    return null;
                }

                @Override
                protected void onPostExecute(Void aVoid) {
                    super.onPostExecute(aVoid);
                    child.show();
                }
            }.execute();
        } else if( dyConsumed < 0 && child.getVisibility() != View.VISIBLE ) {
            child.show();
        }
    }
}
