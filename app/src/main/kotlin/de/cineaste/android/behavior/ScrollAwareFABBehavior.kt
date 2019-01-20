package de.cineaste.android.behavior

import android.content.Context
import android.support.design.widget.CoordinatorLayout
import android.support.design.widget.FloatingActionButton
import android.support.v4.view.ViewCompat
import android.util.AttributeSet
import android.view.View
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

@Suppress("unused")
class ScrollAwareFABBehavior(context: Context, attrs: AttributeSet) : FloatingActionButton.Behavior() {

    override fun onStartNestedScroll(
            coordinatorLayout: CoordinatorLayout,
            child: FloatingActionButton,
            directTargetChild: View,
            target: View,
            nestedScrollAxes: Int,
            type: Int): Boolean {

        return nestedScrollAxes == ViewCompat.SCROLL_AXIS_VERTICAL || super.onStartNestedScroll(
                coordinatorLayout,
                child,
                directTargetChild,
                target,
                nestedScrollAxes,
                type)
    }

    override fun onNestedScroll(
            coordinatorLayout: CoordinatorLayout,
            child: FloatingActionButton,
            target: View,
            dxConsumed: Int,
            dyConsumed: Int,
            dxUnconsumed: Int,
            dyUnconsumed: Int,
            type: Int) {
        super.onNestedScroll(
                coordinatorLayout,
                child,
                target,
                dxConsumed,
                dyConsumed,
                dxUnconsumed,
                dyUnconsumed,
                type)

        if (dyConsumed > 0 && child.visibility == View.VISIBLE) {
            child.hide()
            GlobalScope.launch {
                Thread.sleep(2000)
                launch(Main) {
                    child.show()
                }
            }
        } else if (dyConsumed < 0 && child.visibility != View.VISIBLE) {
            child.show()
        }
    }
}
