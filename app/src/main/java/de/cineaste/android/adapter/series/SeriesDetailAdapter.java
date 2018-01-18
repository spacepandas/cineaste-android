package de.cineaste.android.adapter.series;


import android.content.Context;
import android.content.res.Resources;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import de.cineaste.android.R;
import de.cineaste.android.entity.series.Season;
import de.cineaste.android.entity.series.Series;
import de.cineaste.android.listener.ItemClickListener;

public class SeriesDetailAdapter extends RecyclerView.Adapter {

    public interface SeriesStateManipulationClickListener {
        void onDeleteClicked();
        void onAddToHistoryClicked();
        void onAddToWatchClicked();
    }

    private Series series;
    private ItemClickListener clickListener;
    private int state;
    private SeriesStateManipulationClickListener listener;

    public SeriesDetailAdapter(Series series, ItemClickListener clickListener, int state, SeriesStateManipulationClickListener listener) {
        this.series = series;
        List<Season> seasons = new ArrayList<>();
        for (Season season : series.getSeasons()) {
            if (season.getSeasonNumber() > 0) {
                seasons.add(season);
            }
        }
        this.series.getSeasons().clear();
        this.series.setSeasons(seasons);
        this.clickListener = clickListener;
        this.state = state;
        this.listener = listener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v;
        switch (viewType) {
            case 0:
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.series_detail_base, parent, false);
                return new BaseViewHolder(v, parent.getContext());
            case 1:
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.series_detail_buttons, parent, false);
                return new ButtonsViewHolder(v, state, listener);
            case 2:
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.series_detail_description, parent, false);
                return new DescriptionViewHolder(v, parent.getContext());
            case 3:
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.series_detail_seasons, parent, false);
                return new SeasonsListViewHolder(v, parent.getContext(), clickListener);
        }

        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        switch (position) {
            case 0:
                ((BaseViewHolder) holder).assignData(series);
                break;
            case 1:
                ((ButtonsViewHolder) holder).assignData();
                break;
            case 2:
                ((DescriptionViewHolder) holder).assignData(series);
                break;
            case 3:
                ((SeasonsListViewHolder) holder).assignData(series);
                break;
        }
    }

    @Override
    public int getItemCount() {
//        if (state == R.string.searchState)
//            return 3;
        return 4;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    private class BaseViewHolder extends RecyclerView.ViewHolder {
        private TextView rating;
        private TextView title;
        private TextView seasons;
        private TextView episodes;
        private TextView currentStatus;
        private TextView releaseDate;
        private TextView toBeContinued;
        private Resources resources;

        BaseViewHolder(View itemView, Context context) {
            super(itemView);
            this.rating = itemView.findViewById(R.id.rating);
            this.title = itemView.findViewById(R.id.title);
            this.seasons = itemView.findViewById(R.id.seasons);
            this.episodes = itemView.findViewById(R.id.episodes);
            this.currentStatus = itemView.findViewById(R.id.currentStatus);
            this.releaseDate = itemView.findViewById(R.id.releaseDate);
            this.toBeContinued = itemView.findViewById(R.id.toBeContinued);
            this.resources = context.getResources();
        }

        void assignData(Series series) {
            title.setText(series.getName());
            if (series.getReleaseDate() != null) {
                releaseDate.setText(convertDate(series.getReleaseDate()));
                releaseDate.setVisibility(View.VISIBLE);
            } else {
                releaseDate.setVisibility(View.GONE);
            }

            rating.setText(String.valueOf(series.getVoteAverage()));
            episodes.setText(resources.getString(R.string.episodes, String.valueOf(series.getNumberOfEpisodes())));
            seasons.setText(resources.getString(R.string.seasons, String.valueOf(series.getNumberOfSeasons())));
            currentStatus.setText(resources.getString(R.string.currentStatus,
                    String.valueOf(series.getCurrentNumberOfSeason()),
                    String.valueOf(series.getCurrentNumberOfEpisode())));
            if (series.isInProduction()) {
                toBeContinued.setVisibility(View.VISIBLE);
            } else {
                toBeContinued.setVisibility(View.GONE);
            }
        }

        private String convertDate(Date date) {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy", resources.getConfiguration().locale);
            return simpleDateFormat.format(date);
        }

    }

    private class ButtonsViewHolder extends RecyclerView.ViewHolder {
        private Button deleteBtn;
        private Button historyBtn;
        private Button watchListBtn;
        private SeriesStateManipulationClickListener listener;

        ButtonsViewHolder(View itemView, int state, SeriesStateManipulationClickListener listener) {
            super(itemView);
            deleteBtn = itemView.findViewById(R.id.delete_button);
            historyBtn = itemView.findViewById(R.id.history_button);
            watchListBtn = itemView.findViewById(R.id.to_watchlist_button);
            this.listener = listener;

            switch (state) {
                case R.string.searchState:
                    deleteBtn.setVisibility(View.GONE);
                    historyBtn.setVisibility(View.VISIBLE);
                    watchListBtn.setVisibility(View.VISIBLE);
                    break;
                case R.string.historyState:
                    deleteBtn.setVisibility(View.VISIBLE);
                    historyBtn.setVisibility(View.GONE);
                    watchListBtn.setVisibility(View.VISIBLE);
                    break;
                case R.string.watchlistState:
                    deleteBtn.setVisibility(View.VISIBLE);
                    historyBtn.setVisibility(View.VISIBLE);
                    watchListBtn.setVisibility(View.GONE);
                    break;
            }
        }

        public void assignData() {
            deleteBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onDeleteClicked();
                }
            });

            historyBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onAddToHistoryClicked();
                }
            });

            watchListBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onAddToWatchClicked();
                }
            });
        }
    }

    private class DescriptionViewHolder extends RecyclerView.ViewHolder {
        private TextView description;
        private TextView more;
        private Resources resources;

        DescriptionViewHolder(View itemView, Context context) {
            super(itemView);
            this.description = itemView.findViewById(R.id.description);
            this.more = itemView.findViewById(R.id.more);
            this.resources = context.getResources();
        }

        void assignData(final Series series) {
            more.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showCompleteText(series);
                }
            });
            description.setText(getTrimmedDescription(series));
        }

        private void setDescription(Series series) {
            String original = series.getDescription();
            description.setText(
                    (original == null || original.isEmpty())
                            ? resources.getString(R.string.noDescription) : original);
        }

        private void showCompleteText(Series series) {
            setDescription(series);
            more.setVisibility(View.GONE);

        }

        private String getTrimmedDescription(Series series) {
            String original = series.getDescription();

            if (original == null || original.isEmpty()) {
                return resources.getString(R.string.noDescription);
            }

            if (original.length() <= 200) {
                more.setVisibility(View.GONE);
                return original;
            }
            more.setVisibility(View.VISIBLE);
            return original.substring(0, 200) + "...";
        }
    }

    private class SeasonsListViewHolder extends RecyclerView.ViewHolder {
        private RecyclerView recyclerView;
        private Context context;
        private ItemClickListener itemClickListener;

        SeasonsListViewHolder(View itemView, Context context, ItemClickListener itemClickListener) {
            super(itemView);

            this.recyclerView = itemView.findViewById(R.id.seasonPoster);
            this.context = context;
            this.itemClickListener = itemClickListener;
        }

        void assignData(Series series) {
            LinearLayoutManager layoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);

            recyclerView.setLayoutManager(layoutManager);
            recyclerView.setItemAnimator(new DefaultItemAnimator());
            recyclerView.setNestedScrollingEnabled(false);

            SeasonAdapter adapter = new SeasonAdapter(context, itemClickListener, series);
            recyclerView.setAdapter(adapter);
        }
    }
}
