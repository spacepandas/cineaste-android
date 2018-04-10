package de.cineaste.android.util

import org.junit.Before
import org.junit.Test

import java.util.ArrayList

import de.cineaste.android.entity.movie.MovieDto

import org.junit.Assert.*

class MultiListTest {
    private val initialDtos = ArrayList<MovieDto>()
    private lateinit var list: MultiList

    @Before
    fun init() {
        list = MultiList()

        for (i in 0..4) {
            initialDtos.add(MovieDto(i.toLong(), "path_$i", "title $i"))
        }
    }

    @Test
    fun shouldReturnOneEntry() {
        list.add(initialDtos[0])

        assertEquals(1, list.movieList.size.toLong())
    }

    @Test
    fun shouldReturn5EntriesWithCounter1() {
        list.addAll(initialDtos)

        assertEquals(5, list.movieList.size.toLong())
        for (multiListEntry in list.movieList) {
            assertEquals(1, multiListEntry.counter.toLong())
        }
    }

    @Test
    fun shouldReturnEntriesOrderedByCounter() {
        list.add(initialDtos[0])

        list.add(initialDtos[1])

        list.add(initialDtos[2])
        list.add(initialDtos[2])
        list.add(initialDtos[2])
        list.add(initialDtos[2])

        list.add(initialDtos[3])
        list.add(initialDtos[3])

        list.add(initialDtos[4])
        list.add(initialDtos[4])
        list.add(initialDtos[4])

        assertEquals(5, list.movieList.size.toLong())

        val first = list.movieList[0]
        val second = list.movieList[1]
        val third = list.movieList[2]
        val fourth = list.movieList[3]
        val fifth = list.movieList[4]

        assertEquals(initialDtos[2], first.movieDto)
        assertEquals(4, first.counter.toLong())

        assertEquals(initialDtos[4], second.movieDto)
        assertEquals(3, second.counter.toLong())

        assertEquals(initialDtos[3], third.movieDto)
        assertEquals(2, third.counter.toLong())

        assertEquals(fourth.counter.toLong(), 1)
        assertEquals(fifth.counter.toLong(), 1)
    }
}