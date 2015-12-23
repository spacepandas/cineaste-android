package de.cineaste.android.fragment;

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
import de.cineaste.android.persistence.BaseDao;
import de.cineaste.android.persistence.MovieDbHelper;

public class MovieDetailsFragment extends Fragment {

    @Override
    public View onCreateView( LayoutInflater inflater, ViewGroup container,
                              Bundle savedInstanceState ) {
        View view = inflater.inflate( R.layout.fragment_movie_details, container, false );

        Bundle bundle = getArguments();
        long movieId = bundle.getLong( BaseDao.MovieEntry._ID );

        MovieDbHelper movieDbHelper = MovieDbHelper.getInstance( getActivity() );

        Movie currentMovie = movieDbHelper.readMovie( movieId );
        TextView movieTitle = (TextView) view.findViewById( R.id.movie_title );
        TextView movieRuntime = (TextView) view.findViewById( R.id.movie_runtime );
        TextView movieVote = (TextView) view.findViewById( R.id.movie_vote );
        ImageView moviePoster = (ImageView) view.findViewById( R.id.movie_poster );

        Resources resources = getResources();

        movieTitle.setText( currentMovie.getTitle() );
        movieRuntime.setText( resources.getString( R.string.runtime, currentMovie.getRuntime() ) );
        movieVote.setText( resources.getString( R.string.vote, currentMovie.getVoteAverage() ) );

        String posterUri = Constants.POSTER_URI
                .replace( "<posterName>", currentMovie.getPosterPath() != null ?
                        currentMovie.getPosterPath() : "/" );
        Picasso.with( getActivity() )
                .load( posterUri )
                .error( R.mipmap.ic_launcher )
                .into( moviePoster );


        return view;
    }
}
