package de.cineaste.android.entity;

public class MovieAndState {
	private MovieStateType state;
	private Movie movie;

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
