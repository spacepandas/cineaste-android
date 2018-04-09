package de.cineaste.android.entity.movie;

import android.support.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

public class Movie extends MovieDto implements Comparable<Movie> {

	private int runtime;
	@SerializedName("vote_average")
	private double voteAverage;
	@SerializedName("vote_count")
	private int voteCount;
	@SerializedName("overview")
	private String description;
	private boolean watched;
	private Date watchedDate;
	@SerializedName("release_date")
	private Date releaseDate;
	private int listPosition;

	public Movie() {
		this.watched = false;
	}

	public Movie(
			long id,
			String posterPath,
			int runtime,
			String title,
			double voteAverage,
			String description,
			int voteCount) {
		super(id, posterPath, title);
		this.runtime = runtime;
		this.voteAverage = voteAverage;
		this.voteCount = voteCount;
		this.description = description;
		this.watched = false;
	}

	public int getRuntime() {
		return runtime;
	}

	public void setRuntime(int runtime) {
		this.runtime = runtime;
	}

	public double getVoteAverage() {
		return voteAverage;
	}

	public void setVoteAverage(double voteAverage) {
		this.voteAverage = voteAverage;
	}

	public int getVoteCount() {
		return voteCount;
	}

	public void setVoteCount(int voteCount) {
		this.voteCount = voteCount;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public boolean isWatched() {
		return watched;
	}

	public void setWatched(boolean watched) {
		this.watched = watched;
		if (watched && this.watchedDate == null) {
			this.watchedDate = new Date();
		}
	}

	public Date getWatchedDate() {
		return watchedDate;
	}

	public void setWatchedDate(Date watchedDate) {
		this.watchedDate = watchedDate;
	}

	public Date getReleaseDate() {
		return releaseDate;
	}

	public void setReleaseDate(Date releaseDate) {
		this.releaseDate = releaseDate;
	}

	public int getListPosition() {
		return listPosition;
	}

	public void setListPosition(int listPosition) {
		this.listPosition = listPosition;
	}

	@Override
	public int compareTo(@NonNull Movie another) {
		return this.getTitle().compareTo(another.getTitle());
	}
}
