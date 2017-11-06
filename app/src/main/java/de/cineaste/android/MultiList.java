package de.cineaste.android;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.cineaste.android.entity.MovieDto;

/**
 * Created by marcelgross on 06.11.17.
 */

public class MultiList {

    private Map<Long, MultiListEntry> movies;

    public MultiList() {
        this.movies = new HashMap<>();
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
        entries.addAll(movies.values());

        Collections.sort(entries);

        return entries;
    }

    public class MultiListEntry implements Comparable<MultiListEntry> {
        private MovieDto movieDto;
        private int counter;

        public MultiListEntry(MovieDto movieDto) {
            this.movieDto = movieDto;
            this.counter = 1;
        }

        public MultiListEntry increment() {
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
