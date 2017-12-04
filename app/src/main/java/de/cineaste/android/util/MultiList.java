package de.cineaste.android.util;

import android.support.annotation.NonNull;
import android.util.LongSparseArray;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import de.cineaste.android.entity.MovieDto;

public class MultiList {

    private final LongSparseArray<MultiListEntry> movies;

    public MultiList() {
        this.movies = new LongSparseArray<>();
    }

    public void add(MovieDto movieDto) {
        long movieId = movieDto.getId();
        MultiListEntry entry = movies.get(movieId);

        if (entry == null) {
            movies.put(movieId, new MultiListEntry(movieDto));
        } else {
            movies.put(movieId, entry.increment());
        }
    }

    public void addAll(List<MovieDto> movieDtos) {
        for (MovieDto movieDto : movieDtos) {
            add(movieDto);
        }
    }

    public List<MultiListEntry> getMovieList() {
        List<MultiListEntry> entries = new ArrayList<>();
        for (int i = 0; i < movies.size(); i++) {
            entries.add(movies.valueAt(i));
        }

        Collections.sort(entries);

        return entries;
    }

    public class MultiListEntry implements Comparable<MultiListEntry> {
        private final MovieDto movieDto;
        private int counter;

        private MultiListEntry(MovieDto movieDto) {
            this.movieDto = movieDto;
            this.counter = 1;
        }

        private MultiListEntry increment() {
            this.counter++;
            return this;
        }

        public MovieDto getMovieDto() {
            return movieDto;
        }

        public int getCounter() {
            return counter;
        }

        @Override
        public int compareTo(@NonNull MultiListEntry multiListEntry) {
            return multiListEntry.counter - this.counter;
        }
    }
}
