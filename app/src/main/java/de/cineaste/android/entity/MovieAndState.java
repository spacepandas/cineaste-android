package de.cineaste.android.entity;

public class MovieAndState {
	private final MovieStateType state;
	private final Movie movie;

	public MovieAndState(Movie movie, MovieStateType state) {
		this.movie = movie;
		this.state = state;
	}

	public MovieStateType getState() {
		return state;
	}

	public Movie getMovie() {
		return movie;
	}
}
