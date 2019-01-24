package de.cineaste.android.util

import org.junit.Before
import org.junit.Test

import java.util.ArrayList

import de.cineaste.android.entity.movie.MovieDto

class MultiListTest {
    private val initialDtos = ArrayList<MovieDto>()
    private lateinit var list: MultiList

    @Before
    fun init() {
        list = MultiList()

        val title = "ABCDE"

        for (i in 0..4) {
            initialDtos.add(MovieDto(i.toLong(), "path_$i", "${title[i]} title $i"))
        }
    }

    @Test
    fun shouldReturnOneEntry() {
        list.add(initialDtos[0])

        assertEquals(1, list.getSortedList().size.toLong())
    }

    @Test
    fun shouldReturn5EntriesWithCounter1() {
        list.addAll(initialDtos)

        assertEquals(5, list.getSortedList().size.toLong())

        list.getSortedList().forEachIndexed { index, multiListEntry ->
            assertEquals(1, multiListEntry.counter)
            assertEquals(initialDtos[index], multiListEntry.movieDto)
        }
    }

    @Test
    fun shouldReturnEntriesOrderedByCounterAndName() {
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

        assertEquals(5, list.getSortedList().size.toLong())

        val first = list.getSortedList()[0]
        val second = list.getSortedList()[1]
        val third = list.getSortedList()[2]
        val fourth = list.getSortedList()[3]
        val fifth = list.getSortedList()[4]

        assertEquals(initialDtos[2], first.movieDto)
        assertEquals(4, first.counter.toLong())

        assertEquals(initialDtos[4], second.movieDto)
        assertEquals(3, second.counter.toLong())

        assertEquals(initialDtos[3], third.movieDto)
        assertEquals(2, third.counter.toLong())

        assertEquals(initialDtos[0], fourth.movieDto)
        assertEquals(fourth.counter.toLong(), 1)

        assertEquals(initialDtos[1], fifth.movieDto)
        assertEquals(fifth.counter.toLong(), 1)
    }
}