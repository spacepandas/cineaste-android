package de.cineaste.android.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import de.cineaste.android.Constants;
import de.cineaste.android.MovieClickListener;
import de.cineaste.android.R;
import de.cineaste.android.entity.Movie;
import de.cineaste.android.database.MovieDbHelper;

public class WatchedlistAdapter extends BaseWatchlistAdapter implements Observer {

    private final MovieDbHelper db;
    private final Context context;
    private final WatchlistViewPagerAdapter.WatchlistFragment baseFragment;
    private final MovieClickListener listener;

    public WatchedlistAdapter(Context context, WatchlistViewPagerAdapter.WatchlistFragment baseFragment, MovieClickListener listener) {
        this.db = MovieDbHelper.getInstance(context);
        this.context = context;
        this.db.addObserver(this);
        this.dataset = db.readMoviesByWatchStatus(true);
        this.filteredDataset = new LinkedList<>(dataset);
        this.baseFragment = baseFragment;
        this.listener = listener;
    }

    @Override
    public void update(Observable observable, Object data) {
        Movie changedMovie = (Movie) data;
        int index = dataset.indexOf(changedMovie);
        if (!changedMovie.isWatched() && index != -1) {
            dataset.remove(index);
            filter(oldSearchTerm);
        } else if (changedMovie.isWatched() && index == -1) {
            dataset.add(changedMovie);
            filter(oldSearchTerm);
        }
        baseFragment.configureWatchedlistVisibility();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        public final TextView movieTitle;
        public final TextView movieRuntime;
        public final TextView movieVote;
        public final ImageButton removeMovie;
        public final ImageView imageView;
        final View view;

        public ViewHolder(View v) {
            super(v);
            movieTitle = (TextView) v.findViewById(R.id.movie_title);
            movieRuntime = (TextView) v.findViewById(R.id.movie_runtime);
            movieVote = (TextView) v.findViewById(R.id.movie_vote);
            removeMovie = (ImageButton) v.findViewById(R.id.remove_button);
            imageView = (ImageView) v.findViewById(R.id.movie_poster_image_view);
            view = v;
        }

        public void assignData(final Movie movie) {
            Resources resources = context.getResources();

            movieTitle.setText(movie.getTitle());
            movieRuntime.setText(resources.getString(R.string.runtime, movie.getRuntime()));
            movieVote.setText(resources.getString(R.string.vote, movie.getVoteAverage()));
            String posterName = movie.getPosterPath();
            String posterUri = Constants.POSTER_URI
                    .replace("<posterName>", posterName != null ? posterName : "/");
            Picasso.with(context).load(posterUri).error(R.drawable.placeholder_poster).into(imageView);

            removeMovie.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    removeItemFromDbAndView(movie);
                }
            });

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        listener.onMovieClickListener(movie.getId(),
                                new View[]{view, imageView, movieTitle, movieRuntime, movieVote});
                    }
                }
            });
        }
    }

    public WatchedlistAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewTyp) {
        View v = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.card_watchedlist, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        ((WatchedlistAdapter.ViewHolder) holder).assignData(filteredDataset.get(position));
    }

    @Override
    public int getItemCount() {
        return filteredDataset.size();
    }

    private void removeItemFromDbAndView(Movie movie) {
        int index = filteredDataset.indexOf(movie);
        dataset.remove(movie);
        filteredDataset.remove(index);
        db.deleteMovieFromWatchlist(movie);
        notifyItemRemoved(index);
        baseFragment.configureWatchedlistVisibility();
    }
}