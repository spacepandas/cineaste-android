package de.cineaste.android.controllFlow.movie;

import android.content.res.Resources;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;

import de.cineaste.android.adapter.movie.MovieListAdapter;
import de.cineaste.android.controllFlow.TouchHelperCallback;
import de.cineaste.android.viewholder.movie.MovieViewHolder;

public abstract class BaseMovieTouchHelperCallback extends TouchHelperCallback {

    final MovieListAdapter movieListAdapter;

    BaseMovieTouchHelperCallback(LinearLayoutManager linearLayoutManager, MovieListAdapter movieListAdapter, RecyclerView recyclerView, Resources resources) {
        super(resources, linearLayoutManager, recyclerView);
        this.movieListAdapter = movieListAdapter;
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        movieListAdapter.onItemMove(viewHolder.getAdapterPosition(), target.getAdapterPosition());
        return true;
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

        movieListAdapter.updatePositionsInDb();
    }
}
