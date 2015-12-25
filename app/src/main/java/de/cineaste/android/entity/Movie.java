package de.cineaste.android.entity;

import com.google.gson.annotations.SerializedName;

public class Movie extends MovieDto {

    private int runtime;
    @SerializedName( "vote_average" )
    private double voteAverage;
    @SerializedName( "vote_count" )
    private int voteCount;
    @SerializedName( "overview" )
    private String description;
    private boolean watched;

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
            int voteCount ) {
        super( id, posterPath, title );
        this.runtime = runtime;
        this.voteAverage = voteAverage;
        this.voteCount = voteCount;
        this.description = description;
        this.watched = false;
    }

    public int getRuntime() {
        return runtime;
    }

    public void setRuntime( int runtime ) {
        this.runtime = runtime;
    }

    public double getVoteAverage() {
        return voteAverage;
    }

    public void setVoteAverage( double voteAverage ) {
        this.voteAverage = voteAverage;
    }

    public int getVoteCount() {
        return voteCount;
    }

    public void setVoteCount( int voteCount ) {
        this.voteCount = voteCount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription( String description ) {
        this.description = description;
    }

    public boolean isWatched() {
        return watched;
    }

    public void setWatched( boolean watched ) {
        this.watched = watched;
    }
}
