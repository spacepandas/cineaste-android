package de.cineaste.android.entity.series;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Series {

    private long id;
    private String name;
    @SerializedName("vote_average")
    private double voteAverage;
    @SerializedName("vote_count")
    private int voteCount;
    @SerializedName("overview")
    private String description;
    @SerializedName("first_air_date")
    private Date releaseDate;
    @SerializedName("in_production")
    private boolean inProduction;
    @SerializedName("number_of_episodes")
    private int numberOfEpisodes;
    @SerializedName("number_of_seasons")
    private int numberOfSeasons;
    @SerializedName("poster_path")
    private String posterPath;
    @SerializedName("backdrop_path")
    private String backdropPath;
    private List<Season> seasons;

    private boolean watched;
    private int listPosition;

    public Series() {
        seasons = new ArrayList<>();
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public Date getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(Date releaseDate) {
        this.releaseDate = releaseDate;
    }

    public boolean isInProduction() {
        return inProduction;
    }

    public void setInProduction(boolean inProduction) {
        this.inProduction = inProduction;
    }

    public int getNumberOfEpisodes() {
        return numberOfEpisodes;
    }

    public void setNumberOfEpisodes(int numberOfEpisodes) {
        this.numberOfEpisodes = numberOfEpisodes;
    }

    public int getNumberOfSeasons() {
        return numberOfSeasons;
    }

    public void setNumberOfSeasons(int numberOfSeasons) {
        this.numberOfSeasons = numberOfSeasons;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public void setPosterPath(String posterPath) {
        this.posterPath = posterPath;
    }

    public List<Season> getSeasons() {
        return seasons;
    }

    public void setSeasons(List<Season> seasons) {
        this.seasons = seasons;
    }

    public boolean isWatched() {
        return watched;
    }

    public void setWatched(boolean watched) {
        this.watched = watched;
    }

    public String getBackdropPath() {
        return backdropPath;
    }

    public void setBackdropPath(String backdropPath) {
        this.backdropPath = backdropPath;
    }

    public int getListPosition() {
        return listPosition;
    }

    public void setListPosition(int listPosition) {
        this.listPosition = listPosition;
    }

    public int getCurrentNumberOfSeason() {
        Season lastSeason = new Season();
        for (Season season : seasons) {
            lastSeason = season;
            if (!season.isWatched()) {
                return season.getSeasonNumber();
            }
        }

        return lastSeason.getSeasonNumber();
    }

    public int getCurrentNumberOfEpisode() {
        Season lastSeason = new Season();
        for (Season season : seasons) {
            lastSeason = season;
            if (!season.isWatched()) {
                for (Episode episode : season.getEpisodes()) {
                    if (!episode.isWatched()) {
                        return episode.getEpisodeNumber();
                    }
                }
            }
        }

        return lastSeason.getEpisodeCount();
    }
}
