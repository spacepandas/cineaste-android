package de.cineaste.android.controllFlow.movie

import android.content.res.Resources
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.ItemTouchHelper

import de.cineaste.android.adapter.movie.MovieListAdapter
import de.cineaste.android.controllFlow.TouchHelperCallback
import de.cineaste.android.viewholder.movie.MovieViewHolder

abstract class BaseMovieTouchHelperCallback(
    linearLayoutManager: LinearLayoutManager,
    val movieListAdapter: MovieListAdapter,
    recyclerView: RecyclerView,
    resources: Resources
) :
    TouchHelperCallback(resources, linearLayoutManager, recyclerView) {

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        movieListAdapter.onItemMove(viewHolder.adapterPosition, target.adapterPosition)
        return true
    }

    override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {
        if (actionState != ItemTouchHelper.ACTION_STATE_IDLE) {
            val movieViewHolder = viewHolder as MovieViewHolder
            movieViewHolder.onItemSelected()
        }

        super.onSelectedChanged(viewHolder, actionState)
    }

    override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
        super.clearView(recyclerView, viewHolder)

        val movieViewHolder = viewHolder as MovieViewHolder
        movieViewHolder.onItemClear()

        movieListAdapter.updatePositionsInDb()
    }
}
