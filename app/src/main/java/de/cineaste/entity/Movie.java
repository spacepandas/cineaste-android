package de.cineaste.entity;

import com.google.gson.annotations.SerializedName;

public class Movie extends MovieDto {

    private int runtime;
    @SerializedName("vote_average")
    private double voteAverage;
    @SerializedName("vote_count")
    private int voteCount;
    private boolean watched;

    public Movie() {
        this.watched = false;
    }

    public Movie(long id, String posterPath, int runtime, String title, double voteAverage, int voteCount) {
        super(id, posterPath, title);
        this.runtime = runtime;
        this.voteAverage = voteAverage;
        this.voteCount = voteCount;
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

    public boolean isWatched() {
        return watched;
    }

    public void setWatched( boolean watched ) {
        this.watched = watched;
    }
}
