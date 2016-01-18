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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import de.cineaste.android.Constants;
import de.cineaste.android.MovieClickListener;
import de.cineaste.android.R;
import de.cineaste.android.entity.Movie;
import de.cineaste.android.database.MovieDbHelper;

public class WatchedlistAdapter extends RecyclerView.Adapter<WatchedlistAdapter.ViewHolder> implements Observer {

    private List<Movie> dataset;
    private final MovieDbHelper db;
    private final Context context;
    private final BaseWatchlistPagerAdapter.WatchlistFragment baseFragment;
    private final MovieClickListener listener;

    public WatchedlistAdapter( Context context, BaseWatchlistPagerAdapter.WatchlistFragment baseFragment, MovieClickListener listener ) {
        this.db = MovieDbHelper.getInstance( context );
        this.context = context;
        this.db.addObserver( this );
        this.dataset = db.readMoviesByWatchStatus( true );
        this.baseFragment = baseFragment;
        this.listener = listener;
    }

    @Override
    public void update( Observable observable, Object data ) {
        dataset = db.readMoviesByWatchStatus( true );
        baseFragment.setWatchedlistAdapter();
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final TextView movieTitle;
        public final TextView movieRuntime;
        public final TextView movieVote;
        public final TextView movieDate;
        public final ImageButton removeMovie;
        public final ImageView imageView;
        final View view;
        public Movie currentMovie;

        public ViewHolder( View v ) {
            super( v );
            movieTitle = (TextView) v.findViewById( R.id.movie_title );
            movieRuntime = (TextView) v.findViewById( R.id.movie_runtime );
            movieDate = (TextView) v.findViewById( R.id.movie_date );
            movieVote = (TextView) v.findViewById( R.id.movie_vote );
            removeMovie = (ImageButton) v.findViewById( R.id.remove_button );
            imageView = (ImageView) v.findViewById( R.id.movie_poster_image_view );
            view = v;
        }

        public void assignData( final Movie movie ) {
            Resources resources = context.getResources();

            movieTitle.setText( movie.getTitle() );
            movieRuntime.setText(resources.getString( R.string.runtime, movie.getRuntime() ));
            movieVote.setText( resources.getString( R.string.vote, movie.getVoteAverage() ) );
            SimpleDateFormat sdf = new SimpleDateFormat("dd:MM:yyyy");
            String date = sdf.format(new Date( movie.getWatchedDate() ));
            movieDate.setText( date );
            String posterName = movie.getPosterPath();
            String posterUri = Constants.POSTER_URI
                    .replace( "<posterName>", posterName != null ? posterName : "/" );
            Picasso.with( context ).load( posterUri ).error( R.mipmap.ic_launcher ).into( imageView );

            removeMovie.setOnClickListener( new View.OnClickListener() {

                @Override
                public void onClick( View v ) {
                    int index = dataset.indexOf( movie );
                    removeItemFromViewAndDb( index, movie.getId() );
                }
            } );

            view.setOnClickListener( new View.OnClickListener() {
                @Override
                public void onClick( View v ) {
                    if( listener != null ) {
                        listener.onMovieClickListener( movie.getId() );
                    }
                }
            } );
        }
    }

    public WatchedlistAdapter.ViewHolder onCreateViewHolder( ViewGroup parent, int viewTyp ) {
        View v = LayoutInflater
                .from( parent.getContext() )
                .inflate( R.layout.card_watchedlist, parent, false );
        return new ViewHolder( v );
    }

    @Override
    public void onBindViewHolder( final WatchedlistAdapter.ViewHolder holder, final int position ) {
       holder.assignData( dataset.get( position ) );
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
            baseFragment.setWatchedlistAdapter();
        }
    }
}