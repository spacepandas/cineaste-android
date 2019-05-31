package de.cineaste.android.util

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import android.util.AttributeSet
import android.view.MotionEvent

class CustomRecyclerView : RecyclerView {

    var isScrollingEnabled = true
        private set

    fun enableScrolling(enabled: Boolean) {
        this.isScrollingEnabled = enabled
    }

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet?, defStyle: Int) : super(
        context,
        attrs,
        defStyle
    )

    override fun computeVerticalScrollRange(): Int {
        return if (isScrollingEnabled) super.computeVerticalScrollRange() else 0
    }

    override fun onInterceptTouchEvent(e: MotionEvent): Boolean {
        return isScrollingEnabled && super.onInterceptTouchEvent(e)
    }
}
