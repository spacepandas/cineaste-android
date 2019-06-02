package de.cineaste.android.util

import android.util.LongSparseArray

import java.util.ArrayList

import de.cineaste.android.entity.movie.MovieDto

class MultiList {

    constructor()

    constructor(movieDtos: List<MovieDto>) {
        addAll(movieDtos)
    }

    private val movies: LongSparseArray<MultiListEntry> = LongSparseArray()

    fun clear() = movies.clear()

    fun size(): Int = movies.size()

    fun sortedList(): List<MultiListEntry> {
        val entries = ArrayList<MultiListEntry>()
        for (i in 0 until movies.size()) {
            entries.add(movies.valueAt(i))
        }

        val comparator: Comparator<MultiListEntry> = createComparatorForCounterAndTitle()

        entries.sortWith(comparator)

        return entries
    }

    private fun createComparatorForCounterAndTitle(): Comparator<MultiListEntry> {
        val byCountComparator: Comparator<MultiListEntry> = compareByDescending { it.counter }

        return byCountComparator.thenBy { entry -> entry.movieDto.title }
    }

    fun add(movieDto: MovieDto) {
        val movieId = movieDto.id
        val entry = movies.get(movieId)

        if (entry == null) {
            movies.put(movieId, MultiListEntry(movieDto))
        } else {
            movies.put(movieId, entry.increment())
        }
    }

    fun addAll(movieDtos: List<MovieDto>) {
        for (movieDto in movieDtos) {
            add(movieDto)
        }
    }

    data class MultiListEntry(
        val movieDto: MovieDto,
        var counter: Int = 1
    ) {
        fun increment(): MultiListEntry {
            this.counter++
            return this
        }
    }
}