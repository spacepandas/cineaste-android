package de.cineaste.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.List;

import de.cineaste.R;
import de.cineaste.entity.Movie;
import de.cineaste.persistence.MovieDbHelper;

public class WatchlistAdapter extends RecyclerView.Adapter<WatchlistAdapter.ViewHolder> {

    private List<Movie> mDataset;
    private MovieDbHelper mDb;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView mMovieTitle;
        public ImageButton mRemoveMovieButton;
        public ImageButton mMovieWatchedButton;

        public Movie mCurrentMovie;

        public ViewHolder( View v ) {
            super( v );
            mMovieTitle = (TextView) v.findViewById( R.id.movie_title );
            mRemoveMovieButton = (ImageButton) v.findViewById( R.id.remove_button );
            mMovieWatchedButton = (ImageButton) v.findViewById( R.id.watched_button );
        }
    }

    public WatchlistAdapter( Context context ) {
        mDb = MovieDbHelper.getInstance( context );
        mDataset = mDb.readMoviesByWatchStatus( false );
    }

    @Override
    public WatchlistAdapter.ViewHolder onCreateViewHolder( ViewGroup parent, int viewType ) {
        View v = LayoutInflater.from( parent.getContext() ).inflate( R.layout.watchlist_cardview, parent, false );
        return new ViewHolder( v );
    }

    @Override
    public void onBindViewHolder( final WatchlistAdapter.ViewHolder holder, final int position ) {
        String movieTitle = mDataset.get( position ).getTitle();
        holder.mCurrentMovie = mDataset.get( position );
        holder.mMovieTitle.setText( movieTitle );

        holder.mRemoveMovieButton.setOnClickListener( new View.OnClickListener() {

            @Override
            public void onClick( View v ) {
                int index = mDataset.indexOf( holder.mCurrentMovie );
                mDb.deleteMovieFromWatchlist( holder.mCurrentMovie.getId() );
                mDataset.remove( index );
                notifyItemRemoved( index );
            }
        } );

        holder.mMovieWatchedButton.setOnClickListener( new View.OnClickListener() {

            @Override
            public void onClick( View v ) {
                int index = mDataset.indexOf( holder.mCurrentMovie );
                mDb.updateMovieWatched( true, holder.mCurrentMovie.getId() );
                mDataset.remove( index );
                notifyItemRemoved( index );
            }
        } );
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}
