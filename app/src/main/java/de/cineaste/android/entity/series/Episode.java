package de.cineaste.android.entity.series;

import com.google.gson.annotations.SerializedName;

public class Episode {

    private long id;
    @SerializedName("episode_number")
    private int episodeNumber;
    private String name;
    @SerializedName("overview")
    private String description;
    private long seasonId;
    private boolean watched;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getEpisodeNumber() {
        return episodeNumber;
    }

    public void setEpisodeNumber(int episodeNumber) {
        this.episodeNumber = episodeNumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public long getSeasonId() {
        return seasonId;
    }

    public void setSeasonId(long seasonId) {
        this.seasonId = seasonId;
    }

    public boolean isWatched() {
        return watched;
    }

    public void setWatched(boolean watched) {
        this.watched = watched;
    }
}
