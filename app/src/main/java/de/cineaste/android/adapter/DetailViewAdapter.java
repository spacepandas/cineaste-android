package de.cineaste.android.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import de.cineaste.android.R;
import de.cineaste.android.entity.Movie;
import de.cineaste.android.viewholder.BodyViewHolder;
import de.cineaste.android.viewholder.HeadViewHolder;

public class DetailViewAdapter extends RecyclerView.Adapter {
    private final Movie dataset;
    private final int state;
    private final Context context;
    private final HeadViewHolder.OnBackPressedListener onBackPressedListener;

    public DetailViewAdapter(Context context, Movie movie, int state, HeadViewHolder.OnBackPressedListener onBackPressedListener) {
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
            int layout = R.layout.card_detail_head;
            View v = LayoutInflater.from( parent.getContext() ).inflate(layout, parent, false);
           return new HeadViewHolder( v, context, state, onBackPressedListener );
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
            HeadViewHolder viewHolder = (HeadViewHolder) holder;
            viewHolder.assignData(dataset);
        } else {
            BodyViewHolder viewHolder = (BodyViewHolder) holder;
            viewHolder.assignData(dataset);
        }
    }
}
