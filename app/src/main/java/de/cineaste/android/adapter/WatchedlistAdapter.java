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
import java.util.Observable;
import java.util.Observer;

import de.cineaste.android.Constants;
import de.cineaste.android.R;
import de.cineaste.android.entity.Movie;
import de.cineaste.android.persistence.MovieDbHelper;

public class WatchedlistAdapter extends RecyclerView.Adapter<WatchedlistAdapter.ViewHolder> implements Observer {

    private List<Movie> dataset;
    private final MovieDbHelper db;
    private final Context context;
    private final BaseWatchlistPagerAdapter.WatchlistFragment baseFragment;

    public WatchedlistAdapter( Context context, BaseWatchlistPagerAdapter.WatchlistFragment baseFragment ) {
        this.db = MovieDbHelper.getInstance( context );
        this.context = context;
        this.db.addObserver( this );
        this.dataset = db.readMoviesByWatchStatus( true );
        this.baseFragment = baseFragment;
    }

    @Override
    public void update( Observable observable, Object data ) {
        dataset = db.readMoviesByWatchStatus( true );
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public final TextView movieTitle;
        public final TextView movieRuntime;
        public final TextView movieVote;
        public final ImageButton removeMovie;
        public final ImageView imageView;
        public Movie currentMovie;

        public ViewHolder( View v ) {
            super( v );
            movieTitle = (TextView) v.findViewById( R.id.movie_title );
            movieRuntime = (TextView) v.findViewById( R.id.movie_runtime );
            movieVote = (TextView) v.findViewById( R.id.movie_vote );
            removeMovie = (ImageButton) v.findViewById( R.id.remove_button );
            imageView = (ImageView) v.findViewById( R.id.movie_poster_image_view );
        }
    }

    public WatchedlistAdapter.ViewHolder onCreateViewHolder( ViewGroup parent, int viewType ) {
        View v = LayoutInflater
                .from( parent.getContext() )
                .inflate( R.layout.card_watchedlist, parent, false );
        return new ViewHolder( v );
    }

    @Override
    public void onBindViewHolder( final WatchedlistAdapter.ViewHolder holder, final int position ) {
        Resources resources = context.getResources();

        holder.currentMovie = dataset.get( position );
        holder.movieTitle.setText(holder.currentMovie.getTitle());
        holder.movieRuntime.setText(resources.getString(R.string.runtime, holder.currentMovie.getRuntime()));
        holder.movieVote.setText(resources.getString(R.string.vote, holder.currentMovie.getVoteAverage()));
        String posterName = holder.currentMovie.getPosterPath();
        String posterUri = Constants.POSTER_URI
                .replace( "<posterName>", posterName != null ? posterName : "/" );
        Picasso.with( context ).load( posterUri ).error( R.mipmap.ic_launcher ).into( holder.imageView);

        holder.removeMovie.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                int index = dataset.indexOf(holder.currentMovie);
                removeItemFromViewAndDb(index, holder.currentMovie.getId());
            }
        });
    }

    @Override
    public int getItemCount() {
        return dataset.size();
    }

    private void removeItemFromViewAndDb( int index, long dbId ) {
        db.deleteMovieFromWatchlist(dbId);
        dataset.remove(index);
        notifyItemRemoved( index );

        if( getItemCount() == 0 ) {
            baseFragment.controlWatchedlistAdapter();
        }
    }
}