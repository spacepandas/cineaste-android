package de.cineaste.android;

public interface Constants {

    int DATABASE_VERSION = 1;
    String DATABASE_NAME = "cineaste.db";

    String API_KEY = BuildConfig.MovieKey;
    String POSTER_URI = "https://image.tmdb.org/t/p/w185<posterName>?api_key=" + API_KEY;
}