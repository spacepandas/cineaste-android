package de.cineaste.android.network;

import android.content.Context;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import de.cineaste.android.Constants;
import de.cineaste.android.entity.Movie;

public class TheMovieDb extends BaseNetwork {

    public interface OnSearchMoviesResultListener {
        void onSearchMoviesResultListener( List<Movie> movies );
    }

    public interface OnFetchMovieResultListener {
        void onFetchMovieResultListener( Movie movie );
    }

    private final Context context;

    private final String API_KEY_TAG = "api_key=" + Constants.API_KEY;

    public TheMovieDb( Context context ) {
        super( "https://api.themoviedb.org/3/" );
        this.context = context;
    }

    public void searchMoviesAsync( String query,
                                   final OnSearchMoviesResultListener listener,
                                   String lang ) {

        String url = host +
                "search/movie?query=" + query +
                "&language=" + lang +
                "&" + API_KEY_TAG;
android.util.Log.wtf( "mgr", url );
        requestAsync(
                new Request(
                        url,
                        METHOD_GET,
                        new String[]{"Accept:application/json"},
                        null
                ),
                new OnResultListener() {
                    @Override
                    public void onResultListener( Response response ) {
                        List<Movie> movies = new ArrayList<>();
                        if( successfulRequest( response.getCode() ) ) {
                            JsonParser parser = new JsonParser();
                            JsonObject responseObject =
                                    parser.parse( response.getString() ).getAsJsonObject();
                            String movieListJson = responseObject.get( "results" ).toString();
                            Type listType = new TypeToken<List<Movie>>() {
                            }.getType();
                            movies = gson.fromJson( movieListJson, listType );
                        }
                        listener.onSearchMoviesResultListener( movies );
                    }
                } );
    }

    public void fetchMovie( long movieId, final OnFetchMovieResultListener listener ) {

        String url = host + "movie/" + movieId + "?" + API_KEY_TAG;

        requestAsync(
                new Request(
                        url,
                        METHOD_GET,
                        new String[]{"Accept:application/json"},
                        null
                ),
                new OnResultListener() {
                    @Override
                    public void onResultListener( Response response ) {
                        if( !successfulRequest( response.getCode() ) ) {
                            listener.onFetchMovieResultListener( null );
                        } else {
                            listener.onFetchMovieResultListener(
                                    gson.fromJson( response.getString(), Movie.class )
                            );
                        }
                    }
                } );
    }
}