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

import java.util.Date;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import de.cineaste.android.Constants;
import de.cineaste.android.MovieClickListener;
import de.cineaste.android.R;
import de.cineaste.android.entity.Movie;
import de.cineaste.android.database.MovieDbHelper;

public class WatchlistAdapter extends RecyclerView.Adapter<WatchlistAdapter.ViewHolder> implements Observer {

    private List<Movie> dataset;
    private final MovieDbHelper db;
    private final Context context;
    private final BaseWatchlistPagerAdapter.WatchlistFragment baseFragment;
    private final MovieClickListener listener;

    @Override
    public void update(Observable observable, Object o) {
        Movie changedMovie = (Movie)o;

        int index = dataset.indexOf(changedMovie);
        if(changedMovie.isWatched() && index != -1){
                dataset.remove(index);
                notifyItemRemoved(index);
        }
        else if(!changedMovie.isWatched() && index == -1){
            dataset.add(changedMovie);
            notifyItemInserted(dataset.size());
        }
        else if( index != -1){
            dataset.set(index, changedMovie);
            notifyItemChanged(index);
        }

        baseFragment.configureWatchlistVisibility();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final TextView movieTitle;
        public final TextView movieRuntime;
        public final TextView movieVote;
        public final ImageView imageView;
        public final ImageButton removeMovieButton;
        public final ImageButton movieWatchedButton;
        final View view;

        public ViewHolder( View v ) {
            super( v );
            movieTitle = (TextView) v.findViewById( R.id.movie_title );
            movieRuntime = (TextView) v.findViewById( R.id.movie_runtime );
            movieVote = (TextView) v.findViewById( R.id.movie_vote );
            removeMovieButton = (ImageButton) v.findViewById( R.id.remove_button );
            movieWatchedButton = (ImageButton) v.findViewById( R.id.watched_button );
            imageView = (ImageView) v.findViewById( R.id.movie_poster_image_view );
            view = v;
        }

        public void assignData( final Movie movie ) {
            Resources resources = context.getResources();

            movieTitle.setText( movie.getTitle() );
            movieRuntime.setText( resources.getString( R.string.runtime, movie.getRuntime() ) );
            movieVote.setText( resources.getString( R.string.vote, movie.getVoteAverage() ) );
            String posterName = movie.getPosterPath();
            String posterUri = Constants.POSTER_URI.replace( "<posterName>", posterName != null ? posterName : "/" );
            Picasso.with( context ).load( posterUri ).error(R.drawable.placeholder_poster ).into( imageView );

            view.setOnClickListener( new View.OnClickListener() {
                @Override
                public void onClick( View v ) {
                    if( listener != null )
                        listener.onMovieClickListener( movie.getId(),
                                new View[]{view, imageView, movieTitle, movieRuntime, movieVote} );
                }
            } );

            removeMovieButton.setOnClickListener( new View.OnClickListener() {

                @Override
                public void onClick( View v ) {
                   removeItemFromDbAndView( movie );
                }
            } );

            movieWatchedButton.setOnClickListener( new View.OnClickListener() {

                @Override
                public void onClick( View v ) {
                    int index = dataset.indexOf( movie );
                    movie.setWatched(true);
                    db.createOrUpdate(movie);
                }
            } );
        }
    }

    public WatchlistAdapter( Context context, BaseWatchlistPagerAdapter.WatchlistFragment baseFragment, MovieClickListener listener ) {
        this.db = MovieDbHelper.getInstance( context );
        this.context = context;
        this.dataset = db.readMoviesByWatchStatus( false );
        this.baseFragment = baseFragment;
        this.db.addObserver(this);
        this.listener = listener;
    }

    @Override
    public WatchlistAdapter.ViewHolder onCreateViewHolder( ViewGroup parent, int viewType ) {
        View v = LayoutInflater.from( parent.getContext() ).inflate( R.layout.card_watchlist, parent, false );
        return new ViewHolder( v );
    }

    @Override
    public void onBindViewHolder( final WatchlistAdapter.ViewHolder holder, final int position ) {
        holder.assignData( dataset.get( position ) );
    }

    @Override
    public int getItemCount() {
        return dataset.size();
    }

    private void removeItemFromDbAndView(Movie movie) {
        int index = dataset.indexOf(movie);
        dataset.remove( index );
        db.deleteMovieFromWatchlist(movie);
        notifyItemRemoved( index );
        baseFragment.configureWatchlistVisibility();
    }
}
