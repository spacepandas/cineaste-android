package de.cineaste.android.entity.series;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

public class Season {

    private long id;
    @SerializedName("air_date")
    private Date releaseDate;
    @SerializedName("episode_count")
    private int episodeCount;
    @SerializedName("poster_path")
    private String posterPath;
    @SerializedName("season_number")
    private int seasonNumber;

    private long seriesId;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Date getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(Date releaseDate) {
        this.releaseDate = releaseDate;
    }

    public int getEpisodeCount() {
        return episodeCount;
    }

    public void setEpisodeCount(int episodeCount) {
        this.episodeCount = episodeCount;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public void setPosterPath(String posterPath) {
        this.posterPath = posterPath;
    }

    public int getSeasonNumber() {
        return seasonNumber;
    }

    public void setSeasonNumber(int seasonNumber) {
        this.seasonNumber = seasonNumber;
    }

    public long getSeriesId() {
        return seriesId;
    }

    public void setSeriesId(long seriesId) {
        this.seriesId = seriesId;
    }
}
