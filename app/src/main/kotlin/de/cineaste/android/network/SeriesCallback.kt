package de.cineaste.android.network

import de.cineaste.android.entity.series.Series

interface SeriesCallback {
    fun onFailure()
    fun onSuccess(series: Series)
}
