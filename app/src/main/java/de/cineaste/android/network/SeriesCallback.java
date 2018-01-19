package de.cineaste.android.network;


import de.cineaste.android.entity.series.Series;

public interface SeriesCallback {
    void onFailure();
    void onSuccess(Series series);
}
