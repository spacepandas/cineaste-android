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
import de.cineaste.android.persistence.MovieDbHelper;

public class WatchlistAdapter extends RecyclerView.Adapter<WatchlistAdapter.ViewHolder> {

    private final List<Movie> mDataset;
    private final MovieDbHelper mDb;
    private final Context context;
    private final BaseWatchlistPagerAdapter.WatchlistFragment baseFragment;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public final TextView mMovieTitle;
        public final ImageView mImageView;
        public final ImageButton mRemoveMovieButton;
        public final ImageButton mMovieWatchedButton;

        public Movie mCurrentMovie;

        public ViewHolder( View v ) {
            super( v );
            mMovieTitle = (TextView) v.findViewById( R.id.movie_title );
            mRemoveMovieButton = (ImageButton) v.findViewById( R.id.remove_button );
            mMovieWatchedButton = (ImageButton) v.findViewById( R.id.watched_button );
            mImageView = (ImageView) v.findViewById( R.id.movie_poster_image_view );
        }
    }

    public WatchlistAdapter( Context context , BaseWatchlistPagerAdapter.WatchlistFragment baseFragment) {
        this.mDb = MovieDbHelper.getInstance( context );
        this.context = context;
        this.mDataset = mDb.readMoviesByWatchStatus( false );
        this.baseFragment = baseFragment;
    }

    @Override
    public WatchlistAdapter.ViewHolder onCreateViewHolder( ViewGroup parent, int viewType ) {
        View v = LayoutInflater.from( parent.getContext() ).inflate( R.layout.card_watchlist, parent, false );
        return new ViewHolder( v );
    }

    @Override
    public void onBindViewHolder( final WatchlistAdapter.ViewHolder holder, final int position ) {
        String movieTitle = mDataset.get(position).getTitle();
        holder.mCurrentMovie = mDataset.get( position );
        holder.mMovieTitle.setText( movieTitle );
        String posterName = holder.mCurrentMovie.getPosterPath();
        String posterUri = Constants.POSTER_URI.replace( "<posterName", posterName != null ? posterName : "/" );
        Picasso.with( context ).load( posterUri ).error( R.mipmap.ic_launcher ).into( holder.mImageView );

        holder.mRemoveMovieButton.setOnClickListener( new View.OnClickListener() {

            @Override
            public void onClick( View v ) {
                int index = mDataset.indexOf( holder.mCurrentMovie );
                mDb.deleteMovieFromWatchlist( holder.mCurrentMovie.getId() );
                removeItemFromView(index);
            }
        } );

        holder.mMovieWatchedButton.setOnClickListener( new View.OnClickListener() {

            @Override
            public void onClick( View v ) {
                int index = mDataset.indexOf( holder.mCurrentMovie );
                mDb.updateMovieWatched( true, holder.mCurrentMovie.getId() );
                removeItemFromView(index);
            }
        } );
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    private void removeItemFromView(int index){
        mDataset.remove( index );
        notifyItemRemoved( index );

        if(getItemCount() == 0){
            baseFragment.controlWatchlistAdapter();
        }
    }
}
