package de.cineaste.android.controllFlow;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;

import de.cineaste.android.R;
import de.cineaste.android.adapter.BaseWatchlistAdapter;

/**
 * Created by marcelgross on 08.11.17.
 */

public abstract class BaseItemTouchHelperCallback extends ItemTouchHelper.SimpleCallback {

    private Paint p = new Paint();
    private Resources resources;
    LinearLayoutManager linearLayoutManager;
    BaseWatchlistAdapter baseWatchlistAdapter;
    RecyclerView recyclerView;

    public BaseItemTouchHelperCallback(LinearLayoutManager linearLayoutManager, BaseWatchlistAdapter baseWatchlistAdapter, RecyclerView recyclerView, Resources resources) {
        super(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT);
        this.linearLayoutManager = linearLayoutManager;
        this.baseWatchlistAdapter = baseWatchlistAdapter;
        this.recyclerView = recyclerView;
        this.resources = resources;
    }

    abstract BaseSnackBar getSnackBar();
    abstract int getRightSwipeMessage();


    abstract int getIcon();

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        return false;
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        final int position = viewHolder.getAdapterPosition();

        if (direction == ItemTouchHelper.LEFT) {
            getSnackBar().getSnackBarLeftSwipe(position, R.string.movie_deleted);
        } else {
            getSnackBar().getSnackBarRightSwipe(position, getRightSwipeMessage());
        }
    }

    @Override
    public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {

        if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {

            View itemView = viewHolder.itemView;
            float height = (float) itemView.getBottom() - (float) itemView.getTop();
            float width = height / 3;

            int color;
            RectF background;
            RectF icon_dest;
            int iconId;
            if (dX > 0) {
                color = Color.parseColor("green");
                background = getBackground(itemView, itemView.getLeft(), dX);
                icon_dest = getIconDest(itemView, width, (float) itemView.getLeft() + width, (float) itemView.getLeft() + 2 * width);
                iconId = getIcon();
            } else {
                color = Color.parseColor("red");
                background = getBackground(itemView, (float) itemView.getRight() + dX, (float) itemView.getRight());
                icon_dest = getIconDest(itemView, width, (float) itemView.getRight() - 2 * width, (float) itemView.getRight() - width);
                iconId = R.drawable.ic_delete_white;
            }
            drawBackgroundForSwipe(color, c, background, icon_dest, iconId);
        }
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
    }

    private void drawBackgroundForSwipe(int color, Canvas c, RectF background, RectF icon_dest, int iconId) {
        Bitmap icon;
        p.setColor(color);
        c.drawRect(background, p);
        icon = BitmapFactory.decodeResource(resources, iconId);
        c.drawBitmap(icon, null, icon_dest, p);
    }

    private RectF getBackground(View itemView, float left, float right) {
        return new RectF(left, (float) itemView.getTop(), right, (float) itemView.getBottom());
    }

    private RectF getIconDest(View itemView, float width, float left, float right) {
        return new RectF(left, (float) itemView.getTop() + width, right, (float) itemView.getBottom() - width);
    }
}
