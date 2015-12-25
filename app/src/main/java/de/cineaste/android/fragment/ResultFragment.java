package de.cineaste.android.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;
import com.google.common.collect.Multisets;

import java.util.ArrayList;
import java.util.List;

import de.cineaste.android.MainActivity;
import de.cineaste.android.R;
import de.cineaste.android.adapter.ResultAdapter;
import de.cineaste.android.entity.MatchingResult;
import de.cineaste.android.entity.Movie;
import de.cineaste.android.entity.MovieDto;
import de.cineaste.android.entity.NearbyMessage;
import de.cineaste.android.network.TheMovieDb;
import de.cineaste.android.database.MovieDbHelper;
import de.cineaste.android.database.NearbyMessageHandler;

public class ResultFragment extends Fragment implements ResultAdapter.OnMovieSelectListener {

    private NearbyMessageHandler handler;
    private List<NearbyMessage> nearbyMessages;

    @Override
    public View onCreateView( LayoutInflater inflater, ViewGroup container,
                              Bundle savedInstanceState ) {
        View view = inflater.inflate( R.layout.fragment_result, container, false );

        handler = NearbyMessageHandler.getInstance();
        nearbyMessages = handler.getMessages();

        RecyclerView result = (RecyclerView) view.findViewById( R.id.result_list );

        final LinearLayoutManager llm = new LinearLayoutManager( getActivity() );
        llm.setOrientation( LinearLayoutManager.VERTICAL );
        result.setLayoutManager( llm );
        result.setItemAnimator( new DefaultItemAnimator() );

        ResultAdapter resultAdapter = new ResultAdapter(
                getResult(),
                R.layout.card_result,
                getActivity(),
                this );
        result.setAdapter( resultAdapter );

        return view;
    }

    @Override
    public void onStop() {
        super.onStop();
        handler.clearMessages();
    }

    private ArrayList<MatchingResult> getResult() {

        ArrayList<MatchingResult> results = new ArrayList<>();
        Multiset<MovieDto> movies = HashMultiset.create( getMovies() );

        for ( Multiset.Entry<MovieDto> entry :
                Multisets.copyHighestCountFirst( movies ).entrySet() ) {
            MovieDto current = entry.getElement();
            results.add( new MatchingResult( current, movies.count( current ) ) );
            Log.d( "Test", current.getTitle() + " " + current.getId() );
        }

        return results;
    }

    @Override
    public void onMovieSelectListener( int position ) {
        MainActivity.replaceFragmentPopBackStack( getFragmentManager(), new ViewPagerFragment() );
        TheMovieDb theMovieDb = new TheMovieDb();

        theMovieDb.fetchMovie(
                getResult().get( position ).getId(),
                getActivity().getResources().getString( R.string.language_tag ),
                new TheMovieDb.OnFetchMovieResultListener() {
            @Override
            public void onFetchMovieResultListener( Movie movie ) {
                MovieDbHelper db = MovieDbHelper.getInstance( getActivity() );
                movie.setWatched( true );
                db.createOrUpdate( movie );
            }
        } );
    }

    private ArrayList<MovieDto> getMovies() {
        ArrayList<MovieDto> movies = new ArrayList<>();

        for ( NearbyMessage current : nearbyMessages ) {
            for ( MovieDto movie : current.getMovies() ) {
                movies.add( movie );
            }
        }

        return movies;
    }
}

