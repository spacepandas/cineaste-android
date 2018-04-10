package de.cineaste.android.util

import android.util.LongSparseArray

import java.util.ArrayList

import de.cineaste.android.entity.movie.MovieDto

class MultiList {

    private val movies: LongSparseArray<MultiListEntry> = LongSparseArray()

    val movieList: List<MultiListEntry>
        get() {
            val entries = ArrayList<MultiListEntry>()
            for (i in 0 until movies.size()) {
                entries.add(movies.valueAt(i))
            }

            entries.sort()

            return entries
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

    inner class MultiListEntry constructor(val movieDto: MovieDto) : Comparable<MultiListEntry> {
        var counter: Int = 0
            private set

        init {
            this.counter = 1
        }

        fun increment(): MultiListEntry {
            this.counter++
            return this
        }

        override fun compareTo(other: MultiListEntry): Int {
            return other.counter - this.counter
        }
    }
}
