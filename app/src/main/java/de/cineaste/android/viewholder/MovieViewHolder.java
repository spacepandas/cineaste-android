package de.cineaste.android.viewholder;

import android.content.Context;
import android.content.res.Resources;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;

import de.cineaste.android.Constants;
import de.cineaste.android.MovieClickListener;
import de.cineaste.android.R;
import de.cineaste.android.entity.Movie;

public class MovieViewHolder extends RecyclerView.ViewHolder {

	private final Context context;
	private final MovieClickListener listener;
	private TextView movieTitle;
	private TextView movieReleaseDate;
	private TextView movieRuntime;
	private TextView movieVote;
	private ImageView imageView;
	private View view;

	public MovieViewHolder(View v, Context context, MovieClickListener listener) {
		super(v);
		this.context = context;
		this.listener = listener;
		view = v;
		movieTitle = v.findViewById(R.id.movie_title);
		movieReleaseDate = v.findViewById(R.id.movieReleaseDate);
		movieRuntime = v.findViewById(R.id.movieRuntime);
		movieVote = v.findViewById(R.id.movie_vote);
		imageView = v.findViewById(R.id.movie_poster_image_view);
	}

	public void assignData(final Movie movie) {
		Resources resources = context.getResources();

		movieTitle.setText(movie.getTitle());
		if (movie.getReleaseDate() != null) {
			movieReleaseDate.setText(convertDate(movie.getReleaseDate()));
			movieReleaseDate.setVisibility(View.VISIBLE);
		} else {
			movieReleaseDate.setVisibility(View.GONE);
		}
		movieRuntime.setText(resources.getString(R.string.runtime, movie.getRuntime()));
		movieVote.setText(resources.getString(R.string.vote, String.valueOf(movie.getVoteAverage())));
		String posterName = movie.getPosterPath();
		String posterUri = Constants.POSTER_URI_SMALL.replace("<posterName>", posterName != null ? posterName : "/");
		Picasso.with(context).load(posterUri).resize(222, 334).error(R.drawable.placeholder_poster).into(imageView);

		view.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (listener != null)
					listener.onMovieClickListener(movie.getId(),
							new View[]{view, imageView, movieTitle, movieRuntime, movieVote});
			}
		});
	}

	private String convertDate(Date date) {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy", context.getResources().getConfiguration().locale);
		return simpleDateFormat.format(date);
	}
}
