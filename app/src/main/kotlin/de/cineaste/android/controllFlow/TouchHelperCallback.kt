package de.cineaste.android.controllFlow

import android.content.res.Resources
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.ItemTouchHelper
import de.cineaste.android.R

abstract class TouchHelperCallback protected constructor(
    private val resources: Resources,
    protected val linearLayoutManager: LinearLayoutManager,
    protected val recyclerView: RecyclerView
) : ItemTouchHelper.Callback() {

    protected abstract val snackBar: BaseSnackBar

    protected abstract val rightSwipeMessage: Int

    protected abstract val icon: Int

    override fun isLongPressDragEnabled(): Boolean {
        return true
    }

    override fun isItemViewSwipeEnabled(): Boolean {
        return true
    }

    override fun getMovementFlags(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder
    ): Int {

        val dragFlags = ItemTouchHelper.UP or ItemTouchHelper.DOWN
        val swipeFlags = ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        return makeMovementFlags(dragFlags, swipeFlags)
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        val position = viewHolder.adapterPosition

        if (direction == ItemTouchHelper.LEFT) {
            snackBar.getSnackBarLeftSwipe(position)
        } else {
            snackBar.getSnackBarRightSwipe(position, rightSwipeMessage)
        }
    }

    override fun onChildDraw(
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ) {

        if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {

            val itemView = viewHolder.itemView

            val myIcon: Drawable
            val background: Drawable

            val iconMargin = 48
            val iconLeft: Int
            val iconRight: Int
            val intrinsicWidth: Int
            if (dX > 0) {
                val tempIcon = ContextCompat.getDrawable(recyclerView.context, icon) ?: return
                myIcon = tempIcon
                background =
                    ColorDrawable(ContextCompat.getColor(recyclerView.context, R.color.colorAccent))

                intrinsicWidth = myIcon.intrinsicWidth

                iconLeft = itemView.left + iconMargin
                iconRight = itemView.left + iconMargin + intrinsicWidth
                myIcon.setColorFilter(Color.BLACK, PorterDuff.Mode.SRC_ATOP)
            } else {
                val tempIcon =
                    ContextCompat.getDrawable(recyclerView.context, R.drawable.ic_delete_white)
                        ?: return
                myIcon = tempIcon
                background = ColorDrawable(
                    ContextCompat.getColor(
                        recyclerView.context,
                        R.color.colorPrimary
                    )
                )

                intrinsicWidth = myIcon.intrinsicWidth

                iconLeft = itemView.right - iconMargin - intrinsicWidth
                iconRight = itemView.right - iconMargin
                myIcon.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP)
            }

            val itemHeight = itemView.bottom - itemView.top
            val intrinsicHeight = myIcon.intrinsicHeight
            val iconTop = itemView.top + (itemHeight - intrinsicHeight) / 2
            val iconBottom = iconTop + intrinsicHeight

            myIcon.setBounds(iconLeft, iconTop, iconRight, iconBottom)

            background.setBounds(itemView.left, itemView.top, itemView.right, itemView.bottom)

            background.draw(c)
            myIcon.draw(c)
        }
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
    }
}
