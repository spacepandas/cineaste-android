package de.cineaste.android.util;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import de.cineaste.android.entity.movie.MovieDto;

import static org.junit.Assert.*;

public class MultiListTest {
    private final List<MovieDto> initialDtos = new ArrayList<>();
    private MultiList list;

    @Before
    public void init() {
        list = new MultiList();

        for (int i = 0; i < 5; i++) {
            initialDtos.add(new MovieDto(i, "path_" + i, "title " + i));
        }
    }

    @Test
    public void shouldReturnOneEntry() {
        list.add(initialDtos.get(0));

        assertEquals(1, list.getMovieList().size());
    }

    @Test
    public void shouldReturn5EntriesWithCounter1() {
        list.addAll(initialDtos);

        assertEquals(5, list.getMovieList().size());
        for (MultiList.MultiListEntry multiListEntry : list.getMovieList()) {
            assertEquals(1, multiListEntry.getCounter());
        }
    }

    @Test
    public void shouldReturnEntriesOrderedByCounter() {
        list.add(initialDtos.get(0));

        list.add(initialDtos.get(1));

        list.add(initialDtos.get(2));
        list.add(initialDtos.get(2));
        list.add(initialDtos.get(2));
        list.add(initialDtos.get(2));

        list.add(initialDtos.get(3));
        list.add(initialDtos.get(3));

        list.add(initialDtos.get(4));
        list.add(initialDtos.get(4));
        list.add(initialDtos.get(4));

        assertEquals(5, list.getMovieList().size());

        MultiList.MultiListEntry first = list.getMovieList().get(0);
        MultiList.MultiListEntry second = list.getMovieList().get(1);
        MultiList.MultiListEntry third = list.getMovieList().get(2);
        MultiList.MultiListEntry fourth = list.getMovieList().get(3);
        MultiList.MultiListEntry fifth = list.getMovieList().get(4);

        assertEquals(initialDtos.get(2), first.getMovieDto());
        assertEquals(4, first.getCounter());

        assertEquals(initialDtos.get(4), second.getMovieDto());
        assertEquals(3, second.getCounter());

        assertEquals(initialDtos.get(3), third.getMovieDto());
        assertEquals(2, third.getCounter());

        assertEquals(fourth.getCounter(), 1);
        assertEquals(fifth.getCounter(), 1);
    }

}