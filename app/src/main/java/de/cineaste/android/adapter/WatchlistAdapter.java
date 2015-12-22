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

import java.util.List;

import de.cineaste.android.Constants;
import de.cineaste.android.R;
import de.cineaste.android.entity.Movie;
import de.cineaste.android.persistence.MovieDbHelper;

public class WatchlistAdapter extends RecyclerView.Adapter<WatchlistAdapter.ViewHolder> {

    private final List<Movie> dataset;
    private final MovieDbHelper db;
    private final Context context;
    private final BaseWatchlistPagerAdapter.WatchlistFragment baseFragment;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public final TextView movieTitle;
        public final TextView movieRuntime;
        public final ImageView imageView;
        public final ImageButton removeMovieButton;
        public final ImageButton movieWatchedButton;

        public Movie currentMovie;

        public ViewHolder( View v ) {
            super( v );
            movieTitle = (TextView) v.findViewById( R.id.movie_title );
            movieRuntime = (TextView) v.findViewById( R.id.movie_runtime );
            removeMovieButton = (ImageButton) v.findViewById( R.id.remove_button );
            movieWatchedButton = (ImageButton) v.findViewById( R.id.watched_button );
            imageView = (ImageView) v.findViewById( R.id.movie_poster_image_view );
        }
    }

    public WatchlistAdapter( Context context , BaseWatchlistPagerAdapter.WatchlistFragment baseFragment) {
        this.db = MovieDbHelper.getInstance( context );
        this.context = context;
        this.dataset = db.readMoviesByWatchStatus( false );
        this.baseFragment = baseFragment;
    }

    @Override
    public WatchlistAdapter.ViewHolder onCreateViewHolder( ViewGroup parent, int viewType ) {
        View v = LayoutInflater.from( parent.getContext() ).inflate( R.layout.card_watchlist, parent, false );
        return new ViewHolder( v );
    }

    @Override
    public void onBindViewHolder( final WatchlistAdapter.ViewHolder holder, final int position ) {
        Resources resources = context.getResources();

        holder.currentMovie = dataset.get( position );
        holder.movieTitle.setText(holder.currentMovie.getTitle());
        holder.movieRuntime.setText(resources.getString(R.string.runtime, holder.currentMovie.getRuntime()));
        String posterName = holder.currentMovie.getPosterPath();
        String posterUri = Constants.POSTER_URI.replace( "<posterName", posterName != null ? posterName : "/" );
        Picasso.with( context ).load( posterUri ).error( R.mipmap.ic_launcher ).into( holder.imageView);

        holder.removeMovieButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                int index = dataset.indexOf(holder.currentMovie);
                db.deleteMovieFromWatchlist(holder.currentMovie.getId());
                removeItemFromView(index);
            }
        });

        holder.movieWatchedButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                int index = dataset.indexOf(holder.currentMovie);
                db.updateMovieWatched(true, holder.currentMovie.getId());
                removeItemFromView(index);
            }
        });
    }

    @Override
    public int getItemCount() {
        return dataset.size();
    }

    private void removeItemFromView(int index){
        dataset.remove(index);
        notifyItemRemoved( index );

        if(getItemCount() == 0){
            baseFragment.controlWatchlistAdapter();
        }
    }
}
