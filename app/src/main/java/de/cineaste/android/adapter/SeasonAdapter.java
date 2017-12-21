package de.cineaste.android.adapter;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import de.cineaste.android.R;
import de.cineaste.android.database.SeriesDbHelper;
import de.cineaste.android.entity.Season;
import de.cineaste.android.entity.Series;
import de.cineaste.android.listener.ItemClickListener;
import de.cineaste.android.viewholder.SeasonViewHolder;

public class SeasonAdapter extends RecyclerView.Adapter<SeasonViewHolder> {

    private SeriesDbHelper dbHelper;
    private List<Season> seasons = new ArrayList<>();
    private final Context context;
    private final ItemClickListener listener;

    public SeasonAdapter(Context context, ItemClickListener listener, long seriesId) {
        this.dbHelper = SeriesDbHelper.getInstance(context);
        this.context = context;
        this.listener = listener;
        Series series = dbHelper.readSeries(seriesId);
        this.seasons.clear();
        this.seasons.addAll(series.getSeasons());
    }

    @Override
    public SeasonViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.card_season, parent, false);

        return new SeasonViewHolder(view, listener, context);
    }

    @Override
    public void onBindViewHolder(SeasonViewHolder holder, int position) {
        holder.assignData(seasons.get(position));
    }

    @Override
    public int getItemCount() {
        return seasons.size();
    }
}
