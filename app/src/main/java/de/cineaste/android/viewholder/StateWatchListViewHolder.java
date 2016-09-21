package de.cineaste.android.viewholder;

import android.content.Context;
import android.content.res.Resources;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import de.cineaste.android.R;
import de.cineaste.android.adapter.OnBackPressedListener;
import de.cineaste.android.database.MovieDbHelper;
import de.cineaste.android.entity.Movie;
import de.cineaste.android.network.TheMovieDb;


public class StateWatchListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
	private final TextView movieTitle;
	private final TextView movieRuntime;
	private Movie currentMovie;
	private final Context context;
	private final OnBackPressedListener listener;


	public StateWatchListViewHolder( View v, Context context, OnBackPressedListener listener ) {
		super( v );
		this.context = context;
		movieTitle = (TextView) v.findViewById( R.id.movieTitle );
		movieRuntime = (TextView) v.findViewById( R.id.movieRuntime );
		ImageButton addToWatchedList = (ImageButton) v.findViewById(R.id.addToWatchedList);
		ImageButton delete = (ImageButton) v.findViewById(R.id.remove);
		this.listener = listener;
		addToWatchedList.setOnClickListener( this );
		delete.setOnClickListener( this );
	}

	public void assignData( final Movie movie ) {
		Resources resources = context.getResources();
		currentMovie = movie;
		movieTitle.setText( movie.getTitle() );
		movieRuntime.setText( resources.getString(R.string.runtime, movie.getRuntime()) );
	}

	@Override
	public void onClick( View v ) {
		final MovieDbHelper db = MovieDbHelper.getInstance( context );
		TheMovieDb theMovieDb = new TheMovieDb();

		switch ( v.getId() ) {
			case R.id.addToWatchedList:
				theMovieDb.fetchMovie(
						currentMovie.getId(),
						context.getString( R.string.language_tag ),
						new TheMovieDb.OnFetchMovieResultListener() {
							@Override
							public void onFetchMovieResultListener( Movie movie ) {
								movie.setWatched( true );
								db.update(movie);
							}
						} );
				break;
			case R.id.remove:
				db.deleteMovieFromWatchlist( currentMovie );
				break;
		}
		listener.onBackPressedListener();
	}

}