package de.cineaste.android.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import de.cineaste.android.R;
import de.cineaste.android.entity.Movie;
import de.cineaste.android.viewholder.BodyViewHolder;
import de.cineaste.android.viewholder.StateSearchViewHolder;
import de.cineaste.android.viewholder.StateWatchListViewHolder;
import de.cineaste.android.viewholder.StateWatchedListViewHolder;

public class DetailViewAdapter extends RecyclerView.Adapter {
    private final Movie dataset;
    private final int state;
    private final Context context;
    private final OnBackPressedListener onBackPressedListener;

    public DetailViewAdapter(Context context, Movie movie, int state, OnBackPressedListener onBackPressedListener) {
        this.context = context;
        dataset = movie;
        this.state = state;
        this.onBackPressedListener = onBackPressedListener;
    }

    @Override
    public int getItemViewType(int position) {
       return position;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if ( viewType == 0 )
        {
            View v;
            switch (state) {
                case R.string.searchState:
                    v = LayoutInflater.from( parent.getContext() ).inflate(R.layout.state_search, parent, false);
                    return new StateSearchViewHolder(v, context, onBackPressedListener);
                case R.string.watchedlistState:
                    v = LayoutInflater.from( parent.getContext() ).inflate(R.layout.state_watchedlist, parent, false);
                    return new StateWatchedListViewHolder(v, context, onBackPressedListener);
                case R.string.watchlistState:
                    v = LayoutInflater.from(parent.getContext()).inflate(R.layout.state_watchlist, parent, false);
                    return new StateWatchListViewHolder(v, context, onBackPressedListener);
            }
            return null;
        }
        else
        {
            int layout = R.layout.card_detail_footer;
            View v = LayoutInflater.from( parent.getContext() ).inflate( layout, parent, false );
            return new BodyViewHolder( v, context );
        }
    }

    @Override
    public int getItemCount() {
        return 2;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (position == 0) {
            switch (state) {
                case R.string.searchState:
                    StateSearchViewHolder viewHolder = (StateSearchViewHolder) holder;
                    viewHolder.assignData(dataset);
                    break;
                case R.string.watchedlistState:
                    StateWatchedListViewHolder viewHolder1 = (StateWatchedListViewHolder) holder;
                    viewHolder1.assignData(dataset);
                    break;
                case R.string.watchlistState:
                    StateWatchListViewHolder viewHolder2 = (StateWatchListViewHolder) holder;
                    viewHolder2.assignData(dataset);
                    break;
            }
        } else {
            BodyViewHolder viewHolder = (BodyViewHolder) holder;
            viewHolder.assignData(dataset);
        }
    }
}
