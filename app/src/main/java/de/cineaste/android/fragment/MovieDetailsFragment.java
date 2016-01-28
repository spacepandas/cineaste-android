package de.cineaste.android.fragment;

import android.content.BroadcastReceiver;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import de.cineaste.android.Constants;
import de.cineaste.android.R;
import de.cineaste.android.entity.Movie;
import de.cineaste.android.database.BaseDao;
import de.cineaste.android.database.MovieDbHelper;
import de.cineaste.android.network.TheMovieDb;
import de.cineaste.android.receiver.NetworkChangeReceiver;

public class MovieDetailsFragment extends Fragment {

    private TextView movieTitle;
    private TextView movieRuntime;
    private TextView movieVote;
    private TextView movieDescription;
    private ImageView moviePoster;

    @Override
    public View onCreateView( LayoutInflater inflater, ViewGroup container,
                              Bundle savedInstanceState ) {
        View view = inflater.inflate( R.layout.fragment_movie_details, container, false );

        Bundle bundle = getArguments();
        long movieId = bundle.getLong( BaseDao.MovieEntry._ID );
        movieTitle = (TextView) view.findViewById( R.id.movie_title );
        movieRuntime = (TextView) view.findViewById( R.id.movie_runtime );
        movieVote = (TextView) view.findViewById( R.id.movie_vote );
        movieDescription = (TextView) view.findViewById( R.id.movie_description );
        moviePoster = (ImageView) view.findViewById( R.id.movie_poster );

        MovieDbHelper movieDbHelper = MovieDbHelper.getInstance( getActivity() );
        Movie currentMovie = movieDbHelper.readMovie( movieId );
        if( currentMovie == null ) {
            if(NetworkChangeReceiver.getInstance().isConnected){
                TheMovieDb theMovieDb = new TheMovieDb();
                theMovieDb.fetchMovie(movieId, getResources().getString(R.string.language_tag), new TheMovieDb.OnFetchMovieResultListener() {
                    @Override
                    public void onFetchMovieResultListener(Movie movie) {
                        assignData(movie);
                    }
                });
            }
        } else {
            assignData( currentMovie );
        }

        return view;
    }

    public void assignData( Movie currentMovie ) {
        Resources resources = getResources();
        movieTitle.setText( currentMovie.getTitle() );
        String description = currentMovie.getDescription();
        movieRuntime.setText( resources.getString( R.string.runtime, currentMovie.getRuntime() ) );
        movieVote.setText( resources.getString( R.string.vote, currentMovie.getVoteAverage() ) );
        movieDescription.setText(
                (description == null || description.isEmpty())
                        ? resources.getString( R.string.noDescription ) : description );

        String posterUri = Constants.POSTER_URI
                .replace( "<posterName>", currentMovie.getPosterPath() != null ?
                        currentMovie.getPosterPath() : "/" );
        Picasso.with( getActivity() )
                .load( posterUri )
                .error( R.drawable.placeholder_poster )
                .into( moviePoster );
    }
}