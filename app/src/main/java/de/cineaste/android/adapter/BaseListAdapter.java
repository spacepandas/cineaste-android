package de.cineaste.android.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import de.cineaste.android.fragment.WatchState;
import de.cineaste.android.listener.ItemClickListener;

public abstract class BaseListAdapter  extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements Filterable {

    public interface DisplayMessage {
        void showMessageIfEmptyList();
    }

    protected abstract Filter getInternalFilter();
    protected abstract int getLayout();
    protected abstract RecyclerView.ViewHolder createViewHolder(View v);
    protected abstract void assignDataToViewHolder(RecyclerView.ViewHolder holder, int position);

    protected final Context context;
    protected final DisplayMessage displayMessage;
    protected final ItemClickListener listener;
    protected final WatchState state;

    public BaseListAdapter(Context context, DisplayMessage displayMessage, ItemClickListener listener, WatchState state) {
        this.context = context;
        this.displayMessage = displayMessage;
        this.listener = listener;
        this.state = state;
    }

    @Override
    public Filter getFilter() {
        return getInternalFilter();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater
                .from(parent.getContext())
                .inflate(getLayout(), parent, false);
        return createViewHolder(v);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        assignDataToViewHolder(holder, position);
    }
}
