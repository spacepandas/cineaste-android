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
import de.cineaste.android.adapter.MovieListAdapter;
import de.cineaste.android.viewholder.MovieViewHolder;

public abstract class BaseItemTouchHelperCallback extends ItemTouchHelper.Callback {

    final private Paint p = new Paint();
    final private Resources resources;
    final LinearLayoutManager linearLayoutManager;
    final MovieListAdapter movieListAdapter;
    final RecyclerView recyclerView;

    BaseItemTouchHelperCallback(LinearLayoutManager linearLayoutManager, MovieListAdapter movieListAdapter, RecyclerView recyclerView, Resources resources) {
        this.linearLayoutManager = linearLayoutManager;
        this.movieListAdapter = movieListAdapter;
        this.recyclerView = recyclerView;
        this.resources = resources;
    }

    abstract BaseSnackBar getSnackBar();
    abstract int getRightSwipeMessage();
    abstract int getIcon();

    @Override
    public boolean isLongPressDragEnabled() {
        return true;
    }

    @Override
    public boolean isItemViewSwipeEnabled() {
        return true;
    }

    @Override
    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {

        final int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
        final int swipeFlags = ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT;
        return makeMovementFlags(dragFlags, swipeFlags);
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        movieListAdapter.onItemMove(viewHolder.getAdapterPosition(), target.getAdapterPosition());
        return true;
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        final int position = viewHolder.getAdapterPosition();

        if (direction == ItemTouchHelper.LEFT) {
            getSnackBar().getSnackBarLeftSwipe(position);
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

    @Override
    public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
        if (actionState != ItemTouchHelper.ACTION_STATE_IDLE) {
            MovieViewHolder movieViewHolder = (MovieViewHolder) viewHolder;
            movieViewHolder.onItemSelected();
        }

        super.onSelectedChanged(viewHolder, actionState);
    }

    @Override
    public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        super.clearView(recyclerView, viewHolder);

        MovieViewHolder movieViewHolder = (MovieViewHolder) viewHolder;
        movieViewHolder.onItemClear();
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
