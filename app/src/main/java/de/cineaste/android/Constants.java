package de.cineaste.android;

public interface Constants {

	int DATABASE_VERSION = 2;
	String DATABASE_NAME = "cineaste.db";

	String POSTER_URI_SMALL = "https://image.tmdb.org/t/p/w342<posterName>?api_key=<API_KEY>";
	String POSTER_URI_ORIGINAL = "https://image.tmdb.org/t/p/original<posterName>?api_key=<API_KEY>";
}