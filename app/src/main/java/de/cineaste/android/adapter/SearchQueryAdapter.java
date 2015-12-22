package de.cineaste.android.adapter;

import android.content.Context;
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
import de.cineaste.android.network.TheMovieDb;
import de.cineaste.android.persistence.MovieDbHelper;

public class SearchQueryAdapter extends RecyclerView.Adapter<SearchQueryAdapter.ViewHolder> {
    public List<Movie> dataset;
    private final MovieDbHelper db;
    private final Context context;
    private final TheMovieDb theMovieDb;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public final TextView mMovieTitle;
        public final ImageView mMoviePoster;
        public final ImageButton mAddToWatchlistButton;
        public final ImageButton mMovieWatchedButton;

        public Movie mCurrentMovie;

        public ViewHolder( View v ) {
            super( v );
            mMovieTitle = (TextView) v.findViewById( R.id.movie_title );
            mMoviePoster = (ImageView) v.findViewById( R.id.movie_poster_image_view );
            mAddToWatchlistButton = (ImageButton) v.findViewById( R.id.to_watchlist_button );
            mMovieWatchedButton = (ImageButton) v.findViewById( R.id.watched_button );
        }
    }

    public SearchQueryAdapter( Context context, List<Movie> movies ) {
        db = MovieDbHelper.getInstance( context );
        this.context = context;
        dataset = movies;
        theMovieDb = new TheMovieDb();
    }

    @Override
    public SearchQueryAdapter.ViewHolder onCreateViewHolder( ViewGroup parent, int viewType ) {
        View v = LayoutInflater
                .from( parent.getContext() )
                .inflate( R.layout.card_movie_search_query, parent, false );
        return new ViewHolder( v );
    }

    @Override
    public void onBindViewHolder( final ViewHolder holder, final int position ) {
        String movieTitle = dataset.get( position ).getTitle();
        holder.mCurrentMovie = dataset.get( position );
        holder.mMovieTitle.setText( movieTitle );
        String posterName = holder.mCurrentMovie.getPosterPath();
        String posterUri =
                Constants.POSTER_URI
                        .replace( "<posterName>", posterName != null ? posterName : "/" );
        Picasso.with( context ).load( posterUri ).error( R.mipmap.ic_launcher ).into( holder.mMoviePoster );

        holder.mAddToWatchlistButton.setOnClickListener( new View.OnClickListener() {

            @Override
            public void onClick( View v ) {
                int index = dataset.indexOf( holder.mCurrentMovie );

                theMovieDb.fetchMovie(holder.mCurrentMovie.getId(), new TheMovieDb.OnFetchMovieResultListener() {
                    @Override
                    public void onFetchMovieResultListener(Movie movie) {
                        db.createNewMovieEntry(movie );
                    }
                });

                dataset.remove( index );
                notifyItemRemoved( index );
            }
        } );

        holder.mMovieWatchedButton.setOnClickListener( new View.OnClickListener() {

            @Override
            public void onClick( View v ) {
                int index = dataset.indexOf( holder.mCurrentMovie );

                theMovieDb.fetchMovie(holder.mCurrentMovie.getId(), new TheMovieDb.OnFetchMovieResultListener() {
                    @Override
                    public void onFetchMovieResultListener(Movie movie) {
                        movie.setWatched(true);
                        db.createNewMovieEntry(movie );
                    }
                });

                dataset.remove( index );
                notifyItemRemoved( index );
            }
        } );
    }


    @Override
    public int getItemCount() {
        return dataset.size();
    }
}

