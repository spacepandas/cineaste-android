package de.cineaste.android.behavior;

import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.View;

@SuppressWarnings("unused")
public class ScrollAwareFABBehavior extends FloatingActionButton.Behavior {

	public ScrollAwareFABBehavior(Context context, AttributeSet attrs) {
		super();
	}

	@Override
	public boolean onStartNestedScroll(
			@NonNull CoordinatorLayout coordinatorLayout,
			@NonNull FloatingActionButton child,
			@NonNull View directTargetChild,
			@NonNull View target,
			int nestedScrollAxes) {

		return nestedScrollAxes == ViewCompat.SCROLL_AXIS_VERTICAL ||
				super.onStartNestedScroll(
						coordinatorLayout,
						child,
						directTargetChild,
						target,
						nestedScrollAxes);
	}

	@Override
	public void onNestedScroll(
			@NonNull CoordinatorLayout coordinatorLayout,
			@NonNull final FloatingActionButton child,
			@NonNull View target,
			int dxConsumed,
			int dyConsumed,
			int dxUnconsumed,
			int dyUnconsumed) {
		super.onNestedScroll(
				coordinatorLayout,
				child,
				target,
				dxConsumed,
				dyConsumed,
				dxUnconsumed,
				dyUnconsumed);

		if (dyConsumed > 0 && child.getVisibility() == View.VISIBLE) {
			child.hide();
			new MyAsyncTask().execute(child);
		} else if (dyConsumed < 0 && child.getVisibility() != View.VISIBLE) {
			child.show();
		}
	}

	private static class MyAsyncTask extends AsyncTask<FloatingActionButton, Void, FloatingActionButton> {

		@Override
		protected FloatingActionButton doInBackground(FloatingActionButton... buttons) {
			try {
				Thread.sleep(2000);
			} catch (InterruptedException ex) {
				//do nothing
			}
			return buttons[0];
		}

		@Override
		protected void onPostExecute(FloatingActionButton button) {
			super.onPostExecute(button);
			button.show();
		}
	}
}
