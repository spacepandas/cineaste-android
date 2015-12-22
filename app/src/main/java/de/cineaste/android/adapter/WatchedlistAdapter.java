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
import java.util.Observable;
import java.util.Observer;

import de.cineaste.android.Constants;
import de.cineaste.android.R;
import de.cineaste.android.entity.Movie;
import de.cineaste.android.persistence.MovieDbHelper;

public class WatchedlistAdapter extends RecyclerView.Adapter<WatchedlistAdapter.ViewHolder> implements Observer {

    private List<Movie> mDataset;
    private final MovieDbHelper mDb;
    private final Context context;
    private final BaseWatchlistPagerAdapter.WatchlistFragment baseFragment;

    public WatchedlistAdapter( Context context, BaseWatchlistPagerAdapter.WatchlistFragment baseFragment ) {
        this.mDb = MovieDbHelper.getInstance( context );
        this.context = context;
        this.mDb.addObserver( this );
        this.mDataset = mDb.readMoviesByWatchStatus( true );
        this.baseFragment = baseFragment;
    }

    @Override
    public void update( Observable observable, Object data ) {
        mDataset = mDb.readMoviesByWatchStatus( true );
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public final TextView mMovieTitle;
        public final ImageButton mRemoveMovie;
        public final ImageView mImageView;
        public Movie mCurrentMovie;

        public ViewHolder( View v ) {
            super( v );
            mMovieTitle = (TextView) v.findViewById( R.id.movie_title );
            mRemoveMovie = (ImageButton) v.findViewById( R.id.remove_button );
            mImageView = (ImageView) v.findViewById( R.id.movie_poster_image_view );
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
        String movieTitle = mDataset.get( position ).getTitle();
        holder.mCurrentMovie = mDataset.get( position );
        holder.mMovieTitle.setText( movieTitle );
        String posterName = holder.mCurrentMovie.getPosterPath();
        String posterUri = Constants.POSTER_URI
                .replace( "<posterName>", posterName != null ? posterName : "/" );
        Picasso.with( context ).load( posterUri ).error( R.mipmap.ic_launcher ).into( holder.mImageView );

        holder.mRemoveMovie.setOnClickListener( new View.OnClickListener() {

            @Override
            public void onClick( View v ) {
                int index = mDataset.indexOf( holder.mCurrentMovie );
                removeItemFromViewAndDb( index, holder.mCurrentMovie.getId() );
            }
        } );
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    private void removeItemFromViewAndDb( int index, long dbId ) {
        mDb.deleteMovieFromWatchlist( dbId );
        mDataset.remove( index );
        notifyItemRemoved( index );

        if( getItemCount() == 0 ) {
            baseFragment.controlWatchedlistAdapter();
        }
    }
}