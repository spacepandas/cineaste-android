package de.cineaste.android.util;

public interface Constants {

    int DATABASE_VERSION = 3;
    String DATABASE_NAME = "cineaste.db";

    String POSTER_BASE_URI = "https://image.tmdb.org/t/p/%s<posterName>?api_key=<API_KEY>";
    String POSTER_URI_SMALL = String.format(POSTER_BASE_URI, "w342");
    String POSTER_URI_ORIGINAL = String.format(POSTER_BASE_URI, "original");
}